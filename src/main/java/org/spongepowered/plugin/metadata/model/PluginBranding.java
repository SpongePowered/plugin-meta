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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Inheritable;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.util.Objects;
import java.util.Optional;

/**
 * Specification for an entity representing the branding of an {@link Inheritable inheritable}
 * or {@link PluginMetadata plugin metadata}.
 * <p>
 * Consult the vendor for further information on how this is used.
 *
 * @param icon The {@link String} that represents the location of the icon.
 * @param logo The {@link String} that represents the location of the logo.
 */
public record PluginBranding(Optional<String> icon, Optional<String> logo) {
    private static final PluginBranding NONE = new PluginBranding(Optional.empty(), Optional.empty());

    public PluginBranding {
        Objects.requireNonNull(icon, "icon");
        Objects.requireNonNull(logo, "logo");
    }

    public static PluginBranding none() {
        return PluginBranding.NONE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder().from(this);
    }

    public static final class Builder {
        private Optional<String> logo = Optional.empty(), icon = Optional.empty();

        private Builder() {
        }

        public Builder logo(@Nullable final String logo) {
            this.logo = Optional.ofNullable(logo);
            return this;
        }

        public Builder icon(@Nullable final String icon) {
            this.icon = Optional.ofNullable(icon);
            return this;
        }

        public Builder from(final PluginBranding value) {
            Objects.requireNonNull(value, "value");
            this.logo = value.logo();
            this.icon = value.icon();
            return this;
        }

        public PluginBranding build() {
            return new PluginBranding(this.icon, this.logo);
        }
    }
}
