package com.example.pollutiontracker;

public final class UserSession {

    private static volatile Integer currentUserId;

    private UserSession() {
    }

    public static void setCurrentUserId(int userId) {
        currentUserId = userId > 0 ? userId : null;
    }

    public static Integer getCurrentUserId() {
        return currentUserId;
    }

    public static boolean hasCurrentUser() {
        return currentUserId != null;
    }

    public static void clear() {
        currentUserId = null;
    }
}
