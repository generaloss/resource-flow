package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ClasspathZipEntry extends ClasspathEntry {

    public ClasspathZipEntry(String filePath, String entryPath, String name, boolean isDirectory) {
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
    public ClasspathEntry[] listEntries(StringFilter filter) {
        try(final ZipFile zipFile = new ZipFile(super.filePath)) {

            final Set<String> visitedNames = new HashSet<>();
            final List<ClasspathEntry> entries = new ArrayList<>();

            zipFile.entries().asIterator().forEachRemaining(zipEntry -> {
                final String entryName = zipEntry.getName();
                if(entryName.equals(super.entryPath) || !entryName.startsWith(super.entryPath))
                    return;

                final String relative = entryName.substring(super.entryPath.length());

                final int slashIndex = relative.indexOf('/');
                final String name = (slashIndex == -1) ? relative : relative.substring(0, slashIndex);

                if(visitedNames.contains(name))
                    return;
                visitedNames.add(name);

                if(!filter.test(name))
                    return;

                final boolean isDirectory = zipEntry.isDirectory();
                final String entryPathPostfix = (isDirectory ? "/" : "");

                final ClasspathZipEntry entry = new ClasspathZipEntry(
                    super.filePath,
                    (super.entryPath + name + entryPathPostfix),
                    name,
                    isDirectory
                );
                entries.add(entry);
            });

            return entries.toArray(ClasspathEntry[]::new);
        } catch(IOException e) {
            return new ClasspathZipEntry[0];
        }
    }

    @Override
    public String[] listEntryNames(StringFilter filter) {
        try(final ZipFile zipFile = new ZipFile(super.filePath)) {

            return zipFile.stream()
                .filter(zipEntry -> {
                    final String name = zipEntry.getName();
                    return (name.startsWith(super.entryPath) && !name.equals(super.entryPath));
                })
                .map(zipEntry -> {
                    final String relative = zipEntry.getName().substring(super.entryPath.length());

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

}
