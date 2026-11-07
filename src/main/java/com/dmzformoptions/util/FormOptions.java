package com.dmzformoptions.util;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.common.config.FormConfig;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.StatsProvider;
import com.dragonminez.common.stats.character.Character;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Shared logic for the mixins. Not a mixin itself, so it can be called from anywhere. */
public final class FormOptions {

    /** Guards {@code SaiyanTailRulesMixin} against calling itself. */
    public static boolean tailReentry;

    /** Set per player render by {@code SkinLayerTintMixin}; read by the Saiyan tail redirect. */
    @Nullable public static FormConfig.FormData bodyTint;
    @Nullable public static FormConfig.FormData hairTailTint;
    public static boolean tintContextValid;

    private FormOptions() {
    }

    @Nullable
    public static FormOptionsAccess of(@Nullable FormConfig.FormData data) {
        return data instanceof FormOptionsAccess access ? access : null;
    }

    /** Active stack form first (it is the more recent choice), then the base form. */
    public static List<FormOptionsAccess> active(@Nullable Character character) {
        List<FormOptionsAccess> out = new ArrayList<>(2);
        if (character == null) return out;
        if (character.hasActiveStackForm()) {
            FormOptionsAccess stack = of(character.getActiveStackFormData());
            if (stack != null) out.add(stack);
        }
        if (character.hasActiveForm()) {
            FormOptionsAccess form = of(character.getActiveFormData());
            if (form != null) out.add(form);
        }
        return out;
    }

    @Nullable
    public static Character characterOf(@Nullable Entity entity) {
        if (entity == null) return null;
        StatsData stats = StatsProvider.get(StatsCapability.INSTANCE, entity).orElse(null);
        return stats == null ? null : stats.getCharacter();
    }

    // ------------------------------------------------------------------ tail

    /** @return TRUE = force loose tail, FALSE = force tied tail, null = leave DMZ alone. */
    @Nullable
    public static Boolean tailOverride(@Nullable Entity entity) {
        List<FormOptionsAccess> forms = active(characterOf(entity));
        for (FormOptionsAccess form : forms) if (form.dmzfo$forceLooseTail()) return Boolean.TRUE;
        for (FormOptionsAccess form : forms) if (form.dmzfo$forceTiedTail()) return Boolean.FALSE;
        return null;
    }

    // ------------------------------------------------------------------ eyes

    /** Eye options for the current forms, or null when nothing is set (DMZ draws its own eyes). */
    @Nullable
    public static EyeOptions eyes(@Nullable Character character) {
        float[] sclera = null;
        float[] liner = null;
        boolean noBrows = false;
        for (FormOptionsAccess form : active(character)) {
            if (sclera == null) sclera = form.dmzfo$scleraRgb();
            if (liner == null && form.dmzfo$eyeliner()) liner = form.dmzfo$eyelinerRgb();
            noBrows |= form.dmzfo$noEyebrows();
        }
        if (sclera == null && liner == null && !noBrows) return null;
        return new EyeOptions(sclera, liner, noBrows);
    }

    public record EyeOptions(@Nullable float[] sclera, @Nullable float[] eyeliner, boolean noEyebrows) {
    }

    // ------------------------------------------------------------------ Kaio-ken tint on hair / tail

    /**
     * The tint form DMZ should use for hair and tail. DMZ's own choice is a stack form (Kaio-ken)
     * when one is active, otherwise the base form. When a stack form is active, the BASE form can say:
     * {@code stackTintAffectsHairAndTail: false} (no tint on hair/tail) or
     * {@code stackTintHairAndTailIntensity: 0.1} (weaker tint on hair/tail).
     */
    @Nullable
    public static FormConfig.FormData hairTailTint(StatsData stats, @Nullable FormConfig.FormData original) {
        try {
            if (original == null || stats == null) return original;
            Character character = stats.getCharacter();
            if (character == null || !character.hasActiveStackForm() || character.getActiveStackFormData() != original) return original;
            if (!character.hasActiveForm()) return original;

            FormOptionsAccess base = of(character.getActiveFormData());
            if (base == null) return original;
            if (!base.dmzfo$stackTintAffectsHairAndTail()) return null;

            double intensity = base.dmzfo$stackTintHairAndTailIntensity();
            if (intensity < 0.0) return original;
            if (intensity == 0.0) return null;

            FormOptionsAccess stack = of(original);
            if (stack == null) return original;
            FormConfig.FormData copy = stack.dmzfo$tintCopy(Math.min(1.0, intensity));
            return copy != null ? copy : original;
        } catch (RuntimeException e) {
            return original;
        }
    }

    /** Used by the redirects in the hair and race-parts renderers (call replaces {@code resolveTintForm}). */
    @Nullable
    public static FormConfig.FormData resolveHairTailTint(StatsData stats) {
        FormConfig.FormData original = DMZSkinLayer.resolveTintForm(stats);
        return hairTailTint(stats, original);
    }

    // ------------------------------------------------------------------ ki charge animation

    public static boolean suppressKiChargeAnimation(@Nullable Entity entity) {
        for (FormOptionsAccess form : active(characterOf(entity))) {
            if (!form.dmzfo$chargeKiAnimation()) return true;
        }
        return false;
    }
}
