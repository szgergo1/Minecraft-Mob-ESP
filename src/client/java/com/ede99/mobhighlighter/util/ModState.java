package com.ede99.mobhighlighter.util;

import com.ede99.mobhighlighter.Config;

public class ModState {
    private static boolean highlightEnabled = true;

    public static boolean isHighlightEnabled() {
        return highlightEnabled;
    }

    public static void toggleHighlight() {
        highlightEnabled = !highlightEnabled;
    }

    public static void setHighlightEnabled(boolean enabled) {
        highlightEnabled = enabled;
    }

    public static double getMaxRenderDistance() {
        return Config.getMaxRenderDistance();
    }

    public static void setMaxRenderDistance(double distance) {
        Config.setMaxRenderDistance(distance);
    }
}