package com.ede99.mobhighlighter.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import com.ede99.mobhighlighter.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MobHighlighterScreen extends Screen {
    private final List<ButtonWidget> mobButtons = new ArrayList<>();
    private final List<MobData> allMobs = DefaultMobRegistry.createDefaultMobs();
    private List<MobData> filteredMobs = new ArrayList<>();
    private float scrollOffset = 0;
    private final int panelWidth = 230;
    private final int panelPadding = 5;
    private final int entryHeight = 40;
    private int panelY1 = 80; // Moved down to make space for search
    private int panelY2;
    private int panelX = 10;
    private final int visibleButtons = 11;
    private ButtonWidget closeButton;
    private ButtonWidget infoButton;
    private TextFieldWidget searchField;
    private boolean showInfoPanel = false;

    public MobHighlighterScreen() {
        super(Text.of("Mob ESP Menu"));
        filteredMobs.addAll(allMobs);
    }

    @Override
    protected void init() {
        mobButtons.clear();
        panelY2 = panelY1 + visibleButtons * entryHeight + 10;

        panelX = 10; // bal oldali panel

        // Search field at the top with custom styling
        this.searchField = new TextFieldWidget(
                this.textRenderer,
                this.width / 2 - 100,
                50,
                200,
                20,
                Text.of("Search mobs...")
        ) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                // Draw white background
                context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFFFFFFFF);
                // Draw border
                context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, 0xFF000000);
                context.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width, this.getY() + this.height, 0xFF000000);
                context.fill(this.getX(), this.getY(), this.getX() + 1, this.getY() + this.height, 0xFF000000);
                context.fill(this.getX() + this.width - 1, this.getY(), this.getX() + this.width, this.getY() + this.height, 0xFF000000);

                // Draw text with proper color and visibility
                String text = this.getText();
                int textX = this.getX() + 4;
                int textY = this.getY() + (this.height - 8) / 2;

                // Draw placeholder text when empty and not focused
                if (text.isEmpty() && !this.isFocused()) {
                    context.drawTextWithShadow(MobHighlighterScreen.this.textRenderer, "Search mobs...", textX, textY, 0xFF888888);
                } else if (!text.isEmpty()) {
                    // Draw the actual text with lighter color for better readability
                    context.drawTextWithShadow(MobHighlighterScreen.this.textRenderer, text, textX, textY, 0xFF333333);

                    // Draw cursor when focused
                    if (this.isFocused()) {
                        int cursorX = textX + MobHighlighterScreen.this.textRenderer.getWidth(text);
                        context.fill(cursorX, textY - 1, cursorX + 1, textY + 9, 0xFF000000);
                    }
                } else if (this.isFocused()) {
                    // Draw cursor even when text is empty but focused
                    context.fill(textX, textY - 1, textX + 1, textY + 9, 0xFF000000);
                }
            }
        };

        this.searchField.setChangedListener(this::onSearchChanged);
        this.searchField.setMaxLength(50);
        this.addDrawableChild(this.searchField);

        for (int i = 0; i < filteredMobs.size(); i++) {
            MobData mob = filteredMobs.get(i);
            mob.setEnabled(Config.getState(mob.getName(), mob.isEnabled()));

            ButtonWidget button = ButtonWidget.builder(
                    Text.of(""),
                    btn -> {
                        if (Screen.hasShiftDown()) {
                            MinecraftClient.getInstance().setScreen(new ColorScreen(this, mob));
                        } else {
                            mob.setEnabled(!mob.isEnabled());
                            Config.setState(mob.getName(), mob.isEnabled());
                        }
                    }
            ).dimensions(panelX + panelPadding, panelY1 + i * entryHeight, panelWidth - 2 * panelPadding, entryHeight - 2).build();

            mobButtons.add(button);
            this.addDrawableChild(button);
        }

        // Info button above the close button
        int infoY = this.height - 55;
        infoButton = ButtonWidget.builder(
                Text.of("Info"),
                btn -> this.showInfoPanel = !this.showInfoPanel
        ).dimensions(this.width / 2 - 50, infoY, 100, 20).build();

        this.addDrawableChild(infoButton);

        // Close button at the bottom of the screen
        int closeY = this.height - 30;
        closeButton = ButtonWidget.builder(
                Text.of("Close"),
                btn -> {
                    Config.saveAll(); // Save all changes when closing
                    this.close();
                }
        ).dimensions(this.width / 2 - 50, closeY, 100, 20).build();

        this.addDrawableChild(closeButton);

        ScreenMouseEvents.beforeMouseScroll(this).register((screen, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
            if (verticalAmount != 0 && isMouseOverScrollArea(mouseX, mouseY)) {
                this.handleMouseScroll(verticalAmount);
            }
        });
    }

    private void onSearchChanged(String searchText) {
        filteredMobs.clear();

        if (searchText.isEmpty()) {
            filteredMobs.addAll(allMobs);
        } else {
            String lowercaseSearch = searchText.toLowerCase(Locale.ROOT);
            for (MobData mob : allMobs) {
                if (mob.getName().toLowerCase(Locale.ROOT).contains(lowercaseSearch)) {
                    filteredMobs.add(mob);
                }
            }
        }

        // Reset scroll when search changes
        scrollOffset = 0;

        // Recreate only the mob buttons, not the entire UI
        recreateMobButtons();
    }

    private void recreateMobButtons() {
        // Remove only mob buttons, keep search field and other UI elements
        for (ButtonWidget button : mobButtons) {
            this.remove(button);
        }
        mobButtons.clear();

        // Recreate mob buttons
        for (int i = 0; i < filteredMobs.size(); i++) {
            MobData mob = filteredMobs.get(i);
            mob.setEnabled(Config.getState(mob.getName(), mob.isEnabled()));

            ButtonWidget button = ButtonWidget.builder(
                    Text.of(""),
                    btn -> {
                        if (Screen.hasShiftDown()) {
                            MinecraftClient.getInstance().setScreen(new ColorScreen(this, mob));
                        } else {
                            mob.setEnabled(!mob.isEnabled());
                            Config.setState(mob.getName(), mob.isEnabled());
                        }
                    }
            ).dimensions(panelX + panelPadding, panelY1 + i * entryHeight, panelWidth - 2 * panelPadding, entryHeight - 2).build();

            mobButtons.add(button);
            this.addDrawableChild(button);
        }
    }

    private boolean isMouseOverScrollArea(double mouseX, double mouseY) {
        return mouseX >= panelX && mouseX <= panelX + panelWidth &&
                mouseY >= panelY1 && mouseY <= panelY2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render background
        this.renderBackground(context, mouseX, mouseY, delta);

        // If info panel is open, only render the info panel
        if (showInfoPanel) {
            renderInfoPanel(context, mouseX, mouseY, delta);
            return;
        }

        // Normal UI rendering when info panel is closed
        int x1 = panelX;
        int y1 = panelY1;
        int x2 = x1 + panelWidth;
        int y2 = panelY2;

        // Panel háttér és border
        context.fill(x1, y1, x2, y2, 0xFF444444);

        int borderColor = 0xFFFFFFFF;
        for (int i = 0; i < 3; i++) {
            context.fill(x1 + i, y1 + i, x2 - i, y1 + i + 1, borderColor);
            context.fill(x1 + i, y2 - 1 - i, x2 - i, y2 - i, borderColor);
            context.fill(x1 + i, y1 + i, x1 + i + 1, y2 - i, borderColor);
            context.fill(x2 - 1 - i, y1 + i, x2 - i, y2 - i, borderColor);
        }

        // Scissor csak a görgethető területre
        context.enableScissor(x1, y1, x2, y2);

        for (int i = 0; i < mobButtons.size(); i++) {
            ButtonWidget button = mobButtons.get(i);
            int newY = panelY1 + i * entryHeight - (int) scrollOffset;
            button.setY(newY);
            button.setX(panelX + panelPadding);
        }

        // Render mob buttons first within scissor area
        for (ButtonWidget button : mobButtons) {
            button.render(context, mouseX, mouseY, delta);
        }

        // Render mob names and status within scissor area
        for (int i = 0; i < mobButtons.size(); i++) {
            ButtonWidget button = mobButtons.get(i);
            int newY = panelY1 + i * entryHeight - (int) scrollOffset;

            if (newY + entryHeight > y1 && newY < y2) {
                MobData mob = filteredMobs.get(i);
                int nameX = button.getX() + 40;
                int nameY = newY + (entryHeight - this.textRenderer.fontHeight) / 2;
                String mobName = mob.getName();
                int textColor = mob.getColor();

                context.drawTextWithShadow(this.textRenderer, mobName, nameX - 1, nameY, 0xFF000000);
                context.drawTextWithShadow(this.textRenderer, mobName, nameX + 1, nameY, 0xFF000000);
                context.drawTextWithShadow(this.textRenderer, mobName, nameX, nameY - 1, 0xFF000000);
                context.drawTextWithShadow(this.textRenderer, mobName, nameX, nameY + 1, 0xFF000000);
                context.drawTextWithShadow(this.textRenderer, mobName, nameX, nameY, textColor);

                String status = mob.isEnabled() ? "ON" : "OFF";
                int statusX = button.getX() + panelWidth - 35;
                int statusY = newY + entryHeight - 15;
                int statusColor = mob.isEnabled() ? 0xFF00FF00 : 0xFFFF0000;
                context.drawTextWithShadow(this.textRenderer, status, statusX, statusY, statusColor);
            }
        }

        context.disableScissor();

        // Render other elements (search field, buttons, title) outside scissor area
        this.searchField.render(context, mouseX, mouseY, delta);
        infoButton.render(context, mouseX, mouseY, delta);
        closeButton.render(context, mouseX, mouseY, delta);

        // Panel címe
        context.drawCenteredTextWithShadow(this.textRenderer, "Mob ESP Menu", this.width / 2, 20, 0xFFFFFF);

        // Search results count
        String resultsText = filteredMobs.size() + " of " + allMobs.size() + " mobs";
        context.drawTextWithShadow(this.textRenderer, resultsText, this.width / 2 - 100, 75, 0xAAAAAA);

        // Scrollbar
        int contentHeight = mobButtons.size() * entryHeight;
        int visibleHeight = visibleButtons * entryHeight;
        if (contentHeight > visibleHeight) {
            int barHeight = Math.max(20, visibleHeight * visibleHeight / contentHeight);
            int maxScroll = Math.max(1, contentHeight - visibleHeight);
            int barY = panelY1 + (int)(scrollOffset / maxScroll * (visibleHeight - barHeight));
            context.fill(x2 - 6, y1, x2 - 2, y2, 0xFF222222);
            context.fill(x2 - 6, barY, x2 - 2, barY + barHeight, 0xFFFFFFFF);
        }
    }

    private void renderInfoPanel(DrawContext context, int mouseX, int mouseY, float delta) {
        int panelWidth = 300;
        int panelHeight = 200;
        int panelX = (this.width - panelWidth) / 2;
        int panelY = (this.height - panelHeight) / 2;

        // Draw panel background
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF222222);

        // Draw panel border
        context.fill(panelX, panelY, panelX + panelWidth, panelY + 1, 0xFFFFFFFF);
        context.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, 0xFFFFFFFF);
        context.fill(panelX, panelY, panelX + 1, panelY + panelHeight, 0xFFFFFFFF);
        context.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, 0xFFFFFFFF);

        // Draw title
        context.drawCenteredTextWithShadow(this.textRenderer, "Mob Highlighter Info", panelX + panelWidth / 2, panelY + 10, 0xFFFFFF);

        // Draw info text
        String[] infoLines = {
                "• Click on a mob name to toggle highlighting",
                "• Shift+Click to change the highlight color",
                "• Use the search bar to filter mobs",
                "• Scroll to see more mobs",
                "• Settings are saved automatically",
                "",
                "This mod helps you locate mobs by highlighting",
                "them through walls with colored outlines."
        };

        int textY = panelY + 30;
        for (String line : infoLines) {
            context.drawTextWithShadow(this.textRenderer, line, panelX + 10, textY, 0xAAAAAA);
            textY += 10;
        }

        // Draw close button for info panel
        int closeButtonX = panelX + panelWidth - 70;
        int closeButtonY = panelY + panelHeight - 25;
        context.fill(closeButtonX, closeButtonY, closeButtonX + 60, closeButtonY + 20, 0xFF444444);
        context.drawTextWithShadow(this.textRenderer, "Close", closeButtonX + 20, closeButtonY + 6, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle info panel close button
        if (showInfoPanel) {
            int panelWidth = 300;
            int panelHeight = 200;
            int panelX = (this.width - panelWidth) / 2;
            int panelY = (this.height - panelHeight) / 2;
            int closeButtonX = panelX + panelWidth - 70;
            int closeButtonY = panelY + panelHeight - 25;

            if (mouseX >= closeButtonX && mouseX <= closeButtonX + 60 &&
                    mouseY >= closeButtonY && mouseY <= closeButtonY + 20) {
                showInfoPanel = false;
                return true;
            }
        }

        // Forward click to search field first
        if (this.searchField.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.searchField);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleMouseScroll(double amount) {
        scrollOffset -= amount * 20;
        int contentHeight = mobButtons.size() * entryHeight;
        int visibleHeight = visibleButtons * entryHeight;
        int maxScroll = Math.max(0, contentHeight - visibleHeight);
        scrollOffset = Math.max(0, scrollOffset);
        scrollOffset = Math.min(maxScroll, scrollOffset);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle ESC to close search field or info panel without closing screen
        if (keyCode == 256) {
            if (this.searchField.isFocused()) {
                this.searchField.setFocused(false);
                return true;
            } else if (showInfoPanel) {
                showInfoPanel = false;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}