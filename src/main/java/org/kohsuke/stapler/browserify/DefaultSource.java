package org.kohsuke.stapler.browserify;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * @author Kohsuke Kawaguchi
 */
public class DefaultSource extends Source {
    private final URL res;

    public DefaultSource(SourceLoader loader, ModuleName name, URL res) {
        super(loader,name);
        this.res = res;
        if (res==null)  throw new IllegalArgumentException("No such module "+name);
    }

    @Override
    public Reader load() throws IOException {
        return new InputStreamReader(res.openStream(), Charsets.UTF_8);
    }

    @Override
    public String getSourceFileName() {
        return res.toExternalForm();
    }
}
