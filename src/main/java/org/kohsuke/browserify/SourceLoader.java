package org.kohsuke.browserify;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class SourceLoader {
    /**
     * Loads a module of the given name.
     *
     * @return null
     *      if no such module is found.
     */
    public abstract Source load(ModuleName name);

    public final Source load(String name) {
        return load(ModuleName.from(name));
    }
}
