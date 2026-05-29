package com.android.adahi.utils;

import com.android.adahi.models.Animal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class AnimalUiUtils {

    private static final String SHEEP_IMAGE = "https://lh3.googleusercontent.com/aida-public/AB6AXuDGawK7cAHiJYFCOjowPOZEiDbLcamKppIH6tLe5PFWgUiMgFKCU25uGDWkIr6qiMQ4DuXUxNz4gDleCSZ9IV9rn2xj_XMNjDri-YkS74UcnaC097oAnVRgjrxgc0aN-_d-zRnkCszuYSutG-ELHCstWKG4cR9ipdvO33lymXQWSBQyQ9kHjI0tJWUwvKD0eEG1T86nnmFVNMBzWU9Rf3uWpIRlgmJPRKOJxbZkoGFtfCr12AWIcxqP3X2jE4y3eSnhnppM-UjCx4M";
    private static final String COW_IMAGE = "https://lh3.googleusercontent.com/aida-public/AB6AXuC_NyCfGbRVIryA3HmiCRCCMixWQXWTsIm7_vuvIrbl-4udhnJX0IKk2fYN151E95KJpIp83gZ44aSU45_b2q6rDvZbETD4FmmY8_HXoSlxsRMl45Rgo_yxqXd9SqZPmVBMk4MebqUfszp98y-mhKfFO1E712qsErGuB8T39fctkBssz0OtPjAJKFygI0J_rLMxcxy6ETsWIUofUndTNlOuW9JYgjEbU1w2NoXcKNcMXcCVf5XBx9OdoxDOvssp-qLmYT1lraRPoZA";
    private static final String GOAT_IMAGE = "https://lh3.googleusercontent.com/aida-public/AB6AXuAovBnSE90-gIj7xrCCwu4UJpPg_LYuKITIottKNzRXxTH8e6RlCvSCc3pmmpKbV-42oEcVfHgy9R2KfmwTLbQfot8Q-8pf6fcJq6au50b4ENyXOZMEzZIGm-3UAES8GkslxKEG_D3LU70cA6B5U_IGi79ae3v9tzyjSaMg4PUGxEX962WaCJBPs53nar8GP-64JBktCXnBQhluV9NLLP168bcxh4A0beGhpyS7K-gGUUQ9zejaZ2LFSjQhmhwZQT9x2fY2Al_LqvI";

    private AnimalUiUtils() {
    }

    public static String formatPrice(double price) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(Math.round(price)) + " د.ج";
    }

    public static String resolveImageUrl(Animal animal) {
        if (animal == null) {
            return SHEEP_IMAGE;
        }

        String imageUrl = animal.getImageUrl();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            return imageUrl.trim();
        }

        String type = animal.getType() == null ? "" : animal.getType().toLowerCase(Locale.ROOT);
        if (type.contains("cow") || type.contains("بقر") || type.contains("عجل") || type.contains("ثور") || type.contains("بقرة")) {
            return COW_IMAGE;
        }
        if (type.contains("goat") || type.contains("ماعز")) {
            return GOAT_IMAGE;
        }
        return SHEEP_IMAGE;
    }
}