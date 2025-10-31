package generaloss.resourceflow.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class URLResource extends Resource {

    private final URL url;

    protected URLResource(URL url) {
        this.url = url;
    }

    protected URLResource(String url) throws MalformedURLException {
        this.url = new URL(url);
    }


    public URL url() {
        return url;
    }

    public String protocol() {
        return url.getProtocol();
    }

    public String host() {
        return url.getHost();
    }


    @Override
    public String path() {
        return url.toExternalForm();
    }

    @Override
    public InputStream inStream() throws ResourceAccessException {
        try {
            final InputStream stream = url.openStream();
            if(stream == null)
                throw new ResourceAccessException("Cannot open URL resource: " + url);

            return stream;
        } catch(IOException e) {
            throw new ResourceAccessException(e);
        }
    }

    @Override
    public boolean exists() {
        try {
            url.openStream().close();
            return true;
        } catch(IOException ignored) {
            return false;
        }
    }

}
