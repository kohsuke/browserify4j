package org.kohsuke.stapler.browserify;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class Source {
    /**
     * The full module name, such as 'foo/bar/zot'
     *
     * @see Class#getName()
     */
    public final ModuleName moduleName;

    /**
     * @see Class#getClassLoader()
     */
    public final SourceLoader sourceLoader;

    protected Source(SourceLoader sourceLoader, ModuleName moduleName) {
        this.sourceLoader = sourceLoader;
        if (sourceLoader==null)  throw new IllegalArgumentException();

        this.moduleName = moduleName;
        if (moduleName==null)  throw new IllegalArgumentException();
    }

    public String getSourceFileName() {
        return moduleName+".js";
    }

    /**
     * Obtains the JavaScript source.
     */
    public abstract Reader load() throws IOException;

    /**
     * Just like {@link Class} equality, equality of {@link Source} is pair of name and {@link SourceLoader}.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Source)) return false;

        Source that = (Source) o;

        return this.sourceLoader.equals(that.sourceLoader) && this.moduleName.equals(that.moduleName);
    }

    @Override
    public final int hashCode() {
        return sourceLoader.hashCode() ^ moduleName.hashCode();
    }
}
