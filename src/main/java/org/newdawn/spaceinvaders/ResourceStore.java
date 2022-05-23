package org.newdawn.spaceinvaders;

import java.util.HashMap;
import java.util.function.Supplier;

import org.newdawn.spaceinvaders.resources.IResource;

public class ResourceStore {
    // Singleton Logic
    private static ResourceStore instance = new ResourceStore();

    public static ResourceStore getInstance() {
        return instance;
    }

    private ResourceStore() {

    }

    // ResourceStore Logic
    private HashMap<String, IResource> resources = new HashMap<>();

    public <T extends IResource> T getResource(String path, Supplier<T> constructor) {
        // If resource is in cache, return it
        if (resources.get(path) != null) {
            return (T) resources.get(path);
        }

        // Otherwise, load it, add it to the cache, and return it
        T resource = constructor.get();
        resource.load(path);
        resources.put(path, resource);
        return resource;
    }

    // Clears cache (to handle increasing memory usage)
    public void clear()
    {
        resources.clear();
    }
}
