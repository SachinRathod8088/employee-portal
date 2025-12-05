package com.example.employee.model;

public enum EmployeeStatus {
    Active,
    Inactive,
    Terminated,
    On_Leave;

    public static EmployeeStatus fromString(String s) {
        if (s == null) return null;
        String t = s.trim();
        for (EmployeeStatus st : EmployeeStatus.values()) {
            if (st.name().equalsIgnoreCase(t) || st.name().equals(t)) return st;
        }
        // allow common words
        if ("on leave".equalsIgnoreCase(t) || "on_leave".equalsIgnoreCase(t)) return On_Leave;
        return null;
    }
}
