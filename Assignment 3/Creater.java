import java.io.*;
import java.util.*;

public class Creater {

    public static List<Person> createPersons(List<List<String>> rawData, BufferedWriter writer) {
        List<Person> persons = new ArrayList<>();

        try {
            writer.write("Reading Person Information \n");

            for (List<String> line : rawData) {
                String type = line.get(0);
                int id = Integer.parseInt(line.get(1));
                String name = line.get(2);
                String email = line.get(3);
                String department = line.get(4);

                try {
                    if (!type.equals("S") && !type.equals("F")) {
                        throw new InvalidPersonType("Invalid Person Type");
                    }

                    if (type.equals("S")) {
                        persons.add(new Student(id, name, email, department));
                    } else {
                        persons.add(new AcademicMember(id, name, email, department));
                    }
                } catch (InvalidPersonType e) {
                    writer.write(e.getMessage() + "\n");
                } catch (Exception e) {
                    writer.write("Something went wrong\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return persons;
    }

    public static List<Department> createDepartments(List<List<String>> rawData, List<Person> persons, BufferedWriter writer) {
        List<Department> departments = new ArrayList<>();
        try {
            writer.write("Reading Departments \n");

            for (List<String> line : rawData) {
                try {
                    String code = line.get(0);
                    String name = line.get(1);
                    String description = line.get(2);
                    int headID = Integer.parseInt(line.get(3));

                    AcademicMember head = findAcademicMemberByID(persons, headID);
                    if (head == null) {
                        writer.write("Academic Member Not Found with ID " + headID + "\n");
                    }
                    // Create department with or without head
                    departments.add(new Department(code, name, description, head));
                } catch (Exception e) {
                    try {
                        writer.write("Something went wrong while processing department.\n");
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return departments;
    }

    public static List<Program> createPrograms(List<List<String>> rawData, List<Department> departments, BufferedWriter writer) {
        List<Program> programs = new ArrayList<>();

        try {
            writer.write("Reading Programs \n");

            for (List<String> line : rawData) {
                try {
                    String code = line.get(0);
                    String name = line.get(1);
                    String description = line.get(2);
                    String deptName = line.get(3);
                    String degreeLevel = line.get(4);
                    int requiredCredits = Integer.parseInt(line.get(5));

                    Department dept = findDepartmentByName(departments, deptName);
                    if (dept == null) {
                        writer.write("Department " + deptName + " Not Found\n");
                    }
                    // Create program with or without department
                    programs.add(new Program(code, name, description, dept, degreeLevel, requiredCredits));
                } catch (Exception e) {
                    try {
                        writer.write("Something went wrong while processing program.\n");
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return programs;
    }

    public static List<Course> createCourses(List<List<String>> rawData, List<Department> departments, List<Program> programs, BufferedWriter writer) {
        List<Course> courses = new ArrayList<>();

        try {
            writer.write("Reading Courses \n");

            for (List<String> line : rawData) {
                try {
                    String code = line.get(0);
                    String name = line.get(1);
                    String departmentName = line.get(2);
                    int credits = Integer.parseInt(line.get(3));
                    String semester = line.get(4);
                    String programCode = line.get(5);

                    Department dept = findDepartmentByName(departments, departmentName);
                    if (dept == null) {
                        writer.write("Department " + departmentName + " Not Found\n");
                    }

                    Program program = findProgramByCode(programs, programCode);
                    if (program == null) {
                        throw new ProgramNotFound("Program " + programCode + " Not Found");
                    }
                    // Create course with or without valid department/program
                    courses.add(new Course(code, name, dept, credits, semester, program));
                }catch (ProgramNotFound e){
                    writer.write(e.getMessage() + "\n");
                }
                catch (Exception e) {
                    try {
                        writer.write("Something went wrong while processing course.\n");
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            }
            // Sort courses alphabetically by code
            courses.sort(Comparator.comparing(Course::getCode));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public static void assignCourses(List<List<String>> assignments, List<Person> persons, List<Course> courses, BufferedWriter writer){
        try {
            writer.write("Reading Course Assignments \n");

            for (List<String> assignment : assignments) {
                try {
                    String type = assignment.get(0);
                    int personID = Integer.parseInt(assignment.get(1));
                    String courseCode = assignment.get(2);
                    switch (type) {
                        case "S":
                            Student student = findStudentByID(persons, personID);
                            if (student == null) {
                                throw new StudentNotFound("Student Not Found with ID " + personID);
                            }
                            Course studentCourse = findCourseByCode(courses, courseCode);
                            if (studentCourse == null) {
                                throw new CourseNotFound("Course " + courseCode + " Not Found");
                            }
                            student.enrollToCourse(studentCourse);
                            studentCourse.addWhoTake(student);
                            break;
                        case "F":
                            AcademicMember academicMember = findAcademicMemberByID(persons, personID);
                            if (academicMember == null) {
                                throw new AcademicMemberNotFound("Academic Member Not Found with ID " + personID);
                            }
                            Course academicMemberCourse = findCourseByCode(courses, courseCode);
                            if (academicMemberCourse == null) {
                                throw new CourseNotFound("Course " + courseCode + " Not Found");
                            }
                            // Assign course to academic member and set instructor
                            academicMember.assignToCourse(academicMemberCourse);
                            academicMemberCourse.setInstructor(academicMember);
                            break;
                        default:
                            throw new InvalidPersonType("Invalid Person Type");
                    }

                } catch (CourseNotFound | StudentNotFound | AcademicMemberNotFound e) {
                    writer.write(e.getMessage() + "\n");
                } catch (Exception e) {
                    writer.write("Something went wrong\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void assignGrades(List<List<String>> grades, List<Person> persons, List<Course> courses, BufferedWriter writer){
        try {
            writer.write("Reading Grades \n");

            // Valid grade letters defined as per spec
            Set<String> validGrades = new HashSet<>(Arrays.asList("A1", "A2", "B1", "B2", "C1", "C2", "D1", "D2", "F3"));
            for (List<String> grade : grades) {
                try {
                    String letterGrade = grade.get(0);
                    int studentID = Integer.parseInt(grade.get(1));
                    String courseCode = grade.get(2);

                    Student student = findStudentByID(persons, studentID);
                    if (student == null) {
                        throw new StudentNotFound("Student Not Found with ID " + studentID);
                    }

                    Course course = findCourseByCode(courses, courseCode);
                    if (course == null) {
                        throw new CourseNotFound("Course " + courseCode + " Not Found");
                    }

                    if (!validGrades.contains(letterGrade)) {
                        throw new InvalidGrade("The grade " + letterGrade + " is not valid");
                    }
                    // Assign grade to both student and course
                    student.assignGrade(course, letterGrade);
                    course.assignGrade(student, letterGrade);
                } catch (InvalidGrade | StudentNotFound | CourseNotFound e) {
                    writer.write(e.getMessage() + "\n");
                } catch (Exception e) {
                    writer.write("Something went wrong\n");
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static AcademicMember findAcademicMemberByID(List<Person> persons, int ID) {
        for (Person person : persons) {
            if (person.getID() == ID && person instanceof AcademicMember) {
                return (AcademicMember) person;
            }
        }
        return null;
    }

    private static Department findDepartmentByName(List<Department> departments, String name) {
        for (Department department : departments) {
            if (department.getName().equals(name)) {
                return department;
            }
        }
        return null;
    }

    private static Program findProgramByCode(List<Program> programs, String code){
        for (Program program : programs) {
            if (program.getCode().equals(code)) {
                return program;
            }
        }
        return null;
    }

    private static Course findCourseByCode(List<Course> courses, String code){
        for (Course course : courses) {
            if (course.getCode().equals(code)) {
                return course;
            }
        }
        return null;
    }

    private static Student findStudentByID(List<Person> persons, int ID) {
        for (Person person : persons) {
            if (person.getID() == ID && person instanceof Student) {
                return (Student) person;
            }
        }
        return null;
    }
}