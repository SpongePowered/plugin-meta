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
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Specification for an entity considered to be a "contributor" to a plugin.
 *
 * Required: Name
 * Optional: Description
 *
 * How these values are used is not enforced on an implementation, consult the documentation
 * of that entity for more details.
 */
public final class PluginContributor {

    private final String name;
    @Nullable private final String description;

    private PluginContributor(final Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String name() {
        return this.name;
    }

    public Optional<String> description() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PluginContributor)) {
            return false;
        }
        final PluginContributor that = (PluginContributor) o;
        return this.name.equals(that.name);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PluginContributor.class.getSimpleName() + "[", "]")
                .add("name=" + this.name)
                .add("description=" + this.description)
                .toString();
    }

    public static class Builder {

        @Nullable String name, description;

        private Builder() {
        }

        public Builder name(final String name) {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        public Builder description(@Nullable final String description) {
            this.description = description;
            return this;
        }

        public PluginContributor build() {
            Objects.requireNonNull(this.name, "name");

            return new PluginContributor(this);
        }
    }
}
