package com.dmzformoptions.util;

import com.dragonminez.common.config.FormConfig;

import javax.annotation.Nullable;

/** Implemented on {@code FormConfig.FormData} by {@code FormDataMixin}. All getters are null-safe. */
public interface FormOptionsAccess {

    boolean dmzfo$forceTiedTail();

    boolean dmzfo$forceLooseTail();

    /** Default true (vanilla behavior). */
    boolean dmzfo$stackTintAffectsHairAndTail();

    /** Negative = use the stack form's own intensity. */
    double dmzfo$stackTintHairAndTailIntensity();

    /** RGB 0..1, or null when empty/invalid (= game default). */
    @Nullable
    float[] dmzfo$scleraRgb();

    boolean dmzfo$noEyebrows();

    boolean dmzfo$eyeliner();

    @Nullable
    float[] dmzfo$eyelinerRgb();

    /** Default true. */
    boolean dmzfo$chargeKiAnimation();

    /** A copy of this form whose tintIntensity is replaced (cached), or null if the copy failed. */
    @Nullable
    FormConfig.FormData dmzfo$tintCopy(double intensity);
}
