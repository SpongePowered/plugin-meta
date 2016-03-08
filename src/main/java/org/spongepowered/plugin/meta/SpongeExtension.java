package org.spongepowered.plugin.meta;

import static com.google.common.base.Strings.emptyToNull;

import javax.annotation.Nullable;

public final class SpongeExtension {

    @Nullable private String assets;

    public String getAssetDirectory() {
        return this.assets;
    }

    public void setAssetDirectory(String assets) {
        this.assets = emptyToNull(assets);
    }

}
