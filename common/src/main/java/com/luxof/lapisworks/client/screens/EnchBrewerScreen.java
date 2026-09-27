package com.luxof.lapisworks.client.screens;

import com.luxof.lapisworks.blocks.entities.EnchBrewerEntity;

import static com.luxof.lapisworks.Lapisworks.id;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EnchBrewerScreen extends HandledScreen<EnchBrewerScreenHandler> {
    private static final Identifier TEXTURE = id("textures/gui/brewer/enchanted.png");

    public EnchBrewerScreen(EnchBrewerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private void drawIsBrewingIcon(DrawContext context, int x, int y) {
        int isBrewingIconX = x + 102;
        int isBrewingIconY = y + 30;

        int yesBrewingIconX = 176;
        int yesBrewingIconY = 10;
        context.drawTexture(
            TEXTURE,
            isBrewingIconX,
            isBrewingIconY,
            yesBrewingIconX,
            yesBrewingIconY,
            18,
            18
        );
    }

    private final int BREWING_ROD_HEIGHT = 10;
    private void fillSomeOfBrewingRod(
        DrawContext ctx,
        int rodU,
        int rodV,
        int filledU,
        int filledV,
        int accordingTo,
        int max
    ) {
        if (accordingTo <= 0) return;
        int rodX = this.x + rodU;
        int rodY = this.y + rodV;
        int filled = (int)Math.floor(accordingTo * ((double)BREWING_ROD_HEIGHT / (double)max));
        int offsetY = BREWING_ROD_HEIGHT - filled;
        ctx.drawTexture(
            TEXTURE,
            rodX,
            rodY + offsetY,
            filledU,
            filledV + offsetY,
            3,
            filled
        );
    }

    /*private int offsetBits(int bits, int howMany) { return bits * (int)Math.pow(2, howMany); }
    private int darken(int color) {
        int rangeOf8Bits = 0b1111111;
        int R = color & rangeOf8Bits;
        int G = color & offsetBits(rangeOf8Bits, 8);
        int B = color & offsetBits(rangeOf8Bits, 16);
        return (
            Math.max(R - 0x44, 0) +
            Math.max(G - offsetBits(0x44, 8), 0) +
            Math.max(B - offsetBits(0x44, 16), 0)
        );
    }

    // THIS SHIT DOESN'T FUCKING WORK AND I HAVE NOT A CLUE WHY
    private void drawBrewingRods(DrawContext ctx, int x, int y) {
        RenderSystem.disableDepthTest();
        ctx.getMatrices().push();
        ctx.getMatrices().translate(0.0F, 0.0F, 0.0F);

        RenderLayer renderLayer = RenderLayer.getGuiOverlay();

        int color = handler.getPotion1Color();
        LOGGER.info("color we got " + color);
        if (color != -1) {
            LOGGER.info("running.");
            int borderColor = darken(color);
            LOGGER.info("darkened: " + borderColor);
            ctx.drawHorizontalLine(renderLayer, x + 73, x + 77, y + 57, borderColor);
            ctx.drawHorizontalLine(renderLayer, x + 73, x + 77, y + 58, color);
            ctx.drawHorizontalLine(renderLayer, x + 73, x + 77, y + 59, borderColor);
        }
        color = handler.getPotion2Color();
        if (color != -1) {
            int borderColor = darken(color);
            ctx.drawVerticalLine(renderLayer, x + 86, y + 62, y + 62, borderColor);
            ctx.drawVerticalLine(renderLayer, x + 87, y + 62, y + 62, color);
            ctx.drawVerticalLine(renderLayer, x + 88, y + 62, y + 62, borderColor);
        }
        color = handler.getPotion3Color();
        if (color != -1) {
            int borderColor = darken(color);
            ctx.drawHorizontalLine(renderLayer, x + 86, x + 100, y + 57, borderColor);
            ctx.drawHorizontalLine(renderLayer, x + 86, x + 100, y + 58, color);
            ctx.drawHorizontalLine(renderLayer, x + 86, x + 100, y + 59, borderColor);
        }

        ctx.getMatrices().pop();
        RenderSystem.enableDepthTest();
    }*/

    private int x = 0;
    private int y = 0;
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (handler.isBrewing()) {
            drawIsBrewingIcon(context, x, y);
            //drawBrewingRods(context, x, y);
        }
        fillSomeOfBrewingRod(
            context,
            79, 34,
            176, 0,
            handler.getBrewTime(),
            EnchBrewerEntity.MAX_BREW_TIME
        );
        fillSomeOfBrewingRod(
            context,
            92, 34,
            179, 0,
            handler.getFuel(),
            EnchBrewerEntity.MAX_FUEL
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        drawBackground(context, delta, mouseX, mouseY);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
    
    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}
