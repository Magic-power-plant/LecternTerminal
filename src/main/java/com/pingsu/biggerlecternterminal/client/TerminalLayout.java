package com.pingsu.biggerlecternterminal.client;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import com.pingsu.biggerlecternterminal.mixin.SlotAccessor;
import com.pingsu.biggerlecternterminal.mixin.StorageTerminalMenuAccessor;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public final class TerminalLayout {
    public static final int COLUMNS = 9;
    public static final int COLLAPSED_ROWS = 3;
    public static final int VANILLA_EXPANDED_ROWS = 7;
    public static final int SLOT_STEP = 18;

    public static final int IMAGE_WIDTH = 202;
    public static final int MAIN_PANEL_WIDTH = 184;
    public static final int SCROLLBAR_X = 184;
    public static final int SCROLLBAR_WIDTH = IMAGE_WIDTH - SCROLLBAR_X;
    public static final int VANILLA_IMAGE_HEIGHT = 248;
    public static final int STORAGE_TOP = 21;
    public static final int COLLAPSED_BOTTOM_SRC_Y = 75;
    public static final int COLLAPSED_BOTTOM_HEIGHT = VANILLA_IMAGE_HEIGHT - COLLAPSED_BOTTOM_SRC_Y;
    public static final int COLLAPSED_NON_ROW_HEIGHT = STORAGE_TOP + COLLAPSED_BOTTOM_HEIGHT;
    public static final int EXPANDED_BOTTOM_SRC_Y = 147;
    public static final int EXPANDED_BOTTOM_HEIGHT = VANILLA_IMAGE_HEIGHT - EXPANDED_BOTTOM_SRC_Y;
    public static final int EXPANDED_NON_ROW_HEIGHT = STORAGE_TOP + EXPANDED_BOTTOM_HEIGHT;
    public static final int SCREEN_MARGIN = 48;

    public static final int CRAFT_RESULT_SLOT_X = 126;
    public static final int CRAFT_RESULT_SLOT_Y = 107;
    public static final int CRAFT_SLOT_LEFT = 32;
    public static final int CRAFT_SLOT_TOP = 89;
    public static final int PLAYER_SLOT_BASE_Y = 157;
    public static final int PLAYER_HOTBAR_BASE_Y = 215;

    private static final ResourceLocation SEARCH_PAPER =
            ResourceLocation.fromNamespaceAndPath(ArsNouveau.MODID, "textures/gui/search_paper.png");
    private static final Map<StorageTerminalMenu, Integer> VISIBLE_ROWS =
            Collections.synchronizedMap(new WeakHashMap<>());

    private TerminalLayout() {
    }

    public static int expandedRowsForScreenHeight(int screenHeight) {
        int rows = (screenHeight - SCREEN_MARGIN - EXPANDED_NON_ROW_HEIGHT) / SLOT_STEP;
        return Math.max(VANILLA_EXPANDED_ROWS, rows);
    }

    public static int collapsedRowsForScreenHeight(int screenHeight) {
        int rows = (screenHeight - SCREEN_MARGIN - COLLAPSED_NON_ROW_HEIGHT) / SLOT_STEP;
        return Math.max(COLLAPSED_ROWS, rows);
    }

    public static int collapsedImageHeight(int rows) {
        return COLLAPSED_NON_ROW_HEIGHT + rows * SLOT_STEP;
    }

    public static int expandedImageHeight(int rows) {
        return EXPANDED_NON_ROW_HEIGHT + rows * SLOT_STEP;
    }

    public static int visibleRows(StorageTerminalMenu menu, boolean expanded) {
        return VISIBLE_ROWS.getOrDefault(menu, expanded ? VANILLA_EXPANDED_ROWS : COLLAPSED_ROWS);
    }

    public static void setVisibleRows(StorageTerminalMenu menu, int rows, boolean expanded) {
        VISIBLE_ROWS.put(menu, Math.max(expanded ? VANILLA_EXPANDED_ROWS : COLLAPSED_ROWS, rows));
    }

    public static void updatePlayerInventorySlots(StorageTerminalMenu menu, int rows, boolean expanded) {
        int offset = rowOffset(rows, expanded);
        int firstPlayerSlot = ((StorageTerminalMenuAccessor) menu).biggerlecternterminal$getPlayerSlotsStart() + 1;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                setSlotY(menu.slots.get(firstPlayerSlot + row * COLUMNS + col),
                        PLAYER_SLOT_BASE_Y + offset + row * SLOT_STEP);
            }
        }

        int hotbarStart = firstPlayerSlot + 3 * COLUMNS;
        for (int col = 0; col < COLUMNS; col++) {
            setSlotY(menu.slots.get(hotbarStart + col), PLAYER_HOTBAR_BASE_Y + offset);
        }
    }

    public static void updateCraftingSlots(StorageTerminalMenu menu, int rows, boolean expanded) {
        int offset = expanded ? 0 : rowOffset(rows, false);
        setSlotPosition(menu.slots.get(0), CRAFT_RESULT_SLOT_X, CRAFT_RESULT_SLOT_Y + offset);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                setSlotPosition(menu.slots.get(1 + row * 3 + col),
                        CRAFT_SLOT_LEFT + col * SLOT_STEP,
                        CRAFT_SLOT_TOP + offset + row * SLOT_STEP);
            }
        }
    }

    private static int rowOffset(int rows, boolean expanded) {
        return (rows - (expanded ? VANILLA_EXPANDED_ROWS : COLLAPSED_ROWS)) * SLOT_STEP;
    }

    private static void setSlotPosition(Slot slot, int x, int y) {
        SlotAccessor accessor = (SlotAccessor) slot;
        accessor.biggerlecternterminal$setX(x);
        accessor.biggerlecternterminal$setY(y);
    }

    private static void setSlotY(Slot slot, int y) {
        ((SlotAccessor) slot).biggerlecternterminal$setY(y);
    }

    public static void renderTerminalBackground(
            GuiGraphics graphics,
            ResourceLocation texture,
            int left,
            int top,
            int imageWidth,
            int imageHeight,
            int rows,
            boolean expanded
    ) {
        if (rows <= (expanded ? VANILLA_EXPANDED_ROWS : COLLAPSED_ROWS)) {
            graphics.blit(texture, left, top, 0, 0, imageWidth, imageHeight);
        } else {
            graphics.blit(texture, left, top, 0.0F, 0.0F, IMAGE_WIDTH, STORAGE_TOP, 256, 256);

            int y = top + STORAGE_TOP;
            for (int row = 0; row < rows; row++) {
                graphics.blit(texture, left, y, 0.0F, STORAGE_TOP, MAIN_PANEL_WIDTH, SLOT_STEP, 256, 256);
                y += SLOT_STEP;
            }

            int bottomSrcY = expanded ? EXPANDED_BOTTOM_SRC_Y : COLLAPSED_BOTTOM_SRC_Y;
            int bottomHeight = expanded ? EXPANDED_BOTTOM_HEIGHT : COLLAPSED_BOTTOM_HEIGHT;
            int vanillaRows = expanded ? VANILLA_EXPANDED_ROWS : COLLAPSED_ROWS;
            graphics.blit(
                    texture,
                    left + SCROLLBAR_X,
                    top + STORAGE_TOP,
                    SCROLLBAR_WIDTH,
                    rows * SLOT_STEP,
                    SCROLLBAR_X,
                    STORAGE_TOP,
                    SCROLLBAR_WIDTH,
                    vanillaRows * SLOT_STEP,
                    256,
                    256
            );

            graphics.blit(texture, left, y, 0.0F, bottomSrcY, IMAGE_WIDTH, bottomHeight, 256, 256);
        }

        graphics.blit(SEARCH_PAPER, left + 102, top + 3, 0.0F, 0.0F, 72, 15, 72, 15);
    }
}
