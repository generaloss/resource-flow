package generaloss.resourceflow.resource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class ClasspathEntryHolder {

    private static final int FILE_PROTOCOL_LENGTH = "file:".length(); // = 5

    private boolean isDirectory;
    private final ClasspathEntry[] entries;

    public ClasspathEntryHolder(ClassLoader classLoader, String entryPath) throws ResourceAccessException {
        this.entries = this.extractEntries(classLoader, entryPath);
    }

    private ClasspathEntry[] extractEntries(ClassLoader classLoader, String entryPath) {
        try {
            final Enumeration<URL> urls = classLoader.getResources(entryPath);

                final List<ClasspathEntry> entries = new ArrayList<>();

                while(urls.hasMoreElements()) {
                    final URL url = urls.nextElement();
                    final String protocol = url.getProtocol();

                    if(protocol.equals("file")) {
                        try {
                            final File file = new File(url.toURI());

                            if(file.isDirectory())
                                isDirectory = true;

                            entries.add(new ClasspathFileEntry(file, entryPath));
                        } catch (URISyntaxException ignored) { }

                    }else if(protocol.equals("jar")) {
                        final String urlPath = url.getPath();
                        final int exclamationIndex = urlPath.indexOf("!");
                        final String jarPath = urlPath.substring(FILE_PROTOCOL_LENGTH, exclamationIndex);
                        final String decodedJarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);

                        try(final ZipFile zipFile = new ZipFile(decodedJarPath)) {

                            final ZipEntry zipEntry = zipFile.getEntry(entryPath);
                            if(zipEntry == null)
                                continue;

                            if(zipEntry.isDirectory())
                                isDirectory = true;

                            entries.add(new ClasspathZipEntry(
                                decodedJarPath,
                                entryPath,
                                zipEntry.getName(),
                                zipEntry.isDirectory()
                            ));
                        } catch (IOException ignored) { }
                    }
                }
            return entries.toArray(new ClasspathEntry[0]);
        } catch (IOException e) {
            throw new ResourceAccessException(e);
        }
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public ClasspathEntry[] getEntries() {
        return entries;
    }

    public int size() {
        return entries.length;
    }

    public boolean isEmpty() {
        return (entries.length == 0);
    }


    public void forEach(Consumer<ClasspathEntry> consumer) {
        if(!isDirectory)
            return;
        for(ClasspathEntry entry : entries)
            consumer.accept(entry);
    }

}
