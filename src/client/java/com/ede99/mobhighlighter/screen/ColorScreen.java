package com.ede99.mobhighlighter.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

public class ColorScreen extends Screen {

    private final Screen parent;
    private final MobData mob;
    private int red, green, blue;

    public ColorScreen(Screen parent, MobData mob) {
        super(Text.of("Set Mob Color"));
        this.parent = parent;
        this.mob = mob;

        int color = mob.getColor();
        red = (color >> 16) & 0xFF;
        green = (color >> 8) & 0xFF;
        blue = color & 0xFF;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;

        // Preview box
        int previewSize = 80;
        int previewX = centerX - previewSize / 2;
        int previewY = startY - 100;

        // Red slider
        this.addDrawableChild(new SliderWidget(centerX - 120, startY, 240, 20, Text.of("Red"), red / 255f) {
            @Override
            protected void updateMessage() {
                setMessage(Text.of("Red: " + (int)(value * 255)));
            }
            @Override
            protected void applyValue() {
                red = (int)(value * 255);
            }
        });

        // Green slider
        this.addDrawableChild(new SliderWidget(centerX - 120, startY + 30, 240, 20, Text.of("Green"), green / 255f) {
            @Override
            protected void updateMessage() {
                setMessage(Text.of("Green: " + (int)(value * 255)));
            }
            @Override
            protected void applyValue() {
                green = (int)(value * 255);
            }
        });

        // Blue slider
        this.addDrawableChild(new SliderWidget(centerX - 120, startY + 60, 240, 20, Text.of("Blue"), blue / 255f) {
            @Override
            protected void updateMessage() {
                setMessage(Text.of("Blue: " + (int)(value * 255)));
            }
            @Override
            protected void applyValue() {
                blue = (int)(value * 255);
            }
        });

        // Save button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Save"), button -> {
            int newColor = (0xFF << 24) | (red << 16) | (green << 8) | blue;
            mob.setColor(newColor);
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(centerX - 60, startY + 110, 120, 25).build());

        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(Text.of("Cancel"), button -> {
            MinecraftClient.getInstance().setScreen(parent);
        }).dimensions(centerX - 60, startY + 145, 120, 25).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;

        // Title
        context.drawCenteredTextWithShadow(this.textRenderer, "Set Mob Color: " + mob.getName(), centerX, 20, 0xFFFFFF);

        // Color preview box
        int previewSize = 80;
        int previewX = centerX - previewSize / 2;
        int previewY = this.height / 2 - 160;
        int color = (0xFF << 24) | (red << 16) | (green << 8) | blue;
        context.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, color);

        super.render(context, mouseX, mouseY, delta);
    }
}
