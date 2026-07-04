package com.pingsu.biggerlecternterminal.mixin;

import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu;
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalScreen;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.pingsu.biggerlecternterminal.client.TerminalLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingTerminalScreen.class, remap = false)
public abstract class CraftingTerminalScreenMixin extends AbstractStorageTerminalScreen<CraftingTerminalMenu> {
    @Shadow
    public GuiImageButton btnClr;

    @Shadow
    public GuiImageButton btnRecipeBook;

    @Shadow
    public GuiImageButton btnExpand;

    @Shadow
    public GuiImageButton btnCollapse;

    protected CraftingTerminalScreenMixin(CraftingTerminalMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hollingsworth/arsnouveau/client/container/AbstractStorageTerminalScreen;init()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void biggerlecternterminal$beforeStorageInit(CallbackInfo ci) {
        biggerlecternterminal$applyDynamicRows();
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void biggerlecternterminal$afterInit(CallbackInfo ci) {
        biggerlecternterminal$positionCraftingButtons(rowCount);
    }

    @Inject(method = "collapseScreen", at = @At("RETURN"))
    private void biggerlecternterminal$afterCollapse(CallbackInfo ci) {
        biggerlecternterminal$applyDynamicRows();
    }

    @Inject(method = "expandScreen", at = @At("RETURN"))
    private void biggerlecternterminal$afterExpand(CallbackInfo ci) {
        biggerlecternterminal$applyDynamicRows();
    }

    private void biggerlecternterminal$applyDynamicRows() {
        int rows = expanded
                ? TerminalLayout.expandedRowsForScreenHeight(height)
                : TerminalLayout.collapsedRowsForScreenHeight(height);

        TerminalLayout.setVisibleRows(menu, rows, expanded);
        if (menu.terminalData != null) {
            menu.terminalData.expanded = expanded;
        }
        rowCount = rows;
        imageHeight = expanded
                ? TerminalLayout.expandedImageHeight(rows)
                : TerminalLayout.collapsedImageHeight(rows);
        topPos = (height - imageHeight) / 2;
        inventoryLabelY = imageHeight - 92;

        menu.addStorageSlots(13, TerminalLayout.STORAGE_TOP);
        TerminalLayout.updateCraftingSlots(menu, rows, expanded);
        TerminalLayout.updatePlayerInventorySlots(menu, rows, expanded);
        biggerlecternterminal$positionCraftingButtons(rows);
        menu.scrollTo(currentScroll);
    }

    private void biggerlecternterminal$positionCraftingButtons(int rows) {
        int storageBottom = topPos + TerminalLayout.STORAGE_TOP + rows * TerminalLayout.SLOT_STEP;
        if (btnClr != null) {
            btnClr.setPosition(leftPos + 86, storageBottom + 15);
        }
        if (btnRecipeBook != null) {
            btnRecipeBook.setPosition(leftPos + 98, storageBottom + 15);
        }
        if (btnExpand != null) {
            btnExpand.setPosition(leftPos + 86, storageBottom + 3);
            btnExpand.visible = !expanded;
        }
        if (btnCollapse != null) {
            btnCollapse.setPosition(leftPos + 86, storageBottom);
            btnCollapse.visible = expanded;
        }
    }
}
