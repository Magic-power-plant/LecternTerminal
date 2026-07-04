package com.pingsu.biggerlecternterminal.mixin;

import com.hollingsworth.arsnouveau.client.container.SlotStorage;
import com.hollingsworth.arsnouveau.client.container.SortSettings;
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

    @Shadow
    public SortSettings terminalData;

    @Shadow
    private int lines;

    /**
     * Rebuilds Ars Nouveau's virtual storage slots with a screen-controlled row count.
     *
     * @author pingsu
     * @reason Ars Nouveau 1.20.1 stores the visible virtual slot count in this method.
     */
    @Overwrite
    public void addStorageSlots(int x, int y) {
        StorageTerminalMenu menu = (StorageTerminalMenu) (Object) this;
        boolean expanded = terminalData != null && terminalData.expanded;
        int visibleRows = TerminalLayout.visibleRows(menu, expanded);

        storageSlotList.clear();
        lines = visibleRows;
        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < TerminalLayout.COLUMNS; col++) {
                int index = row * TerminalLayout.COLUMNS + col;
                storageSlotList.add(new SlotStorage(
                        te,
                        index,
                        x + col * TerminalLayout.SLOT_STEP,
                        y + row * TerminalLayout.SLOT_STEP
                ));
            }
        }

        menu.scrollTo(0.0F);
    }
}
