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
package org.spongepowered.plugin.metadata.builtin.adapter;

import com.google.gson.*;
import org.spongepowered.plugin.metadata.builtin.InheritableMetadata;
import org.spongepowered.plugin.metadata.builtin.StandardPluginMetadata;
import org.spongepowered.plugin.metadata.builtin.adapter.util.GsonUtils;
import org.spongepowered.plugin.metadata.builtin.adapter.util.LegacyIds;
import org.spongepowered.plugin.metadata.model.PluginEntrypoints;

import java.lang.reflect.Type;
import java.util.List;

public final class StandardPluginMetadataBuilderDeserializer implements JsonDeserializer<StandardPluginMetadata.Builder> {

    @Override
    public StandardPluginMetadata.Builder deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = element.getAsJsonObject();
        return StandardPluginMetadata.builder()
                .id(LegacyIds.fix(GsonUtils.require(obj, "id").getAsString()))
                .entrypoints(
                        GsonUtils.optional(obj, "entrypoints").map(v -> StandardPluginMetadataBuilderDeserializer.deserializeEntrypoints(v, context))
                                .or(() -> GsonUtils.optional(obj, "entrypoint").map(v -> new PluginEntrypoints(List.of(v.getAsString())))) // legacy
                                .orElseGet(PluginEntrypoints::none)
                )
                .override(context.deserialize(element, InheritableMetadata.class));
    }

    /**
     * Should be in PluginEntrypointsAdapter but Gson refuses to pass a JsonArray to a JsonDeserializer.
     */
    private static PluginEntrypoints deserializeEntrypoints(final JsonElement element, final JsonDeserializationContext context) {
        if (element.isJsonNull()) {
            return PluginEntrypoints.none();
        }
        if (element instanceof JsonArray array) {
            return new PluginEntrypoints(array.asList().stream().map(JsonElement::getAsString).toList());
        }
        return context.deserialize(element, PluginEntrypoints.class);
    }
}
