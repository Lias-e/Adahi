package com.android.adahi.utils;

import com.android.adahi.models.Animal;
import java.util.ArrayList;
import java.util.List;

/**
 * SampleDataGenerator provides sample animal data for demonstration purposes.
 * In a production application, this data would come from Firebase.
 */
public class SampleDataGenerator {

    /**
     * Generate sample animals data
     * @return List of sample Animal objects
     */
    public static List<Animal> generateSampleAnimals() {
        List<Animal> animals = new ArrayList<>();

        // Sample Sheep
        animals.add(new Animal(
                "1",
                "كبش أولاد جلال",
                "كبش",
                50000,
                45.5,
                "سلالة أولاد جلال أصيلة، مفحوصة بيطرياً ومناسبة للحجز المباشر.",
                "",
                5
        ));

        animals.add(new Animal(
                "2",
                "كبش أسود",
                "كبش",
                55000,
                48.0,
                "كبش قوي ومعتنى به جيداً، مناسب للحجز والفرز السريع.",
                "",
                3
        ));

        // Sample Goats
        animals.add(new Animal(
                "3",
                "ماعز جبلي",
                "ماعز",
                45000,
                40.0,
                "ماعز جبلي صحي ومربى في ظروف طبيعية مناسبة للموسم.",
                "",
                7
        ));

        animals.add(new Animal(
                "4",
                "ماعز أبيض",
                "ماعز",
                48000,
                42.0,
                "ماعز أبيض بصحة ممتازة وتغذية طبيعية ووزن مناسب للحجز.",
                "",
                4
        ));

        // Sample Cows
        animals.add(new Animal(
                "5",
                "عجل محلي",
                "عجل",
                150000,
                550.0,
                "عجل محلي كبير بصحة جيدة وتغذية ممتازة، مناسب لعدة عائلات.",
                "",
                2
        ));

        animals.add(new Animal(
                "6",
                "عجل أسود",
                "عجل",
                160000,
                580.0,
                "عجل مميز بلون أسود وجودة لحم ممتازة ومراقبة صحية دقيقة.",
                "",
                2
        ));

        // Sample Buffalo
        animals.add(new Animal(
                "7",
                "ثور ماء",
                "ثور",
                200000,
                650.0,
                "ثور ماء قوي ولحمه ممتاز ويكفي لعدد كبير من العائلات.",
                "",
                1
        ));

        return animals;
    }

    /**
     * Get animal types available
     * @return Array of animal type names
     */
    public static String[] getAnimalTypes() {
        return new String[]{"كبش", "ماعز", "عجل", "ثور"};
    }

    /**
     * Get default description for animal type
     * @param animalType Type of animal
     * @return Description string
     */
    public static String getDefaultDescription(String animalType) {
        switch (animalType) {
            case "كبش":
                return "كبش صحي ومناسب للأضحية والحجز المباشر.";
            case "ماعز":
                return "ماعز ممتاز للأضحية وفي حالة صحية جيدة.";
            case "عجل":
                return "عجل محلي يوفر لحمًا كافيًا لعدة عائلات.";
            case "ثور":
                return "ثور قوي وجيد للعائلات الكبيرة والمناسبات.";
            default:
                return "حيوان جيد ومناسب لموسم الأضاحي.";
        }
    }

    /**
     * Get typical weight range for animal type
     * @param animalType Type of animal
     * @return String representing typical weight range
     */
    public static String getWeightRange(String animalType) {
        switch (animalType) {
            case "كبش":
                return "40-50 كغ";
            case "ماعز":
                return "35-45 كغ";
            case "عجل":
                return "500-700 كغ";
            case "ثور":
                return "600-800 كغ";
            default:
                return "يختلف";
        }
    }
}
