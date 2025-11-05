package generaloss.resourceflow.stream;

import java.lang.reflect.Modifier;
import java.util.function.Predicate;

public interface ClassFilter extends Predicate<Class<?>> {

    ClassFilter ANY = (c -> true);

    ClassFilter INTERFACE = (Class::isInterface);
    ClassFilter NON_INTERFACE = (c -> !c.isInterface());
    ClassFilter ENUM = (Class::isEnum);
    ClassFilter NON_ENUM = (c -> !c.isEnum());
    ClassFilter ANNOTATION = (Class::isAnnotation);
    ClassFilter NON_ANNOTATION = (c -> !c.isAnnotation());
    ClassFilter ANONYMOUS = (Class::isAnonymousClass);
    ClassFilter NON_ANONYMOUS = (c -> !c.isAnonymousClass());

    ClassFilter ABSTRACT = (c -> Modifier.isAbstract(c.getModifiers()));
    ClassFilter NON_ABSTRACT = (c -> !Modifier.isAbstract(c.getModifiers()));
    ClassFilter FINAL = (c -> Modifier.isFinal(c.getModifiers()));
    ClassFilter NON_FINAL = (c -> !Modifier.isFinal(c.getModifiers()));
    ClassFilter PUBLIC = (c -> Modifier.isPublic(c.getModifiers()));
    ClassFilter NON_PUBLIC = (c -> !Modifier.isPublic(c.getModifiers()));
    ClassFilter PROTECTED = (c -> Modifier.isProtected(c.getModifiers()));
    ClassFilter NON_PROTECTED = (c -> !Modifier.isProtected(c.getModifiers()));
    ClassFilter PRIVATE = (c -> Modifier.isPrivate(c.getModifiers()));
    ClassFilter NON_PRIVATE = (c -> !Modifier.isPrivate(c.getModifiers()));

}
