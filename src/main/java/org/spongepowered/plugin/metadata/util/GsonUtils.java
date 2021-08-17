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
package org.spongepowered.plugin.metadata.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GsonUtils {

    public static <T> void writeIfPresent(final JsonWriter out, final String name, final Optional<T> value) throws IOException {
        if (value.isPresent()) {
            out.name(name).value(value.get().toString());
        }
    }

    public static <T, V extends Collection<T>> V read(final JsonArray in, final TypeAdapter<T> adapter, final Supplier<V> collector) {
        final V parsed = collector.get();

        for (final JsonElement element : in) {
            parsed.add(adapter.fromJsonTree(element));
        }

        return parsed;
    }

    public static Map<String, Object> read(final JsonObject in, final Function<JsonElement, Object> valFunc, final Supplier<Map<String, Object>> collector) {
        final Map<String, Object> parsed = collector.get();

        for (final Map.Entry<String, JsonElement> entry : in.entrySet()) {
            parsed.put(entry.getKey(), valFunc.apply(entry.getValue()));
        }

        return parsed;
    }

    public static <T> JsonArray write(final TypeAdapter<T> adapter, final Collection<T> value) {
        final JsonArray array = new JsonArray();
        for (final T val : value) {
            array.add(adapter.toJsonTree(val));
        }
        return array;
    }

    public static <T> JsonObject write(final Function<Object, JsonElement> valFunc, final Map<String, Object> value) {
        final JsonObject obj = new JsonObject();
        for (final Map.Entry<String, Object> entry : value.entrySet()) {
            obj.add(entry.getKey(), valFunc.apply(entry.getValue()));
        }

        return obj;
    }
}
