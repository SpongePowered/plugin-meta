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
package org.spongepowered.plugin.metadata.builtin.adapter.model;

import com.google.gson.*;
import org.spongepowered.plugin.metadata.builtin.adapter.util.GsonUtils;
import org.spongepowered.plugin.metadata.model.PluginEntrypoints;

import java.lang.reflect.Type;
import java.util.List;

public final class PluginEntrypointsAdapter implements JsonSerializer<PluginEntrypoints>, JsonDeserializer<PluginEntrypoints> {

    @Override
    public PluginEntrypoints deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        if (element.isJsonNull()) {
            return PluginEntrypoints.none();
        }
        if (element instanceof JsonArray array) {
            return new PluginEntrypoints(array.asList().stream().map(JsonElement::getAsString).toList());
        }
        final JsonObject obj = element.getAsJsonObject();
        return new PluginEntrypoints(
                PluginEntrypointsAdapter.deserialize(obj, "main"),
                PluginEntrypointsAdapter.deserialize(obj, "server"),
                PluginEntrypointsAdapter.deserialize(obj, "client")
        );
    }

    @Override
    public JsonElement serialize(final PluginEntrypoints value, final Type type, final JsonSerializationContext context) {
        if (value.server().isEmpty() && value.client().isEmpty()) {
            return GsonUtils.toArray(value.main().stream().map(JsonPrimitive::new));
        }
        final JsonObject obj = new JsonObject();
        PluginEntrypointsAdapter.serialize(obj, "main", value.main());
        PluginEntrypointsAdapter.serialize(obj, "server", value.server());
        PluginEntrypointsAdapter.serialize(obj, "client", value.client());
        return obj;
    }

    private static List<String> deserialize(final JsonObject obj, final String name) {
        return GsonUtils.stream(obj, name).map(JsonElement::getAsString).toList();
    }

    private static void serialize(final JsonObject obj, final String name, final List<String> list) {
        if (!list.isEmpty()) {
            obj.add(name, GsonUtils.toArray(list.stream().map(JsonPrimitive::new)));
        }
    }
}
