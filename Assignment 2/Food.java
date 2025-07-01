import java.util.Locale;

class Food {

    private static double meatAmount = 0;
    private static double plantAmount = 0;
    private static double fishAmount = 0;

    public Food(String type, double amount) {
        switch (type) {
            case "Meat":
                meatAmount += amount;
                break;
            case "Plant":
                plantAmount += amount;
                break;
            case "Fish":
                fishAmount += amount;
                break;
        }
    }

    public static double getMeatAmount() { return meatAmount; }
    public static void setMeatAmount(double amount) { meatAmount = amount; }

    public static double getPlantAmount() { return plantAmount; }
    public static void setPlantAmount(double amount) { plantAmount = amount; }

    public static double getFishAmount() { return fishAmount; }
    public static void setFishAmount(double amount) { fishAmount = amount; }

    public static String listFoodStock() {
        return "Listing available Food Stock:\n" +
                "Plant: " + String.format(Locale.US, "%.3f", plantAmount) + " kgs\n" +
                "Fish: " + String.format(Locale.US, "%.3f", fishAmount) + " kgs\n" +
                "Meat: " + String.format(Locale.US, "%.3f", meatAmount) + " kgs";
    }
}
