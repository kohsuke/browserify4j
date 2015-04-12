package org.kohsuke.browserify;

import java.net.URL;

/**
 * @author Kohsuke Kawaguchi
 */
public class DefaultSourceLoader extends SourceLoader {
    private final ClassLoader classLoader;

    public DefaultSourceLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public DefaultSourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public Source load(ModuleName name) {
        URL res = classLoader.getResource(name + ".js");
        if (res==null) {
            res = classLoader.getResource(name + "/index.js");
            if (res==null)
                return null;
            name = name.resolve("./index");
        }
        return new DefaultSource(this,name,res);
    }
}
