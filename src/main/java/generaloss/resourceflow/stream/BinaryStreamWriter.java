package generaloss.resourceflow.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface BinaryStreamWriter {

    void write(BinaryOutputStream stream) throws IOException;

    static byte[] writeBytes(BinaryStreamWriter writer) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try(final BinaryOutputStream extStream = new BinaryOutputStream(byteStream)){
            writer.write(extStream);
            return byteStream.toByteArray();
        }catch(IOException ignored) {
            return null;
        }
    }

}