package generaloss.resourceflow.resource;

import generaloss.rawlist.StringList;
import generaloss.resourceflow.ResUtils;
import generaloss.resourceflow.stream.ClassFilter;
import generaloss.resourceflow.stream.StringFilter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClasspathResource extends Resource {

    private static final int FILE_PROTOCOL_LENGTH = "file:".length(); // = 5
    private static final String CLASS_EXTENSION = ".class";

    private final ClassLoader classLoader;
    private String entryPath;
    private final boolean disableCaching;

    private ClasspathEntry[] entries;
    private boolean isDirectory;

    protected ClasspathResource(ClassLoader classLoader, boolean disableCaching, String entryPath) throws ResourceAccessException {
        this.classLoader = classLoader;
        this.disableCaching = disableCaching;
        this.entryPath = ResUtils.normalizePath(entryPath);
    }

    protected ClasspathResource(ClassLoader classLoader, String entryPath) throws ResourceAccessException {
        this(ClasspathResource.class.getClassLoader(), false, entryPath);
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

    @Override
    public InputStream inStream() throws ResourceAccessException {
        this.initEntries();

        if(entries.length == 0)
            throw new ResourceAccessException("JAR resource does not exist: " + entryPath);
        if(isDirectory)
            throw new ResourceAccessException("Cannot create InputStream for directory: " + entryPath);

        return entries[0].openInputStream();
    }

    @Override
    public boolean exists() {
        this.initEntries();
        return (entries.length != 0);
    }

    public boolean isDirectory() {
        this.initEntries();
        return isDirectory;
    }


    public ClasspathResource getSubentry(String name) {
        return new ClasspathResource(classLoader, entryPath + name);
    }


    public String[] listSubentriesNames(StringFilter nameFilter) {
        if(nameFilter == null)
            throw new IllegalArgumentException("Argurment 'nameFilter' cannot be null");

        this.initEntries();
        if(!isDirectory)
            return new String[0];

        final StringList list = new StringList();
        for(ClasspathEntry entry : entries)
            list.add(entry.list(nameFilter));

        return list.trim().array();
    }

    public String[] listSubentriesNames() {
        return this.listSubentriesNames(StringFilter.ANY);
    }

    public ClasspathResource[] listSubentries(StringFilter nameFilter) {
        final String[] names = this.listSubentriesNames(nameFilter);

        final ClasspathResource[] list = new ClasspathResource[names.length];
        for(int i = 0; i < names.length; i++)
            list[i] = this.getSubentry(names[i]);

        return list;
    }

    public ClasspathResource[] listSubentries() {
        return this.listSubentries(StringFilter.ANY);
    }

    public Class<?>[] listClasses(ClassFilter filter) {
        final String[] classFilenames = this.listSubentriesNames(name -> name.endsWith(CLASS_EXTENSION));
        final Class<?>[] list = new Class[classFilenames.length];

        for(int i = 0; i < classFilenames.length; i++) {
            try {
                final String classFilename = classFilenames[i];
                final int extensionStartIndex = (classFilename.length() - CLASS_EXTENSION.length());
                final String simpleClassName = classFilename.substring(0, extensionStartIndex);
                final String className = (entryPath + simpleClassName).replace('/', '.');
                final Class<?> clazz = Class.forName(className);
                list[i] = clazz;
            } catch(ClassNotFoundException ignored) { }
        }

        return list;
    }


    private void initEntries() throws ResourceAccessException {
        if(disableCaching || entries != null)
            return;

        try {
            final Enumeration<URL> urls = classLoader.getResources(entryPath);
            final List<ClasspathEntry> entries = new ArrayList<>();

            while(urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                final String protocol = url.getProtocol();

                if(protocol.equals("file")) {
                    try {
                        final File file = new File(url.toURI());

                        if(file.isDirectory())
                            this.enterDirectoryMode();

                        entries.add(new ClasspathEntryFile(
                            file.getAbsolutePath(),
                            entryPath,
                            file.getName(),
                            file.isDirectory()
                        ));
                    } catch(URISyntaxException ignored) { }

                }else if(protocol.equals("jar")) {
                    final String urlPath = url.getPath();
                    final int exclamationIndex = urlPath.indexOf("!");
                    final String jarPath = urlPath.substring(FILE_PROTOCOL_LENGTH, exclamationIndex);
                    final String decodedJarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

                    try(final ZipFile zipFile = new ZipFile(decodedJarPath)) {

                        final ZipEntry zipEntry = zipFile.getEntry(entryPath);
                        if(zipEntry == null)
                            continue;

                        if(zipEntry.isDirectory())
                            this.enterDirectoryMode();

                        entries.add(new ClasspathEntryZip(
                            decodedJarPath,
                            entryPath,
                            zipEntry.getName(),
                            zipEntry.isDirectory()
                        ));
                    } catch(IOException ignored) { }
                }
            }

            this.entries = entries.toArray(new ClasspathEntry[0]);
        } catch(IOException e) {
            throw new ResourceAccessException(e);
        }
    }

    private void enterDirectoryMode() {
        isDirectory = true;
        if(!entryPath.endsWith("/"))
            entryPath += "/";
    }

}
