import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args){

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(args[6]));

            // Read and parse input files
            List<List<String>> persons = Reader.readFileAndParse(args[0]);
            List<List<String>> departments = Reader.readFileAndParse(args[1]);
            List<List<String>> programs = Reader.readFileAndParse(args[2]);
            List<List<String>> courses = Reader.readFileAndParse(args[3]);
            List<List<String>> assignments = Reader.readFileAndParse(args[4]);
            List<List<String>> grades = Reader.readFileAndParse(args[5]);

            // Create objects from input data
            List<Person> createdPersons = Creater.createPersons(persons, writer);
            List<Department> createdDepartments = Creater.createDepartments(departments, createdPersons, writer);
            List<Program> createdPrograms = Creater.createPrograms(programs, createdDepartments, writer);
            List<Course> createdCourses = Creater.createCourses(courses, createdDepartments, createdPrograms, writer);

            // Sort persons and courses
            createdCourses.sort(Comparator.comparing(Course::getCode));
            createdPersons.sort(Comparator.comparing(Person::getID));

            // Assign courses and grades
            Creater.assignCourses(assignments, createdPersons, createdCourses, writer);
            Creater.assignGrades(grades, createdPersons, createdCourses, writer);

            // Write general reports
            Person.writePersons(createdPersons, writer);
            Department.writeDepartments(createdDepartments, writer);
            Program.writePrograms(createdPrograms, createdCourses, writer);
            Course.writeCourses(createdCourses, writer);

            // Write course reports
            writer.write("----------------------------------------\n");
            writer.write("            COURSE REPORTS\n");
            writer.write("----------------------------------------\n");
            for (Course course : createdCourses) {
                course.generateReport(writer);
            }

            // Write student reports
            writer.write("----------------------------------------\n");
            writer.write("            STUDENT REPORTS\n");
            writer.write("----------------------------------------\n");
            for (Person person : createdPersons) {
                if (person instanceof Student) {
                    ((Student) person).generateReport(writer);
                }
            }
            writer.close();
        }catch (Exception e){
            System.out.println("Something went wrong");
        }
    }
}