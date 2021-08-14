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

import java.util.regex.Pattern;

public final class Constants {

    /**
     * The pattern plugin IDs must match. Plugin IDs must be lower case, and
     * start with an alphabetic character. It may only contain alphanumeric
     * characters, dashes or underscores. It must be at least 2 characters, and
     * cannot be longer than 64 characters.
     */
    public static final Pattern VALID_ID_PATTERN = Pattern.compile("^[a-z][a-z0-9-_]{1,63}$");

    public static final String INVALID_ID_REQUIREMENTS_MESSAGE = "Ids must be lower case, start with an alphabetic character, and contain only a "
            + "mix of alphanumeric, dashes, or underscore characters between 2 to 64 in length";

    private Constants() {
    }
}
