package com.pingsu.biggerlecternterminal.mixin

import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu
import com.hollingsworth.arsnouveau.client.container.StoredItemStack
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField
import com.hollingsworth.arsnouveau.setup.config.Config
import com.pingsu.biggerlecternterminal.client.TerminalLayout
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Constant
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.ModifyConstant
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(value = [AbstractStorageTerminalScreen::class], remap = false)
abstract class AbstractStorageTerminalScreenMixin<T : StorageTerminalMenu> private constructor(
    menu: T,
    playerInventory: Inventory,
    title: Component,
) : AbstractContainerScreen<T>(menu, playerInventory, title) {
    @field:Shadow
    protected var currentScroll: Float = 0.0f

    @field:Shadow
    protected var rowCount: Int = 0

    @field:Shadow
    protected var expanded: Boolean = false

    @field:Shadow
    protected lateinit var itemsSorted: List<StoredItemStack>

    @field:Shadow
    protected lateinit var searchField: NoShadowTextField

    @Shadow
    abstract fun getGui(): ResourceLocation

    @Shadow
    abstract fun scrollTo(scroll: Float)

    @Inject(method = ["mouseClicked"], at = [At("HEAD")])
    private fun `biggerlecternterminal$activateSearchOnlyWhenClicked`(
        mouseX: Double,
        mouseY: Double,
        mouseButton: Int,
        cir: CallbackInfoReturnable<Boolean>,
    ) {
        val searchClicked = mouseX >= searchField.x &&
            mouseY >= searchField.y &&
            mouseX < searchField.x + searchField.width &&
            mouseY < searchField.y + searchField.height
        searchField.active = searchClicked
        searchField.isFocused = searchClicked
        if (searchClicked) {
            setFocused(searchField)
        } else if (getFocused() == searchField) {
            clearFocus()
        }
    }

    @Inject(method = ["keyPressed"], at = [At("HEAD")], cancellable = true)
    private fun `biggerlecternterminal$doNotAutoFocusSearchOnKeyPressed`(
        keyCode: Int,
        scanCode: Int,
        modifiers: Int,
        cir: CallbackInfoReturnable<Boolean>,
    ) {
        if (searchField.canConsumeInput()) {
            cir.returnValue = searchField.keyPressed(keyCode, scanCode, modifiers)
        }
    }

    @Inject(method = ["charTyped"], at = [At("HEAD")], cancellable = true)
    private fun `biggerlecternterminal$doNotAutoFocusSearchOnCharTyped`(
        codePoint: Char,
        modifiers: Int,
        cir: CallbackInfoReturnable<Boolean>,
    ) {
        if (!searchField.canConsumeInput()) {
            cir.returnValue = false
        }
    }

    @ModifyConstant(method = ["scrollTo"], constant = [Constant(intValue = 3)])
    private fun `biggerlecternterminal$collapsedScrollRows`(original: Int): Int =
        maxOf(TerminalLayout.COLLAPSED_ROWS, rowCount)

    @ModifyConstant(method = ["scrollTo"], constant = [Constant(intValue = 7)])
    private fun `biggerlecternterminal$expandedScrollRows`(original: Int): Int =
        maxOf(TerminalLayout.VANILLA_EXPANDED_ROWS, rowCount)

    @Inject(method = ["mouseScrolled"], at = [At("HEAD")], cancellable = true)
    private fun `biggerlecternterminal$dynamicMouseScrolled`(
        mouseX: Double,
        mouseY: Double,
        deltaX: Double,
        deltaY: Double,
        cir: CallbackInfoReturnable<Boolean>,
    ) {
        val totalRows = (itemsSorted.size + TerminalLayout.COLUMNS - 1) / TerminalLayout.COLUMNS
        val scrollRows = totalRows - rowCount
        if (scrollRows <= 0) {
            cir.returnValue = false
            return
        }

        val scrollDelta = if (Config.INVERT_LECTERN_SCROLLING.getAsBoolean()) -deltaY else deltaY
        currentScroll = (currentScroll + scrollDelta / scrollRows).toFloat()
        currentScroll = Mth.clamp(currentScroll, 0.0f, 1.0f)
        scrollTo(currentScroll)
        cir.returnValue = true
    }

    @Inject(method = ["renderBg"], at = [At("HEAD")], cancellable = true)
    private fun `biggerlecternterminal$renderDynamicBackground`(
        graphics: GuiGraphics,
        partialTicks: Float,
        mouseX: Int,
        mouseY: Int,
        ci: CallbackInfo,
    ) {
        TerminalLayout.renderTerminalBackground(
            graphics,
            getGui(),
            leftPos,
            topPos,
            imageWidth,
            imageHeight,
            rowCount,
            expanded,
        )
        ci.cancel()
    }
}
