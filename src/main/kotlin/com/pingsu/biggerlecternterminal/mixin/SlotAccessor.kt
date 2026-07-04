package com.pingsu.biggerlecternterminal.mixin

import net.minecraft.world.inventory.Slot
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Mutable
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(Slot::class)
interface SlotAccessor {
    @Mutable
    @Accessor("x")
    fun `biggerlecternterminal$setX`(x: Int)

    @Mutable
    @Accessor("y")
    fun `biggerlecternterminal$setY`(y: Int)
}
