package org.kohsuke.stapler.browserify;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

public class RequireFinderTest extends Assert {
    @Test
    public void find() throws Exception {
        String src = IOUtils.toString(getClass().getResourceAsStream("requires.js"));
        List<Require> requires = new RequireFinder().discover(new StringReader(src), "requires.js", 1);
        String all = StringUtils.join(requires, ",");
        assertEquals(all,"foo,bar,zot");
    }
}