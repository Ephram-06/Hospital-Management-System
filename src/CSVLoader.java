import java.io.*;
import java.util.*;

public class CSVLoader {

    public static List<PatientRecord> loadPatients(String filePath) throws IOException {
        List<PatientRecord> records = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        reader.readLine(); // skip header
        String line;
        int id = 1;
        while ((line = reader.readLine()) != null) {
            String[] parts = parseLine(line);
            if (parts.length < 7) continue;
            try {
                String name        = parts[0].trim();
                int age            = Integer.parseInt(parts[1].trim());
                String gender      = parts[2].trim();
                String diagnosis   = parts[3].trim();
                String admType     = parts[4].trim();
                int doctorId       = Integer.parseInt(parts[5].trim());
                double billing     = Double.parseDouble(parts[6].trim());
                records.add(new PatientRecord(id++, name, age, gender, diagnosis, admType, doctorId, billing));
            } catch (NumberFormatException e) {
                System.err.println("Skipping malformed patient row: " + line);
            }
        }
        reader.close();
        return records;
    }

    public static List<Doctor> loadDoctors(String filePath) throws IOException {
        List<Doctor> doctors = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        reader.readLine(); // skip header
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = parseLine(line);
            if (parts.length < 4) continue;
            try {
                int doctorId        = Integer.parseInt(parts[0].trim());
                String name         = parts[1].trim();
                String specialization = parts[2].trim();
                String hours        = parts[3].trim();
                doctors.add(new Doctor(doctorId, name, specialization, hours));
            } catch (NumberFormatException e) {
                System.err.println("Skipping malformed doctor row: " + line);
            }
        }
        reader.close();
        return doctors;
    }

    private static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }
}
