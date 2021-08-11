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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Specification for an entity considered to be a "dependency" for a plugin.
 *
 * Required: Id, Version, LoadOrder (defaults to {@link LoadOrder#UNDEFINED}),
 * IsOptional (defaults to false)
 *
 * How these values are used is not enforced on an implementation, consult the documentation
 * of that entity for more details.
 */
public final class PluginDependency {

    private final String id, version;
    private final LoadOrder loadOrder;
    private final boolean optional;

    private PluginDependency(final Builder builder) {
        this.id = builder.id;
        this.version = builder.version;
        this.loadOrder = builder.loadOrder;
        this.optional = builder.optional;
    }

    /**
     * Returns a new {@link Builder} for creating a PluginDependency.
     *
     * @return A builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public String id() {
        return this.id;
    }

    public String version() {
        return this.version;
    }

    public LoadOrder loadOrder() {
        return this.loadOrder;
    }

    public boolean optional() {
        return this.optional;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PluginDependency)) {
            return false;
        }
        final PluginDependency that = (PluginDependency) o;
        return this.id.equals(that.id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PluginDependency.class.getSimpleName() + "[", "]")
                .add("id=" + this.id)
                .add("version=" + this.version)
                .add("loadOrder=" + this.loadOrder)
                .add("optional=" + this.optional)
                .toString();
    }

    /**
     * Represents the ordering of the dependency being loaded vs. the plugin by the implementation
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

    public static class Builder {

        @Nullable String id, version;
        LoadOrder loadOrder = LoadOrder.UNDEFINED;
        boolean optional = false;

        private Builder() {
        }

        public Builder id(final String id) {
            this.id = Objects.requireNonNull(id, "id");
            return this;
        }

        public Builder version(final String version) {
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

        public PluginDependency build() {
            Objects.requireNonNull(this.id, "id");
            Objects.requireNonNull(this.version, "version");

            return new PluginDependency(this);
        }
    }
}
