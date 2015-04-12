package org.kohsuke.stapler.browserify;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * CommonJS module name.
 *
 * @see <a href="http://wiki.commonjs.org/wiki/Modules/1.1.1">spec</a>
 * @author Kohsuke Kawaguchi
 */
public final class ModuleName {
    private final String moduleName;

    public ModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Resolves a possibly absolute/relative path
     *
     */
    ModuleName resolve(String path) {
        if (!path.startsWith(".")) {
            // in common JS module, names like 'foo' is considered absolute, unlike file system
            return new ModuleName(path);
        }

        List<String> tokens = new ArrayList<String>();
        tokens.addAll(tokenize(moduleName));
        tokens.addAll(tokenize(path));

        for (int i=0; i<tokens.size(); ) {
            String token = tokens.get(i);
            if (token.equals(".")) {
                tokens.remove(i);
            } else
            if (token.equals("..")) {
                if (i==0) {
                    throw new IllegalArgumentException("Invalid relative path '"+path+"' against '"+moduleName+"'");
                }
                tokens.remove(i);  // remove this token and the previous token that cancel out each other
                tokens.remove(i-1);
            } else {
                i++;
            }
        }

        return new ModuleName(StringUtils.join(tokens,"/"));
    }

    private Collection<String> tokenize(String path) {
        return Arrays.asList(path.split("/"));
    }

    @Override
    public String toString() {
        return moduleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleName that = (ModuleName) o;

        return moduleName.equals(that.moduleName);
    }

    @Override
    public int hashCode() {
        return moduleName.hashCode();
    }
}
