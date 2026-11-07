package com.dmzformoptions.mixin;

import com.dmzformoptions.util.FormOptions;
import com.dragonminez.client.render.layer.AuraTintTracker;
import com.dragonminez.common.config.FormConfig;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The Saiyan tail on the body layer is drawn by the anonymous body-layer sink inside DMZSkinLayer
 * ({@code DMZSkinLayer$1}). Its {@code saiyanTail} applies the skin tint; here we apply the
 * hair/tail version of it instead when it differs.
 */
@Mixin(targets = "com.dragonminez.client.render.layer.DMZSkinLayer$1", remap = false)
public abstract class SkinLayerTailMixin {

    @Redirect(method = "saiyanTail", remap = false,
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/dragonminez/client/render/layer/DMZSkinLayer;tintInPlace([F)V"))
    private void dmzfo$tailTint(com.dragonminez.client.render.layer.DMZSkinLayer<?> layer, float[] rgb) {
        FormConfig.FormData wanted = FormOptions.hairTailTint;
        if (!FormOptions.tintContextValid || wanted == FormOptions.bodyTint) {
            ((DMZSkinLayerInvoker) layer).dmzfo$tintInPlace(rgb);
            return;
        }
        if (wanted == null) return; // the form says: the tail is not tinted
        float[] color = wanted.getRgbTintColor();
        float intensity = (float) wanted.getTintIntensity();
        if (color == null || intensity <= 0.0f) return;
        float scaled = Mth.clamp(intensity, 0.0f, 1.0f) * AuraTintTracker.darkTintScale(rgb);
        rgb[0] = Mth.clamp(rgb[0] * (1.0f - scaled) + color[0] * scaled, 0.0f, 1.0f);
        rgb[1] = Mth.clamp(rgb[1] * (1.0f - scaled) + color[1] * scaled, 0.0f, 1.0f);
        rgb[2] = Mth.clamp(rgb[2] * (1.0f - scaled) + color[2] * scaled, 0.0f, 1.0f);
    }
}
