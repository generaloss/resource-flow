package generaloss.resourceflow;

import java.nio.file.Path;

public class ResUtils {

    public static String osGeneralizePath(String pathStr) {
        return pathStr.replace("\\", "/");
    }

    public static String normalizePath(String pathStr) {
        final Path path = Path.of(pathStr);
        path.normalize();
        return path.toString();
    }

    public static void close(AutoCloseable closeable) {
        if(closeable == null)
            return;
        try {
            closeable.close();
        } catch(Exception ignored) { }
    }

}
