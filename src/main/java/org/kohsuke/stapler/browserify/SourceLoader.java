package org.kohsuke.stapler.browserify;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class SourceLoader {
    public abstract Source load(ModuleName name);
}
