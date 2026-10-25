package generaloss.resourceflow.handle;

import generaloss.resourceflow.Disposable;

import java.util.HashMap;
import java.util.Map;

public class ResHandleMap<K, H extends ResHandle<K, ?>> implements Disposable {

    private ResHandleFactory<K, H> handleFactory;
    private ResourceProvider provider;
    private final Map<K, H> map;

    public ResHandleMap() {
        this.map = new HashMap<>();
    }

    public ResHandleMap(ResourceProvider provider) {
        this();
        this.setProvider(provider);
    }

    public ResHandleMap(ResHandleFactory<K, H> handleFactory) {
        this();
        this.setHandleFactory(handleFactory);
    }

    public ResHandleMap(ResourceProvider provider, ResHandleFactory<K, H> handleFactory) {
        this();
        this.setProvider(provider);
        this.setHandleFactory(handleFactory);
    }


    public ResHandleFactory<K, H> getHandleFactory() {
        return handleFactory;
    }

    public void setHandleFactory(ResHandleFactory<K, H> handleFactory) {
        this.handleFactory = handleFactory;
    }


    public ResourceProvider getProvider() {
        return provider;
    }

    public void setProvider(ResourceProvider provider) {
        this.provider = provider;
    }


    public int size() {
        return map.size();
    }


    public H get(K key) {
        return map.get(key);
    }

    public void dispose(H handle) {
        if(handle == null)
            return;

        handle.dispose();
        map.remove(handle.getKey());
    }

    public void dispose(K key) {
        this.dispose(map.get(key));
    }

    public H create(K key, String path) {
        if(provider == null)
            throw new IllegalStateException("Resource provider is not set");
        if(handleFactory == null)
            throw new IllegalStateException("Resource handle factory not set");

        final H handle = handleFactory.create(key, path);
        handle.load(provider, path);
        map.put(key, handle);

        return handle;
    }

    public H create(H handle) {
        if(provider == null)
            throw new IllegalStateException("Resource provider is not set");

        handle.load(provider, handle.getPath());
        map.put(handle.getKey(), handle);
        return handle;
    }

    public void reload() {
        if(provider == null)
            throw new IllegalStateException("Resource provider is not set");

        map.forEach((key, handle) ->
            handle.load(provider, handle.getPath()));
    }

    @Override
    public void dispose() {
        for(H handle: map.values())
            handle.dispose();
        map.clear();
    }

}
