package generaloss.resourceflow.handle;

import generaloss.resourceflow.resource.Resource;

@FunctionalInterface
public interface ResourceProvider {

    Resource getResource(String path);

}
