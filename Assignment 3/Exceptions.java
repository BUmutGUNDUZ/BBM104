// Base exception class for all custom exceptions in the system
public class Exceptions extends RuntimeException {
    public Exceptions(String message) {
        super(message);
    }
}
// Thrown when a person type is not 'S' or 'F'
class InvalidPersonType extends Exceptions{
    public InvalidPersonType(String message) {
        super(message);
    }
}
// Thrown when a student with a given ID cannot be found
class StudentNotFound extends Exceptions{
    public StudentNotFound(String message){
        super(message);
    }
}
// Thrown when an academic member with a given ID cannot be found
class AcademicMemberNotFound extends Exceptions{
    public AcademicMemberNotFound(String message) {
        super(message);
    }
}
// Thrown when a course code is not found in the course list
class CourseNotFound extends Exceptions{
    public CourseNotFound(String message) {
        super(message);
    }
}
// Thrown when a letter grade is invalid (not in the allowed set)
class InvalidGrade extends Exceptions{
    public InvalidGrade(String message) {
        super(message);
    }
}
class ProgramNotFound extends Exceptions{
    public ProgramNotFound(String message) {
        super(message);
    }
}
