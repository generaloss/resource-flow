package generaloss.resourceflow.resource;

@FunctionalInterface
public interface ResourceSource {

    Resource getResource(String path);

}
