package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ClasspathEntryZip extends ClasspathEntry {

    public ClasspathEntryZip(String filePath, String entryPath, String name, boolean isDirectory) {
        super(filePath, entryPath, name, isDirectory);
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @SuppressWarnings("resource")
    @Override
    public InputStream openInputStream() {
        try {
            final ZipFile zipFile = new ZipFile(super.filePath);

            final ZipEntry zipEntry = zipFile.getEntry(super.entryPath);
            if(zipEntry == null)
                throw new ResourceAccessException("ZIP entry does not exist: " + super.filePath + "!" + super.entryPath);

            final InputStream inputStream = zipFile.getInputStream(zipEntry);
            return new FilterInputStream(inputStream) {
                @Override
                public void close() throws IOException {
                    super.close();
                    zipFile.close();
                }
            };

        } catch(IOException e) {
            throw new ResourceAccessException("Cannot open ZIP entry: " + super.filePath + "!" + super.entryPath, e);
        }
    }

    @Override
    public String[] list(StringFilter filter) {
        try(final ZipFile zipFile = new ZipFile(super.filePath)) {

            return zipFile.stream()
                .filter(zipEntry -> {
                    final String name = zipEntry.getName();
                    return (name.startsWith(entryPath) && !name.equals(entryPath));
                })
                .map(zipEntry -> {
                    final String relative = zipEntry.getName().substring(entryPath.length());

                    final int slashIndex = relative.indexOf('/');
                    if(slashIndex == -1)
                        return relative;

                    return relative.substring(0, slashIndex);
                })
                .distinct()
                .filter(filter)
                .toArray(String[]::new);

        } catch(IOException e) {
            return new String[0];
        }
    }

    @Override
    public ClasspathEntry[] listEntries(boolean directories, boolean files, StringFilter filter) {
        try(final ZipFile zipFile = new ZipFile(super.filePath)) {

            final Set<String> visitedNames = new HashSet<>();

            zipFile.entries().asIterator().forEachRemaining(zipEntry -> {
                if( != directories)
                    return;

                final String entryName = zipEntry.getName();
                if(entryName.equals(entryPath) || !entryName.startsWith(entryPath))
                    return;

                final String relative = entryName.substring(entryPath.length());

                final int slashIndex = relative.indexOf('/');
                final String name = (slashIndex == -1) ? relative : relative.substring(0, slashIndex);

                if(visitedNames.contains(name))
                    return;
                visitedNames.add(name);

                if(!filter.test(name))
                    return;

                final ClasspathEntryZip entry = new ClasspathEntryZip(
                    filePath,
                    entryPath + entryName,
                    entryName,
                    zipEntry.isDirectory()
                );
            });

            return zipFile.stream()
                .map(entryName ->

                )
                .toArray(ClasspathEntryZip[]::new);

        } catch(IOException e) {
            return new ClasspathEntryZip[0];
        }
    }

}
