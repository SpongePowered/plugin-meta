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
package org.spongepowered.plugin.metadata;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A simple container of one or more {@link PluginMetadata}.
 */
public final class PluginMetadataContainer {

    private final Map<String, PluginMetadata> pluginMetadata;

    public PluginMetadataContainer(final Iterable<PluginMetadata> pluginMetadata) {
        Preconditions.checkNotNull(pluginMetadata);

        this.pluginMetadata = new HashMap<>();

        for (PluginMetadata metadata : pluginMetadata) {
            this.pluginMetadata.put(metadata.getId(), metadata);
        }
    }

    /**
     * Gets a {@link PluginMetadata} based on it's {@link PluginMetadata#getId()}.
     * @param pluginId The id
     * @return The metadata or {@link Optional#empty()} if not present
     */
    public Optional<PluginMetadata> getMetadata(final String pluginId) {
        Preconditions.checkNotNull(pluginId);

        return Optional.ofNullable(this.pluginMetadata.get(pluginId));
    }

    /**
     * Gets all {@link PluginMetadata} in this container, mapped by the {@link PluginMetadata#getId()}.
     *
     * <p>The returned {@link Map} cannot be mutated.</p>
     *
     * @return The map of metadata
     */
    public Map<String, PluginMetadata> getAllMetadata() {
        return Collections.unmodifiableMap(this.pluginMetadata);
    }
}
