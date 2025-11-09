package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.*;
import java.util.Arrays;

class ClasspathFileEntry extends ClasspathEntry {

    public ClasspathFileEntry(File file, String internalEntryPath) {
        super(
            file.getAbsolutePath(),
            internalEntryPath,
            file.getName(),
            file.isDirectory()
        );
    }

    public ClasspathFileEntry(String absoluteFilePath, String internalEntryPath, String name, boolean isDirectory) {
        super(absoluteFilePath, internalEntryPath, name, isDirectory);
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public InputStream openInputStream() {
        try {
            final File file = new File(super.absoluteFilePath);
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new ResourceAccessException("Cannot open entry file: " + internalEntryPath, e);
        }
    }

    @Override
    public ClasspathEntry[] listEntries(StringFilter filter) {
        final File dirFile = new File(super.absoluteFilePath);
        final FilenameFilter filenameFilter = (file, name) -> filter.test(name);

        final File[] files = dirFile.listFiles(filenameFilter);
        if(files == null)
            return new ClasspathEntry[0];

        final String filePath = ClasspathEntry.addEndSlash(super.absoluteFilePath);
        final String entryPath = ClasspathEntry.addEndSlash(super.internalEntryPath);

        return Arrays.stream(files)
            .map(childFile -> {
                final boolean isDirectory = childFile.isDirectory();
                final String childNamePostfix = (isDirectory ? "/" : "");
                final String childName = childFile.getName() + childNamePostfix;

                final String newFilePath = (filePath + childName);
                final String newEntryPath = (entryPath + childName);

                return new ClasspathFileEntry(newFilePath, newEntryPath, childName, isDirectory);
            })
            .toArray(ClasspathEntry[]::new);
    }

    @Override
    public String[] listEntryNames(StringFilter filter) {
        final File dirFile = new File(super.absoluteFilePath);
        final FilenameFilter filenameFilter = (file, name) -> filter.test(name);
        return dirFile.list(filenameFilter);
    }

}
