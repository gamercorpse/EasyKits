package com.gamercorpse.easykits.utils;

public class TimeUtil {

    private TimeUtil() {

    }

    public static String formatTime(long seconds) {

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        StringBuilder builder = new StringBuilder();

        if (hours > 0) {
            builder.append(hours).append("h ");
        }

        if (minutes > 0) {
            builder.append(minutes).append("m ");
        }

        if (remainingSeconds > 0) {
            builder.append(remainingSeconds).append("s");
        }

        return builder.toString().trim();
    }
}