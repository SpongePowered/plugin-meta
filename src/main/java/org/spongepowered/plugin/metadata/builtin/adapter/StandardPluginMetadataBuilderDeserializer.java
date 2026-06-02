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
import org.spongepowered.plugin.metadata.builtin.StandardInheritable;
import org.spongepowered.plugin.metadata.builtin.StandardPluginMetadata;
import org.spongepowered.plugin.metadata.builtin.adapter.util.GsonUtils;

import java.lang.reflect.Type;

public final class StandardPluginMetadataBuilderDeserializer implements JsonDeserializer<StandardPluginMetadata.Builder> {

    @Override
    public StandardPluginMetadata.Builder deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject obj = element.getAsJsonObject();
        return StandardPluginMetadata.builder()
                .id(GsonUtils.require(obj, "id").getAsString())
                .entrypoint(GsonUtils.require(obj, "entrypoint").getAsString())
                .name(GsonUtils.optional(obj, "name").map(JsonElement::getAsString).orElse(null))
                .description(GsonUtils.optional(obj, "description").map(JsonElement::getAsString).orElse(null))
                .merge(context.deserialize(element, StandardInheritable.class));
    }
}
