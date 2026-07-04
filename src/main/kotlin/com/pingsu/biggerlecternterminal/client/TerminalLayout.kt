package com.pingsu.biggerlecternterminal.client

import com.hollingsworth.arsnouveau.ArsNouveau
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu
import com.pingsu.biggerlecternterminal.mixin.SlotAccessor
import com.pingsu.biggerlecternterminal.mixin.StorageTerminalMenuAccessor
import java.util.Collections
import java.util.WeakHashMap
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.Slot

object TerminalLayout {
    const val COLUMNS = 9
    const val COLLAPSED_ROWS = 3
    const val VANILLA_EXPANDED_ROWS = 7
    const val SLOT_STEP = 18

    const val IMAGE_WIDTH = 202
    const val MAIN_PANEL_WIDTH = 184
    const val SCROLLBAR_X = 184
    const val SCROLLBAR_WIDTH = IMAGE_WIDTH - SCROLLBAR_X
    const val VANILLA_IMAGE_HEIGHT = 248
    const val STORAGE_TOP = 21
    const val COLLAPSED_BOTTOM_SRC_Y = 75
    const val COLLAPSED_BOTTOM_HEIGHT = VANILLA_IMAGE_HEIGHT - COLLAPSED_BOTTOM_SRC_Y
    const val COLLAPSED_NON_ROW_HEIGHT = STORAGE_TOP + COLLAPSED_BOTTOM_HEIGHT
    const val EXPANDED_BOTTOM_SRC_Y = 147
    const val EXPANDED_BOTTOM_HEIGHT = VANILLA_IMAGE_HEIGHT - EXPANDED_BOTTOM_SRC_Y
    const val EXPANDED_NON_ROW_HEIGHT = STORAGE_TOP + EXPANDED_BOTTOM_HEIGHT
    const val SCREEN_MARGIN = 48

    const val CRAFT_RESULT_SLOT_X = 126
    const val CRAFT_RESULT_SLOT_Y = 107
    const val CRAFT_SLOT_LEFT = 32
    const val CRAFT_SLOT_TOP = 89
    const val PLAYER_SLOT_BASE_Y = 157
    const val PLAYER_HOTBAR_BASE_Y = 215

    private val searchPaper: ResourceLocation = ArsNouveau.prefix("textures/gui/search_paper.png")
    private val visibleRows: MutableMap<StorageTerminalMenu, Int> =
        Collections.synchronizedMap(WeakHashMap())

    fun expandedRowsForScreenHeight(screenHeight: Int): Int {
        val rows = (screenHeight - SCREEN_MARGIN - EXPANDED_NON_ROW_HEIGHT) / SLOT_STEP
        return maxOf(VANILLA_EXPANDED_ROWS, rows)
    }

    fun collapsedRowsForScreenHeight(screenHeight: Int): Int {
        val rows = (screenHeight - SCREEN_MARGIN - COLLAPSED_NON_ROW_HEIGHT) / SLOT_STEP
        return maxOf(COLLAPSED_ROWS, rows)
    }

    fun collapsedImageHeight(rows: Int): Int = COLLAPSED_NON_ROW_HEIGHT + rows * SLOT_STEP

    fun expandedImageHeight(rows: Int): Int = EXPANDED_NON_ROW_HEIGHT + rows * SLOT_STEP

    fun visibleRows(menu: StorageTerminalMenu, expanded: Boolean): Int =
        visibleRows.getOrDefault(menu, if (expanded) VANILLA_EXPANDED_ROWS else COLLAPSED_ROWS)

    fun setVisibleRows(menu: StorageTerminalMenu, rows: Int, expanded: Boolean) {
        visibleRows[menu] = maxOf(if (expanded) VANILLA_EXPANDED_ROWS else COLLAPSED_ROWS, rows)
    }

    fun updatePlayerInventorySlots(menu: StorageTerminalMenu, rows: Int, expanded: Boolean) {
        val offset = rowOffset(rows, expanded)
        val firstPlayerSlot =
            (menu as StorageTerminalMenuAccessor).`biggerlecternterminal$getPlayerSlotsStart`() + 1

        for (row in 0 until 3) {
            for (col in 0 until COLUMNS) {
                setSlotY(
                    menu.slots[firstPlayerSlot + row * COLUMNS + col],
                    PLAYER_SLOT_BASE_Y + offset + row * SLOT_STEP,
                )
            }
        }

        val hotbarStart = firstPlayerSlot + 3 * COLUMNS
        for (col in 0 until COLUMNS) {
            setSlotY(menu.slots[hotbarStart + col], PLAYER_HOTBAR_BASE_Y + offset)
        }
    }

    fun updateCraftingSlots(menu: StorageTerminalMenu, rows: Int, expanded: Boolean) {
        val offset = if (expanded) 0 else rowOffset(rows, false)
        setSlotPosition(menu.slots[0], CRAFT_RESULT_SLOT_X, CRAFT_RESULT_SLOT_Y + offset)

        for (row in 0 until 3) {
            for (col in 0 until 3) {
                setSlotPosition(
                    menu.slots[1 + row * 3 + col],
                    CRAFT_SLOT_LEFT + col * SLOT_STEP,
                    CRAFT_SLOT_TOP + offset + row * SLOT_STEP,
                )
            }
        }
    }

    private fun rowOffset(rows: Int, expanded: Boolean): Int =
        (rows - if (expanded) VANILLA_EXPANDED_ROWS else COLLAPSED_ROWS) * SLOT_STEP

    private fun setSlotPosition(slot: Slot, x: Int, y: Int) {
        val accessor = slot as SlotAccessor
        accessor.`biggerlecternterminal$setX`(x)
        accessor.`biggerlecternterminal$setY`(y)
    }

    private fun setSlotY(slot: Slot, y: Int) {
        (slot as SlotAccessor).`biggerlecternterminal$setY`(y)
    }

    fun renderTerminalBackground(
        graphics: GuiGraphics,
        texture: ResourceLocation,
        left: Int,
        top: Int,
        imageWidth: Int,
        imageHeight: Int,
        rows: Int,
        expanded: Boolean,
    ) {
        if (rows <= if (expanded) VANILLA_EXPANDED_ROWS else COLLAPSED_ROWS) {
            graphics.blit(texture, left, top, 0, 0, imageWidth, imageHeight)
        } else {
            graphics.blit(texture, left, top, 0.0f, 0.0f, IMAGE_WIDTH, STORAGE_TOP, 256, 256)

            var y = top + STORAGE_TOP
            for (row in 0 until rows) {
                graphics.blit(texture, left, y, 0.0f, STORAGE_TOP.toFloat(), MAIN_PANEL_WIDTH, SLOT_STEP, 256, 256)
                y += SLOT_STEP
            }

            val bottomSrcY = if (expanded) EXPANDED_BOTTOM_SRC_Y else COLLAPSED_BOTTOM_SRC_Y
            val bottomHeight = if (expanded) EXPANDED_BOTTOM_HEIGHT else COLLAPSED_BOTTOM_HEIGHT
            val vanillaRows = if (expanded) VANILLA_EXPANDED_ROWS else COLLAPSED_ROWS
            graphics.blit(
                texture,
                left + SCROLLBAR_X,
                top + STORAGE_TOP,
                SCROLLBAR_WIDTH,
                rows * SLOT_STEP,
                SCROLLBAR_X.toFloat(),
                STORAGE_TOP.toFloat(),
                SCROLLBAR_WIDTH,
                vanillaRows * SLOT_STEP,
                256,
                256,
            )

            graphics.blit(
                texture,
                left,
                y,
                0.0f,
                bottomSrcY.toFloat(),
                IMAGE_WIDTH,
                bottomHeight,
                256,
                256,
            )
        }

        graphics.blit(searchPaper, left + 102, top + 3, 0.0f, 0.0f, 72, 15, 72, 15)
    }
}
