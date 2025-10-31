package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.*;

class ClasspathEntryFile extends ClasspathEntry {

    public ClasspathEntryFile(String filePath, String entryPath, String name, boolean isDir) {
        super(filePath, entryPath, name, isDir);
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public InputStream openInputStream() {
        try {
            final File file = new File(super.filePath);
            return new FileInputStream(file);
        } catch(IOException e) {
            throw new ResourceAccessException("Cannot open entry file: " + entryPath, e);
        }
    }

    @Override
    public String[] list(StringFilter filter) {
        final File dirFile = new File(super.filePath);
        final FilenameFilter filenameFilter = (file, name) -> filter.test(name);
        return dirFile.list(filenameFilter);
    }

}
