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
package org.spongepowered.plugin.metadata.builtin.adapter.util;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.builtin.MissingRequiredFieldException;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class GsonUtils {

    public static JsonElement require(final JsonObject obj, final String name) {
        final JsonElement element = obj.get(name);
        if (element == null || element.isJsonNull()) {
            throw new MissingRequiredFieldException(name);
        }
        return element;
    }

    public static Optional<JsonElement> optional(final JsonObject obj, final String name) {
        final JsonElement element = obj.get(name);
        if (element == null || element.isJsonNull()) {
            return Optional.empty();
        }
        return Optional.of(element);
    }

    public static Stream<JsonElement> stream(final JsonObject obj, final String name) {
        final JsonElement element = obj.get(name);
        if (element == null || element.isJsonNull()) {
            return Stream.empty();
        }
        return element.getAsJsonArray().asList().stream();
    }

    public static JsonArray toArray(final Stream<JsonElement> stream) {
        final JsonArray array = new JsonArray();
        stream.forEach(array::add);
        return array;
    }

    public static Map<String, Object> deserializeMap(final @Nullable JsonElement element, final Function<JsonElement, Object> valFunc, final Supplier<Map<String, Object>> collector) {
        final Map<String, Object> map = collector.get();
        if (element instanceof JsonObject obj) {
            for (final Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                map.put(entry.getKey(), valFunc.apply(entry.getValue()));
            }
        }
        return map;
    }

    public static JsonObject serializeMap(final Map<String, Object> map, final Function<Object, JsonElement> valFunc) {
        final JsonObject obj = new JsonObject();
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            obj.add(entry.getKey(), valFunc.apply(entry.getValue()));
        }
        return obj;
    }
}
