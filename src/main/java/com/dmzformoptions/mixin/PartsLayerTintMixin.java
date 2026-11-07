package com.dmzformoptions.mixin;

import com.dmzformoptions.util.FormOptions;
import com.dragonminez.client.render.layer.DMZRacePartsLayer;
import com.dragonminez.common.config.FormConfig;
import com.dragonminez.common.stats.StatsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Tail and other race parts drawn by the parts layer: same "hair/tail" tint as the hair. */
@Mixin(value = DMZRacePartsLayer.class, remap = false)
public abstract class PartsLayerTintMixin {

    @Redirect(method = {"renderRacePartsForAnchor", "renderOriginBones"}, remap = false,
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/dragonminez/client/render/layer/DMZSkinLayer;resolveTintForm(Lcom/dragonminez/common/stats/StatsData;)Lcom/dragonminez/common/config/FormConfig$FormData;"))
    private static FormConfig.FormData dmzfo$partsTint(StatsData stats) {
        return FormOptions.resolveHairTailTint(stats);
    }
}
