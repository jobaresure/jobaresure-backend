package com.jobaresure.util;

import java.security.SecureRandom;

public class PublicIdGenerator {

    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(String prefix) {
        StringBuilder sb = new StringBuilder(prefix);

        for (int i = 0; i < 8; i++) {
            sb.append(ALPHANUM.charAt(RANDOM.nextInt(ALPHANUM.length())));
        }

//        sb.append(System.currentTimeMillis());
        return sb.toString();
    }
}
