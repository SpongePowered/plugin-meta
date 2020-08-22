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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        Assertions.assertEquals(2, meta.size(), "Parsed data should have 2 elements.");

        final PluginMetadata testPlugin = meta.get(0);
        final PluginMetadata testPluginTwo = meta.get(1);

        Assertions.assertEquals("testplugin", testPlugin.getId());
        Assertions.assertEquals("testplugin2", testPluginTwo.getId());

        Assertions.assertEquals("TestPlugin", testPlugin.getName());
        Assertions.assertEquals("1.0-SNAPSHOT", testPlugin.getVersion());
        Assertions.assertEquals("This is a test plugin", testPlugin.getDescription());
        Assertions.assertEquals("http://example.com/testplugin", testPlugin.getUrl());
        Assertions.assertEquals(Collections.singletonList("Minecrell"), testPlugin.getAuthors());

        Collection<PluginDependency> dependencies = new HashSet<>(testPlugin.getDependencies());
        Assertions.assertEquals(3, dependencies.size());

        Assertions.assertEquals(new HashSet<>(Arrays.asList(
                new PluginDependency(PluginDependency.LoadOrder.BEFORE, "testdependency", "[1.0.0]", false),
                new PluginDependency(PluginDependency.LoadOrder.BEFORE, "optionaldependency", null, true),
                new PluginDependency(PluginDependency.LoadOrder.NONE, "requireddependency", null, false)
        )), dependencies);
    }

}
