import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.IRFactory;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.StringLiteral;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class Foo {
    public static void main(String[] args) throws IOException {
        CompilerEnvirons env = new CompilerEnvirons();
       	env.setRecoverFromErrors(true);
       	env.setGenerateDebugInfo(true);

       	Reader r = new InputStreamReader(Foo.class.getClassLoader().getResourceAsStream("test.js"));

       	IRFactory factory = new IRFactory(env);
        AstRoot ast = factory.parse(r, "test.js", 1);

        ast.visit(new NodeVisitor() {
            public boolean visit(AstNode node) {
//                System.out.println(node.shortName());
//                System.out.println(node.toSource(2));

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

                System.out.println("require: "+arg.getValue());
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
    }
}
