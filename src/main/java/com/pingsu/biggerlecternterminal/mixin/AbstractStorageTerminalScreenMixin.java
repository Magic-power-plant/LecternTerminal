package com.pingsu.biggerlecternterminal.mixin;

import com.hollingsworth.arsnouveau.client.container.AbstractStorageTerminalScreen;
import com.hollingsworth.arsnouveau.client.container.StorageTerminalMenu;
import com.hollingsworth.arsnouveau.client.gui.NoShadowTextField;
import com.pingsu.biggerlecternterminal.client.TerminalLayout;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractStorageTerminalScreen.class, remap = false)
public abstract class AbstractStorageTerminalScreenMixin<T extends StorageTerminalMenu> extends AbstractContainerScreen<T> {
    @Shadow
    protected float currentScroll;

    @Shadow
    protected int rowCount;

    @Shadow
    protected boolean expanded;

    @Shadow
    protected NoShadowTextField searchField;

    protected AbstractStorageTerminalScreenMixin(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Shadow
    public abstract ResourceLocation getGui();

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void biggerlecternterminal$activateSearchOnlyWhenClicked(
            double mouseX,
            double mouseY,
            int mouseButton,
            CallbackInfoReturnable<Boolean> cir
    ) {
        boolean searchClicked = mouseX >= searchField.getX()
                && mouseY >= searchField.getY()
                && mouseX < searchField.getX() + searchField.getWidth()
                && mouseY < searchField.getY() + searchField.getHeight();
        searchField.active = searchClicked;
        searchField.setFocused(searchClicked);
        if (searchClicked) {
            setFocused(searchField);
        } else if (getFocused() == searchField) {
            setFocused(null);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void biggerlecternterminal$doNotAutoFocusSearchOnKeyPressed(
            int keyCode,
            int scanCode,
            int modifiers,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (searchField.canConsumeInput()) {
            cir.setReturnValue(searchField.keyPressed(keyCode, scanCode, modifiers));
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void biggerlecternterminal$doNotAutoFocusSearchOnCharTyped(
            char codePoint,
            int modifiers,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!searchField.canConsumeInput()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void biggerlecternterminal$dynamicMouseScrolled(
            double mouseX,
            double mouseY,
            double delta,
            CallbackInfoReturnable<Boolean> cir
    ) {
        int totalRows = (menu.itemListClientSorted.size() + TerminalLayout.COLUMNS - 1) / TerminalLayout.COLUMNS;
        int scrollRows = totalRows - rowCount;
        if (scrollRows <= 0) {
            cir.setReturnValue(false);
            return;
        }

        currentScroll = (float) (currentScroll - delta / scrollRows);
        currentScroll = Mth.clamp(currentScroll, 0.0F, 1.0F);
        menu.scrollTo(currentScroll);
        cir.setReturnValue(true);
    }

    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    private void biggerlecternterminal$renderDynamicBackground(
            GuiGraphics graphics,
            float partialTicks,
            int mouseX,
            int mouseY,
            CallbackInfo ci
    ) {
        TerminalLayout.renderTerminalBackground(
                graphics,
                getGui(),
                leftPos,
                topPos,
                imageWidth,
                imageHeight,
                rowCount,
                expanded
        );
        ci.cancel();
    }
}
