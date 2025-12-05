package com.example.employee.model;

public enum Gender {
    Male,
    Female,
    Other;

    public static Gender fromString(String s) {
        if (s == null) return null;
        String t = s.trim();
        for (Gender g : Gender.values()) {
            if (g.name().equalsIgnoreCase(t) || g.name().equals(t)) return g;
        }
        // try common lowercase forms
        if ("m".equalsIgnoreCase(t)) return Male;
        if ("f".equalsIgnoreCase(t)) return Female;
        if ("o".equalsIgnoreCase(t)) return Other;
        return null;
    }
}
