package com.carebridge.api.global.util;

import java.security.SecureRandom;

public class CodeGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    public static String generatePinCode() {
        int number = secureRandom.nextInt(1000000);
        return String.format("%06d", number);
    }

    public static String generateLinkCode() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int index = secureRandom.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }
}