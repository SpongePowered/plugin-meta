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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Constants;
import org.spongepowered.plugin.metadata.PluginMetadata;

import java.util.Objects;
import java.util.Optional;

/**
 * Specification for an entity considered to be conflicting with a {@link PluginMetadata plugin metadata}.
 * <p>
 * The vendor will either log a warning or refuse to load when this entity is present.
 *
 * @see Constants#VALID_ID_PATTERN
 * @param id The {@link String id}
 * @param version The {@link VersionRange version}, as a maven range.
 * @param fatal Whether this conflict should prevent loading
 * @param reason The {@link String reason}
 */
public record PluginConflict(String id, VersionRange version, boolean fatal, Optional<String> reason) {

    public PluginConflict {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(reason, "reason");

        if (!Constants.VALID_ID_PATTERN.matcher(id).matches()) {
            throw new IllegalStateException(String.format("Conflict with supplied ID '{%s}' is invalid. %s", id,
                    Constants.INVALID_ID_REQUIREMENTS_MESSAGE));
        }
    }

    public PluginConflict(String id, VersionRange version, boolean fatal, @Nullable String reason) {
        this(id, version, fatal, Optional.ofNullable(reason));
    }
}
