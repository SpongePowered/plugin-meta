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

import org.apache.maven.artifact.versioning.VersionRange;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.builtin.model.StandardPluginDependency;

/**
 * Specification for an entity considered to be a "dependency" for an {@link Inheritable inheritable}
 * or {@link PluginMetadata plugin metadata}.
 * <p>
 * Consult the vendor for further information on how this is used.
 * @see StandardPluginDependency StandardPluginDependency, for a generic implementation
 */
public interface PluginDependency {

    /**
     * Gets the {@link String id}.
     *
     * <p>Ids must conform to the following requirements:</p>
     *
     * <ul>
     *     <li>Must be between 2 and 64 characters in length</li>
     *     <li>Must start with a lower case letter (a-z)</li>
     *     <li>May only contain a mix of lower case letters (a-z),
     *     numbers (0-9), dashes (-), and underscores (_)</li>
     * </ul>
     * @return The id
     */
    String id();

    /**
     * @return The {@link VersionRange version}, as a maven range.
     */
    VersionRange version();

    /**
     * @return The {@link LoadOrder load order}
     */
    LoadOrder loadOrder();

    /**
     * @return True if optional, false if not
     */
    boolean optional();

    /**
     * Represents the ordering of how dependencies are loaded versus others.
     * <p>
     * A vendor *may* choose to introduce additional behavior beyond what is
     * documented here. It is recommended to consult with that entity on any
     * further behavioral changes.
     */
    enum LoadOrder {
        /**
         * The plugin can be loaded regardless of when the dependency is loaded.
         */
        UNDEFINED,
        /**
         * The plugin must be loaded before the dependency
         */
        BEFORE,
        /**
         * The plugin must be loaded after the dependency.
         */
        AFTER
    }
}
