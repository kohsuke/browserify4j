package org.kohsuke.stapler.browserify;

/**
 * Indicates that a module was 'require'-d but it was not found.
 *
 * @author Kohsuke Kawaguchi
 */
public class NoSourceFoundException extends RuntimeException {
    private final SourceLoader loader;
    private final ModuleName name;

    public NoSourceFoundException(SourceLoader loader, ModuleName n) {
        super(n.toString());
        this.loader = loader;
        this.name = n;
    }

    public SourceLoader getLoader() {
        return loader;
    }

    public ModuleName getName() {
        return name;
    }
}
