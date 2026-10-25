package generaloss.resourceflow.resource;

import java.io.InputStream;

public abstract class InternalResourceSource {

    private final int priority;

    public InternalResourceSource(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public abstract InputStream inStream(Class<?> resourceClass, String path) throws ResourceAccessException;

    public abstract boolean exists(Class<?> resourceClass, String path);

}
