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
package org.spongepowered.plugin.metadata.builtin;

import org.apache.maven.artifact.versioning.VersionRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.model.ContainerLoader;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;
import org.spongepowered.plugin.metadata.model.PluginLinks;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.Objects;

public class MetadataParserTest {

    private static Reader resourceReader(final String path) {
        return new InputStreamReader(Objects.requireNonNull(MetadataParserTest.class.getResourceAsStream(path)));
    }

    private static List<String> readLines(final String path) throws IOException {
        try (final BufferedReader reader = new BufferedReader(MetadataParserTest.resourceReader(path))) {
            return reader.lines().toList();
        }
    }

    private static MetadataContainer readContainer(final String path) throws IOException {
        try (final Reader reader = MetadataParserTest.resourceReader(path)) {
            return MetadataParser.read(reader);
        }
    }

    private static List<String> writeContainer(final MetadataContainer container) throws IOException {
        final StringWriter writer = new StringWriter();
        MetadataParser.write(writer, container, true);
        return List.of(writer.toString().split("\n"));
    }

    private static final MetadataContainer container1 = MetadataContainer.builder()
            .loader(new ContainerLoader("java_plain", VersionRange.createFromVersion("1.0")))
            .license("some_license")
            .addMetadata(StandardPluginMetadata.builder()
                    .id("test_plugin")
                    .name("TestPlugin")
                    .version("1.2.3")
                    .entrypoint("my.test.package.MyTestPlugin")
                    .description("Some test plugin")
                    .links(new PluginLinks(
                            URI.create("https://spongepowered.org/"),
                            URI.create("https://github.com/SpongePowered/Sponge"),
                            URI.create("https://github.com/SpongePowered/Sponge/issues")
                    ))
                    .addContributor(new PluginContributor("Spongie", "Mascot"))
                    .addDependency(new PluginDependency("spongeapi", VersionRange.createFromVersion("17.0.0"), PluginDependency.LoadOrder.AFTER, false))
                    .build())
            .build();

    @Test
    public void readValidContainer1() throws IOException {
        // TODO MetadataContainer#equals ?
        final MetadataContainer parsed = MetadataParserTest.readContainer("/valid/container1.json");
        Assertions.assertEquals(container1.loader(), parsed.loader());
        Assertions.assertEquals(container1.license(), parsed.license());
        Assertions.assertEquals(1, parsed.metadata().size());

        final PluginMetadata plugin1 = container1.metadata().iterator().next();
        final PluginMetadata parsedPlugin = parsed.metadata().iterator().next();
        Assertions.assertEquals(plugin1.id(), parsedPlugin.id());
        Assertions.assertEquals(plugin1.name(), parsedPlugin.name());
        Assertions.assertEquals(plugin1.version(), parsedPlugin.version());
        Assertions.assertEquals(plugin1.entrypoint(), parsedPlugin.entrypoint());
        Assertions.assertEquals(plugin1.description(), parsedPlugin.description());
        Assertions.assertEquals(plugin1.links(), parsedPlugin.links());
        Assertions.assertIterableEquals(plugin1.contributors(), parsedPlugin.contributors());
        Assertions.assertIterableEquals(plugin1.dependencies(), parsedPlugin.dependencies());
    }

    @Test
    public void writeContainer1() throws IOException {
        final List<String> expected = MetadataParserTest.readLines("/valid/container1.json");
        final List<String> result = MetadataParserTest.writeContainer(container1);
        Assertions.assertLinesMatch(expected, result);
    }

    @Test
    public void readLegacyIdContainer() throws IOException {
        final String pluginIdWarning = "Plugin id 'test-plugin' is invalid and has been converted to 'test_plugin'.";
        final String dependencyIdWarning = "Plugin id 'test-dependency' is invalid and has been converted to 'test_dependency'.";
        Assertions.assertFalse(MetadataParser.warnings().contains(pluginIdWarning));
        Assertions.assertFalse(MetadataParser.warnings().contains(dependencyIdWarning));

        final MetadataContainer parsed = MetadataParserTest.readContainer("/legacy/legacy_id.json");

        Assertions.assertEquals(1, parsed.metadata().size());
        final PluginMetadata plugin = parsed.metadata().iterator().next();
        Assertions.assertEquals("test_plugin", plugin.id());

        Assertions.assertEquals(1, plugin.dependencies().size());
        final PluginDependency dependency = plugin.dependencies().iterator().next();
        Assertions.assertEquals("test_dependency", dependency.id());

        Assertions.assertTrue(MetadataParser.warnings().contains(pluginIdWarning));
        Assertions.assertTrue(MetadataParser.warnings().contains(dependencyIdWarning));
    }
}
