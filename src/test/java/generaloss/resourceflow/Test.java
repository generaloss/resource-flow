package generaloss.resourceflow;

import generaloss.resourceflow.resource.ClasspathResource;
import generaloss.resourceflow.resource.Resource;
import generaloss.resourceflow.stream.ClassFilter;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        final ClasspathResource res = Resource.classpath("generaloss/");
        System.out.println(Arrays.toString(res.listClassesRecursive(ClassFilter.ANY)));
    }

}
