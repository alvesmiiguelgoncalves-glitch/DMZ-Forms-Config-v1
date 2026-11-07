package com.dmzformoptions;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * DragonMine Z addon: new options for the transformation JSON files
 * (tail, Kaio-ken tint on hair/tail, sclera color, eyebrows, eyeliner, ki-charge animation).
 * Everything is done with mixins; this class intentionally does nothing else.
 */
@Mod(DmzFormOptions.MOD_ID)
public class DmzFormOptions {

    public static final String MOD_ID = "dmz_form_options";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DmzFormOptions() {
    }
}
