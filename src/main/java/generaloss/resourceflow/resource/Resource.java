package generaloss.resourceflow.resource;

import generaloss.rawlist.StringList;
import generaloss.resourceflow.stream.BinaryInputStream;
import generaloss.resourceflow.stream.FastReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class Resource {

    protected Resource() { }


    public BinaryInputStream inStreamBin() {
        return new BinaryInputStream(this.inStream());
    }

    public FastReader reader() {
        return new FastReader(this.inStream());
    }


    public byte[] readBytes() {
        try(InputStream inStream = this.inStream()) {
            return inStream.readAllBytes();
        } catch(IOException e) {
            return null;
        }
    }

    public ByteBuffer readByteBuffer() {
        final byte[] bytes = this.readBytes();
        if(bytes == null)
            return null;

        final ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.order(ByteOrder.nativeOrder());
        buffer.put(bytes);
        buffer.flip();

        return buffer;
    }

    public String readString(Charset charset) {
        final byte[] bytes = this.readBytes();
        if(bytes == null)
            return null;

        return new String(bytes, charset);
    }

    public String readString() {
        return this.readString(StandardCharsets.UTF_8);
    }

    public String[] readLines(Charset charset) {
        final StringList lines = new StringList();

        final FastReader reader = this.reader();
        while(reader.hasNext())
            lines.add(reader.nextLine(charset));
        reader.close();

        return lines.trim().array();
    }

    public String[] readLines() {
        return this.readLines(StandardCharsets.UTF_8);
    }


    public String name() {
        final String path = this.path();

        final int lastSeparatorIndex = path.lastIndexOf("/");
        if(lastSeparatorIndex == -1)
            return path;

        return  path.substring(lastSeparatorIndex + 1);
    }

    public String simpleName() {
        final String name = this.name();

        final int lastDotIndex = name.lastIndexOf('.');
        if(lastDotIndex == -1)
            return name;

        return name.substring(0, lastDotIndex);
    }

    public String extension() {
        final String name = this.name();

        final int lastDotIndex = name.lastIndexOf(".");
        if(lastDotIndex == -1)
            return "";

        return  name.substring(lastDotIndex + 1);
    }


    public boolean isFileRes() {
        return (this instanceof FileResource);
    }

    public boolean isTempRes() {
        return (this instanceof TempFileResource);
    }

    public boolean isInternalRes() {
        return (this instanceof InternalResource);
    }

    public boolean isUrlRes() {
        return (this instanceof URLResource);
    }

    public boolean isZipRes() {
        return (this instanceof ZipResource);
    }

    public boolean isJarRes() {
        return (this instanceof ClasspathResource);
    }


    public FileResource asFileRes() {
        return ((FileResource) this);
    }

    public TempFileResource asTempRes() {
        return ((TempFileResource) this);
    }

    public InternalResource asInternalRes() {
        return ((InternalResource) this);
    }

    public URLResource asUrlRes() {
        return ((URLResource) this);
    }

    public ZipResource asZipRes() {
        return ((ZipResource) this);
    }

    public ClasspathResource asJarRes() {
        return ((ClasspathResource) this);
    }


    public abstract String path();

    public abstract InputStream inStream() throws ResourceAccessException;

    public abstract boolean exists();


    @Override
    public String toString() {
        return this.path();
    }


    public static FileResource file(String pathStr) {
        if(pathStr == null)
            throw new IllegalArgumentException("Argument 'pathStr' cannot be null");
        return new FileResource(pathStr);
    }

    public static FileResource file(File file) {
        if(file == null)
            throw new IllegalArgumentException("Argument 'file' cannot be null");
        return new FileResource(file);
    }

    public static FileResource file(Path path) {
        if(path == null)
            throw new IllegalArgumentException("Argument 'path' cannot be null");
        return new FileResource(path);
    }

    public static FileResource file(File parent, String child) {
        if(child == null)
            throw new IllegalArgumentException("Argument 'child' cannot be null");
        return new FileResource(parent, child);
    }

    public static FileResource file(URI uri) {
        if(uri == null)
            throw new IllegalArgumentException("Argument 'uri' cannot be null");
        return new FileResource(uri);
    }

    public static FileResource[] file(String... paths) {
        if(paths == null)
            throw new IllegalArgumentException("Argument 'paths' cannot be null");

        final FileResource[] arr = new FileResource[paths.length];
        for(int i = 0; i < arr.length; i++)
            arr[i] = file(paths[i]);

        return arr;
    }

    public static FileResource[] file(File... files) {
        if(files == null)
            throw new IllegalArgumentException("Argument 'files' cannot be null");

        final FileResource[] arr = new FileResource[files.length];
        for(int i = 0; i < arr.length; i++)
            arr[i] = file(files[i]);

        return arr;
    }

    public static FileResource[] file(Path... paths) {
        if(paths == null)
            throw new IllegalArgumentException("Argument 'paths' cannot be null");

        final FileResource[] arr = new FileResource[paths.length];
        for(int i = 0; i < arr.length; i++)
            arr[i] = file(paths[i]);

        return arr;
    }

    public static FileResource[] file(URI... uris) {
        if(uris == null)
            throw new IllegalArgumentException("Argument 'uris' cannot be null");

        final FileResource[] arr = new FileResource[uris.length];
        for(int i = 0; i < arr.length; i++)
            arr[i] = file(uris[i]);

        return arr;
    }


    public static TempFileResource temp(String prefix, String suffix, File directory) throws IOException {
        if(prefix == null)
            throw new IllegalArgumentException("Argument 'prefix' cannot be null");
        return new TempFileResource(prefix, suffix, directory);
    }

    public static TempFileResource temp(String prefix, String suffix) throws IOException {
        if(prefix == null)
            throw new IllegalArgumentException("Argument 'prefix' cannot be null");
        return new TempFileResource(prefix, suffix);
    }


    public static InternalResource internal(Class<?> classLoader, String path) {
        if(classLoader == null)
            throw new IllegalArgumentException("Argument 'classLoader' cannot be null");
        if(path == null)
            throw new IllegalArgumentException("Argument 'path' cannot be null");

        return new InternalResource(classLoader, path);
    }

    public static InternalResource internal(String path) {
        return internal(InternalResource.class, path);
    }

    public static InternalResource[] internal(Class<?> classLoader, String... paths) {
        if(paths == null)
            throw new IllegalArgumentException("Argument 'paths' cannot be null");

        final InternalResource[] arr = new InternalResource[paths.length];
        for(int i = 0; i < arr.length; i++)
            arr[i] = internal(classLoader, paths[i]);

        return arr;
    }

    public static InternalResource[] internal(String... paths) {
        return internal(InternalResource.class, paths);
    }


    public static URLResource url(URL url) {
        if(url == null)
            throw new IllegalArgumentException("Argument 'url' cannot be null");
        return new URLResource(url);
    }

    public static URLResource url(String url) throws MalformedURLException {
        if(url == null)
            throw new IllegalArgumentException("Argument 'url' cannot be null");
        return new URLResource(url);
    }

    public static URLResource[] url(URL... urls) {
        if(urls == null)
            throw new IllegalArgumentException("Argument 'urls' cannot be null");

        final URLResource[] arr = new URLResource[urls.length];
        for(int i = 0; i < arr.length; i++)
            arr[i] = url(urls[i]);

        return arr;
    }

    public static URLResource[] url(String... urls) throws MalformedURLException {
        if(urls == null)
            throw new IllegalArgumentException("Argument 'urls' cannot be null");

        final URLResource[] arr = new URLResource[urls.length];
        for(int i = 0; i < arr.length; i++)
            arr[i] = url(urls[i]);

        return arr;
    }


    public static ZipResource zip(ZipFile zipFile, ZipEntry entry) {
        if(zipFile == null)
            throw new IllegalArgumentException("Argument 'zipFile' cannot be null");
        if(entry == null)
            throw new IllegalArgumentException("Argument 'entry' cannot be null");

        return new ZipResource(zipFile, entry);
    }

    public static ZipResource[] zip(ZipFile zipFile) {
        if(zipFile == null)
            throw new IllegalArgumentException("Argument 'zipFile' cannot be null");

        final ZipResource[] arr = new ZipResource[zipFile.size()];
        final Enumeration<? extends ZipEntry> entries = zipFile.entries();
        for(int i = 0; entries.hasMoreElements(); i++)
            arr[i] = zip(zipFile, entries.nextElement());

        return arr;
    }


    public static ClasspathResource classpath(ClassLoader classLoader, boolean disableCaching, String entryPath) {
        if(classLoader == null)
            throw new IllegalArgumentException("Argument 'classLoader' cannot be null");
        if(entryPath == null)
            throw new IllegalArgumentException("Argument 'path' cannot be null");

        return new ClasspathResource(classLoader, disableCaching, entryPath);
    }

    public static ClasspathResource classpath(ClassLoader classLoader, String entryPath) {
        return classpath(classLoader, false, entryPath);
    }

    public static ClasspathResource classpath(String entryPath) {
        return classpath(ClasspathResource.class.getClassLoader(), entryPath);
    }

    public static ClasspathResource[] classpath(ClassLoader classLoader, boolean disableCaching, String... entriesPaths) {
        if(entriesPaths == null)
            throw new IllegalArgumentException("Argument 'entriesPaths' cannot be null");

        final ClasspathResource[] arr = new ClasspathResource[entriesPaths.length];
        for(int i = 0; i < arr.length; i++)
            arr[i] = classpath(classLoader, disableCaching, entriesPaths[i]);

        return arr;
    }

    public static ClasspathResource[] classpath(ClassLoader classLoader, String... entriesPaths) {
        return classpath(classLoader, false, entriesPaths);
    }

    public static ClasspathResource[] classpath(String... entriesPaths) {
        return classpath(ClasspathResource.class.getClassLoader(), entriesPaths);
    }

}