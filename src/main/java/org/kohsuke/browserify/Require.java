package org.kohsuke.browserify;

import org.mozilla.javascript.ast.FunctionCall;

/**
 * Occurrence of a global {@code require('...')} call found in JavaScript.
 *
 * @author Kohsuke Kawaguchi
 */
final class Require {
    /**
     * AST Node of the require('...') call
     */
    public final FunctionCall call;
    /**
     * The module reference that is being required.
     */
    public final String target;

    public Require(FunctionCall call, String target) {
        this.call = call;
        this.target = target;
    }

    @Override
    public String toString() {
        return target;
    }
}
