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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * Represents additional metadata for a specific version of a plugin.
 */
public final class PluginMetadata {

    /**
     * The pattern plugin IDs must match. Plugin IDs must be lower case, and
     * start with an alphabetic character. It may only contain alphanumeric
     * characters, dashes or underscores. It cannot be longer than
     * 64 characters.
     */
    public static final Pattern ID_PATTERN = Pattern.compile("[a-z][a-z0-9-_]{0,63}");

    private String id;
    @Nullable private String name;
    @Nullable private String version;

    @Nullable private String description;
    @Nullable private String url;

    private final List<String> authors = new ArrayList<>();

    private final Map<String, PluginDependency> dependencies = new HashMap<>();

    private final Map<String, Object> extensions = new LinkedHashMap<>();

    /**
     * Constructs a new {@link PluginMetadata} with the specified plugin ID.
     *
     * @param id The plugin ID, must match {@link #ID_PATTERN}
     */
    public PluginMetadata(String id) {
        setId(id);
    }

    /**
     * Returns the plugin ID that is represented by this {@link PluginMetadata}.
     *
     * @return The plugin ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * Sets the plugin ID that is represented by this {@link PluginMetadata}.
     *
     * @param id The plugin ID, must match {@link #ID_PATTERN}
     * @throws IllegalArgumentException If the plugin ID does not match
     *     {@link #ID_PATTERN}
     */
    public void setId(String id) {
        checkNotNull(id, "id");
        checkArgument(ID_PATTERN.matcher(id).matches(), "id must match pattern %s", ID_PATTERN);
        this.id = id;
    }

    /**
     * Returns the plugin name.
     *
     * @return The plugin name or {@code null} if unknown
     */
    @Nullable
    public String getName() {
        return this.name;
    }

    /**
     * Sets the plugin name.
     *
     * @param name The plugin name or {@code null} to reset
     */
    public void setName(@Nullable String name) {
        this.name = emptyToNull(name);
    }

    /**
     * Returns the plugin version.
     *
     * @return The plugin version or {@code null} if unknown
     */
    @Nullable
    public String getVersion() {
        return this.version;
    }

    /**
     * Sets the plugin version.
     *
     * @param version The plugin version or {@code null} to reset
     */
    public void setVersion(@Nullable String version) {
        this.version = emptyToNull(version);
    }

    /**
     * Returns the plugin description.
     *
     * @return The plugin description or {@code null} if unknown
     */
    @Nullable
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the plugin description.
     *
     * @param description The plugin description or {@code null} to reset
     */
    public void setDescription(@Nullable String description) {
        this.description = emptyToNull(description);
    }

    /**
     * Returns a URL where additional information about a plugin may be found.
     *
     * @return The URL or {@code null} if unknown
     */
    @Nullable
    public String getUrl() {
        return this.url;
    }

    /**
     * Sets a URL where additional information about a plugin may be found.
     *
     * @param url The URL or {@code null} to reset
     */
    public void setUrl(@Nullable String url) {
        this.url = emptyToNull(url);
    }

    /**
     * Returns a mutable {@link List} of authors for this plugin.
     *
     * <p>The returned list can be used to remove an author from the
     * metadata.</p>
     *
     * @return The list of authors, can be empty
     */
    public List<String> getAuthors() {
        return this.authors;
    }

    /**
     * Adds an author to the {@link List} of authors for this plugin.
     *
     * @param author The author to add
     * @throws IllegalArgumentException If the author is empty
     */
    public void addAuthor(String author) {
        checkNotNull(author, "author");
        checkArgument(!author.isEmpty(), "Author cannot be empty");
        this.authors.add(author);
    }

    /**
     * Removes an author from the {@link List} of authors for this plugin.
     *
     * @param author The author to remove
     * @return True if the operation was successful
     */
    public boolean removeAuthor(String author) {
        return this.authors.remove(author);
    }

    /**
     * Returns a {@link Collection} with all dependencies of the plugin
     * represented by this {@link PluginMetadata}.
     *
     * <p>It is possible to remove elements from the returned collection,
     * however new elements must be added using
     * {@link #addDependency(PluginDependency)}.</p>
     *
     * @return A collection with all dependencies
     */
    public Collection<PluginDependency> getDependencies() {
        return this.dependencies.values();
    }

    /**
     * Collects all dependencies of this {@link PluginMetadata} that are not
     * optional.
     *
     * @return An immutable set of all required dependencies
     */
    public Set<PluginDependency> collectRequiredDependencies() {
        if (this.dependencies.isEmpty()) {
            return ImmutableSet.of();
        }

        ImmutableSet.Builder<PluginDependency> builder = ImmutableSet.builder();

        for (PluginDependency dependency : this.dependencies.values()) {
            if (!dependency.isOptional()) {
                builder.add(dependency);
            }
        }

        return builder.build();
    }

    /**
     * Groups the dependencies of this {@link PluginMetadata} based on their
     * {@link PluginDependency.LoadOrder}.
     *
     * @return An immutable map with all dependencies grouped by their load
     *     order
     */
    public Map<PluginDependency.LoadOrder, Set<PluginDependency>> groupDependenciesByLoadOrder() {
        if (this.dependencies.isEmpty()) {
            return ImmutableMap.of();
        }

        EnumMap<PluginDependency.LoadOrder, Set<PluginDependency>> map = new EnumMap<>(PluginDependency.LoadOrder.class);

        for (PluginDependency.LoadOrder order : PluginDependency.LoadOrder.values()) {
            ImmutableSet.Builder<PluginDependency> dependencies = ImmutableSet.builder();

            for (PluginDependency dependency : this.dependencies.values()) {
                if (dependency.getLoadOrder() == order) {
                    dependencies.add(dependency);
                }
            }

            map.put(order, dependencies.build());
        }

        return Maps.immutableEnumMap(map);
    }

    /**
     * Returns the {@link PluginDependency} that is currently associated with
     * the specified plugin ID.
     *
     * @param id The plugin ID of the dependency
     * @return The dependency or {@code null} if there is no such dependency
     */
    @Nullable
    public PluginDependency getDependency(String id) {
        return this.dependencies.get(id);
    }

    /**
     * Adds a new {@link PluginDependency} to this {@link PluginMetadata}.
     *
     * @param dependency The dependency to add
     * @throws IllegalArgumentException If this plugin already has a dependency
     *     with the specified plugin ID
     */
    public void addDependency(PluginDependency dependency) {
        String id = dependency.getId();
        checkArgument(!this.dependencies.containsKey(id), "Duplicate dependency with plugin ID: %s", id);
        this.dependencies.put(id, dependency);
    }

    /**
     * Replaces the current {@link PluginDependency} with the same plugin ID
     * with a new one. Unlike {@link #addDependency(PluginDependency)} this
     * method doesn't throw an exception if a dependency with the same plugin
     * ID exists already.
     *
     * @param dependency The dependency to add
     * @return The dependency that was previously registered for the plugin ID
     *     or {@code null} if this is a new dependency
     */
    public PluginDependency replaceDependency(PluginDependency dependency) {
        return this.dependencies.put(id, dependency);
    }

    /**
     * Removes a dependency from this {@link PluginMetadata}.
     *
     * @param id The plugin ID of the dependency to remove
     * @return True if the operation was successful
     */
    public boolean removeDependency(String id) {
        return this.dependencies.remove(id) != null;
    }

    /**
     * Returns a {@link Map} with additional properties for this
     * {@link PluginMetadata}.
     *
     * <p>To serialize arbitrary objects, it may be necessary to register a
     * custom serializer for the extension object.</p>
     *
     * @return An unmodifiable map with additional properties
     */
    public Map<String, Object> getExtensions() {
        return Collections.unmodifiableMap(this.extensions);
    }

    /**
     * Returns the value for a specific extension.
     *
     * <p><b>Note:</b> This method will not verify if this metadata contains a
     * value with the specified type. A {@link ClassCastException} will be
     * thrown if the value has a different type.</p>
     *
     * @param key The key of the extension
     * @param <T> The type to cast the extension to
     * @return The extension or {@code null} if not set
     * @throws ClassCastException If the given type does not match the type of
     *     the extension
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getExtension(String key) {
        return (T) this.extensions.get(key);
    }

    /**
     * Adds the specified extension to this {@link PluginMetadata}.
     *
     * @param key The key of the extension
     * @param extension The extension
     */
    public void setExtension(String key, Object extension) {
        checkNotNull(extension, "extension");
        this.extensions.put(key, extension);
    }

    /**
     * Removes an extension from this {@link PluginMetadata}.
     *
     * @param key The key of the extension
     * @return True if the operation was successful
     */
    public boolean removeExtension(String key) {
        return this.extensions.remove(key) != null;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .add("id", this.id)
                .add("name", this.name)
                .add("version", this.version)
                .add("description", this.description)
                .add("url", this.url)
                .add("authors", this.authors)
                .add("dependencies", this.dependencies)
                .add("extensions", this.extensions)
                .toString();
    }

}
