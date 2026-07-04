package com.pingsu.biggerlecternterminal;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(Biggerlecternterminal.MODID)
public class Biggerlecternterminal {
    public static final String MODID = "biggerlecternterminal";

    private static final Logger LOGGER = LogUtils.getLogger();

    public Biggerlecternterminal() {
        LOGGER.info("Loaded {}", MODID);
    }
}
