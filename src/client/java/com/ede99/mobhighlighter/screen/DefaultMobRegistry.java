package com.ede99.mobhighlighter.screen;

import java.util.ArrayList;
import java.util.List;

public class DefaultMobRegistry {
    public static List<MobData> createDefaultMobs() {
        List<MobData> mobs = new ArrayList<>();

        // Original mobs (mind false flaggel)
        mobs.add(new MobData("Zombie", 0x00FF00, false));
        mobs.add(new MobData("Skeleton", 0xAAAAAA, false));
        mobs.add(new MobData("Creeper", 0x00FF00, false));
        mobs.add(new MobData("Spider", 0xFF0000, false));
        mobs.add(new MobData("Enderman", 0x5500AA, false));
        mobs.add(new MobData("Cow", 0xFFFFFF, false));
        mobs.add(new MobData("Pig", 0xFFC0CB, false));
        mobs.add(new MobData("Sheep", 0xFFFFFF, false));
        mobs.add(new MobData("Chicken", 0xFFFF99, false));
        mobs.add(new MobData("Wolf", 0xAAAAAA, false));
        mobs.add(new MobData("Ocelot", 0xFFCC66, false));
        mobs.add(new MobData("Villager", 0xFFCC99, false));
        mobs.add(new MobData("Witch", 0x550055, false));
        mobs.add(new MobData("Slime", 0x00FF00, false));
        mobs.add(new MobData("Magma Cube", 0xFF6600, false));
        mobs.add(new MobData("Silverfish", 0xAAAAAA, false));
        mobs.add(new MobData("Ghast", 0xFFFFFF, false));
        mobs.add(new MobData("Blaze", 0xFFAA00, false));
        mobs.add(new MobData("Ender Dragon", 0x5500AA, false));
        mobs.add(new MobData("Wither", 0x000000, false));
        mobs.add(new MobData("Phantom", 0x5555FF, false));
        mobs.add(new MobData("Drowned", 0x0066CC, false));
        mobs.add(new MobData("Pillager", 0x888888, false));
        mobs.add(new MobData("Ravager", 0x333333, false));
        mobs.add(new MobData("Stray", 0xCCCCCC, false));
        mobs.add(new MobData("Husk", 0xAAAA44, false));
        mobs.add(new MobData("Vindicator", 0x666666, false));
        mobs.add(new MobData("Evoker", 0x5555AA, false));
        mobs.add(new MobData("Shulker", 0xAA55FF, false));

        // Új mobok 1.21-ből
        mobs.add(new MobData("Bee", 0xFFD700, false));         // 1.15
        mobs.add(new MobData("Piglin", 0xFFB6C1, false));       // 1.16
        mobs.add(new MobData("Hoglin", 0x964B00, false));       // 1.16
        mobs.add(new MobData("Strider", 0x8B0000, false));      // 1.16
        mobs.add(new MobData("Zoglin", 0x696969, false));       // 1.16
        mobs.add(new MobData("Piglin Brute", 0x800000, false)); // 1.16.2
        mobs.add(new MobData("Goat", 0xFFFFFF, false));         // 1.17
        mobs.add(new MobData("Axolotl", 0xF5A9B8, false));      // 1.17
        mobs.add(new MobData("Glow Squid", 0x00FFFF, false));   // 1.17
        mobs.add(new MobData("Allay", 0x87CEFA, false));        // 1.19
        mobs.add(new MobData("Frog", 0x32CD32, false));         // 1.19
        mobs.add(new MobData("Tadpole", 0x2F4F4F, false));      // 1.19
        mobs.add(new MobData("Warden", 0x006400, false));       // 1.19
        mobs.add(new MobData("Camel", 0xF0E68C, false));        // 1.20
        mobs.add(new MobData("Sniffer", 0x8B4513, false));      // 1.20
        mobs.add(new MobData("Breeze", 0x87CEEB, false));       // 1.21
        mobs.add(new MobData("Bogged", 0x8FBC8F, false));       // 1.21
        // Player as an "entity (if supported by Fabric mod highlighting)"
        mobs.add(new MobData("Player", 0xFFD700, false));       // mock entity

        return mobs;
    }
}
