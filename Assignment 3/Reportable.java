import java.io.*;

public interface Reportable {
    void generateReport(BufferedWriter writer) throws IOException;
}
