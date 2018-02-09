package com.lol768.battlekits.utilities;

import java.util.HashMap;

public class Localisation {

    public static HashMap<String, String> messagesCache = new HashMap<>();

    public static String m(String key, Object... args) {
        return String.format(messagesCache.get(key), args);
    }

}
