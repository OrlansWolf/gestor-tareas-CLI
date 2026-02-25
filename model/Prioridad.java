package com.taskmanager.model;

public enum Prioridad {
    LOW("Bajo", "🟢"),
    MEDIUM("Medio", "🟡"),
    HIGH("Alto", "🔴"),
    URGENT("Urgente", "🔥");

    private final String displayName;
    private final String icon;

    Prioridad(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return icon + " " + displayName;
    }
}
