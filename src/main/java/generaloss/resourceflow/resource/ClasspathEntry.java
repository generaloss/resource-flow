package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.InputStream;

abstract class ClasspathEntry {

    protected final String absoluteFilePath;
    protected final String internalEntryPath;
    protected final String name;
    protected final boolean isDirectory;

    public ClasspathEntry(String absoluteFilePath, String internalEntryPath, String name, boolean isDirectory) {
        this.absoluteFilePath = absoluteFilePath;
        this.internalEntryPath = internalEntryPath;
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public abstract boolean isInternal();

    public abstract InputStream openInputStream();

    public abstract ClasspathEntry[] listEntries(StringFilter filter);

    public abstract String[] listEntryNames(StringFilter filter);


    public static String addEndSlash(String path) {
        if(path.isEmpty())
            return "";
        if(path.endsWith("/"))
            return path;
        return (path + "/");
    }

}
