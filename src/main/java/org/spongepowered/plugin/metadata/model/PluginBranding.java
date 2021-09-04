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

import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.builtin.model.StandardPluginBranding;

import java.util.Optional;

/**
 * Specification for an entity representing the branding of an {@link Inheritable inheritable}
 * or {@link PluginMetadata plugin metadata}.
 * <p>
 * Consult the vendor for further information on how this is used.
 * @see StandardPluginBranding StandardPluginBranding, for a generic implementation
 */
public interface PluginBranding {

    /**
     * Gets the {@link String} that represents the location of the icon.
     * <p>
     * Consult the vendor on the composition of this value. For example, it
     * could be an absolute path or relative path (commonly fed to a {@link java.io.File} or
     * {@link java.nio.file.Path}), or a serialized {@link java.net.URI}.
     *
     * @return The icon or {@link Optional#empty()} otherwise
     */
    Optional<String> icon();

    /**
     * Gets the {@link String} that represents the location of the logo.
     * <p>
     * Consult the vendor on the composition of this value. For example, it
     * could be an absolute path or relative path (commonly fed to a {@link java.io.File} or
     * {@link java.nio.file.Path}), or a serialized {@link java.net.URI}.
     *
     * @return The icon or {@link Optional#empty()} otherwise
     */
    Optional<String> logo();
}
