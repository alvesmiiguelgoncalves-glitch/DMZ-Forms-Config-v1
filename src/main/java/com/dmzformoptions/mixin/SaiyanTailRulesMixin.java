package com.dmzformoptions.mixin;

import com.dmzformoptions.util.FormOptions;
import com.dragonminez.client.util.SaiyanTailRules;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * forceTiedTail / forceLooseTail: every tail check in DMZ goes through {@code wrapAmount(entity, tailOut)}
 * (tailOut = "tail is out"). We swap {@code tailOut} when the active form asks for it; DMZ then animates
 * the wrap/unwrap by itself.
 */
@Mixin(value = SaiyanTailRules.class, remap = false)
public abstract class SaiyanTailRulesMixin {

    @Inject(method = "wrapAmount", at = @At("HEAD"), cancellable = true, remap = false)
    private static void dmzfo$wrapAmount(Entity entity, boolean tailOut, CallbackInfoReturnable<Float> cir) {
        if (FormOptions.tailReentry) return;
        Boolean forced = FormOptions.tailOverride(entity);
        if (forced == null || forced == tailOut) return;
        FormOptions.tailReentry = true;
        try {
            cir.setReturnValue(SaiyanTailRules.wrapAmount(entity, forced));
        } finally {
            FormOptions.tailReentry = false;
        }
    }
}
