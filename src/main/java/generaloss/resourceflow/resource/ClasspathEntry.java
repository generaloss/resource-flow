package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.InputStream;

abstract class ClasspathEntry {

    protected final String filePath;
    protected final String entryPath;
    protected final String name;
    protected final boolean isDirectory;

    public ClasspathEntry(String filePath, String entryPath, String name, boolean isDirectory) {
        this.filePath = filePath;
        this.entryPath = entryPath;
        this.name = name;
        this.isDirectory = isDirectory;
    }

    public abstract boolean isInternal();

    public abstract InputStream openInputStream();

    public abstract String[] list(StringFilter filter);

    public abstract ClasspathEntry[] listEntries(boolean directories, boolean files, StringFilter filter);

}
