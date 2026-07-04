package com.pingsu.biggerlecternterminal.mixin

import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalMenu
import com.hollingsworth.arsnouveau.client.container.CraftingTerminalScreen
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton
import com.pingsu.biggerlecternterminal.client.TerminalLayout
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(value = [CraftingTerminalScreen::class], remap = false)
abstract class CraftingTerminalScreenMixin private constructor(
    menu: CraftingTerminalMenu,
    playerInventory: Inventory,
    title: Component,
) : AbstractStorageTerminalScreen<CraftingTerminalMenu>(menu, playerInventory, title) {
    @field:Shadow
    var btnClr: GuiImageButton? = null

    @field:Shadow
    var btnRecipeBook: GuiImageButton? = null

    @field:Shadow
    var btnExpand: GuiImageButton? = null

    @field:Shadow
    var btnCollapse: GuiImageButton? = null

    @Inject(
        method = ["init"],
        at = [
            At(
                value = "INVOKE",
                target = "Lcom/hollingsworth/arsnouveau/client/container/AbstractStorageTerminalScreen;init()V",
                shift = At.Shift.BEFORE,
            ),
        ],
    )
    private fun `biggerlecternterminal$beforeStorageInit`(ci: CallbackInfo) {
        `biggerlecternterminal$applyDynamicRows`()
    }

    @Inject(method = ["onExpandedChanged"], at = [At("RETURN")])
    private fun `biggerlecternterminal$afterExpandedChanged`(expanded: Boolean, ci: CallbackInfo) {
        `biggerlecternterminal$applyDynamicRows`()
    }

    private fun `biggerlecternterminal$applyDynamicRows`() {
        val rows = if (expanded) {
            TerminalLayout.expandedRowsForScreenHeight(height)
        } else {
            TerminalLayout.collapsedRowsForScreenHeight(height)
        }

        TerminalLayout.setVisibleRows(menu, rows, expanded)
        rowCount = rows
        imageHeight = if (expanded) {
            TerminalLayout.expandedImageHeight(rows)
        } else {
            TerminalLayout.collapsedImageHeight(rows)
        }
        inventoryLabelY = imageHeight - 92

        menu.addStorageSlots(expanded)
        TerminalLayout.updateCraftingSlots(menu, rows, expanded)
        TerminalLayout.updatePlayerInventorySlots(menu, rows, expanded)
        `biggerlecternterminal$positionCraftingButtons`(rows)
        scrollTo(currentScroll)
    }

    private fun `biggerlecternterminal$positionCraftingButtons`(rows: Int) {
        val storageBottom = topPos + TerminalLayout.STORAGE_TOP + rows * TerminalLayout.SLOT_STEP
        btnClr?.setPosition(leftPos + 86, storageBottom + 15)
        btnRecipeBook?.setPosition(leftPos + 98, storageBottom + 15)
        btnExpand?.setPosition(leftPos + 86, storageBottom + 3)
        btnCollapse?.setPosition(leftPos + 86, storageBottom)
    }
}
