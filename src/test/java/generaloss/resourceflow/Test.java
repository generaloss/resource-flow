package generaloss.resourceflow;

import generaloss.resourceflow.resource.ClasspathResource;
import generaloss.resourceflow.resource.Resource;

public class Test {

    public static void main(String[] args) {
        ClasspathResource res = Resource.classpath("generaloss/resourceflow");
        //System.out.println(new StringList(res.listClasses(ClassFilter.ANY, c -> (c).getSimpleName())));
    }

}
