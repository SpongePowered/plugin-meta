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

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.spongepowered.plugin.metadata.model.PluginBranding;
import org.spongepowered.plugin.metadata.model.PluginContributor;
import org.spongepowered.plugin.metadata.model.PluginDependency;
import org.spongepowered.plugin.metadata.model.PluginLinks;
import org.spongepowered.plugin.metadata.model.PluginLoaderSpecification;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the metadata of a plugin.
 *
 * @see org.spongepowered.plugin.metadata.builtin.StandardPluginMetadata StandardPluginMetadata, for a generic implementation
 */
public interface PluginMetadata {

    /**
     * Gets the {@link String id}.
     *
     * <p>Ids must conform to the following requirements:</p>
     *
     * <ul>
     *     <li>Must be between 2 and 64 characters in length</li>
     *     <li>Must start with a lower case letter (a-z)</li>
     *     <li>May only contain a mix of lower case letters (a-z),
     *     numbers (0-9) and underscores (_)</li>
     * </ul>
     * @return The id
     */
    String id();

    /**
     * Gets the {@link String entrypoint}.
     * <p>
     * Consult the vendor for how this field is used. As an example, this could be
     * the name of a module or a fully realized path to a discrete class.
     *
     * @return The entrypoint
     */
    String entrypoint();

    /**
     * @return The {@link ArtifactVersion version}.
     */
    ArtifactVersion version();

    /**
     * @return The {@link PluginLoaderSpecification loader}.
     */
    PluginLoaderSpecification loader();

    /**
     * @return The {@link String name} or {@link Optional#empty()} otherwise.
     */
    Optional<String> name();

    /**
     * @return The {@link String description} or {@link Optional#empty()} otherwise.
     */
    Optional<String> description();

    /**
     * Gets the {@link String license} of the data within this container.
     * <p>
     * Notable examples include MIT or All Rights Reserved.
     *
     * @return The license
     */
    Optional<String> license();

    /**
     * @return The {@link PluginBranding branding}.
     */
    PluginBranding branding();

    /**
     * @return The {@link PluginLinks links} to various web resources.
     */
    PluginLinks links();

    /**
     * @return The {@link PluginContributor contributors} as an unmodifiable {@link List}.
     */
    List<PluginContributor> contributors();

    /**
     * Gets the {@link PluginDependency plugin dependency} by {@link String id}.
     * <p>
     * This maps to {@link PluginDependency#id()}.
     * @param id The id
     * @return The dependency or {@link Optional#empty()} otherwise.
     */
    Optional<PluginDependency> dependency(String id);

    /**
     * @return The {@link PluginDependency dependencies} as an unmodifiable {@link Collection}.
     */
    Collection<PluginDependency> dependencies();

    /**
     * Gets the {@link T property} by {@link String key}.
     *
     * @param key The key
     * @param <T> The type
     * @return The property or {@link Optional#empty()} otherwise.
     */
    <T> Optional<T> property(String key);

    /**
     * @return The properties as an unmodifiable {@link Map}.
     */
    Map<String, Object> properties();

}
