package generaloss.resourceflow.resource;

import generaloss.resourceflow.ResUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResource extends Resource {

    protected final ZipFile file;
    protected final ZipEntry entry;

    protected ZipResource(ZipFile file, ZipEntry entry) {
        this.file = file;
        this.entry = entry;
    }

    public ZipFile file() {
        return file;
    }

    public ZipEntry entry() {
        return entry;
    }


    public boolean isDir() {
        return entry.isDirectory();
    }

    public boolean isFile() {
        return !entry.isDirectory();
    }


    private ZipEntry[] listEntries() {
        if(this.isFile())
            throw new IllegalStateException("File entry cannot be listed");

        final List<ZipEntry> list = new ArrayList<>();
        final Enumeration<? extends ZipEntry> entries = file.entries();

        while(entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();
            if(entry.isDirectory() || entry == this.entry)
                continue;
            if(entry.getName().startsWith(this.entry.getName()))
                list.add(entry);
        }

        return list.toArray(new ZipEntry[0]);
    }

    public String[] list() {
        final ZipEntry[] entries = this.listEntries();

        final String[] array = new String[entries.length];
        for(int i = 0; i < array.length; i++)
            array[i] = entries[i].getName();

        return array;
    }

    public ZipResource[] listResources() {
        final ZipEntry[] entries = this.listEntries();

        final ZipResource[] array = new ZipResource[entries.length];
        for(int i = 0; i < array.length; i++)
            array[i] = new ZipResource(file, entries[i]);

        return array;
    }


    public String absolutePath() {
        return ResUtils.osGeneralizePath(file.getName()) + "/" + this.path();
    }


    @Override
    public String name() {
        String name = entry.getName();
        if(this.isDir())
            name = name.substring(0, name.length() - 1);
        return name.substring(name.lastIndexOf('/') + 1);
    }

    @Override
    public String path() {
        return ResUtils.osGeneralizePath(entry.getName());
    }

    @Override
    public InputStream inStream() {
        try{
            return file.getInputStream(entry);
        }catch(IOException e){
            throw new RuntimeException("Unable to open zip file entry " + e.getMessage());
        }
    }

    @Override
    public boolean exists() {
        return (file != null && entry != null);
    }

}