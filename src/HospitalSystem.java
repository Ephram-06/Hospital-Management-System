import java.io.IOException;
import java.util.*;

public class HospitalSystem {

    private final PatientLinkedList patientList     = new PatientLinkedList();
    private final DoctorHashMap doctorMap           = new DoctorHashMap();
    private final AppointmentQueue appointmentQueue = new AppointmentQueue();
    private final EmergencyPriorityQueue emergencyQ = new EmergencyPriorityQueue();
    private final TreatmentStack treatmentStack     = new TreatmentStack();
    private SearchModule search;

    private final List<PatientRecord> allPatients = new ArrayList<>();
    private int nextId = 1;

    // ─── Entry Point ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        HospitalSystem hms = new HospitalSystem();
        hms.loadData();
        hms.run();
    }

    // ─── Data Loading ────────────────────────────────────────────────────────────

    private void loadData() {
        try {
            List<Doctor> doctors = CSVLoader.loadDoctors("data/doctors.csv");
            for (Doctor d : doctors) doctorMap.addDoctor(d);
            System.out.println("  Loaded " + doctors.size() + " doctors.");

            allPatients.addAll(CSVLoader.loadPatients("data/patients.csv"));
            nextId = allPatients.size() + 1;
            for (PatientRecord p : allPatients) {
                patientList.insert(p);
                if (p.admissionType.equalsIgnoreCase("emergency") ||
                    p.admissionType.equalsIgnoreCase("urgent")) {
                    emergencyQ.insert(p);
                } else {
                    appointmentQueue.enqueue(p);
                }
            }
            System.out.println("  Loaded " + allPatients.size() + " patients.");
        } catch (IOException e) {
            System.out.println("  Warning: Could not load CSV files — starting with empty system.");
        }
        search = new SearchModule(patientList, doctorMap);
    }

    // ─── Main Menu Loop ──────────────────────────────────────────────────────────

    private void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            printMenu();
            System.out.print("  Choice: ");
            String choice = sc.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1":  addPatient(sc);              break;
                case "2":  listAllPatients();            break;
                case "3":  searchPatient(sc);            break;
                case "4":  removePatient(sc);            break;
                case "5":  viewDoctors();                break;
                case "6":  addDoctor(sc);                break;
                case "7":  removeDoctor(sc);             break;
                case "8":  processNextAppointment();     break;
                case "9":  viewAppointmentQueues();      break;
                case "10": logTreatment(sc);             break;
                case "11": undoTreatment();              break;
                case "12": viewTreatmentLog();           break;
                case "13": billingStatistics();          break;
                case "14": BenchmarkRunner.run(patientList, allPatients);
                           System.out.print("\n  Press ENTER to return to the menu, or type 0 to exit: ");
                           if ("0".equals(sc.nextLine().trim())) { System.out.println("  Goodbye."); sc.close(); return; }
                           break;
                case "0":  System.out.println("  Goodbye."); sc.close(); return;
                default:   System.out.println("  Invalid option, try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║      Hospital Management System  (HMS)       ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  PATIENT MANAGEMENT                          ║");
        System.out.println("║   1.  Add Patient                            ║");
        System.out.println("║   2.  List All Patients                      ║");
        System.out.println("║   3.  Search Patient                         ║");
        System.out.println("║   4.  Remove Patient                         ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  DOCTOR MANAGEMENT                           ║");
        System.out.println("║   5.  View All Doctors                       ║");
        System.out.println("║   6.  Add Doctor                             ║");
        System.out.println("║   7.  Remove Doctor                          ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  APPOINTMENTS                                ║");
        System.out.println("║   8.  Process Next Appointment               ║");
        System.out.println("║   9.  View Appointment Queues                ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  TREATMENT LOG                               ║");
        System.out.println("║   10. Log Treatment Action                   ║");
        System.out.println("║   11. Undo Last Treatment                    ║");
        System.out.println("║   12. View Treatment Log                     ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  REPORTS & TOOLS                             ║");
        System.out.println("║   13. Billing Statistics                     ║");
        System.out.println("║   14. Run Performance Benchmarks             ║");
        System.out.println("║   0.  Exit                                   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    // ─── Patient Management ──────────────────────────────────────────────────────

    private void addPatient(Scanner sc) {
        try {
            System.out.print("  Name: ");              String name    = sc.nextLine().trim();
            System.out.print("  Age: ");               int age        = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Gender (Male/Female): "); String gender = sc.nextLine().trim();
            System.out.print("  Diagnosis: ");         String diag    = sc.nextLine().trim();
            System.out.print("  Admission Type (Emergency/Urgent/Elective): ");
                                                       String admType = sc.nextLine().trim();
            System.out.print("  Doctor ID: ");         int docId      = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Billing Amount ($): "); double billing = Double.parseDouble(sc.nextLine().trim());

            PatientRecord p = new PatientRecord(nextId++, name, age, gender, diag, admType, docId, billing);
            patientList.insert(p);
            allPatients.add(p);

            if (admType.equalsIgnoreCase("emergency") || admType.equalsIgnoreCase("urgent")) {
                emergencyQ.insert(p);
            } else {
                appointmentQueue.enqueue(p);
            }
            System.out.println("  Patient added: " + p);
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input — patient not added.");
        }
    }

    private void listAllPatients() {
        System.out.println("--- All Patients (" + patientList.size() + ") ---");
        patientList.printAll();
    }

    private void searchPatient(Scanner sc) {
        System.out.println("  Search by:  1) ID   2) Name   3) Diagnosis");
        System.out.print("  Choice: ");
        String opt = sc.nextLine().trim();
        switch (opt) {
            case "1": {
                System.out.print("  Patient ID: ");
                try {
                    int id = Integer.parseInt(sc.nextLine().trim());
                    PatientRecord r = search.findPatientById(id);
                    if (r != null) {
                        System.out.println("  Found: " + r);
                        Doctor d = search.findDoctorById(r.doctorId);
                        if (d != null) System.out.println("  Assigned Doctor: " + d);
                    } else System.out.println("  Not found.");
                } catch (NumberFormatException e) { System.out.println("  Invalid ID."); }
                break;
            }
            case "2": {
                System.out.print("  Name (partial ok): ");
                List<PatientRecord> res = search.findPatientsByName(sc.nextLine().trim());
                if (res.isEmpty()) System.out.println("  No matches.");
                else res.forEach(r -> System.out.println("  " + r));
                break;
            }
            case "3": {
                System.out.print("  Diagnosis (partial ok): ");
                List<PatientRecord> res = search.findPatientsByDiagnosis(sc.nextLine().trim());
                if (res.isEmpty()) System.out.println("  No matches.");
                else res.forEach(r -> System.out.println("  " + r));
                break;
            }
            default: System.out.println("  Invalid option.");
        }
    }

    private void removePatient(Scanner sc) {
        System.out.print("  Patient ID to remove: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            boolean removed = patientList.deleteById(id);
            if (removed) {
                allPatients.removeIf(p -> p.id == id);
                System.out.println("  Patient #" + id + " removed.");
            } else {
                System.out.println("  Patient #" + id + " not found.");
            }
        } catch (NumberFormatException e) { System.out.println("  Invalid ID."); }
    }

    // ─── Doctor Management ───────────────────────────────────────────────────────

    private void viewDoctors() {
        System.out.println("--- Doctors (" + doctorMap.size() + ") ---");
        doctorMap.printAll();
    }

    private void addDoctor(Scanner sc) {
        try {
            System.out.print("  Doctor ID: ");          int id    = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Name: ");               String name  = sc.nextLine().trim();
            System.out.print("  Specialization: ");     String spec  = sc.nextLine().trim();
            System.out.print("  Available Hours: ");    String hours = sc.nextLine().trim();
            Doctor d = new Doctor(id, name, spec, hours);
            doctorMap.addDoctor(d);
            System.out.println("  Doctor added: " + d);
        } catch (NumberFormatException e) { System.out.println("  Invalid input — doctor not added."); }
    }

    private void removeDoctor(Scanner sc) {
        System.out.print("  Doctor ID to remove: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            if (doctorMap.removeDoctor(id)) System.out.println("  Doctor #" + id + " removed.");
            else System.out.println("  Doctor #" + id + " not found.");
        } catch (NumberFormatException e) { System.out.println("  Invalid ID."); }
    }

    // ─── Appointment Queues ──────────────────────────────────────────────────────

    private void processNextAppointment() {
        if (!emergencyQ.isEmpty()) {
            PatientRecord p = emergencyQ.extractMin();
            System.out.println("  Serving [PRIORITY — " + p.admissionType.toUpperCase() + "]: " + p);
        } else if (!appointmentQueue.isEmpty()) {
            PatientRecord p = appointmentQueue.dequeue();
            System.out.println("  Serving [REGULAR]: " + p);
        } else {
            System.out.println("  No patients in any queue.");
        }
    }

    private void viewAppointmentQueues() {
        System.out.println("--- Emergency / Priority Queue (" + emergencyQ.size() + ") ---");
        emergencyQ.printAll();
        System.out.println("\n--- Regular Appointment Queue (" + appointmentQueue.size() + ") ---");
        appointmentQueue.printAll();
    }

    // ─── Treatment Log ────────────────────────────────────────────────────────────

    private void logTreatment(Scanner sc) {
        System.out.print("  Treatment action: ");
        String action = sc.nextLine().trim();
        treatmentStack.push(action);
        System.out.println("  Logged: " + action);
    }

    private void undoTreatment() {
        String action = treatmentStack.pop();
        if (action != null) System.out.println("  Undone: " + action);
        else System.out.println("  No treatment actions to undo.");
    }

    private void viewTreatmentLog() {
        System.out.println("--- Treatment Log (" + treatmentStack.size() + " entries) ---");
        treatmentStack.printAll();
    }

    // ─── Billing Statistics (extra feature) ─────────────────────────────────────

    private void billingStatistics() {
        if (allPatients.isEmpty()) { System.out.println("  No patient data."); return; }

        double total = 0, max = Double.MIN_VALUE, min = Double.MAX_VALUE;
        int emergency = 0, urgent = 0, elective = 0;
        PatientRecord highestBill = null;

        for (PatientRecord p : allPatients) {
            total += p.billingAmount;
            if (p.billingAmount > max) { max = p.billingAmount; highestBill = p; }
            if (p.billingAmount < min) min = p.billingAmount;
            switch (p.admissionType.toLowerCase()) {
                case "emergency": emergency++; break;
                case "urgent":    urgent++;    break;
                default:          elective++;  break;
            }
        }

        System.out.println("--- Billing & Admission Statistics ---");
        System.out.printf("  Total patients     : %d%n",           allPatients.size());
        System.out.printf("  Emergency          : %d%n",           emergency);
        System.out.printf("  Urgent             : %d%n",           urgent);
        System.out.printf("  Elective           : %d%n",           elective);
        System.out.printf("  Total billing      : $%,.2f%n",       total);
        System.out.printf("  Average billing    : $%,.2f%n",       total / allPatients.size());
        System.out.printf("  Highest billing    : $%,.2f  — %s%n", max, highestBill != null ? highestBill.name : "N/A");
        System.out.printf("  Lowest billing     : $%,.2f%n",       min);
    }
}
