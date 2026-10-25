package generaloss.resourceflow.resource;

import generaloss.resourceflow.ResUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class InternalResource extends Resource {

    private final String path;
    private final Class<?> resourceClass;

    protected InternalResource(Class<?> resourceClass, String path) {
        this.resourceClass = resourceClass;
        this.path = ResUtils.osGeneralizePath(path);
    }

    protected InternalResource(String path) {
        this(InternalResource.class, path);
    }

    @Override
    public String path() {
        return path;
    }

    public Class<?> resourceClass() {
        return resourceClass;
    }

    @Override
    public InputStream inStream() throws ResourceAccessException {
        for(InternalResourceSource source : SOURCES) {
            try {
                return source.inStream(resourceClass, path);
            } catch (ResourceNotFoundException ignored) { }
        }

        throw new ResourceNotFoundException("Cannot open internal resource '" + path + "'.");
    }

    @Override
    public boolean exists() {
        for(InternalResourceSource source : SOURCES)
            if(source.exists(resourceClass, path))
                return true;
        return false;
    }


    private static final List<InternalResourceSource> SOURCES = new CopyOnWriteArrayList<>();

    public static synchronized void registerSource(InternalResourceSource source) {
        Objects.requireNonNull(source, "Argument 'source' cannot be null.");
        if(SOURCES.contains(source))
            return;
        SOURCES.add(source);
        SOURCES.sort(Comparator.comparingInt(InternalResourceSource::getPriority).reversed());
    }

    static {
        registerSource(new DefaultSource());
    }

    private static class DefaultSource extends InternalResourceSource {

        public DefaultSource() {
            super(0);
        }

        @Override
        public InputStream inStream(Class<?> resourceClass, String path) throws ResourceAccessException {
            final InputStream stream = resourceClass.getResourceAsStream(path);
            if(stream == null)
                throw new ResourceNotFoundException();
            return stream;
        }

        @Override
        public boolean exists(Class<?> resourceClass, String path) {
            final URL url = resourceClass.getResource(path);
            return (url != null);
        }

    }

}
