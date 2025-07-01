abstract class Person {

    private final String name;
    private final int Id;

    public Person(String name, int Id){
        this.name = name;
        this.Id = Id;
    }

    public int getID() {return Id;}
    public String getName() {return name;}

    abstract String visitAnimal(Animal animal);
}

class Personnel extends Person{

    public Personnel(String name, int Id){
        super(name, Id);
    }

    public String feed(Animal animal, double numberOfMeals) {
        return animal.eat(numberOfMeals);
    }

    private String clean(Animal animal){
        String action;

        if (animal instanceof Lion) {
            action = "Removing bones and refreshing sand.";
        } else if (animal instanceof Elephant) {
            action = "Washing the water area.";
        } else if (animal instanceof Penguin) {
            action = "Replenishing ice and scrubbing walls.";
        } else {
            action = "Sweeping the enclosure and replacing branches.";
        }
        return getName() + " attempts to clean " + animal.getName() + "'s habitat.\n" +
                getName() + " started cleaning " + animal.getName() + "'s habitat.\n" +
                "Cleaning " + animal.getName() + "'s habitat: " + action;
    }

    @Override
    public String visitAnimal(Animal animal) {
        return clean(animal);
    }
}

class Visitor extends Person{

    public Visitor(String name, int Id){
        super(name, Id);
    }

    @Override
    public String visitAnimal(Animal animal) {
        return getName() + " tried to register for a visit to " + animal.getName() + ".\n" +
                getName() + " successfully visited " + animal.getName() + ".";
    }
}
