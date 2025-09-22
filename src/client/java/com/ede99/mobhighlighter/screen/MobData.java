package com.ede99.mobhighlighter.screen;

import com.ede99.mobhighlighter.Config;

public class MobData {
    private final String name;
    private boolean enabled;
    private int defaultColor;

    public MobData(String name, int defaultColor, boolean enabled) {
        this.name = name;
        this.defaultColor = defaultColor;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getColor() {
        return Config.getColor(name, defaultColor);
    }

    public void setColor(int color) {
        this.defaultColor = color;
        Config.setColor(name, color);
    }
}
