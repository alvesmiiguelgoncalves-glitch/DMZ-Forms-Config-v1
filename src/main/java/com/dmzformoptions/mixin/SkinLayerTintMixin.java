package com.dmzformoptions.mixin;

import com.dmzformoptions.util.FormOptions;
import com.dragonminez.client.render.layer.DMZSkinLayer;
import com.dragonminez.common.config.FormConfig;
import com.dragonminez.common.stats.StatsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The Saiyan tail on the body layer is tinted with the same tint as the skin. We remember, once per
 * player render, which tint the body got and which one hair/tail should get; the tail redirect in
 * {@link SkinLayerTailMixin} reads it.
 */
@Mixin(value = DMZSkinLayer.class, remap = false)
public abstract class SkinLayerTintMixin {

    @Redirect(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/player/AbstractClientPlayer;Lsoftware/bernie/geckolib/cache/object/BakedGeoModel;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;FII)V",
            remap = false,
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/dragonminez/client/render/layer/DMZSkinLayer;resolveTintForm(Lcom/dragonminez/common/stats/StatsData;)Lcom/dragonminez/common/config/FormConfig$FormData;"))
    private FormConfig.FormData dmzfo$bodyTint(StatsData stats) {
        FormConfig.FormData original = DMZSkinLayer.resolveTintForm(stats);
        FormOptions.bodyTint = original;
        FormOptions.hairTailTint = FormOptions.hairTailTint(stats, original);
        FormOptions.tintContextValid = true;
        return original;
    }
}
