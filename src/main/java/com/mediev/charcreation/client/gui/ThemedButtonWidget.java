package com.mediev.charcreation.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ThemedButtonWidget extends ButtonWidget {
    private float hoverProgress;

    protected ThemedButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
    }

    public static ThemedButtonWidget create(int x, int y, int width, int height, Text message, PressAction onPress) {
        return new ThemedButtonWidget(x, y, width, height, message, onPress);
    }

    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        boolean hovered = this.isHovered();
        hoverProgress += (hovered ? 1f : -1f) * delta * 0.15f;
        hoverProgress = Math.max(0f, Math.min(1f, hoverProgress));

        int baseColor = this.active ? MedievalTheme.STONE_COLOR : 0xFF1A1A1A;
        int borderColor = this.active
                ? interpolateColor(MedievalTheme.GOLD_ACCENT, MedievalTheme.GOLD_ACCENT_BRIGHT, hoverProgress)
                : 0xFF4A4A4A;

        context.fill(getX(), getY(), getX() + width, getY() + height, baseColor);
        context.fill(getX(), getY(), getX() + width, getY() + 1, borderColor);
        context.fill(getX(), getY() + height - 1, getX() + width, getY() + height, borderColor);
        context.fill(getX(), getY(), getX() + 1, getY() + height, borderColor);
        context.fill(getX() + width - 1, getY(), getX() + width, getY() + height, borderColor);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textColor = this.active ? MedievalTheme.TEXT_COLOR : MedievalTheme.TEXT_MUTED;
        int textX = getX() + (width - textRenderer.getWidth(getMessage())) / 2;
        int textY = getY() + (height - 8) / 2;
        context.drawText(textRenderer, getMessage(), textX, textY, textColor, false);
    }

    private static int interpolateColor(int from, int to, float progress) {
        int fromA = (from >> 24) & 0xFF, fromR = (from >> 16) & 0xFF, fromG = (from >> 8) & 0xFF, fromB = from & 0xFF;
        int toA = (to >> 24) & 0xFF, toR = (to >> 16) & 0xFF, toG = (to >> 8) & 0xFF, toB = to & 0xFF;
        int a = (int) (fromA + (toA - fromA) * progress);
        int r = (int) (fromR + (toR - fromR) * progress);
        int g = (int) (fromG + (toG - fromG) * progress);
        int b = (int) (fromB + (toB - fromB) * progress);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}