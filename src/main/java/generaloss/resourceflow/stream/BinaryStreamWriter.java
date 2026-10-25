package generaloss.resourceflow.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public interface BinaryStreamWriter {

    void write(BinaryOutputStream stream) throws IOException;

    static byte[] toByteArray(BinaryStreamWriter writer) throws IOException {
        Objects.requireNonNull(writer, "Argument 'writer' cannot be null.");

        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try(BinaryOutputStream extStream = new BinaryOutputStream(byteStream)) {
            writer.write(extStream);
            return byteStream.toByteArray();
        }
    }

}