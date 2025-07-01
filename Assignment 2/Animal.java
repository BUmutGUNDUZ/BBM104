import java.util.Locale;

abstract class Animal{

    private final String name;
    private final int age;
    private double dailyFood;

    public Animal(String name, int age){
        this.name = name;
        this.age = age;
    }

    public int getAge() {return age;}
    public String getName() {return name;}
    public double getDailyFood(){return dailyFood;}

    // Setter
    protected void setDailyFoodValue(double dailyFood) {this.dailyFood = dailyFood;}

    //This is abstract method defined in every animal class.
    abstract void setDailyFood();
    abstract String eat(double numberOfMeals);
}

class Lion extends Animal {

    public Lion(String name, int age) {
        super(name, age);
        setDailyFood();
    }

    @Override
    void setDailyFood() {
        int age = getAge();
        if (age == 5) {
            setDailyFoodValue(5);
        } else if (age < 5) {
            double amount = 5 - age;
            setDailyFoodValue(5 - amount * 0.050);
        } else {
            double amount = age - 5;
            setDailyFoodValue(5 + amount * 0.050);
        }
    }

    @Override
    public String eat(double numberOfMeals) {
        double neededFood = getDailyFood() * numberOfMeals;

        if (Food.getMeatAmount() >= neededFood) {
            Food.setMeatAmount(Food.getMeatAmount() - neededFood);
            return getName() + " has been given " + String.format(Locale.US, "%.3f", neededFood) + " kgs of meat";
        } else {
            throw new NotEnoughFoodException("Error: Not enough Meat");
        }
    }
}

class Elephant extends Animal {

    public Elephant(String name, int age) {
        super(name, age);
        setDailyFood();
    }

    @Override
    void setDailyFood() {
        int age = getAge();
        if (age == 20) {
            setDailyFoodValue(10);
        } else if (age < 20) {
            double amount = 20 - age;
            setDailyFoodValue(10 - amount * 0.015);
        } else {
            double amount = age - 20;
            setDailyFoodValue(10 + amount * 0.015);
        }

    }
    @Override
    public String eat(double numberOfMeals) {
        double neededFood = getDailyFood() * numberOfMeals;

        if (Food.getPlantAmount() >= neededFood) {
            Food.setPlantAmount(Food.getPlantAmount() - neededFood);
            return getName() + " has been given " + String.format(Locale.US, "%.3f", neededFood) + " kgs assorted fruits and hay";
        } else {
            throw new NotEnoughFoodException("Error: Not enough Plant");
        }
    }
}

class Penguin extends Animal{

    public Penguin(String name, int age){
        super(name, age);
        setDailyFood();
    }

    @Override
    void setDailyFood() {
        int age = getAge();
        if (age == 4) {
            setDailyFoodValue(3);
        } else if (age < 4) {
            double amount = 4 - age;
            setDailyFoodValue(3 - amount * 0.040);
        } else {
            double amount = age - 4;
            setDailyFoodValue(3 + amount * 0.040);
        }
    }

    @Override
    public String eat(double numberOfMeals) {
        double neededFood = getDailyFood() * numberOfMeals;

        if (Food.getFishAmount() >= neededFood) {
            Food.setFishAmount(Food.getFishAmount() - neededFood);
            return getName() + " has been given " + String.format(Locale.US, "%.3f", neededFood) + " kgs of various kinds of fish";
        } else {
            throw new NotEnoughFoodException("Error: Not enough Fish");
        }
    }
}

class Chimpanzee extends Animal {

    public Chimpanzee(String name, int age) {
        super(name, age);
        setDailyFood();
    }

    @Override
    void setDailyFood() {
        int age = getAge();
        if (age == 10) {
            setDailyFoodValue(6);
        } else if (age < 10) {
            double amount = 10 - age;
            setDailyFoodValue(6 - amount * 0.025);
        } else {
            double amount = age - 10;
            setDailyFoodValue(6 + amount * 0.025);
        }
    }

    @Override
    public String eat(double numberOfMeals) {
        double halfFood = getDailyFood() / 2.0 * numberOfMeals;

        if (Food.getMeatAmount() >= halfFood && Food.getPlantAmount() >= halfFood) {
            Food.setMeatAmount(Food.getMeatAmount() - halfFood);
            Food.setPlantAmount(Food.getPlantAmount() - halfFood);
            return getName() + " has been given " + String.format(Locale.US, "%.3f", halfFood) + " kgs of meat and " + String.format(Locale.US, "%.3f", halfFood) + " kgs of leaves";
        } else {
            if (halfFood > Food.getPlantAmount()) {
                throw new NotEnoughFoodException("Error: Not enough Plant");
            } else if (halfFood > Food.getMeatAmount()) {
                throw new NotEnoughFoodException("Error: Not enough Meat");
            }
        }
        return "Something went wrong.";
    }
}
