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
import org.spongepowered.plugin.metadata.model.PluginLinks;
import org.spongepowered.plugin.metadata.builtin.adapter.util.GsonUtils;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Optional;

public final class PluginLinksAdapter implements JsonSerializer<PluginLinks>, JsonDeserializer<PluginLinks> {

    @Override
    public PluginLinks deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = element.getAsJsonObject();
        return new PluginLinks(
                PluginLinksAdapter.deserialize(obj, "homepage", context),
                PluginLinksAdapter.deserialize(obj, "source", context),
                PluginLinksAdapter.deserialize(obj, "issues", context)
        );
    }

    @Override
    public JsonElement serialize(final PluginLinks value, final Type type, final JsonSerializationContext context) {
        final JsonObject obj = new JsonObject();
        PluginLinksAdapter.serialize(obj, "homepage", context, value.homepage());
        PluginLinksAdapter.serialize(obj, "source", context, value.source());
        PluginLinksAdapter.serialize(obj, "issues", context, value.issues());
        return obj;
    }

    private static Optional<URI> deserialize(final JsonObject obj, final String name, final JsonDeserializationContext context) throws JsonParseException {
        return GsonUtils.optional(obj, name).map(v -> context.deserialize(v, URI.class));
    }

    private static void serialize(final JsonObject obj, final String name, final JsonSerializationContext context, final Optional<URI> value) throws JsonParseException {
        value.ifPresent(v -> obj.add(name, context.serialize(v, URI.class)));
    }
}
