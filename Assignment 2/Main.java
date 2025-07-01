import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    
    public static void main(String[] args) throws IOException {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(args[4]));

            List<List<String>> animals = readFileAndParse(args[0]);
            List<List<String>> persons = readFileAndParse(args[1]);
            List<List<String>> foods = readFileAndParse(args[2]);
            List<List<String>> commands = readFileAndParse(args[3]);

            List<Animal> createdAnimals = createAnimals(animals, writer);
            List<Person> createdPersons = createPersons(persons, writer);
            List<Food> createdFoods = createFoods(foods, writer);

            readAndCallCommands(commands, createdAnimals, createdPersons, createdFoods, writer);

            writer.close();

        }catch(Exception e){
            System.out.println("Something went wrong " + e);
        }
    }

    public static List<List<String>> readFileAndParse(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName)));
        List<List<String>> allData = new ArrayList<>();
        String[] lines = content.split("\n+");
        for (String line : lines) {
            line = line.trim();
            allData.add(new ArrayList<>(Arrays.asList(line.split(","))));
        }
        return allData;
    }

    public static List<Animal> createAnimals(List<List<String>> animals,  BufferedWriter writer) throws IOException {
        List<Animal> createdAnimals = new ArrayList<>();
        writer.write("***********************************\n");
        writer.write("***Initializing Animal information***\n");
        for (List<String> animal : animals) {
            switch (animal.get(0)) {
                case "Lion":
                    Lion lion = new Lion(animal.get(1), Integer.parseInt(animal.get(2)));
                    createdAnimals.add(lion);
                    writer.write("Added new Lion with name " + animal.get(1) + " aged " + animal.get(2) + ".");
                    break;
                case "Elephant":
                    Elephant elephant = new Elephant(animal.get(1), Integer.parseInt(animal.get(2)));
                    createdAnimals.add(elephant);
                    writer.write("Added new Elephant with name " + animal.get(1) + " aged " + animal.get(2) + ".");
                    break;
                case "Penguin":
                    Penguin penguin = new Penguin(animal.get(1), Integer.parseInt(animal.get(2)));
                    createdAnimals.add(penguin);
                    writer.write("Added new Penguin with name " + animal.get(1) + " aged " + animal.get(2) + ".");
                    break;
                case "Chimpanzee":
                    Chimpanzee chimpanzee = new Chimpanzee(animal.get(1), Integer.parseInt(animal.get(2)));
                    createdAnimals.add(chimpanzee);
                    writer.write("Added new Chimpanzee with name " + animal.get(1) + " aged " + animal.get(2) + ".");
                    break;
                default:
                    System.out.println("Unknown animal type");
            }
            writer.write("\n");
        }
        return createdAnimals;
    }

    public static List<Person> createPersons(List<List<String>> persons,  BufferedWriter writer) throws IOException {
        List<Person> createPersons = new ArrayList<>();
        writer.write("***********************************\n");
        writer.write("***Initializing Visitor and Personnel information***\n");
        for (List<String> person : persons) {
            switch (person.get(0)) {
                case "Visitor":
                    Visitor visitor = new Visitor(person.get(1), Integer.parseInt(person.get(2)));
                    createPersons.add(visitor);
                    writer.write("Added new Visitor with id " + person.get(2) + " and name " + person.get(1) + ".");
                    break;
                case "Personnel":
                    Personnel personnel = new Personnel(person.get(1), Integer.parseInt(person.get(2)));
                    createPersons.add(personnel);
                    writer.write("Added new Personnel with id " + person.get(2) + " and name " + person.get(1) + ".");
                    break;
                default:
                    System.out.println("Unknown person type");
            }
            writer.write("\n");
        }
        return createPersons;
    }

    public static List<Food> createFoods(List<List<String>> foods,  BufferedWriter writer) throws IOException {
        //Although foods are categorized based on animal types, they are managed through a shared stock pool in the system,
        //meaning all animals consume from the same meat, fish, and plant resources.
        List<Food> createdFoods = new ArrayList<>();
        writer.write("***********************************\n");
        writer.write("***Initializing Food Stock***\n");
        for (List<String> food : foods) {
            double amount = Double.parseDouble(food.get(1));
            Food newFood = new Food(food.get(0), amount);
            createdFoods.add(newFood);
            writer.write("There are " + String.format(Locale.US, "%.3f", amount) + " kg of " + food.get(0) + " in stock\n");
        }
        return createdFoods;
    }

    public static void readAndCallCommands(List<List<String>> commands, List<Animal> animals, List<Person> persons, List<Food> foods, BufferedWriter writer) throws IOException {
        for (List<String> command : commands) {
            writer.write("***********************************\n");
            writer.write("***Processing new Command***\n");
            switch (command.get(0).trim()){
                case "List Food Stock":
                    writer.write(Food.listFoodStock());
                    break;
                case "Animal Visitation":
                    try {
                        String id = command.get(1).trim();
                        String animalName = command.get(2).trim();
                        Person visitPerson = findPersonByID(persons, Integer.parseInt(id));
                        // Check if a visitor exists in the system.
                        if (visitPerson == null) {
                            throw new PersonNotFoundException("Error: There are no visitors or personnel with the id " + id);
                        }
                        Animal visitAnimal = findAnimalByName(animals, animalName);
                        // Check if an animal exists in the system.
                        if (visitAnimal == null) {
                            writer.write(visitPerson.getName() + " attempts to clean " + animalName + "'s habitat.\n");
                            throw new AnimalNotFoundException("Error: There are no animals with the name " + animalName + ".");
                        }
                        String visitMessage = visitPerson.visitAnimal(visitAnimal);
                        writer.write(visitMessage);

                    }catch (PersonNotFoundException | AnimalNotFoundException e) {
                        writer.write(e.getMessage());
                    }
                    break;

                case "Feed Animal":
                    try {
                        String id = command.get(1).trim();
                        String animalName = command.get(2).trim();
                        double numberOfMeals = Double.parseDouble(command.get(3).trim());
                        Person feedPerson = findPersonByID(persons, Integer.parseInt(id));
                        // Check if a person exists in the system.
                        if (feedPerson == null) {
                            throw new PersonNotFoundException("Error: There are no visitors or personnel with the id " + id);
                        }
                        Animal feedAnimal = findAnimalByName(animals, animalName);
                        // Check if the person is a visitor.
                        if (feedPerson instanceof Visitor) {
                            writer.write(feedPerson.getName() + " tried to feed " + animalName + "\n");
                            throw new UnauthorizedFeedingException("Error: Visitors do not have the authority to feed animals.");
                        }
                        // Check if an animal exists in the system.
                        if (feedAnimal == null) {
                            writer.write(feedPerson.getName() + " attempts to feed " + animalName + "\n");
                            throw new AnimalNotFoundException("Error: There are no animals with the name " + animalName + ".");
                        }
                        // Check if the person is a personnel.
                        if (feedPerson instanceof Personnel) {
                            writer.write(feedPerson.getName() + " attempts to feed " + animalName + ".\n");
                            String feedMessage = ((Personnel) feedPerson).feed(feedAnimal, numberOfMeals);
                            writer.write(feedMessage);
                        }

                    } catch (PersonNotFoundException | AnimalNotFoundException | UnauthorizedFeedingException | NotEnoughFoodException e) {
                        writer.write(e.getMessage());
                    // Check if a value cannot be converted to a number.
                    } catch(NumberFormatException e){
                        writer.write("Error processing command: " + command.get(0) + "," + command.get(1) + "," + command.get(2) + "," + command.get(3) + "\n");
                        writer.write("Error:For input string: \"" + command.get(3) + "\"");
                    }
                    break;
            }
            writer.write("\n");
        }
    }

    private static Person findPersonByID(List<Person> persons, int ID) {
        for (Person person : persons) {
            if (person.getID() == ID) {
                return person;
            }
        }
        return null;
    }

    private static Animal findAnimalByName(List<Animal> animals, String name) {
        for (Animal animal : animals) {
            if (animal.getName().equals(name)) {
                return animal;
            }
        }
        return null;
    }
}
// Exception Classes
class Exceptions extends RuntimeException {

    public Exceptions(String message) {
        super(message);
    }
}

class NotEnoughFoodException extends Exceptions{

    public NotEnoughFoodException(String message) {
        super(message);
    }
}

class PersonNotFoundException extends Exceptions{

    public PersonNotFoundException(String message){
        super(message);
    }
}

class AnimalNotFoundException extends Exceptions{

    public AnimalNotFoundException(String message){
        super(message);
    }
}

class UnauthorizedFeedingException extends Exceptions{

    public UnauthorizedFeedingException(String message){
        super(message);
    }
}