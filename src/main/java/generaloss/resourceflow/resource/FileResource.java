package generaloss.resourceflow.resource;

import generaloss.resourceflow.ResUtils;
import generaloss.resourceflow.stream.BinaryOutputStream;

import java.io.*;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileResource extends Resource {

    protected final File file;

    protected FileResource(File file) {
        this.file = file;
    }

    protected FileResource(Path path) {
        this.file = path.toFile();
    }

    protected FileResource(String pathStr) {
        pathStr = ResUtils.osGeneralizePath(pathStr);
        final Path path = Paths.get(pathStr).normalize();

        this.file = path.toFile();
    }

    protected FileResource(File parent, String child) {
        this.file = new File(parent, child);
    }

    protected FileResource(URI uri) {
        this.file = new File(uri);
    }


    public File file() {
        return file;
    }


    public boolean create() {
        try {
            return file.createNewFile();
        } catch(IOException ignored) {
            return false;
        }
    }

    public boolean createWithParents() {
        final File parent = file.getParentFile();
        if(parent == null)
            return false;

        parent.mkdirs();
        return this.create();
    }

    public boolean mkdir() {
        return file.mkdir();
    }

    public boolean mkdirs() {
        return file.mkdirs();
    }


    public boolean renameTo(File dst) {
        return file.renameTo(dst);
    }

    public boolean renameTo(FileResource dst) {
        return this.renameTo(dst.file);
    }

    public FileResource rename(String name, boolean overwrite) {
        final File dst = new File(file.getParentFile(), name);

        if(!overwrite && dst.exists())
            return this;
        if(!this.renameTo(dst))
            return this;

        return new FileResource(dst);
    }

    public FileResource rename(String name) {
        return this.rename(name, false);
    }


    public FileResource move(Path target, CopyOption... options) throws IOException {
        target = target.normalize();
        Files.move(file.toPath(), target, options);
        return new FileResource(target);
    }

    public FileResource move(String targetPath, CopyOption... options) throws IOException {
        return this.move(Paths.get(targetPath), options);
    }


    public FileResource copy(Path target, CopyOption... options) throws IOException {
        target = target.normalize();
        Files.copy(file.toPath(), target, options);
        return new FileResource(target);
    }

    public FileResource copy(String targetPath, CopyOption... options) throws IOException {
        return this.copy(Paths.get(targetPath), options);
    }


    public boolean delete() {
        return file.delete();
    }

    public void deleteOnExit() {
        file.deleteOnExit();
    }


    public boolean isDir() {
        return file.isDirectory();
    }

    public boolean isFile() {
        return file.isFile();
    }


    public String parentPath() {
        return file.getParent();
    }

    public File parentFile() {
        return file.getParentFile();
    }

    public FileResource parent() {
        final File parent = this.parentFile();
        if(parent == null)
            return null;
        return new FileResource(parent);
    }


    public File childFile(String name) {
        if(this.path().isEmpty())
            return new File(name);
        return new File(file, name);
    }

    public FileResource child(String name) {
        final File childFile = this.childFile(name);
        return new FileResource(childFile);
    }

    public FileResource createChildFile(String name) {
        final FileResource child = this.child(name);
        if(!child.createWithParents())
            return null;
        return child;
    }

    public FileResource createChildDir(String name) {
        final FileResource child = this.child(name);
        if(!child.mkdirs())
            return null;
        return child;
    }


    public FileOutputStream outStream() throws ResourceAccessException {
        try {
            return new FileOutputStream(file);
        } catch(FileNotFoundException e) {
            throw new ResourceAccessException(e);
        }
    }

    public BinaryOutputStream outStreamBin() {
        return new BinaryOutputStream(this.outStream());
    }


    public PrintWriter writer(boolean autoFlush, Charset charset) {
        return new PrintWriter(this.outStream(), autoFlush, charset);
    }

    public PrintWriter writer(boolean autoFlush) {
        return this.writer(autoFlush, Charset.defaultCharset());
    }

    public PrintWriter writer() {
        return this.writer(true);
    }


    public boolean writeBytes(byte[] bytes) {
        try(FileOutputStream out = this.outStream()) {
            out.write(bytes);
            out.flush();

            return true;
        } catch(IOException e) {
            return false;
        }
    }

    public boolean writeString(String string, Charset charset) {
        return this.writeBytes(string.getBytes(charset));
    }

    public boolean writeString(String string) {
        return this.writeString(string, StandardCharsets.UTF_8);
    }

    public boolean appendString(String string, Charset charset) {
        return this.writeString(this.readString() + string, charset);
    }

    public boolean appendString(String string) {
        return this.appendString(string, StandardCharsets.UTF_8);
    }


    public String[] list() {
        final String[] list = file.list();
        if(list == null)
            return new String[0];
        return list;
    }

    public String[] list(FilenameFilter filter) {
        final String[] list = file.list(filter);
        if(list == null)
            return new String[0];
        return list;
    }

    public FileResource[] listResources() {
        final String[] list = this.list();

        final FileResource[] resources = new FileResource[list.length];
        for(int i = 0; i < list.length; i++)
            resources[i] = this.child(list[i]);

        return resources;
    }

    public FileResource[] listResources(FilenameFilter filter) {
        final String[] list = file.list(filter);
        if(list == null)
            return new FileResource[0];

        final FileResource[] resources = new FileResource[list.length];
        for(int i = 0; i < list.length; i++)
            resources[i] = this.child(list[i]);

        return resources;
    }


    public String absolutePath() {
        return ResUtils.osGeneralizePath(file.getAbsolutePath());
    }

    public Path toPath() {
        return file.toPath();
    }


    @Override
    public String name() {
        return file.getName();
    }

    @Override
    public String path() {
        return ResUtils.osGeneralizePath(file.getPath());
    }

    @Override
    public InputStream inStream() throws ResourceAccessException {
        try {
            return new FileInputStream(file);
        } catch(FileNotFoundException e) {
            throw new ResourceAccessException(e);
        }
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

}
