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

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.model.PluginConflict;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;
import org.spongepowered.plugin.metadata.model.PluginEntrypoints;
import org.spongepowered.plugin.metadata.model.PluginLinks;
import org.spongepowered.plugin.metadata.model.PluginLoaderSpecification;

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

    private static final InheritableMetadata global = InheritableMetadata.builder()
            .loader(new PluginLoaderSpecification("java_plain", VersionRange.createFromVersion("1.0")))
            .license("some_license")
            .build();

    private static final InheritableMetadata override = InheritableMetadata.builder()
            .name("TestPlugin")
            .version(new DefaultArtifactVersion("1.2.3"))
            .description("Some test plugin")
            .links(new PluginLinks(
                    URI.create("https://spongepowered.org/"),
                    URI.create("https://github.com/SpongePowered/Sponge"),
                    URI.create("https://github.com/SpongePowered/Sponge/issues")
            ))
            .addContributor(new PluginContributor("Spongie", "Mascot"))
            .addConflict(new PluginConflict("bad_plugin", VersionRange.createFromVersion("3.2.1"), true, "Incompatible for some reasons"))
            .addDependency(new PluginDependency("spongeapi", VersionRange.createFromVersion("17.0.0"), PluginDependency.LoadOrder.AFTER, false))
            .build();

    private static final InheritableMetadata full = global.with(override);

    private static final PluginEntrypoints entrypoints = new PluginEntrypoints(
            List.of("my.test.package.MyTestPlugin_Main"),
            List.of("my.test.package.MyTestPlugin_Server"),
            List.of("my.test.package.MyTestPlugin_Client")
    );

    private static final PluginEntrypoints mainOnlyEntrypoints = new PluginEntrypoints(List.of("my.test.package.MyTestPlugin"));

    private static final MetadataContainer mixContainer = new MetadataContainer(global, List.of(
            StandardPluginMetadata.builder()
                    .id("test_plugin")
                    .entrypoints(entrypoints)
                    .global(global)
                    .override(override)
                    .build()
    ));

    private static final MetadataContainer fullGlobalContainer = new MetadataContainer(full, List.of(
            StandardPluginMetadata.builder()
                    .id("test_plugin")
                    .entrypoints(entrypoints)
                    .global(full)
                    .build()
    ));

    private static final MetadataContainer fullOverrideContainer = new MetadataContainer(InheritableMetadata.none(), List.of(
            StandardPluginMetadata.builder()
                    .id("test_plugin")
                    .entrypoints(entrypoints)
                    .override(full)
                    .build()
    ));

    private static final MetadataContainer mainEntrypointOnlyContainer = new MetadataContainer(global, List.of(
            StandardPluginMetadata.builder()
                    .id("test_plugin")
                    .entrypoints(mainOnlyEntrypoints)
                    .global(global)
                    .override(override)
                    .build()
    ));

    @Test
    public void readMix() throws IOException {
        final MetadataContainer parsed = MetadataParserTest.readContainer("/valid/mix.json");
        Assertions.assertEquals(mixContainer, parsed);
    }

    @Test
    public void readMixInRoot() throws IOException {
        final MetadataContainer parsed = MetadataParserTest.readContainer("/valid/mix_in_root.json");
        Assertions.assertEquals(mixContainer, parsed);
    }

    @Test
    public void readFullGlobal() throws IOException {
        final MetadataContainer parsed = MetadataParserTest.readContainer("/valid/full_global.json");
        Assertions.assertEquals(fullGlobalContainer, parsed);
    }

    @Test
    public void readFullOverride() throws IOException {
        final MetadataContainer parsed = MetadataParserTest.readContainer("/valid/full_override.json");
        Assertions.assertEquals(fullOverrideContainer, parsed);
    }

    @Test
    public void readMainEntrypointOnly() throws IOException {
        final MetadataContainer parsed = MetadataParserTest.readContainer("/valid/main_entrypoint_only.json");
        Assertions.assertEquals(mainEntrypointOnlyContainer, parsed);
    }

    @Test
    public void writeMix() throws IOException {
        final List<String> expected = MetadataParserTest.readLines("/valid/mix.json");
        final List<String> result = MetadataParserTest.writeContainer(mixContainer);
        Assertions.assertLinesMatch(expected, result);
    }

    @Test
    public void writeFullGlobal() throws IOException {
        final List<String> expected = MetadataParserTest.readLines("/valid/full_global.json");
        final List<String> result = MetadataParserTest.writeContainer(fullGlobalContainer);
        Assertions.assertLinesMatch(expected, result);
    }

    @Test
    public void writeFullOverride() throws IOException {
        final List<String> expected = MetadataParserTest.readLines("/valid/full_override.json");
        final List<String> result = MetadataParserTest.writeContainer(fullOverrideContainer);
        Assertions.assertLinesMatch(expected, result);
    }

    @Test
    public void writeMainEntrypointOnly() throws IOException {
        final List<String> expected = MetadataParserTest.readLines("/valid/main_entrypoint_only.json");
        final List<String> result = MetadataParserTest.writeContainer(mainEntrypointOnlyContainer);
        Assertions.assertLinesMatch(expected, result);
    }

    @Test
    public void readLegacyDashInId() throws IOException {
        final String pluginIdWarning = "Plugin id 'test-plugin' is invalid and has been converted to 'test_plugin'.";
        final String dependencyIdWarning = "Plugin id 'test-dependency' is invalid and has been converted to 'test_dependency'.";
        Assertions.assertFalse(MetadataParser.warnings().contains(pluginIdWarning));
        Assertions.assertFalse(MetadataParser.warnings().contains(dependencyIdWarning));

        final MetadataContainer parsed = MetadataParserTest.readContainer("/legacy/dash_in_id.json");

        Assertions.assertEquals(1, parsed.plugins().size());
        final PluginMetadata plugin = parsed.plugins().getFirst();
        Assertions.assertEquals("test_plugin", plugin.id());

        Assertions.assertEquals(1, plugin.dependencies().size());
        final PluginDependency dependency = plugin.dependencies().iterator().next();
        Assertions.assertEquals("test_dependency", dependency.id());

        Assertions.assertTrue(MetadataParser.warnings().contains(pluginIdWarning));
        Assertions.assertTrue(MetadataParser.warnings().contains(dependencyIdWarning));
    }

    @Test
    public void readLegacyEntrypoint() throws IOException {
        final MetadataContainer parsed = MetadataParserTest.readContainer("/legacy/entrypoint.json");
        Assertions.assertEquals(mainEntrypointOnlyContainer, parsed);
    }
}
