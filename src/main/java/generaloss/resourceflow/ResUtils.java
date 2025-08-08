package generaloss.resourceflow;

public class ResUtils {

    public static String osGeneralizePath(String path) {
        return path.replace("\\", "/");
    }

    public static void close(AutoCloseable closeable) {
        if(closeable == null)
            return;
        try{
            closeable.close();
        }catch(Exception ignored){ }
    }

}
