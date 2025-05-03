package com.eldritchvoid.modules.obsidianforgemaster.gui;

import com.eldritchvoid.EldritchVoid;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Screen for the Obsidian Forge crafting GUI.
 */
public class ForgeScreen extends AbstractContainerScreen<AbstractContainerMenu> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation TEXTURE = new ResourceLocation(EldritchVoid.MOD_ID, "textures/gui/obsidian_forge.png");
    
    private int progress = 0;
    private int maxProgress = 100;
    private int heat = 0;
    private int maxHeat = 100;
    
    /**
     * Constructor for the forge screen.
     * 
     * @param menu The container menu
     * @param inventory The player inventory
     * @param title The screen title
     */
    public ForgeScreen(AbstractContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        
        // Set dimensions
        this.imageWidth = 176;
        this.imageHeight = 166;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Initialize GUI elements
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        
        // Add buttons and other interactive elements
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        
        // Render tooltips for progress and heat bars
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        
        // Progress bar tooltip
        if (mouseX >= relX + 79 && mouseX <= relX + 103 && mouseY >= relY + 34 && mouseY <= relY + 38) {
            guiGraphics.renderTooltip(this.font, Component.literal(progress + "/" + maxProgress), mouseX, mouseY);
        }
        
        // Heat bar tooltip
        if (mouseX >= relX + 79 && mouseX <= relX + 103 && mouseY >= relY + 52 && mouseY <= relY + 56) {
            guiGraphics.renderTooltip(this.font, Component.literal("Heat: " + heat + "/" + maxHeat), mouseX, mouseY);
        }
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        // Calculate relative positions
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        
        // Render background
        guiGraphics.blit(TEXTURE, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
        
        // Render progress bar
        int progressPixels = (int)(((float)progress / maxProgress) * 24);
        guiGraphics.blit(TEXTURE, relX + 79, relY + 34, 176, 0, progressPixels, 4);
        
        // Render heat bar
        int heatPixels = (int)(((float)heat / maxHeat) * 24);
        guiGraphics.blit(TEXTURE, relX + 79, relY + 52, 176, 4, heatPixels, 4);
        
        // Render flame animation if forge is active
        if (heat > 0) {
            int flameHeight = Math.min(14, (int)(((float)heat / maxHeat) * 14));
            guiGraphics.blit(TEXTURE, relX + 56, relY + 36 + (14 - flameHeight), 176, 8 + (14 - flameHeight), 14, flameHeight);
        }
    }
    
    /**
     * Update the crafting progress.
     * 
     * @param progress The current progress
     * @param maxProgress The maximum progress
     */
    public void updateProgress(int progress, int maxProgress) {
        this.progress = progress;
        this.maxProgress = maxProgress;
    }
    
    /**
     * Update the forge heat.
     * 
     * @param heat The current heat
     * @param maxHeat The maximum heat
     */
    public void updateHeat(int heat, int maxHeat) {
        this.heat = heat;
        this.maxHeat = maxHeat;
    }
}
