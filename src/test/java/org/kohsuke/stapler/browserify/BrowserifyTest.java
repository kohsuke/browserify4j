package org.kohsuke.stapler.browserify;

import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.FileWriter;
import java.io.StringWriter;

/**
 * @author Kohsuke Kawaguchi
 */
public class BrowserifyTest extends Assert {
    Browserify browserify = new Browserify();
    SourceLoader loader = new DefaultSourceLoader();

    @Test
    public void math() throws Exception {
        Object o = parse("require('org/kohsuke/stapler/browserify/math/math').value",
                "math/math");
        assertEquals(6, ((Number)o).intValue());

    }

    private Object parse(String main, String... paths) throws Exception {
        for (String path : paths) {
            browserify.require(loader.load("org/kohsuke/stapler/browserify/"+path));
        }

        StringWriter sw = new StringWriter();
        browserify.writeTo(sw);
        sw.write("\n"+main);

        FileWriter fw = new FileWriter("/tmp/requires.js");
        fw.write(sw.toString());
        fw.close();

        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine engine = m.getEngineByName("JavaScript");
        return engine.eval(sw.toString());
    }
}
