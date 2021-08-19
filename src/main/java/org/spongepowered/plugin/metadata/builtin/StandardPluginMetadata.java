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
package org.spongepowered.plugin.metadata.builtin;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.plugin.metadata.Constants;
import org.spongepowered.plugin.metadata.Holder;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.util.GsonUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public final class StandardPluginMetadata extends StandardInheritable implements PluginMetadata {

    private Holder holder;
    private final String id, mainClass;
    @Nullable private final String name, description;

    private StandardPluginMetadata(final Builder builder) {
        super(builder);
        this.id = builder.id;
        this.mainClass = builder.mainClass;
        this.name = builder.name;
        this.description = builder.description;
    }

    @Override
    public Holder holder() {
        return this.holder;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String mainClass() {
        return this.mainClass;
    }

    @Override
    public Optional<String> name() {
        return Optional.ofNullable(this.name);
    }

    @Override
    public Optional<String> description() {
        return Optional.ofNullable(this.description);
    }

    void setHolder(final MetadataHolder holder) {
        this.holder = holder;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof StandardPluginMetadata)) {
            return false;
        }

        final StandardPluginMetadata other = (StandardPluginMetadata) o;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(", ", StandardPluginMetadata.class.getSimpleName() + "[", "]")
                .add("holder=" + this.holder.name())
                .add("id=" + this.id)
                .add("name=" + this.name)
                .add("mainClass=" + this.mainClass)
                .add("description=" + description);
        joiner.merge(this.stringJoiner());
        return joiner.toString();
    }

    public static final class Builder extends StandardInheritable.AbstractBuilder<StandardPluginMetadata, Builder> {

        @Nullable String id, mainClass, name, description;

        private Builder() {
        }

        public Builder id(final String id) {
            this.id = Objects.requireNonNull(id, "id");
            return this;
        }

        public Builder mainClass(final String mainClass) {
            this.mainClass = Objects.requireNonNull(mainClass, "mainClass");
            return this;
        }

        public Builder name(final String name) {
            this.name = Objects.requireNonNull(name, "name");
            return this;
        }

        public Builder description(final String description) {
            this.description = Objects.requireNonNull(description, "description");
            return this;
        }

        @Override
        protected StandardPluginMetadata build0() {
            if (!Constants.VALID_ID_PATTERN.matcher(Objects.requireNonNull(this.id, "id")).matches()) {
                throw new IllegalStateException(String.format("PluginMetadata with supplied ID '{%s}' is invalid. %s", this.id,
                        Constants.INVALID_ID_REQUIREMENTS_MESSAGE));
            }
            if (this.version == NullVersion.instance()) {
                throw new IllegalStateException(String.format("PluginMetadata with supplied ID '{%s}' has no version specified.", this.id));
            }
            Objects.requireNonNull(this.mainClass, "mainClass");

            return new StandardPluginMetadata(this);
        }
    }

    public static final class Deserializer implements JsonDeserializer<StandardPluginMetadata> {

        private final @Nullable StandardInheritable globalMetadata;

        public Deserializer(final @Nullable StandardInheritable globalMetadata) {
            this.globalMetadata = globalMetadata;
        }

        @Override
        public StandardPluginMetadata deserialize(final JsonElement element, final Type type, final JsonDeserializationContext context)
                throws JsonParseException {
            final JsonObject obj = element.getAsJsonObject();
            final StandardInheritable globalOverride = context.deserialize(element, StandardInheritable.class);
            final StandardPluginMetadata.Builder builder = new StandardPluginMetadata.Builder();
            builder
                    .id(obj.get("id").getAsString())
                    .mainClass(obj.get("main-class").getAsString());
            GsonUtils.consumeIfPresent(obj, "name", e -> builder.name(e.getAsString()));
            GsonUtils.consumeIfPresent(obj, "description", e -> builder.description(e.getAsString()));
            builder.from(globalOverride);
            if (this.globalMetadata != null) {
                builder.from(this.globalMetadata);
            }

            return builder.build();
        }
    }

    public static final class Serializer implements JsonSerializer<StandardPluginMetadata> {

        @Override
        public JsonElement serialize(final StandardPluginMetadata value, final Type type, final JsonSerializationContext context) {
            final JsonObject obj = new JsonObject();
            obj.addProperty("id", value.id);
            obj.addProperty("main-class", value.mainClass);
            GsonUtils.writeIfPresent(obj, "name", value.name());
            GsonUtils.writeIfPresent(obj, "description", value.description());

            final JsonObject inheritableElement = (JsonObject) context.serialize(value, StandardInheritable.class);
            // TODO Sort this?
            for (final Map.Entry<String, JsonElement> entry : inheritableElement.entrySet()) {
                obj.add(entry.getKey(), entry.getValue());
            }
            return obj;
        }
    }
}
