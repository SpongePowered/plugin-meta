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
package org.spongepowered.plugin.metadata.builtin;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Constants;
import org.spongepowered.plugin.metadata.Container;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class StandardPluginMetadata extends StandardInheritable implements PluginMetadata {

    private final String id, entrypoint;
    @Nullable private final String name, description;
    @Nullable private Container container;

    private StandardPluginMetadata(final Builder builder) {
        super(builder);
        this.id = builder.id;
        this.entrypoint = builder.entrypoint;
        this.name = builder.name;
        this.description = builder.description;
    }

    @Override
    public Container container() {
        return this.container;
    }

    void setContainer(final MetadataContainer container) {
        this.container = container;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String entrypoint() {
        return this.entrypoint;
    }

    @Override
    public Optional<String> name() {
        return Optional.ofNullable(this.name);
    }

    @Override
    public Optional<String> description() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof StandardPluginMetadata other)) {
            return false;
        }

        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(", ", StandardPluginMetadata.class.getSimpleName() + "[", "]")
                .add("id=" + this.id)
                .add("name=" + this.name)
                .add("entrypoint=" + this.entrypoint)
                .add("description=" + this.description);
        joiner.merge(this.stringJoiner());
        return joiner.toString();
    }

    public StandardPluginMetadata.Builder toBuilder() {
        return new StandardPluginMetadata.Builder().from(this);
    }

    public static StandardPluginMetadata.Builder builder() {
        return new StandardPluginMetadata.Builder();
    }

    public static final class Builder extends StandardInheritable.AbstractBuilder<StandardPluginMetadata, Builder> {

        private @MonotonicNonNull String id, entrypoint;
        private @Nullable String name, description;

        private Builder() {}

        public Builder id(final String id) {
            this.id = Objects.requireNonNull(id, "id");
            return this;
        }

        public Builder entrypoint(final String entrypoint) {
            this.entrypoint = Objects.requireNonNull(entrypoint, "entrypoint");
            return this;
        }

        public Builder name(final @Nullable String name) {
            this.name = name;
            return this;
        }

        public Builder description(final @Nullable String description) {
            this.description = description;
            return this;
        }

        @Override
        public Builder from(final StandardPluginMetadata value) {
            this.id = value.id;
            this.entrypoint = value.entrypoint;
            this.name = value.name;
            this.description = value.description;
            return super.from(value);
        }

        @Override
        protected StandardPluginMetadata build0() {
            if (!Constants.VALID_ID_PATTERN.matcher(Objects.requireNonNull(this.id, "id")).matches()) {
                throw new IllegalStateException(String.format("PluginMetadata with supplied ID '{%s}' is invalid. %s", this.id,
                        Constants.INVALID_ID_REQUIREMENTS_MESSAGE));
            }
            if (this.version == NullVersion.instance()) {
                throw new IllegalStateException(String.format("PluginMetadata with supplied ID '{%s}' has no version specified.", this.id));
            }
            Objects.requireNonNull(this.entrypoint, "entrypoint");

            return new StandardPluginMetadata(this);
        }
    }

}
