package com.pingsu.biggerlecternterminal.mixin;

import com.hollingsworth.arsnouveau.client.container.SlotStorage;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.common.block.tile.StorageLecternTile;
import com.pingsu.biggerlecternterminal.client.TerminalLayout;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = StorageTerminalMenu.class, remap = false)
public abstract class StorageTerminalMenuMixin {
    @Shadow
    protected StorageLecternTile te;

    @Shadow
    protected List<SlotStorage> storageSlotList;

    /**
     * Rebuilds Ars Nouveau's virtual storage slots with a screen-controlled row count.
     */
    @Overwrite
    public void addStorageSlots(boolean expanded) {
        StorageTerminalMenu menu = (StorageTerminalMenu) (Object) this;
        int visibleRows = TerminalLayout.INSTANCE.visibleRows(menu, expanded);
        int slotCount = visibleRows * TerminalLayout.COLUMNS;

        for (int index = storageSlotList.size(); index < slotCount; index++) {
            int row = index / TerminalLayout.COLUMNS;
            int col = index % TerminalLayout.COLUMNS;
            storageSlotList.add(
                    new SlotStorage(
                            te,
                            index,
                            13 + col * TerminalLayout.SLOT_STEP,
                            TerminalLayout.STORAGE_TOP + row * TerminalLayout.SLOT_STEP,
                            true));
        }

        for (int index = 0; index < storageSlotList.size(); index++) {
            SlotStorage slot = storageSlotList.get(index);
            boolean show = index < slotCount;
            slot.show = show;
            if (!show) {
                slot.setStack(null);
            }
        }
    }
}
