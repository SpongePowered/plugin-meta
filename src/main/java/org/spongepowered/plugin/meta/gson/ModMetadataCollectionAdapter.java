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
package org.spongepowered.plugin.meta.gson;

import com.google.common.collect.ImmutableList;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.spongepowered.plugin.meta.PluginMetadata;

import java.io.IOException;
import java.util.List;

public final class ModMetadataCollectionAdapter extends TypeAdapter<List<PluginMetadata>> {

    public static final ModMetadataCollectionAdapter DEFAULT = new ModMetadataCollectionAdapter(ModMetadataAdapter.DEFAULT);

    private final ModMetadataAdapter metadataAdapter;

    public ModMetadataCollectionAdapter(ModMetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
    }

    @Override
    public List<PluginMetadata> read(JsonReader in) throws IOException {
        in.beginArray();
        ImmutableList.Builder<PluginMetadata> result = ImmutableList.builder();
        while (in.hasNext()) {
            result.add(this.metadataAdapter.read(in));
        }
        in.endArray();
        return result.build();
    }

    @Override
    public void write(JsonWriter out, List<PluginMetadata> values) throws IOException {
        out.beginArray();
        for (PluginMetadata meta : values) {
            this.metadataAdapter.write(out, meta);
        }
        out.endArray();
    }

}
