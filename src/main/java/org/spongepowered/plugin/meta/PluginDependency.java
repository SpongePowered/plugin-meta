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

import javax.annotation.Nullable;

/**
 * Represents a dependency on another plugin.
 */
public final class PluginDependency {

    /**
     * Defines when the dependency should be loaded in relation to the plugin.
     */
    public enum LoadOrder {

        /**
         * The plugin and the dependency can be loaded in any order.
         */
        NONE,

        /**
         * The dependency should be loaded <b>before</b> the plugin.
         */
        BEFORE,

        /**
         * The dependency should be loaded <b>after</b> the plugin.
         */
        AFTER

    }

    private final LoadOrder loadOrder;

    private final String id;
    @Nullable private final String version;

    private final boolean optional;

    /**
     * Constructs a new {@link PluginDependency} with the given plugin ID and
     * version range.
     *
     * @param loadOrder The order to load the dependency in relation to the
     *     plugin
     * @param id The plugin ID of the dependency
     * @param version The version range of the dependency or {@code null}
     * @param optional Whether the dependency is optional
     * @throws IllegalArgumentException If the plugin ID is empty
     * @see #getVersion() Version range syntax
     */
    public PluginDependency(LoadOrder loadOrder, String id, @Nullable String version, boolean optional) {
        this.loadOrder = checkNotNull(loadOrder, "loadOrder");
        this.id = checkNotNull(id, "id");
        checkArgument(!id.isEmpty(), "id cannot be empty");
        this.version = emptyToNull(version);
        this.optional = optional;
    }

    /**
     * Returns the order to load the dependency in relation to the plugin.
     *
     * @return The load order
     */
    public LoadOrder getLoadOrder() {
        return this.loadOrder;
    }

    /**
     * Returns the plugin ID of this {@link PluginDependency}.
     *
     * @return The plugin ID
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the version range this {@link PluginDependency} should match.
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

    /**
     * Returns whether the dependency is optional for the plugin to work
     * correctly.
     *
     * @return True if the dependency is optional
     */
    public boolean isOptional() {
        return this.optional;
    }

    /**
     * Returns a new {@link PluginDependency} that has {@link #isOptional()}
     * set to {@code true}.
     *
     * @return The new optional dependency
     */
    public PluginDependency optional() {
        return setOptional(true);
    }

    /**
     * Returns a new {@link PluginDependency} that has {@link #isOptional()}
     * set to {@code false}.
     *
     * @return The new required dependency
     */
    public PluginDependency required() {
        return setOptional(false);
    }

    private PluginDependency setOptional(boolean optional) {
        if (this.optional == optional) {
            return this;
        }

        return new PluginDependency(this.loadOrder, this.id, this.version, optional);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PluginDependency that = (PluginDependency) o;
        return this.loadOrder == that.loadOrder
                && this.id.equals(that.id)
                && Objects.equal(this.version, that.version)
                && this.optional == that.optional;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.loadOrder, this.id, this.version, this.optional);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .omitNullValues()
                .addValue(this.loadOrder)
                .add("id", this.id)
                .add("version", this.version)
                .add("optional", this.optional)
                .toString();
    }

}
