/*
 * This file is part of plugin-meta, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.plugin.meta;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class McModInfoTest {

    @Test
    public void parseMcModInfo() throws IOException {
        List<PluginMetadata> meta;
        try (InputStream in = getClass().getResourceAsStream("mcmod1.info.json")) {
            meta = McModInfo.DEFAULT.read(in);
        }

        assertEquals("Parsed data should have 2 elements", 2, meta.size());

        PluginMetadata testplugin = meta.get(0);
        PluginMetadata testplugin2 = meta.get(1);

        assertEquals("testplugin", testplugin.getId());
        assertEquals("testplugin2", testplugin2.getId());

        assertEquals("TestPlugin", testplugin.getName());
        assertEquals("1.0-SNAPSHOT", testplugin.getVersion());
        assertEquals("This is a test plugin", testplugin.getDescription());
        assertEquals("http://example.com/testplugin", testplugin.getUrl());
        assertEquals(Collections.singletonList("Minecrell"), testplugin.getAuthors());

        Collection<PluginDependency> dependencies = new HashSet<>(testplugin.getDependencies());
        assertEquals(3, dependencies.size());

        assertEquals(new HashSet<>(Arrays.asList(
                new PluginDependency(PluginDependency.LoadOrder.BEFORE, "testdependency", "[1.0.0]", false),
                new PluginDependency(PluginDependency.LoadOrder.BEFORE, "optionaldependency", null, true),
                new PluginDependency(PluginDependency.LoadOrder.NONE, "requireddependency", null, false)
        )), dependencies);
    }

}
