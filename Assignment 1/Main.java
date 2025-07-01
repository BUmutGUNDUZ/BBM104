import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        // Files are read with the readFileAndParse function
        List<List<String>> items = readFileAndParse(args[0]);
        List<List<String>> users = readFileAndParse(args[1]);
        List<List<String>> commands = readFileAndParse(args[2]);
        String outputFilename = args[3];

        List<Item> createdItems = createItems(items);
        List<User> createdUsers = createUsers(users);

        // Commands are read and executed by calling the necessary functions within function readAndCallCommands
        readAndCallCommands(commands, createdUsers, createdItems);

        // Lines stacked on top of each other are being written to the file
        writeOutput(outputFilename);

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

    public static List<User> createUsers(List<List<String>> users) {
        List<User> createdUsers = new ArrayList<>();
        for (List<String> user : users) {
            switch (user.get(0)) {
                case "S":
                    Student student = new Student(user.get(1), user.get(2), user.get(3), user.get(4), user.get(5), user.get(6).trim());
                    createdUsers.add(student);
                    break;
                case "A":
                    AcademicStaff academic = new AcademicStaff(user.get(1), user.get(2), user.get(3), user.get(4), user.get(5), user.get(6));
                    createdUsers.add(academic);
                    break;
                case "G":
                    Guest guest = new Guest(user.get(1), user.get(2), user.get(3), user.get(4));
                    createdUsers.add(guest);
                    break;
                default:
                    System.out.println("Unknown type: " + user.get(0));
                    break;
            }
        }
        return createdUsers;

    }

    public static List<Item> createItems(List<List<String>> items) {
        List<Item> createdItems = new ArrayList<>();
        for (List<String> item : items) {
            switch (item.get(0)) {
                case "B":
                    Book book = new Book(item.get(1), item.get(2), item.get(3), item.get(4), item.get(5));
                    createdItems.add(book);
                    break;
                case "M":
                    Magazine magazine = new Magazine(item.get(1), item.get(2), item.get(3), item.get(4), item.get(5));
                    createdItems.add(magazine);
                    break;
                case "D":
                    DVD dvd = new DVD(item.get(1), item.get(2), item.get(3), item.get(4), item.get(5), item.get(6));
                    createdItems.add(dvd);
                    break;

                default:
                    System.out.println("Unknown type: " + item.get(0));
                    break;
            }
        }
        return createdItems;
    }

    public static void readAndCallCommands(List<List<String>> commands, List<User> users, List<Item> items){
        for (List<String> command : commands) {
            switch (command.get(0).trim()) {
                case "borrow":
                    User borrowUser = findUserByID(users, command.get(1).trim());
                    Item borrowItem = findItemByID(items, command.get(2).trim());
                    String date = command.get(3).trim();
                    int borrow = borrowUser.borrow(borrowItem, date);
                    if (borrow == 0) {
                        mergeOutput(borrowUser.getName() + " successfully borrowed! " + borrowItem.getName());
                    } else if (borrow == 1) {
                        mergeOutput(borrowUser.getName() + " cannot borrow " + borrowItem.getName() + ", you must first pay the penalty amount! " + borrowUser.getPenalty() + "$");
                    } else if (borrow == 2) {
                        mergeOutput(borrowUser.getName() + " cannot borrow "+  borrowItem.getType() +" item!");
                    } else if (borrow == 3) {
                        mergeOutput(borrowUser.getName() + " cannot borrow " + borrowItem.getName() + ", since the borrow limit has been reached!");
                    } else if (borrow == 4) {
                        mergeOutput(borrowUser.getName() + " cannot borrow " + borrowItem.getName() + ", it is not available!" );
                    }
                    borrowUser.checkOverdueItems();
                    break;
                case "return":
                    User returnUser = findUserByID(users, command.get(1).trim());
                    Item return_item = findItemByID(items, command.get(2).trim());
                    boolean returned = returnUser.returnItem(return_item);
                    if (returned) {
                        mergeOutput(returnUser.getName() + " successfully returned " + return_item.getName());
                    } else {
                        mergeOutput("Error: Item " + return_item.getName() + " not found in borrowed items.");
                    }
                    returnUser.checkOverdueItems();
                    break;
                case "pay":
                    User payUser = findUserByID(users, command.get(1).trim());
                    payUser.pay();
                    mergeOutput(payUser.getName() + " has paid penalty");
                    payUser.checkOverdueItems();
                    break;
                case "displayUsers":
                    mergeOutput("");
                    displayUsers(users);
                    break;
                case "displayItems":
                    mergeOutput("");
                    displayItems(items);
                    break;
                default:
                    System.out.println("Unknown command");
                    break;
            }
        }
    }

    private static void displayUsers(List<User> users) {
        mergeOutput("");
        Collections.sort(users, Comparator.comparing(User::getID));
        for (User user : users) {
            mergeOutput("------ User Information for " + user.getID() + " ------");

            if (user instanceof Student) {
                mergeOutput("Name: " + user.getName() + " Phone: " + user.getPhoneNumber());
                Student student = (Student) user;
                mergeOutput("Faculty: " + student.getFaculty() + " Department: " + student.getDepartment() + " Grade: " + student.getGrade() + "th");
                if(user.getPenalty() != 0){
                    mergeOutput("Penalty: " + user.getPenalty()+ "$");
                }

            } else if (user instanceof AcademicStaff) {
                AcademicStaff academic = (AcademicStaff) user;
                mergeOutput("Name: "+ academic.getTitle().trim() + " "+  user.getName() + " Phone: " + user.getPhoneNumber());
                mergeOutput("Faculty: " + academic.getFaculty() + " Department: " + academic.getDepartment());
                if(user.getPenalty() != 0){
                    mergeOutput("Penalty: " + user.getPenalty()+ "$");
                }

            } else if (user instanceof Guest) {
                mergeOutput("Name: " + user.getName() + " Phone: " + user.getPhoneNumber());
                Guest guest = (Guest) user;
                mergeOutput("Occupation: " + guest.getOccupation());
                if(user.getPenalty() != 0){
                    mergeOutput("Penalty: " + user.getPenalty()+ "$");
                }
            }
            mergeOutput("");
        }
    }

    private static void displayItems(List<Item> items) {
        Collections.sort(items, Comparator.comparing(Item::getID));
        Item lastItem = items.get(items.size() - 1);
        for (Item item : items) {
            mergeOutput("------ Item Information for " + item.getID() + " ------");

            if (item.getBorrowedDate() != null && item.getBorrowedBy() != null) {
                mergeOutput("ID: " + item.getID() + " Name: " + item.getName() + " Status: Borrowed " +"Borrowed Date: " + item.getBorrowedDate() + " Borrowed by: " + item.getBorrowedBy());
            }else{
                mergeOutput("ID: " + item.getID() + " Name: " + item.getName() + " Status: Available");
            }

            if (item instanceof Book) {
                Book book = (Book) item;
                mergeOutput("Author: " + book.getAuthor() + " Genre: " + book.getGenre());

            } else if (item instanceof Magazine) {
                Magazine magazine = (Magazine) item;
                mergeOutput("Publisher: " + magazine.getPublisher() + " Category: " + magazine.getCategory());

            } else if (item instanceof DVD) {
                DVD dvd = (DVD) item;
                mergeOutput("Director: " + dvd.getDirectory() + " Category: " + dvd.getCategory() + " Runtime: " + dvd.getRuntime());
            }

            if (item != lastItem) {
                mergeOutput("");
            }
        }
    }

    private static User findUserByID(List<User> users, String ID) {
        for (User user : users) {
            if (user.getID().equals(ID)) {
                return user;
            }
        }
        return null;
    }

    private static Item findItemByID(List<Item> items, String ID) {
        for (Item item : items) {
            if (item.getID().equals(ID)) {
                return item;
            }
        }
        return null;
    }

    private static void mergeOutput(String message) {outputBuffer.append(message).append(System.lineSeparator());}

    public static void writeOutput(String filename) {
        // A BufferedWriter object is created, associated with the specified file name (filename) for writing.
        // The second parameter "false" indicates that the file will be overwritten (existing content will be deleted).

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            writer.write(outputBuffer.toString());
            writer.flush();
            outputBuffer.setLength(0);
        } catch (IOException e) {
            System.out.println("Error writing to output file: " + e.getMessage());
        }
    }

    private static StringBuilder outputBuffer = new StringBuilder();
}