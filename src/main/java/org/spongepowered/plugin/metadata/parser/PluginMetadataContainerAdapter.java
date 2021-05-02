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
package org.spongepowered.plugin.metadata.parser;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.spongepowered.plugin.metadata.PluginMetadataContainer;

import java.io.IOException;
import java.util.Objects;

public final class PluginMetadataContainerAdapter extends TypeAdapter<PluginMetadataContainer> {

    private final PluginMetadataCollectionAdapter adapter;

    public PluginMetadataContainerAdapter(final PluginMetadataCollectionAdapter adapter) {
        this.adapter = Objects.requireNonNull(adapter);
    }

    public PluginMetadataCollectionAdapter collectionAdapter() {
        return this.adapter;
    }

    @Override
    public void write(final JsonWriter out, final PluginMetadataContainer value) throws IOException {
        Objects.requireNonNull(out);
        Objects.requireNonNull(value);

        this.adapter.write(out, value.allMetadata().values());
    }

    @Override
    public PluginMetadataContainer read(final JsonReader in) throws IOException {
        Objects.requireNonNull(in);

        return new PluginMetadataContainer(this.adapter.read(in));
    }
}
