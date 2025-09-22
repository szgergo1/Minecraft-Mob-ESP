package com.ede99.mobhighlighter;

import com.ede99.mobhighlighter.render.MobHighlighterRenderer;
import com.ede99.mobhighlighter.screen.MobHighlighterScreen;
import com.ede99.mobhighlighter.util.ModKeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MobESPModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeyBindings.registerKeyBindings();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeyBindings.OPEN_MENU.wasPressed()) {
                client.setScreen(new MobHighlighterScreen());
            }
        });

        Config.load();
        MobHighlighterRenderer.register();

    }
}