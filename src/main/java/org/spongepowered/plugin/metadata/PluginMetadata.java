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

import java.util.Optional;

/**
 * Represents specific, unique metadata to a plugin.
 *
 * @see Inheritable for metadata that might be shared between multiple plugin metadata.
 */
public interface PluginMetadata extends Inheritable {

    /**
     * @return The {@link Container container}.
     */
    Container container();

    /**
     * Gets the {@link String id}.
     *
     * <p>Ids must conform to the following requirements:</p>
     *
     * <ul>
     *     <li>Must be between 2 and 64 characters in length</li>
     *     <li>Must start with a lower case letter (a-z)</li>
     *     <li>May only contain a mix of lower case letters (a-z),
     *     numbers (0-9), dashes (-), and underscores (_)</li>
     * </ul>
     * @return The id
     */
    String id();

    /**
     * Gets the {@link String entrypoint}.
     *
     * <p>Consult the vendor of this library for how this field is used. As an example,
     * this could be the name of a module or a fully realized path to a discrete class.</p>
     * @return The entrypoint
     */
    String entrypoint();

    /**
     * @return The {@link String name} or {@link Optional#empty()} otherwise.
     */
    Optional<String> name();

    /**
     * @return The {@link String description} or {@link Optional#empty()} otherwise.
     */
    Optional<String> description();
}
