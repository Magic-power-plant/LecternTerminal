package com.pingsu.biggerlecternterminal

import net.neoforged.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(Biggerlecternterminal.ID)
object Biggerlecternterminal {
    const val ID = "biggerlecternterminal"

    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.info("Loaded {}", ID)
    }
}
