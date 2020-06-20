/*
 * This file is part of plugin-spi, licensed under the MIT License (MIT).
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

import com.google.common.base.Preconditions;

import java.util.Objects;

public final class PluginDependency {

    private final String id, version;

    private PluginDependency(final Builder builder) {
        this.id = builder.id;
        this.version = builder.version;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return this.id;
    }

    public String getVersion() {
        return this.version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final PluginDependency that = (PluginDependency) o;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public enum LoadOrder {
        NONE,
        AFTER
    }

    public static class Builder {

        String id, version;
        boolean optional;
        LoadOrder loadOrder = LoadOrder.NONE;

        public Builder setId(final String id) {
            this.id = Preconditions.checkNotNull(id);
            return this;
        }

        public Builder setVersion(final String version) {
            this.version = Preconditions.checkNotNull(version);
            return this;
        }

        public Builder setOptional(final boolean optional) {
            this.optional = optional;
            return this;
        }

        public Builder setLoadOrder(final LoadOrder loadOrder) {
            this.loadOrder = loadOrder;
            return this;
        }

        public PluginDependency build() {
            Preconditions.checkNotNull(this.id);
            Preconditions.checkNotNull(this.version);

            return new PluginDependency(this);
        }
    }
}
