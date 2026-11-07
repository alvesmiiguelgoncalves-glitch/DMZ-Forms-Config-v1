package com.dmzformoptions.mixin;

import com.dmzformoptions.DmzFormOptions;
import com.dmzformoptions.util.FormOptionsAccess;
import com.dragonminez.client.util.ColorUtils;
import com.dragonminez.common.config.FormConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

/**
 * Adds the new options as real fields of {@code FormConfig.FormData}. DMZ reads and writes its form
 * files with Gson, which reflects over every non-transient field, so the keys below work in the
 * JSON files exactly like the built-in ones (and are kept when DMZ rewrites the file).
 */
@Mixin(value = FormConfig.FormData.class, remap = false)
public abstract class FormDataMixin implements FormOptionsAccess {

    // ---- JSON keys (boxed on purpose: a missing/null key falls back to the default in the getters) ----
    @Unique private Boolean forceTiedTail = false;
    @Unique private Boolean forceLooseTail = false;
    @Unique private Boolean stackTintAffectsHairAndTail = true;
    @Unique private Double stackTintHairAndTailIntensity = -1.0;
    @Unique private String scleraColor = "";
    @Unique private Boolean noEyebrows = false;
    @Unique private Boolean eyeliner = false;
    @Unique private String eyelinerColor = "#000000";
    @Unique private Boolean chargeKiAnimation = true;

    // ---- caches (transient: never written to JSON) ----
    @Unique private transient float[] dmzfo$scleraRgbCache;
    @Unique private transient String dmzfo$scleraRgbKey;
    @Unique private transient float[] dmzfo$eyelinerRgbCache;
    @Unique private transient String dmzfo$eyelinerRgbKey;
    @Unique private transient FormConfig.FormData dmzfo$tintCopyCache;
    @Unique private transient double dmzfo$tintCopyIntensity = Double.NaN;

    @Override
    public boolean dmzfo$forceTiedTail() {
        return Boolean.TRUE.equals(forceTiedTail);
    }

    @Override
    public boolean dmzfo$forceLooseTail() {
        return Boolean.TRUE.equals(forceLooseTail);
    }

    @Override
    public boolean dmzfo$stackTintAffectsHairAndTail() {
        return stackTintAffectsHairAndTail == null || stackTintAffectsHairAndTail;
    }

    @Override
    public double dmzfo$stackTintHairAndTailIntensity() {
        return stackTintHairAndTailIntensity == null ? -1.0 : stackTintHairAndTailIntensity;
    }

    @Override
    @Nullable
    public float[] dmzfo$scleraRgb() {
        String key = scleraColor == null ? "" : scleraColor.trim();
        if (!key.equals(dmzfo$scleraRgbKey)) {
            dmzfo$scleraRgbKey = key;
            dmzfo$scleraRgbCache = dmzfo$parse(key);
        }
        return dmzfo$scleraRgbCache;
    }

    @Override
    public boolean dmzfo$noEyebrows() {
        return Boolean.TRUE.equals(noEyebrows);
    }

    @Override
    public boolean dmzfo$eyeliner() {
        return Boolean.TRUE.equals(eyeliner);
    }

    @Override
    @Nullable
    public float[] dmzfo$eyelinerRgb() {
        String key = eyelinerColor == null || eyelinerColor.isBlank() ? "#000000" : eyelinerColor.trim();
        if (!key.equals(dmzfo$eyelinerRgbKey)) {
            dmzfo$eyelinerRgbKey = key;
            dmzfo$eyelinerRgbCache = dmzfo$parse(key);
        }
        return dmzfo$eyelinerRgbCache;
    }

    @Override
    public boolean dmzfo$chargeKiAnimation() {
        return chargeKiAnimation == null || chargeKiAnimation;
    }

    @Override
    @Nullable
    public FormConfig.FormData dmzfo$tintCopy(double intensity) {
        if (dmzfo$tintCopyCache != null && dmzfo$tintCopyIntensity == intensity) return dmzfo$tintCopyCache;
        try {
            Gson gson = new Gson();
            JsonObject tree = gson.toJsonTree((FormConfig.FormData) (Object) this).getAsJsonObject();
            tree.addProperty("tintIntensity", intensity);
            dmzfo$tintCopyCache = gson.fromJson(tree, FormConfig.FormData.class);
            dmzfo$tintCopyIntensity = intensity;
        } catch (RuntimeException e) {
            DmzFormOptions.LOGGER.warn("[dmz_form_options] could not copy form data for the hair/tail tint; using the stack form's own tint.", e);
            dmzfo$tintCopyCache = null;
        }
        return dmzfo$tintCopyCache;
    }

    @Unique
    @Nullable
    private static float[] dmzfo$parse(String hex) {
        if (hex.isEmpty()) return null;
        try {
            float[] rgb = ColorUtils.hexToRgb(hex);
            return rgb != null && rgb.length >= 3 ? rgb : null;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
