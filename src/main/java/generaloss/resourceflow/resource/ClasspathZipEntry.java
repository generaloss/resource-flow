package generaloss.resourceflow.resource;

import generaloss.resourceflow.stream.StringFilter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ClasspathZipEntry extends ClasspathEntry {

    public ClasspathZipEntry(String absoluteFilePath, String internalEntryPath, String name, boolean isDirectory) {
        super(absoluteFilePath, internalEntryPath, name, isDirectory);
    }

    @Override
    public boolean isInternal() {
        return true;
    }

    @SuppressWarnings("resource")
    @Override
    public InputStream openInputStream() {
        try {
            final ZipFile zipFile = new ZipFile(super.absoluteFilePath);

            final ZipEntry zipEntry = zipFile.getEntry(super.internalEntryPath);
            if(zipEntry == null)
                throw new ResourceAccessException("ZIP entry does not exist: " +
                        super.absoluteFilePath + "!" + super.internalEntryPath);

            final InputStream inputStream = zipFile.getInputStream(zipEntry);
            return new FilterInputStream(inputStream) {
                @Override
                public void close() throws IOException {
                    super.close();
                    zipFile.close();
                }
            };

        } catch (IOException e) {
            throw new ResourceAccessException("Cannot open ZIP entry: " +
                    super.absoluteFilePath + "!" + super.internalEntryPath, e);
        }
    }

    @Override
    public ClasspathEntry[] listEntries(StringFilter filter) {
        try (final ZipFile zipFile = new ZipFile(super.absoluteFilePath)) {

            final String parentEntryPath = ClasspathEntry.addEndSlash(super.internalEntryPath);

            return zipFile.stream()
                .filter(entry -> {
                    final String entryPath = entry.getName();
                    return entryPath.startsWith(parentEntryPath) && !entryPath.equals(parentEntryPath);
                })
                .map(childEntry -> {
                    final String childPath = childEntry.getName();
                    final String childResidualPath = childPath.substring(parentEntryPath.length());
                    
                    final int slashIndex = childResidualPath.indexOf('/');
                    if(slashIndex == -1)
                        return childResidualPath;

                    return childResidualPath.substring(0, slashIndex);
                })
                .distinct()
                .filter(childSimpleName ->
                    !childSimpleName.isEmpty() && filter.test(childSimpleName)
                )
                .map(childSimpleName -> {
                    // check is directory
                    final String childEntryAsDir = (parentEntryPath + childSimpleName + "/");
                    final boolean isDirectory = zipFile.stream()
                        .anyMatch(e -> e.getName().startsWith(childEntryAsDir));

                    final String childPathPostfix = (isDirectory ? "/" : "");
                    final String newEntryPath = (parentEntryPath + childSimpleName + childPathPostfix);

                    return new ClasspathZipEntry(super.absoluteFilePath, newEntryPath, childSimpleName, isDirectory);
                })
                .toArray(ClasspathEntry[]::new);

        } catch (IOException e) {
            return new ClasspathZipEntry[0];
        }
    }

    @Override
    public String[] listEntryNames(StringFilter filter) {
        try(final ZipFile zipFile = new ZipFile(super.absoluteFilePath)) {

            final String parentEntryPath = ClasspathEntry.addEndSlash(super.internalEntryPath);

            return zipFile.stream()
                .filter(entry -> {
                    final String entryPath = entry.getName();
                    return entryPath.startsWith(parentEntryPath) && !entryPath.equals(parentEntryPath);
                })
                .map(childEntry -> {
                    final String childPath = childEntry.getName();
                    final String childResidualPath = childPath.substring(parentEntryPath.length());

                    final int slashIndex = childResidualPath.indexOf('/');
                    if(slashIndex == -1)
                        return childResidualPath;

                    return childResidualPath.substring(0, slashIndex);
                })
                .distinct()
                .filter(childSimpleName ->
                    !childSimpleName.isEmpty() && filter.test(childSimpleName)
                )
                .toArray(String[]::new);

        } catch (IOException e) {
            return new String[0];
        }
    }

}
