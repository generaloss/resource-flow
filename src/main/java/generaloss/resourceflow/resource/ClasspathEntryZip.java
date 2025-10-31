package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ClasspathEntryZip extends ClasspathEntry {

    public ClasspathEntryZip(String filepath, String entrypath, String name, boolean isDir) {
        super(filepath, entrypath, name, isDir);
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
                .filter(entry -> {
                    final String name = entry.getName();
                    return (name.startsWith(entryPath) && !name.equals(entryPath));
                }).map(zipEntry -> {
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

}
