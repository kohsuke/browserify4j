package org.kohsuke.browserify;

import org.junit.Assert;
import org.junit.Test;

public class ModuleNameTest extends Assert {
    @Test
    public void from() {
        assertEquals("foo/bar/zot", ModuleName.from("foo/bar/zot").toString());
        assertEquals("foo/bar/zot", ModuleName.from("foo/./bar/zot").toString());
        assertEquals("foo/bar/zot", ModuleName.from("foo/bar/gone/../zot").toString());
    }

    @Test
    public void resolve() throws Exception {
        ModuleName base = ModuleName.from("foo/bar");
        assertEquals("foo/zot", base.resolve("./zot").toString());
        assertEquals("zot", base.resolve("zot").toString());
    }
}