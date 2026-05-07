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
                "White Sheep",
                "Sheep",
                50000,
                45.5,
                "Healthy white sheep, perfect for Eid ul-Adha. Well-fed and vaccinated.",
                "",
                5
        ));

        animals.add(new Animal(
                "2",
                "Black Sheep",
                "Sheep",
                55000,
                48.0,
                "Premium quality black sheep with excellent health status.",
                "",
                3
        ));

        // Sample Goats
        animals.add(new Animal(
                "3",
                "Brown Goat",
                "Goat",
                45000,
                40.0,
                "Brown goat in perfect condition, ideal for the festival.",
                "",
                7
        ));

        animals.add(new Animal(
                "4",
                "White Goat",
                "Goat",
                48000,
                42.0,
                "White goat with white and brown markings, healthy and strong.",
                "",
                4
        ));

        // Sample Cows
        animals.add(new Animal(
                "5",
                "Red Cow",
                "Cow",
                150000,
                550.0,
                "Large red cow, healthy and well-nourished. Sufficient meat for 7-8 people.",
                "",
                2
        ));

        animals.add(new Animal(
                "6",
                "Black Cow",
                "Cow",
                160000,
                580.0,
                "Premium quality black cow with excellent meat quality.",
                "",
                2
        ));

        // Sample Buffalo
        animals.add(new Animal(
                "7",
                "Water Buffalo",
                "Buffalo",
                200000,
                650.0,
                "Strong water buffalo with premium meat quality. Sufficient for 10+ people.",
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
        return new String[]{"Sheep", "Goat", "Cow", "Buffalo"};
    }

    /**
     * Get default description for animal type
     * @param animalType Type of animal
     * @return Description string
     */
    public static String getDefaultDescription(String animalType) {
        switch (animalType) {
            case "Sheep":
                return "Healthy sheep suitable for sacrifice during Eid ul-Adha.";
            case "Goat":
                return "Premium quality goat for the holy occasion.";
            case "Cow":
                return "Large cow providing meat for multiple families.";
            case "Buffalo":
                return "Strong buffalo with excellent meat quality for large gatherings.";
            default:
                return "Quality animal for Eid ul-Adha celebration.";
        }
    }

    /**
     * Get typical weight range for animal type
     * @param animalType Type of animal
     * @return String representing typical weight range
     */
    public static String getWeightRange(String animalType) {
        switch (animalType) {
            case "Sheep":
                return "40-50 kg";
            case "Goat":
                return "35-45 kg";
            case "Cow":
                return "500-700 kg";
            case "Buffalo":
                return "600-800 kg";
            default:
                return "Varies";
        }
    }
}
