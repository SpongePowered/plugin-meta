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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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

    private final Set<Dependency> dependencies = new HashSet<>();
    private final Set<Dependency> loadBefore = new HashSet<>();
    private final Set<Dependency> loadAfter = new HashSet<>();

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
     * Adds an author to the list of authors for this plugin.
     *
     * @param author The author to add
     */
    public void addAuthor(String author) {
        checkNotNull(author, "author");
        checkArgument(!author.isEmpty(), "Author cannot be empty");
        this.authors.add(author);
    }

    @Deprecated
    public Set<Dependency> getRequiredDependencies() {
        return this.dependencies;
    }

    @Deprecated
    public Set<Dependency> getLoadBefore() {
        return this.loadBefore;
    }

    @Deprecated
    public Set<Dependency> getLoadAfter() {
        return this.loadAfter;
    }

    @Deprecated
    public void addRequiredDependency(Dependency dependency) {
        checkNotNull(dependency, "dependency");
        this.dependencies.add(dependency);
    }

    @Deprecated
    public void loadBefore(Dependency dependency) {
        checkNotNull(dependency, "dependency");
        this.loadBefore.add(dependency);
    }

    @Deprecated
    public void loadBefore(Dependency dependency, boolean required) {
        loadBefore(dependency);
        if (required) {
            addRequiredDependency(dependency);
        }
    }

    @Deprecated
    public void loadAfter(Dependency dependency) {
        checkNotNull(dependency, "dependency");
        this.loadAfter.add(dependency);
    }

    @Deprecated
    public void loadAfter(Dependency dependency, boolean required) {
        loadAfter(dependency);
        if (required) {
            addRequiredDependency(dependency);
        }
    }

    /**
     * Returns a map with additional properties for this {@link PluginMetadata}.
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
     */
    public void removeExtension(String key) {
        this.extensions.remove(key);
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
                .add("loadBefore", this.loadBefore)
                .add("loadAfter", this.loadAfter)
                .add("extensions", this.extensions)
                .toString();
    }

    /**
     * Represents a dependency on another plugin.
     */
    public static final class Dependency {

        private final String id;
        @Nullable private final String version;

        /**
         * Constructs a new {@link Dependency} with the given plugin ID.
         *
         * @param id The plugin ID of the dependency
         */
        public Dependency(String id) {
            this(id, null);
        }

        /**
         * Constructs a new {@link Dependency} with the given plugin ID and
         * version range.
         *
         * @param id The plugin ID of the dependency
         * @param version The version range of the dependency or {@code null}
         *
         * @see #getVersion() Version range syntax
         */
        public Dependency(String id, @Nullable String version) {
            this.id = checkNotNull(id, "id");
            checkArgument(!id.isEmpty(), "id cannot be empty");
            this.version = emptyToNull(version);
        }

        /**
         * Returns the plugin ID of this {@link Dependency}.
         *
         * @return The plugin ID
         */
        public String getId() {
            return this.id;
        }

        /**
         * Returns the version range this {@link Dependency} should match.
         *
         * <p>The version range should use the <b>Maven version range
         * syntax</b>:</p>
         *
         * <table>
         * <caption>Maven version range syntax</caption>
         * <tr><th>Range</th><th>Meaning</th></tr>
         * <tr><td>1.0</td><td>Any dependency version, 1.0 is recommended</td></tr>
         * <tr><td>[1.0]</td><td>x == 1.0</td></tr>
         * <tr><td>[1.0,)</td><td>x &gt;= 1.0</td></tr>
         * <tr><td>(1.0,)</td><td>x &gt; 1.0</td></tr>
         * <tr><td>(,1.0]</td><td>x &lt;= 1.0</td></tr>
         * <tr><td>(,1.0)</td><td>x &lt; 1.0</td></tr>
         * <tr><td>(1.0,2.0)</td><td>1.0 &lt; x &lt; 2.0</td></tr>
         * <tr><td>[1.0,2.0]</td><td>1.0 &lt;= x &lt;= 2.0</td></tr>
         * </table>
         *
         * @return The version range, or {@code null} if unspecified
         * @see <a href="https://goo.gl/edrup4">Maven version range specification</a>
         * @see <a href="https://goo.gl/WBsFIu">Maven version design document</a>
         */
        @Nullable
        public String getVersion() {
            return this.version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Dependency)) {
                return false;
            }

            Dependency that = (Dependency) o;
            return this.id.equals(that.id)
                    && Objects.equal(this.version, that.version);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.id, this.version);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .omitNullValues()
                    .add("id", this.id)
                    .add("version", this.version)
                    .toString();
        }

    }


}
