package generaloss.resourceflow.resource;

import generaloss.resourceflow.ResUtils;

import java.io.InputStream;
import java.net.URL;

public class InternalResource extends Resource {

    protected final String path;
    protected final Class<?> classLoader;

    protected InternalResource(Class<?> classLoader, String path) {
        this.classLoader = classLoader;
        this.path = ResUtils.osGeneralizePath(path);
    }

    protected InternalResource(String path) {
        this(InternalResource.class, path);
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public InputStream inStream() throws ResourceAccessException {
        final InputStream stream = classLoader.getResourceAsStream(path);
        if(stream == null)
            throw new ResourceAccessException("Internal resource does not exist: " + path);
        return stream;
    }

    @Override
    public boolean exists() {
        final URL url = classLoader.getResource(path);
        return (url != null);
    }

}
