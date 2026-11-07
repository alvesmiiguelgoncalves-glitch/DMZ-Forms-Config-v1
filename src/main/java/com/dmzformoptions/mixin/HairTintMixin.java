package com.dmzformoptions.mixin;

import com.dmzformoptions.util.FormOptions;
import com.dragonminez.client.render.hair.HairStyleResolver;
import com.dragonminez.common.config.FormConfig;
import com.dragonminez.common.stats.StatsData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Hair color tint: use the "hair/tail" version of the tint form (see {@link FormOptions#hairTailTint}). */
@Mixin(value = HairStyleResolver.class, remap = false)
public abstract class HairTintMixin {

    @Redirect(method = "resolve", remap = false,
            at = @At(value = "INVOKE", remap = false,
                    target = "Lcom/dragonminez/client/render/layer/DMZSkinLayer;resolveTintForm(Lcom/dragonminez/common/stats/StatsData;)Lcom/dragonminez/common/config/FormConfig$FormData;"))
    private static FormConfig.FormData dmzfo$hairTint(StatsData stats) {
        return FormOptions.resolveHairTailTint(stats);
    }
}
