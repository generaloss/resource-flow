package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class ClasspathEntryFile extends ClasspathEntry {

    public ClasspathEntryFile(String filePath, String entryPath, String name, boolean isDirectory) {
        super(filePath, entryPath, name, isDirectory);
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

    @Override
    public ClasspathEntry[] listEntries(StringFilter filter) {
        final File dirFile = new File(super.filePath);
        final FilenameFilter filenameFilter = (file, name) -> filter.test(name);

        final File[] files = dirFile.listFiles(filenameFilter);
        if(files == null)
            return new ClasspathEntry[0];

        final List<ClasspathEntry> entries = new ArrayList<>();

        for(File file : files) {
            final String name = file.getName();
            if(!filter.test(name))
                continue;

            final boolean isDirectory = file.isDirectory();

            final ClasspathEntryFile entry = new ClasspathEntryFile(
                (filePath + "/" + name),
                (entryPath + name + (isDirectory ? "/" : "")),
                name,
                isDirectory
            );

            entries.add(entry);
        }

        return entries.toArray(new ClasspathEntry[0]);
    }

}
