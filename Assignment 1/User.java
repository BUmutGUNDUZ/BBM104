import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class User {

    // Variables that will not be changed later are assigned as Final.
    private final String ID;
    private final String name;
    private final String phoneNumber;

    private int penalty = 0;
    private int maxItems;
    private int overdueDays;
    private final int penaltyThreshold = 6;

    private List<Item> borrowedItems = new ArrayList<>(); // The list of books that the user borrows is kept

    // The constructor used by the partners in each class connected to the User class.
    protected User(String name, String ID, String phoneNumber) {
        this.ID = ID;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public int borrow(Item item, String date) {
        if (penalty >= getPenaltyThreshold()) {
            return 1;
        }
        if (borrowedItems.size() >= getMaxItems()) {
            return 3;
        }
        if (!canBorrow(item)) {
            return 2;
        }
        if (item.getBorrowedBy() != null) {
            return 4;
        }

        borrowedItems.add(item);
        item.setBorrowed_date(date);
        item.setBorrowed_by(getName());
        return 0;
    }

    public boolean returnItem(Item item) {
        if (borrowedItems.contains(item)) {
            borrowedItems.remove(item);
            item.setBorrowed_date(null);
            item.setBorrowed_by(null);
            return true;
        }
        // This phrase was written for the case of trying to return an object that is not an expression.
        return false;
    }

    public void  pay() {penalty = 0;}

    private boolean canBorrow(Item item) {
        // Added a check because some users couldn't get some items according to the class

        if (this instanceof Student && item.getType().equals("reference")) {
            return false;
        }
        if(this instanceof AcademicStaff){
            return true;
        }
        if (this instanceof Guest && item.getType().equals("rare") || item.getType().equals("limited")) {
            return false;
        }
        return true;
    }

    public void checkOverdueItems() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<Item> overdueItems = new ArrayList<>();

        // The operation of determining the items that have passed the time
        for (Item item : borrowedItems) {
            LocalDate borrowDate = LocalDate.parse(item.getBorrowedDate(), formatter);
            long daysBetween = ChronoUnit.DAYS.between(borrowDate, currentDate) + 1;

            if (daysBetween > getOverdueDays()) {
                overdueItems.add(item);
            }
        }
        // Return operation and applying penalty
        for (Item item : overdueItems) {
            borrowedItems.remove(item);
            item.setBorrowed_date(null);
            item.setBorrowed_by(null);
            penalty += 2;
        }
    }

    //Functions about user
    public String getID() { return ID; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }

    // Functions about setting item
    public void setMaxItems(int maxItems){this.maxItems = maxItems;}
    public void setOverdueDays(int overdueDays) {this.overdueDays = overdueDays;}

    // Functions about getting item's penalty-related variables
    public int getPenalty() { return penalty; }
    public int getMaxItems() { return maxItems; }
    public int getOverdueDays() { return overdueDays; }
    public int getPenaltyThreshold() { return penaltyThreshold; }

}

class Student extends User {

    private final String faculty;
    private final String department;
    private final String grade;

    public Student(String name, String ID, String phoneNumber,String department ,String faculty, String grade) {
        super(name,ID,phoneNumber);
        this.faculty = faculty;
        this.department = department;
        this.grade = grade;

        // Special rules for student
        setMaxItems(5);
        setOverdueDays(30);
    }
    public String getFaculty() { return faculty; }
    public String getDepartment() { return department; }
    public String getGrade() { return grade; }

}

class AcademicStaff extends User {

    private final String faculty;
    private final String department;
    private final String title;

    public AcademicStaff(String name, String ID, String phoneNumber, String department, String faculty, String title) {
        super(name,ID,phoneNumber);
        this.faculty = faculty;
        this.department = department;
        this.title = title;

        // Special rules for academic staff
        setMaxItems(3);
        setOverdueDays(15);

    }
    public String getFaculty() { return faculty; }
    public String getDepartment() { return department; }
    public String getTitle() { return title; }

}

class Guest extends User {

    private final String occupation;

    public Guest(String name, String ID, String phoneNumber, String occupation) {
        super(name,ID,phoneNumber);
        this.occupation = occupation;

        // Special rules for guest
        setMaxItems(1);
        setOverdueDays(7);
    }
    public String getOccupation() { return occupation; }

}