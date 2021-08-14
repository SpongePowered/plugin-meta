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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Holder;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class StandardPluginMetadata extends StandardInheritable implements PluginMetadata {

    private final Holder holder;
    private final String id, mainClass;
    @Nullable private final String name, description;

    private StandardPluginMetadata(final Builder builder) {
        super(builder);
        this.holder = builder.holder;
        this.id = builder.id;
        this.mainClass = builder.mainClass;
        this.name = builder.name;
        this.description = builder.description;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Holder holder() {
        return this.holder;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String mainClass() {
        return this.mainClass;
    }

    @Override
    public Optional<String> name() {
        return Optional.ofNullable(this.name);
    }

    @Override
    public Optional<String> description() {
        return Optional.ofNullable(this.description);
    }

    public Builder toBuilder() {
        final Builder builder = new Builder();
        builder.holder = this.holder;
        builder.id = this.id;
        builder.name = this.name;
        builder.mainClass = this.mainClass;
        builder.description = this.description;
        builder.contributors.addAll(this.contributors);
        builder.dependencies.addAll(this.dependencies);
        builder.properties.putAll(this.properties);
        builder.version = this.version;
        builder.branding = this.branding.toBuilder().build();
        builder.links = this.links.toBuilder().build();
        return builder;
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

        if (!(o instanceof StandardPluginMetadata)) {
            return false;
        }

        final StandardPluginMetadata other = (StandardPluginMetadata) o;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(", ", StandardPluginMetadata.class.getSimpleName() + "[", "]")
                .add("holder=" + this.holder.name())
                .add("id=" + this.id)
                .add("name=" + this.name)
                .add("mainClass=" + this.mainClass)
                .add("description=" + description);
        joiner.merge(this.stringJoiner());
        return joiner.toString();
    }

    public static final class Builder extends StandardInheritable.Builder<PluginMetadata, Builder> {

        @Nullable Holder holder;
        @Nullable String id, mainClass, name, description;

        private Builder() {
        }

        public Builder holder(final Holder holder) {
            this.holder = Objects.requireNonNull(holder, "holder");
            return this;
        }

        public Builder id(final String id) {
            this.id = Objects.requireNonNull(id, "id");
            return this;
        }

        public Builder mainClass(final String mainClass) {
            this.mainClass = Objects.requireNonNull(mainClass, "mainClass");
            return this;
        }

        public Builder name(final String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        public Builder description(final String description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        @Override
        protected PluginMetadata build0() {
            Objects.requireNonNull(this.holder, "holder");
            Objects.requireNonNull(this.id, "id");
            Objects.requireNonNull(this.mainClass, "mainClass");

            return new StandardPluginMetadata(this);
        }
    }
}
