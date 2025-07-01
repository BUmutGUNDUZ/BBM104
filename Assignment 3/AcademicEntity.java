import java.io.*;
import java.util.*;

public class AcademicEntity {

    private final String code;
    private final String name;

    public AcademicEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {return code;}
    public String getName() {return name;}
}
class Department extends AcademicEntity {

    private final String description;
    private final AcademicMember head;

    public Department(String code, String name, String description, AcademicMember head) {
        super(code, name);
        this.description = description;
        this.head = head;
    }

    public AcademicMember getHead() {return head;}

    // Writes formatted department information to the output
    public static void writeDepartments(List<Department> departments, BufferedWriter writer) throws IOException {

        writer.write("---------------------------------------\n");
        writer.write("              DEPARTMENTS\n");
        writer.write("---------------------------------------\n");

        for (Department dep : departments) {
            writer.write("Department Code: " + dep.getCode() + "\n");
            writer.write("Name: " + dep.getName() + "\n");
            if (dep.getHead() != null) {
                writer.write("Head: " + dep.getHead().getName() + "\n");
            } else {
                writer.write("Head: Not assigned\n");
            }

        }
        writer.write("\n----------------------------------------\n\n");

    }
}
class Program extends AcademicEntity {

    private final String description;
    private final Department department;
    private final String degreeLevel;
    private final int totalCredits;

    public Program(String code, String name, String description, Department department, String degreeLevel, int totalCredits) {
        super(code, name);
        this.description = description;
        this.department = department;
        this.degreeLevel = degreeLevel;
        this.totalCredits = totalCredits;
    }

    public Department getDepartment() {return department;}
    public String getDegreeLevel() {return degreeLevel;}
    public int getTotalCredits() {return totalCredits;}

    // Writes formatted program information and associated courses
    public static void writePrograms(List<Program> programs, List<Course> courses, BufferedWriter writer) throws IOException {
        programs.sort(Comparator.comparing(Program::getCode));

        writer.write("--------------------------------------\n");
        writer.write("                PROGRAMS\n");
        writer.write("---------------------------------------\n");

        for (Program program : programs) {
            writer.write("Program Code: " + program.getCode() + "\n");
            writer.write("Name: " + program.getName() + "\n");
            writer.write("Department: " + program.getDepartment().getName() + "\n");
            writer.write("Degree Level: " + program.getDegreeLevel() + "\n");
            writer.write("Required Credits: " + program.getTotalCredits() + "\n");
            // List courses belonging to this program
            writer.write("Courses: ");
            boolean first = true;
            boolean foundCourse = false;
            for (Course course : courses) {
                if (course.getProgram() != null && course.getProgram().equals(program)) {
                    if (!foundCourse) {
                        writer.write("{");
                        foundCourse = true;
                    }
                    if (!first) writer.write(",");
                    writer.write(course.getCode());
                    first = false;
                }
            }
            if (foundCourse) {
                writer.write("}");
            } else {
                writer.write("-");
            }
            writer.write("\n\n");
        }
        writer.write("----------------------------------------\n\n");
    }
}
class Course extends AcademicEntity implements Reportable{

    private final Department department;
    private final int credits;
    private final String semester;
    private final Program program;

    private AcademicMember instructor;

    private Map<Student, String> grades = new HashMap<>();

    private List<Student> whoTake = new ArrayList<>();

    public Course(String code, String name, Department department, int credits, String semester, Program programCode) {
        super(code, name);
        this.department = department;
        this.credits = credits;
        this.semester = semester;
        this.program = programCode;
    }

    public Department getDepartment() {return department;}
    public int getCredits() {return credits;}
    public String getSemester() {return semester;}
    public Program getProgram() {return program;}

    public AcademicMember getInstructor() {return instructor;}
    public void setInstructor(AcademicMember instructor) {this.instructor = instructor;}

    public void addWhoTake(Student student){whoTake.add(student);}
    public List<Student> getWhoTake(){return whoTake;}

    public void assignGrade(Student student, String grade) {grades.put(student, grade);}

    // Writes basic course information (without grades)
    public static void writeCourses(List<Course> courses, BufferedWriter writer) throws IOException {
        courses.sort(Comparator.comparing(Course::getCode));

        writer.write("---------------------------------------\n");
        writer.write("                COURSES\n");
        writer.write("---------------------------------------\n");

        for (Course course : courses) {
            writer.write("Course Code: " + course.getCode() + "\n");
            writer.write("Name: " + course.getName() + "\n");
            writer.write("Department: " + course.getDepartment().getName() + "\n");
            writer.write("Credits: " + course.getCredits() + "\n");
            writer.write("Semester: " + course.getSemester() + "\n\n");
        }
        writer.write("----------------------------------------\n\n");
    }

    // Returns average grade as formatted string
    public String getGradeAverage() {
        double total = 0;
        int count = 0;
        for (String grade : grades.values()) {
            total += getPoint(grade);
            count++;
        }
        if (count == 0){
            return "0.00";
        }else{
            double average = Math.round((total / count) * 100.0) / 100.0;
            return String.format(Locale.US, "%.2f", average);
        }
    }

    // Writes a full report for this course, including students and grades
    @Override
    public void generateReport(BufferedWriter writer) throws IOException {
        writer.write("Course Code: " + getCode() + "\n");
        writer.write("Name: " + getName() + "\n");
        writer.write("Department: "+ getDepartment().getName() + "\n");
        writer.write("Credits: " + getCredits() + "\n");
        writer.write("Semester: " + getSemester() + "\n\n");
        writer.write("Instructor: ");
        if (getInstructor() != null) {
            writer.write(getInstructor().getName() + "\n\n");
        } else {
            writer.write("Not assigned" + "\n\n");
        }
        writer.write("Enrolled Students:\n");
        for(Student student: getWhoTake()){
            writer.write("- " + student.getName() + " (ID: " + student.getID() + ")" + "\n");
        }
        writer.write("\nGrade Distribution:" + "\n");
        Map<String, Integer> dist = getGradeDistribution();
        for (String grade : dist.keySet()) {
            writer.write(grade + ": " + dist.get(grade) + "\n");
        }
        writer.write("\nAverage Grade: " + getGradeAverage() + "\n");
        writer.write("\n----------------------------------------\n\n");
    }

    // Computes grade frequency distribution
    public Map<String, Integer> getGradeDistribution() {
        Map<String, Integer> distribution = new TreeMap<>();
        for (String grade : grades.values()) {
            distribution.put(grade, distribution.getOrDefault(grade, 0) + 1);
        }
        return distribution;
    }

    private double getPoint(String grade) {
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