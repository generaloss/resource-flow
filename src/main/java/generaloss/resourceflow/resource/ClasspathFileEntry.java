package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.*;
import java.util.Arrays;

class ClasspathFileEntry extends ClasspathEntry {

    public ClasspathFileEntry(String filePath, String entryPath, String name, boolean isDirectory) {
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
    public ClasspathEntry[] listEntries(StringFilter filter) {
        final File dirFile = new File(super.filePath);
        final FilenameFilter filenameFilter = (file, name) -> filter.test(name);

        final File[] files = dirFile.listFiles(filenameFilter);
        if(files == null)
            return new ClasspathEntry[0];

        return Arrays.stream(files)
            .filter(file ->
                filter.test(file.getName())
            )
            .map(file -> {
                final boolean isDirectory = file.isDirectory();
                final String entryPathPostfix = (isDirectory ? "/" : "");

                return new ClasspathFileEntry(
                    (super.filePath + "/" + super.name),
                    (super.entryPath + super.name + entryPathPostfix),
                    super.name,
                    isDirectory
                );
            })
            .toArray(ClasspathEntry[]::new);
    }

    @Override
    public String[] listEntryNames(StringFilter filter) {
        final File dirFile = new File(super.filePath);
        final FilenameFilter filenameFilter = (file, name) -> filter.test(name);
        return dirFile.list(filenameFilter);
    }

}
