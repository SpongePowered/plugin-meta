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

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * Specification for an entity representing the links to "web resources" of an {@link Inheritable inheritable}
 * or {@link PluginMetadata plugin metadata}.
 * <p>
 * Consult the vendor for further information on how this is used.
 *
 * @param homepage The {@link URI homepage}
 * @param source The {@link URI source}
 * @param issues The {@link URI issues}
 */
public record PluginLinks(Optional<URI> homepage, Optional<URI> source, Optional<URI> issues) {
    private static final PluginLinks NONE = new PluginLinks(Optional.empty(), Optional.empty(), Optional.empty());

    public PluginLinks {
        Objects.requireNonNull(homepage, "homepage");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(issues, "issues");
    }

    public PluginLinks(@Nullable URI homepage, @Nullable URI source, @Nullable URI issues) {
        this(Optional.ofNullable(homepage), Optional.ofNullable(source), Optional.ofNullable(issues));
    }

    public static PluginLinks none() {
        return PluginLinks.NONE;
    }
}
