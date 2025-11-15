package generaloss.resourceflow.resource;

import generaloss.rawlist.StringList;
import generaloss.resourceflow.ResUtils;
import generaloss.resourceflow.stream.ClassFilter;
import generaloss.resourceflow.stream.StringFilter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ClasspathResource extends Resource {

    private static final String CLASS_EXTENSION = ".class";

    private final ClassLoader classLoader;
    private String entryPath;
    private final boolean disableCaching;

    private ClasspathEntryHolder entriesHolder;

    protected ClasspathResource(ClassLoader classLoader, boolean disableCaching, String entryPath) throws ResourceAccessException {
        this.classLoader = classLoader;
        this.disableCaching = disableCaching;
        this.entryPath = ResUtils.normalizePath(entryPath);
    }

    protected ClasspathResource(ClassLoader classLoader, String entryPath) throws ResourceAccessException {
        this(classLoader, false, entryPath);
    }

    protected ClasspathResource(String entryPath) throws ResourceAccessException {
        this(ClasspathResource.class.getClassLoader(), entryPath);
    }

    public ClassLoader classLoader() {
        return classLoader;
    }

    @Override
    public String path() {
        return entryPath;
    }


    private void initEntries() throws ResourceAccessException {
        if(!disableCaching && entriesHolder != null)
            return;

        entriesHolder = new ClasspathEntryHolder(classLoader, entryPath);

        if(entriesHolder.isDirectory() && !entryPath.endsWith("/"))
            entryPath += "/";
    }

    @Override
    public InputStream inStream() throws ResourceAccessException {
        this.initEntries();

        if(entriesHolder.isEmpty())
            throw new ResourceAccessException("JAR resource does not exist: " + entryPath);
        if(entriesHolder.isDirectory())
            throw new ResourceAccessException("Cannot create InputStream for directory: " + entryPath);

        final ClasspathEntry entry = entriesHolder.getEntries()[0];
        return entry.openInputStream();
    }

    @Override
    public boolean exists() {
        this.initEntries();
        return !entriesHolder.isEmpty();
    }

    public boolean isDirectory() {
        this.initEntries();
        return entriesHolder.isDirectory();
    }


    public ClasspathResource child(String name) {
        if(!this.isDirectory())
            throw new ResourceAccessException("Cannot get child of non-directory resource: " + entryPath);
        return new ClasspathResource(classLoader, entryPath + name);
    }


    public String[] listNames(StringFilter nameFilter) {
        this.initEntries();
        final StringList list = new StringList();

        entriesHolder.forEach(entry ->
            list.add(entry.listEntryNames(nameFilter)));

        return list.trim().array();
    }

    public String[] listNames() {
        return this.listNames(StringFilter.ANY);
    }


    public ClasspathResource[] list(StringFilter nameFilter) {
        final String[] names = this.listNames(nameFilter);

        final ClasspathResource[] list = new ClasspathResource[names.length];
        for(int i = 0; i < names.length; i++)
            list[i] = this.child(names[i]);

        return list;
    }

    public ClasspathResource[] list() {
        return this.list(StringFilter.ANY);
    }


    private static Class<?> classByFilename(String classPath) {
        try {
            final int extensionStartIndex = (classPath.length() - CLASS_EXTENSION.length());
            final String classPathWithoutExtension = classPath.substring(0, extensionStartIndex);
            final String className = classPathWithoutExtension.replace('/', '.');
            return Class.forName(className);
        } catch (ClassNotFoundException ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T>[] listClasses(ClassFilter filter) {
        final String[] classFilenames = this.listNames(name -> name.endsWith(CLASS_EXTENSION));
        final List<Class<?>> list = new ArrayList<>(classFilenames.length);

        for(String classFilename : classFilenames) {
            final Class<?> clazz = classByFilename(entryPath + classFilename);
            if(clazz != null && filter.test(clazz))
                list.add(clazz);
        }

        return list.toArray(new Class[0]);
    }

    public <T> Class<T>[] listClasses() {
        return this.listClasses(ClassFilter.ANY);
    }


    @SuppressWarnings("unchecked")
    public <T> Class<T>[] listClassesRecursive(ClassFilter filter) {
        this.initEntries();

        final List<Class<?>> list = new ArrayList<>(entriesHolder.size());
        for(ClasspathEntry entry : entriesHolder.getEntries())
            collectClassesRecursive(entry, list, filter);

        return list.toArray(new Class[0]);
    }

    public <T> Class<T>[] listClassesRecursive() {
        return this.listClassesRecursive(ClassFilter.ANY);
    }

    private static void collectClassesRecursive(ClasspathEntry prevEntry, List<Class<?>> output, ClassFilter filter) {
        final ClasspathEntry[] entries = prevEntry.listEntries(StringFilter.ANY);
        for(ClasspathEntry entry : entries) {

            if(entry.isDirectory) {
                collectClassesRecursive(entry, output, filter);
            }else {
                final Class<?> clazz = classByFilename(entry.internalEntryPath);
                if(clazz != null && filter.test(clazz))
                    output.add(clazz);
            }
        }
    }

}
