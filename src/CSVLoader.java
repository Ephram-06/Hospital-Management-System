import java.io.*;
import java.util.*;

public class CSVLoader {

    // Kaggle column order:
    // 0=Name, 1=Age, 2=Gender, 3=Blood Type, 4=Medical Condition,
    // 5=Date of Admission, 6=Doctor, 7=Hospital, 8=Insurance Provider,
    // 9=Billing Amount, 10=Room Number, 11=Admission Type,
    // 12=Discharge Date, 13=Medication, 14=Test Results
    public static List<PatientRecord> loadPatients(String filePath) throws IOException {
        List<PatientRecord> records = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        reader.readLine(); // skip header
        String line;
        int id = 1;
        while ((line = reader.readLine()) != null) {
            String[] p = parseLine(line);
            if (p.length < 15) continue;
            try {
                String name              = toTitleCase(p[0].trim());
                int    age               = Integer.parseInt(p[1].trim());
                String gender            = p[2].trim();
                String bloodType         = p[3].trim();
                String diagnosis         = p[4].trim();
                String dateOfAdmission   = p[5].trim();
                String doctorName        = toTitleCase(p[6].trim());
                String hospital          = p[7].trim();
                String insuranceProvider = p[8].trim();
                double billing           = Double.parseDouble(p[9].trim());
                int    roomNumber        = Integer.parseInt(p[10].trim());
                String admissionType     = p[11].trim();
                String dischargeDate     = p[12].trim();
                String medication        = p[13].trim();
                String testResults       = p[14].trim();
                records.add(new PatientRecord(id++, name, age, gender, bloodType, diagnosis,
                        dateOfAdmission, doctorName, hospital, insuranceProvider,
                        billing, roomNumber, admissionType, dischargeDate, medication, testResults));
            } catch (NumberFormatException e) {
                System.err.println("Skipping malformed row: " + line);
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
            if (parts.length < 3) continue;
            try {
                String name           = parts[0].trim();
                String specialization = parts[1].trim();
                String hours          = parts[2].trim();
                doctors.add(new Doctor(name, specialization, hours));
            } catch (Exception e) {
                System.err.println("Skipping malformed doctor row: " + line);
            }
        }
        reader.close();
        return doctors;
    }

    private static String toTitleCase(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] words = s.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (w.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(w.charAt(0)));
            sb.append(w.substring(1).toLowerCase());
        }
        return sb.toString();
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
