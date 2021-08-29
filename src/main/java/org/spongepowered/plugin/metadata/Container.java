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

import org.spongepowered.plugin.metadata.model.PluginLoader;

import java.util.Optional;
import java.util.Set;

/**
 *  A container joins together {@link Inheritable global metadata} with specific one or more
 *  {@link PluginMetadata plugin metadata}.
 *
 *  <p>How a library consumer utilizes this concept is largely left up to their discretion. A
 *  typical use case would be to load a file as a container</p>
 *
 * @see org.spongepowered.plugin.metadata.builtin.MetadataContainer for a generic implementation
 */
public interface Container {

    /**
     * @return The {@link PluginLoader loader}.
     */
    PluginLoader loader();

    /**
     * Gets the {@link String license} of the data within this container.
     *
     * <p>Consult the vendor of this library for how this field is used. In the generic
     * implementation, a license's name is expected (i.e. MIT or All Rights Reserved).</p>
     *
     * @return The license
     */
    String license();

    /**
     * Gets the {@link String mappings} that code within this container might be written in.
     *
     * <p>The format of this string should be in maven dependency format (group:artifact:version).</p>
     *
     * <p>Consult the vendor of this library for how this field is used. As an example, a vendor could
     * use this purely for information purposes or go farther and perform artifact remapping from
     * this mappings to another.</p>
     *
     * @return The {@link String mappings} or {@link Optional#empty()} otherwise
     */
    Optional<String> mappings();

    /**
     * @return The {@link Inheritable global metadata} or {@link Optional#empty()} otherwise
     */
    Optional<Inheritable> globalMetadata();

    /**
     * Gets a {@link PluginMetadata} by its {@link String id}.
     *
     * <p>This maps to {@link PluginMetadata#id()}</p>
     *
     * @param id The id
     * @return The plugin metadata or {@link Optional#empty()} otherwise
     */
    Optional<PluginMetadata> metadata(String id);

    /**
     * @return The {@link PluginMetadata plugin metadata} as an unmodifiable {@link Set}.
     */
    Set<PluginMetadata> metadata();
}
