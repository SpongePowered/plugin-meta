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
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.spongepowered.plugin.metadata.Constants;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.util.Objects;

/**
 * Specification for an entity considered to be a "dependency" for an {@link Inheritable inheritable}
 * or {@link PluginMetadata plugin metadata}.
 * <p>
 * Consult the vendor for further information on how this is used.
 *
 * <p>Ids must conform to the following requirements:</p>
 *
 * <ul>
 *     <li>Must be between 2 and 64 characters in length</li>
 *     <li>Must start with a lower case letter (a-z)</li>
 *     <li>May only contain a mix of lower case letters (a-z),
 *     numbers (0-9), dashes (-), and underscores (_)</li>
 * </ul>
 *
 * @param id The {@link String id}
 * @param version The {@link VersionRange version}, as a maven range.
 * @param loadOrder The {@link LoadOrder load order}
 * @param optional Whether this dependency is optional
 */
public record PluginDependency(String id, VersionRange version, LoadOrder loadOrder, boolean optional) {

    public PluginDependency {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(loadOrder, "loadOrder");

        if (!Constants.VALID_ID_PATTERN.matcher(id).matches()) {
            throw new IllegalStateException(String.format("Dependency with supplied ID '{%s}' is invalid. %s", id,
                    Constants.INVALID_ID_REQUIREMENTS_MESSAGE));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static final class Builder {
        private @MonotonicNonNull String id;
        private @MonotonicNonNull VersionRange version;
        private LoadOrder loadOrder = LoadOrder.UNDEFINED;
        private boolean optional = false;

        private Builder() {
        }

        public Builder id(final String id) {
            this.id = Objects.requireNonNull(id, "id");
            return this;
        }

        public Builder version(final String version) {
            this.version = VersionRange.createFromVersion(Objects.requireNonNull(version, "version"));
            return this;
        }

        public Builder version(final VersionRange version) {
            this.version = Objects.requireNonNull(version, "version");
            return this;
        }

        public Builder loadOrder(final LoadOrder loadOrder) {
            this.loadOrder = Objects.requireNonNull(loadOrder, "load order");
            return this;
        }

        public Builder optional(final boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder from(final PluginDependency value) {
            Objects.requireNonNull(value, "value");
            this.id = value.id;
            this.version = value.version;
            this.loadOrder = value.loadOrder;
            this.optional = value.optional;
            return this;
        }

        public PluginDependency build() {
            return new PluginDependency(this.id, this.version, this.loadOrder, this.optional);
        }
    }

    /**
     * Represents the ordering of how dependencies are loaded versus others.
     * <p>
     * A vendor *may* choose to introduce additional behavior beyond what is
     * documented here. It is recommended to consult with that entity on any
     * further behavioral changes.
     */
    public enum LoadOrder {
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
