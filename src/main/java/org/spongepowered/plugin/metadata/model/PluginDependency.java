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

/**
 * Specification for an entity considered to be a "dependency" for a plugin.
 *
 * How these values are used are not enforced on an implementation, consult the documentation
 * of that entity for more details.
 *
 * @see org.spongepowered.plugin.metadata.builtin.model.StandardPluginDependency for a generic implementation
 */
public interface PluginDependency {

    String id();

    VersionRange version();

    LoadOrder loadOrder();

    boolean optional();

    /**
     * Represents the ordering of the dependency being loaded vs. the plugin by the implementation
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
