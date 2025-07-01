import java.io.*;
import java.util.*;

public class Person {

    private final int ID;
    private final String name;
    private final String email;
    private final String department;

    public Person(int ID, String name, String email, String department) {
        this.ID = ID;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public int getID() {return ID;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getDepartment() {return department;}

    // Static method to write all persons (students and academic members) to the output
    public static void writePersons(List<Person> persons, BufferedWriter writer) throws IOException {
        writer.write("----------------------------------------\n");
        writer.write("            Academic Members\n");
        writer.write("----------------------------------------\n");

        for (Person person : persons) {
            if (person instanceof AcademicMember) {
                AcademicMember academic = (AcademicMember) person;
                writer.write("Faculty ID: " + academic.getID() + "\n");
                writer.write("Name: " + academic.getName() + "\n");
                writer.write("Email: " + academic.getEmail() + "\n");
                writer.write("Department: " + academic.getDepartment() + "\n\n");;
            }
        }
        writer.write("----------------------------------------\n\n");

        writer.write("----------------------------------------\n");
        writer.write("                STUDENTS\n");
        writer.write("----------------------------------------\n");

        for (Person person : persons) {
            if (person instanceof Student) {
                Student student = (Student) person;
                writer.write("Student ID: " + student.getID() + "\n");
                writer.write("Name: " + student.getName() + "\n");
                writer.write("Email: " + student.getEmail() + "\n");
                writer.write("Major: " + student.getDepartment() + "\n");
                writer.write("Status: Active\n\n");
            }
        }
        writer.write("----------------------------------------\n\n");
    }
}
class Student extends Person implements Reportable {

    // Courses the student is enrolled in but not yet graded
    private List<Course> enrolledCourses = new ArrayList<>();
    // Courses completed with corresponding letter grades
    private Map<Course, String> completedCourses = new HashMap<>();

    public Student(int ID, String name, String email, String department) {
        super(ID, name, email, department);
    }

    public void enrollToCourse(Course course) {enrolledCourses.add(course);}
    public void assignGrade(Course course, String grade) {completedCourses.put(course, grade);}

    // Generates student-specific report including enrolled/completed courses and GPA
    @Override
    public void generateReport(BufferedWriter writer) throws IOException {

        writer.write("Student ID: " + getID() + "\n");
        writer.write("Name: " + getName() + "\n");
        writer.write("Email: " + getEmail() + "\n");
        writer.write("Major: " + getDepartment() + "\n");
        writer.write("Status: Active\n\n");

        writer.write("\nEnrolled Courses:" + "\n");
        for (Course c : enrolledCourses) {
            if (!completedCourses.containsKey(c)) {
                writer.write("- " + c.getName() + " (" + c.getCode() + ")" + "\n");
            }
        }

        writer.write("\nCompleted Courses:\n");

        for (Map.Entry<Course, String> entry : completedCourses.entrySet()) {
            Course course = entry.getKey();
            String grade = entry.getValue();
            writer.write("- " + course.getName() + " (" + course.getCode() + "): " + grade + "\n");
        }

        writer.write("\nGPA: " + String.format(Locale.US, "%.2f", calculateGPA()) + "\n");
        writer.write("----------------------------------------\n\n");
    }

    //Calculates GPA from completed courses using grade weight
    private double calculateGPA() {
        double totalPoints = 0;
        int totalCredits = 0;

        for (Map.Entry<Course, String> entry : completedCourses.entrySet()) {
            Course course = entry.getKey();
            String grade = entry.getValue();
            totalPoints += getGradePoint(grade) * course.getCredits();
            totalCredits += course.getCredits();
        }
        return totalCredits == 0 ? 0.0 : Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    private double getGradePoint(String grade) {
        switch (grade) {
            case "A1": return 4.0;
            case "A2": return 3.5;
            case "B1": return 3.0;
            case "B2": return 2.5;
            case "C1": return 2.0;
            case "C2": return 1.5;
            case "D1": return 1.0;
            case "D2": return 0.5;
            case "F3": return 0.0;
            default: return 0.0;
        }
    }
}
class AcademicMember extends Person{

    // Courses this academic member is teaching
    private List<Course> teachingCourses = new ArrayList<>();

    public AcademicMember(int ID, String name, String email, String department) {
        super(ID, name, email, department);
    }
    public void assignToCourse(Course course) {teachingCourses.add(course);}
}