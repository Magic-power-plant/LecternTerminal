package com.pingsu.biggerlecternterminal.mixin;

import com.hollingsworth.arsnouveau.client.container.SlotStorage;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = StorageTerminalMenu.class, remap = false)
public interface StorageTerminalMenuAccessor {
    @Accessor("playerSlotsStart")
    int biggerlecternterminal$getPlayerSlotsStart();

    @Accessor("storageSlotList")
    List<SlotStorage> biggerlecternterminal$getStorageSlotList();
}
