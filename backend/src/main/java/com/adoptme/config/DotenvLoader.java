package com.adoptme.config;

import io.github.cdimascio.dotenv.Dotenv;

public class DotenvLoader {
    public static void load() {
        // Load from current directory (backend/) first
        Dotenv current = Dotenv.configure()
                .ignoreIfMissing()
                .directory(".")
                .load();
        apply(current);

        // Load from repo root (../) if running from backend/
        Dotenv parent = Dotenv.configure()
                .ignoreIfMissing()
                .directory("..")
                .load();
        apply(parent);
    }

    private static void apply(Dotenv dotenv) {
        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null || value.isBlank()) {
                return;
            }
            if (System.getProperty(key) == null && System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        });
    }
}
