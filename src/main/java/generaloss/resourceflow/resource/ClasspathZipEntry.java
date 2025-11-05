package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        try (final ZipFile zipFile = new ZipFile(super.filePath)) {
            final String zipEntryPath = addEndSlash(super.entryPath);

            return zipFile.stream()
                .filter(zipEntry -> {
                    final String name = zipEntry.getName();
                    return name.startsWith(zipEntryPath) && !name.equals(zipEntryPath);
                })
                .map(zipEntry -> {
                    final String relative = zipEntry.getName().substring(zipEntryPath.length());
                    final int slashIndex = relative.indexOf('/');
                    return (slashIndex == -1) ? relative : relative.substring(0, slashIndex);
                })
                .filter(name -> !name.isEmpty())
                .distinct()
                .filter(filter)
                .map(name -> {
                    final String childEntryPath = zipEntryPath + name;
                    final boolean isDirectory = zipFile.stream().anyMatch(e ->
                        e.getName().startsWith(childEntryPath + "/")
                    );
                    final String entryPathPostfix = (isDirectory ? "/" : "");

                    return new ClasspathZipEntry(
                        super.filePath,
                        (zipEntryPath + name + entryPathPostfix),
                        name,
                        isDirectory
                    );
                })
                .toArray(ClasspathEntry[]::new);

        } catch (IOException e) {
            return new ClasspathZipEntry[0];
        }
    }

    @Override
    public String[] listEntryNames(StringFilter filter) {
        try(final ZipFile zipFile = new ZipFile(super.filePath)) {

            final String zipEntryPath = addEndSlash(super.entryPath);

            return zipFile.stream()
                .map(ZipEntry::getName)
                .filter(name ->
                    name.startsWith(zipEntryPath) && !name.equals(zipEntryPath)
                )
                .map(name -> {
                    final String relative = name.substring(zipEntryPath.length());

                    final int slashIndex = relative.indexOf('/');
                    if(slashIndex == -1)
                        return relative;

                    return relative.substring(0, slashIndex);
                })
                .distinct()
                .filter(name ->
                    !name.isEmpty() && filter.test(name)
                )
                .toArray(String[]::new);

        } catch(IOException e) {
            return new String[0];
        }
    }

    private static String addEndSlash(String path) {
        if(path.isEmpty())
            return "";
        if(path.endsWith("/"))
            return path;
        return (path + "/");
    }

}
