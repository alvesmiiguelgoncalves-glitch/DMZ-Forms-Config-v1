package com.dmzformoptions.mixin;

import com.dragonminez.client.render.layer.DMZSkinLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Lets the tail redirect call DMZ's private {@code tintInPlace}. */
@Mixin(value = DMZSkinLayer.class, remap = false)
public interface DMZSkinLayerInvoker {

    @Invoker(value = "tintInPlace", remap = false)
    void dmzfo$tintInPlace(float[] rgb);
}
