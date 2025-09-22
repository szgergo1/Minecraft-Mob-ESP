package com.ede99.mobhighlighter.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyBinding OPEN_MENU;
    public static KeyBinding TOGGLE_HIGHLIGHT;

    private static boolean wasToggleKeyPressed = false;

    public static void registerKeyBindings() {
        OPEN_MENU = registerKey("Open Menu", GLFW.GLFW_KEY_M);
        TOGGLE_HIGHLIGHT = registerKey("Toggle Highlight", GLFW.GLFW_KEY_H);

        // Register tick event to check for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (TOGGLE_HIGHLIGHT.wasPressed()) {
                // Only toggle if the key was just pressed (not held)
                if (!wasToggleKeyPressed) {
                    ModState.toggleHighlight();
                    wasToggleKeyPressed = true;

                    // Show a message to the player
                    if (client.player != null) {
                        String status = ModState.isHighlightEnabled() ? "§aON" : "§cOFF";
                        client.player.sendMessage(Text.literal("Mob Highlight: " + status), true);
                    }
                }
            } else {
                wasToggleKeyPressed = false;
            }
        });
    }

    private static KeyBinding registerKey(String name, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                name,
                InputUtil.Type.KEYSYM,
                key,
                "Mob ESP"
        ));
    }
}