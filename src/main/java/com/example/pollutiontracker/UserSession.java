package com.example.pollutiontracker;

public final class UserSession {

    private static String city;
    private static int aqi;

    public static void setCity(String c) {
        city = c;
    }

    public static String getCity() {
        return city;
    }

    public static void setAqi(int value) {
        aqi = value;
    }

    public static int getAqi() {
        return aqi;
    }
    private static volatile Integer currentUserId;
    private static UserDAO.UserProfile userProfile;
    public static void setUserProfile(UserDAO.UserProfile profile) {
        userProfile = profile;
    }
    public static UserDAO.UserProfile getUserProfile() {
        return userProfile;
    }

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
