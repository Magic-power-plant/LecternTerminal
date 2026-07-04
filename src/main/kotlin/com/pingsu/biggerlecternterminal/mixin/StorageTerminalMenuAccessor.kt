package com.pingsu.biggerlecternterminal.mixin

import com.hollingsworth.arsnouveau.client.container.SlotStorage
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.gen.Accessor

@Mixin(value = [StorageTerminalMenu::class], remap = false)
interface StorageTerminalMenuAccessor {
    @Accessor("playerSlotsStart")
    fun `biggerlecternterminal$getPlayerSlotsStart`(): Int

    @Accessor("storageSlotList")
    fun `biggerlecternterminal$getStorageSlotList`(): MutableList<SlotStorage>
}
