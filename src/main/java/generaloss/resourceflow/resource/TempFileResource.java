package generaloss.resourceflow.resource;

import java.io.File;
import java.io.IOException;

public class TempFileResource extends FileResource {

    protected TempFileResource(String prefix, String suffix, File directory) throws IOException {
        super(File.createTempFile(prefix, suffix, directory));
    }

    protected TempFileResource(String prefix, String suffix) throws IOException {
        this(prefix, suffix, null);
    }

}
