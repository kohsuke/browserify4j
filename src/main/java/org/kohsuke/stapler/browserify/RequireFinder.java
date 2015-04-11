package org.kohsuke.stapler.browserify;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.StringLiteral;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans JavaScript source code and discovers {@code require(...)} calls.
 *
 * @author Kohsuke Kawaguchi
 */
public class RequireFinder {
    private final IRFactory factory;

    public RequireFinder() {
        CompilerEnvirons env = new CompilerEnvirons();
       	env.setRecoverFromErrors(true);
       	env.setGenerateDebugInfo(true);
        factory = new IRFactory(env);
    }

    public List<Require> discover(Reader src, String sourceFileName, int line) throws IOException {
        return discover(factory.parse(src, sourceFileName, line));
    }

    public List<Require> discover(AstNode ast) throws IOException {
        final List<Require> list = new ArrayList<Require>();

        ast.visit(new NodeVisitor() {
            public boolean visit(AstNode node) {
                if (node instanceof FunctionCall) {
                    onFunctionCall((FunctionCall) node);
                }
                return true;
            }

            private void onFunctionCall(FunctionCall f) {
                Name n = as(Name.class, f.getTarget());
                if (n==null)
                    return; // left-hand side is not a simple symbol (e.g., "x[5]()")
                if (n.getDefiningScope()!=null)
                    return; // don't care about calling local symbol 'require'
                if (!n.getIdentifier().equals("require"))
                    return; // not a call t o require function

                List<AstNode> args = f.getArguments();
                if (args.size()!=1)
                    return; // expecting just one argument

                StringLiteral arg = as(StringLiteral.class, args.get(0));
                if (arg==null)
                    return; // we can only handle string literal

                list.add(new Require(f,arg.getValue()));
            }

            /**
             * Acts like a cast but instead of {@link ClassCastException} you get {@code null}.
             */
            private <T extends AstNode> T as(Class<T> type, AstNode node) {
                if (type.isInstance(node)) {
                    return type.cast(node);
                } else {
                    return null;
                }
            }
        });

        return list;
    }
}
