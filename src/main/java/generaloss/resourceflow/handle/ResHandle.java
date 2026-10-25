package generaloss.resourceflow.handle;

import generaloss.resourceflow.Disposable;

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


    abstract public void load(ResourceProvider provider, String path);

    abstract public H resource();

}
