package generaloss.resourceflow.stream;

import java.util.function.Predicate;

public interface StringFilter extends Predicate<String> {

    StringFilter ANY = (str -> true);

}
