package generaloss.resourceflow.handle;

import generaloss.resourceflow.Disposable;
import generaloss.resourceflow.resource.ResourceSource;

public abstract class ResHandle<K, H> implements Disposable {

    private final K key;
    private final String path;

    public ResHandle(K key, String path) {
        this.key = key;
        this.path = path;
    }

    public K getKey() {
        return key;
    }

    public String getPath() {
        return path;
    }


    abstract public void load(ResourceSource source, String path);

    abstract public H resource();

}
