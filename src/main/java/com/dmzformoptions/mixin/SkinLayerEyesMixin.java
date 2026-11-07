package com.dmzformoptions.mixin;

import com.dmzformoptions.util.FormOptions;
import com.dragonminez.Reference;
import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.common.stats.character.Character;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.BakedGeoModel;

/**
 * scleraColor / noEyebrows / eyeliner for the human-style face ({@code renderHumanEyes}).
 *
 * <p>Only when the active form sets one of these options we draw the eyes ourselves (same layers as
 * DMZ, in the same order) and cancel the original. Otherwise DMZ's code runs untouched.</p>
 *
 * <ul>
 *   <li>sclera: layer {@code _0} is drawn with the form's color instead of white;</li>
 *   <li>noEyebrows: draws DMZ's {@code ssj3eyebrows_eye_N} layer (the one that hides the eyebrows in SSJ3);</li>
 *   <li>eyeliner: draws DMZ's {@code ssj4_eyes_N} layer (the SSJ4 eye outline) with {@code eyelinerColor}.</li>
 * </ul>
 */
@Mixin(value = DMZSkinLayer.class, remap = false)
public abstract class SkinLayerEyesMixin {

    private static final String FACE_FOLDER = "textures/entity/races/humansaiyan/faces/";

    @Shadow
    protected abstract void renderColoredLayer(BakedGeoModel model, PoseStack poseStack, AbstractClientPlayer animatable,
                                               MultiBufferSource bufferSource, String path, float[] rgb,
                                               float partialTick, int packedLight, int packedOverlay, float alpha);

    @Inject(method = "renderHumanEyes", at = @At("HEAD"), cancellable = true, remap = false)
    private void dmzfo$humanEyes(BakedGeoModel model, PoseStack poseStack, AbstractClientPlayer animatable,
                                 MultiBufferSource bufferSource, Character character, int eyeType,
                                 float[] sclera, float[] eye1, float[] eye2, float[] brow, float[] ssj3Brow,
                                 float pt, int pl, int po, float alpha, CallbackInfo ci) {
        FormOptions.EyeOptions options = FormOptions.eyes(character);
        if (options == null) return;

        String eyeBase = FACE_FOLDER + "humansaiyan_eye_" + eyeType;
        String fallback = FACE_FOLDER + "humansaiyan_eye_0";
        float[] scleraColor = options.sclera() != null ? options.sclera() : sclera;

        renderColoredLayer(model, poseStack, animatable, bufferSource, safe(eyeBase + "_0.png", fallback + "_0.png"), scleraColor, pt, pl, po, alpha);
        renderColoredLayer(model, poseStack, animatable, bufferSource, safe(eyeBase + "_1.png", fallback + "_1.png"), eye1, pt, pl, po, alpha);
        renderColoredLayer(model, poseStack, animatable, bufferSource, safe(eyeBase + "_2.png", fallback + "_2.png"), eye2, pt, pl, po, alpha);
        renderColoredLayer(model, poseStack, animatable, bufferSource, safe(eyeBase + "_3.png", fallback + "_3.png"), brow, pt, pl, po, alpha);

        boolean ssj3 = (character.hasActiveStackForm() && character.getActiveStackFormData() != null
                && character.getActiveStackFormData().getHairType().equalsIgnoreCase("ssj3"))
                || (character.hasActiveForm() && character.getActiveFormData() != null
                && character.getActiveFormData().getHairType().equalsIgnoreCase("ssj3"));
        if (ssj3 || options.noEyebrows()) {
            renderColoredLayer(model, poseStack, animatable, bufferSource, FACE_FOLDER + "ssj3eyebrows_eye_" + eyeType + ".png", ssj3Brow, pt, pl, po, alpha);
        }

        if (options.eyeliner() != null) {
            String linerPath = FACE_FOLDER + "ssj4_eyes_" + eyeType + ".png";
            ResourceLocation liner = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, linerPath);
            if (DMZSkinLayer.getSafeTexture(liner).equals(liner)) {
                renderColoredLayer(model, poseStack, animatable, bufferSource, linerPath, options.eyeliner(), pt, pl, po, alpha);
            }
        }
        ci.cancel();
    }

    private static String safe(String path, String fallbackPath) {
        return DMZSkinLayer.getSafeTexture(
                ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, path),
                ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, fallbackPath)).getPath();
    }
}
