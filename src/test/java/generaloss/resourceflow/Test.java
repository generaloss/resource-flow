package generaloss.resourceflow;

import generaloss.resourceflow.resource.ClasspathResource;
import generaloss.resourceflow.resource.Resource;

public class Test {

    public static void main(String[] args) {
        System.out.println("Classes Recursive:\n");

        final ClasspathResource res1 = Resource.classpath("generaloss/resourceflow");
        final Class<?>[] classes1 = res1.listClassesRecursive(c -> !c.isAnonymousClass());
        for(Class<?> clazz : classes1)
            System.out.println(clazz.getName());

        System.out.println("Classes:\n");

        final ClasspathResource res2 = Resource.classpath("generaloss/resourceflow");
        final Class<?>[] classes2 = res2.listClasses(c -> !c.isAnonymousClass());
        for(Class<?> clazz : classes2)
            System.out.println(clazz.getName());
    }

}
