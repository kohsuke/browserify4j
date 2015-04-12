package org.kohsuke.stapler.browserify;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author Kohsuke Kawaguchi
 */
public class Browserify {
    private final RequireFinder finder = new RequireFinder();

    /**
     * Modules that are found in the transitive require closures, keyed by the module name.
     */
    private final Map<Source,ParsedSource> modules = new HashMap<Source, ParsedSource>();

    private final List<Source> entryPoints = new ArrayList<Source>();

    final class ParsedSource {
        final Source source;
        final List<Dependency> dependencies = new ArrayList<Dependency>();

        ParsedSource(Source source) {
            this.source = source;
        }
    }

    final class Dependency {
        /**
         * Relative path reference as it appears in the invocation of {@code require(...)}
         */
        final String rel;
        /**
         * Resolved target.
         */
        final ParsedSource target;

        Dependency(String rel, ParsedSource target) {
            this.rel = rel;
            this.target = target;
        }
    }

    public void require(Source src) throws IOException {
        Stack<Source> queue = new Stack<Source>();
        if (add(src)) {
            queue.add(src);
            entryPoints.add(src);
        }

        while (!queue.isEmpty()) {
            src = queue.pop();
            ParsedSource cur = modules.get(src);
            assert cur!=null;   // this entry should have been created

            for (Require r : finder.discover(src)) {
                Source target = resolve(src, r.target);
                // record the discovery, and if it's newly found module, plan on visiting it later
                if (add(target))
                    queue.push(target);

                // remember dependency from cur->target
                cur.dependencies.add(new Dependency(r.target, modules.get(target)));
            }
        }
    }

    private Source resolve(Source src, String target) {
        ModuleName n = src.moduleName.resolve(target);
        Source t = src.sourceLoader.load(n);
        if (t==null)
            throw new NoSourceFoundException(src.sourceLoader,n);
        return t;
    }

    private boolean add(Source src) {
        ParsedSource v = modules.get(src);
        if (v!=null)    return false;   // already there
        modules.put(src, new ParsedSource(src));
        return true;    // newly added
    }

    public void writeTo(Writer w) throws IOException {
        w.write(";\nvar require=");

        // prelude
        IOUtils.copy(Browserify.class.getResourceAsStream("prelude.js"), w, "UTF-8");

        // body
        w.write("({");
        boolean firstModule = true;
        for (ParsedSource ps : modules.values()) {
            if (firstModule)    firstModule = false;
            else                w.write(",");

            w.write(quote(ps.source.moduleName));
            w.write(":[function(require,module,exports){\n");
            IOUtils.copy(ps.source.load(),w);
            w.write("\n},{");
            {// map from relative reference to actual module name
                boolean firstDependency = true;
                for (Dependency d : ps.dependencies) {
                    if (firstDependency)    firstDependency = false;
                    else                    w.write(",");
                    w.write(quote(d.rel));
                    w.write(':');
                    w.write(quote(d.target.source.moduleName));
                }
            }
            w.write("}]");
        }
        w.write("},");

        w.write("{},"); // 2nd 'cache' argument

        // entry points
        w.write("[");
        boolean firstEntryPoint = true;
        for (ParsedSource ps : modules.values()) {
            if (firstEntryPoint)    firstEntryPoint = false;
            else                    w.write(",");
            w.write(quote(ps.source.moduleName));
        }
        w.write("]);");
    }

    private String quote(Object n) {
        return '"'+n.toString()+'"';
    }
}
