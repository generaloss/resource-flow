package generaloss.resourceflow.stream;

import java.io.IOException;

public interface BinaryStreamReader {

    void read(BinaryInputStream stream) throws IOException;

}
