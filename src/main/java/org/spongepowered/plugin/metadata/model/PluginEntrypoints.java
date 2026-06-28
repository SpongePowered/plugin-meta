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
package org.spongepowered.plugin.metadata.model;

import org.spongepowered.plugin.metadata.PluginMetadata;

import java.util.List;
import java.util.Objects;

/**
 * Specification for an entity representing the entrypoints of a {@link PluginMetadata plugin metadata}.
 * <p>
 * Consult the vendor for further information on how this is used.
 * Usually these are the fully qualified name of some classes.
 *
 * @param main The main entrypoints
 * @param server The server entrypoints
 * @param client The client entrypoints
 */
public record PluginEntrypoints(List<String> main, List<String> server, List<String> client) {
    private static final PluginEntrypoints NONE = new PluginEntrypoints(List.of());

    public PluginEntrypoints {
        main = List.copyOf(Objects.requireNonNull(main, "main"));
        server = List.copyOf(Objects.requireNonNull(server, "server"));
        client = List.copyOf(Objects.requireNonNull(client, "client"));
    }

    public PluginEntrypoints(List<String> main) {
        this(main, List.of(), List.of());
    }

    public static PluginEntrypoints none() {
        return PluginEntrypoints.NONE;
    }
}
