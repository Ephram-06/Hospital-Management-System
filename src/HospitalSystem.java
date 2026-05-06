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
        printBanner();
        HospitalSystem hms = new HospitalSystem();
        hms.loadData();
        hms.run();
    }

    private static void printBanner() {
        System.out.println();
        System.out.println("  ╔══════════════════════════════════════════════════╗");
        System.out.println("  ║                                                  ║");
        System.out.println("  ║      HOSPITAL MANAGEMENT SYSTEM                  ║");
        System.out.println("  ║      COSC 214 — Data Structures & Algorithms     ║");
        System.out.println("  ║                                                  ║");
        System.out.println("  ╚══════════════════════════════════════════════════╝");
        System.out.println();
        System.out.print("  Initializing system");
        for (int i = 0; i < 3; i++) {
            try { Thread.sleep(350); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            System.out.print(".");
        }
        System.out.println();
    }

    // ─── Data Loading ────────────────────────────────────────────────────────────

    private void loadData() {
        try {
            List<Doctor> doctors = CSVLoader.loadDoctors("data/doctors.csv");
            for (Doctor d : doctors) doctorMap.addDoctor(d);
            System.out.println("  ✓  " + doctors.size() + " doctors loaded.");

            allPatients.addAll(CSVLoader.loadPatients("data/patients.csv"));
            nextId = allPatients.size() + 1;
            for (PatientRecord p : allPatients) {
                patientList.insert(p);
                // Auto-register doctor from patient record if not already known
                if (doctorMap.getDoctor(p.doctorName) == null) {
                    doctorMap.addDoctor(new Doctor(p.doctorName, "General Practice", "N/A"));
                }
                if (p.admissionType.equalsIgnoreCase("emergency") ||
                    p.admissionType.equalsIgnoreCase("urgent")) {
                    emergencyQ.insert(p);
                } else {
                    appointmentQueue.enqueue(p);
                }
            }
            System.out.println("  ✓  " + allPatients.size() + " patients loaded.");
            System.out.println();
            System.out.println("  System ready. Welcome!");
            System.out.println();
        } catch (IOException e) {
            System.out.println("  Warning: Could not load CSV files — starting with empty system.");
        }
        search = new SearchModule(patientList, doctorMap);
    }

    // ─── Main Menu Loop ──────────────────────────────────────────────────────────

    private void run() {
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("  Choice: ");
            String choice = sc.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1":  addPatient(sc);                                          break;
                case "2":  listAllPatients(sc);           if (pauseOrExit(sc)) running = false; break;
                case "3":  searchPatient(sc);            if (pauseOrExit(sc)) running = false; break;
                case "4":  removePatient(sc);                                       break;
                case "5":  viewDoctors(sc);               if (pauseOrExit(sc)) running = false; break;
                case "6":  addDoctor(sc);                                           break;
                case "7":  removeDoctor(sc);                                        break;
                case "8":  processNextAppointment();     if (pauseOrExit(sc)) running = false; break;
                case "9":  viewAppointmentQueues();      if (pauseOrExit(sc)) running = false; break;
                case "10": logTreatment(sc);                                        break;
                case "11": undoTreatment();              if (pauseOrExit(sc)) running = false; break;
                case "12": viewTreatmentLog();           if (pauseOrExit(sc)) running = false; break;
                case "13": billingStatistics();          if (pauseOrExit(sc)) running = false; break;
                case "14": BenchmarkRunner.run(patientList, allPatients);
                                                         if (pauseOrExit(sc)) running = false; break;
                case "15": sortPatients(sc);              if (pauseOrExit(sc)) running = false; break;
                case "0":  running = false; break;
                default:   System.out.println("  Invalid option, try again.");
            }
        }
        System.out.println("  Goodbye.");
        sc.close();
    }

    // Returns true if user typed 0 (wants to exit), false to continue
    private boolean pauseOrExit(Scanner sc) {
        System.out.print("\n  Press ENTER to return to the menu, or type 0 to exit: ");
        return "0".equals(sc.nextLine().trim());
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
        System.out.println("║   15. Sort & Browse Patients                 ║");
        System.out.println("║   0.  Exit                                   ║");
        System.out.println("╚══════════════════════════════════════════════╝");
    }

    // ─── Patient Management ──────────────────────────────────────────────────────

    private void addPatient(Scanner sc) {
        try {
            System.out.print("  Name: ");                           String name     = sc.nextLine().trim();
            System.out.print("  Age: ");                            int    age      = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Gender (Male/Female): ");           String gender   = sc.nextLine().trim();
            System.out.print("  Blood Type (e.g. A+): ");           String blood    = sc.nextLine().trim();
            System.out.print("  Diagnosis: ");                      String diag     = sc.nextLine().trim();
            System.out.print("  Date of Admission (MM/DD/YYYY): "); String admDate  = sc.nextLine().trim();
            System.out.print("  Doctor Name: ");                    String docName  = sc.nextLine().trim();
            System.out.print("  Hospital: ");                       String hosp     = sc.nextLine().trim();
            System.out.print("  Insurance Provider: ");             String ins      = sc.nextLine().trim();
            System.out.print("  Billing Amount ($): ");             double billing  = Double.parseDouble(sc.nextLine().trim());
            System.out.print("  Room Number: ");                    int    room     = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Admission Type (Emergency/Urgent/Elective): "); String admType = sc.nextLine().trim();
            System.out.print("  Discharge Date (MM/DD/YYYY): ");    String discDate = sc.nextLine().trim();
            System.out.print("  Medication: ");                     String med      = sc.nextLine().trim();
            System.out.print("  Test Results (Normal/Abnormal/Inconclusive): "); String results = sc.nextLine().trim();

            PatientRecord p = new PatientRecord(nextId++, name, age, gender, blood, diag,
                    admDate, docName, hosp, ins, billing, room, admType, discDate, med, results);
            patientList.insert(p);
            allPatients.add(p);
            if (doctorMap.getDoctor(docName) == null)
                doctorMap.addDoctor(new Doctor(docName, "General Practice", "N/A"));

            if (admType.equalsIgnoreCase("emergency") || admType.equalsIgnoreCase("urgent")) {
                emergencyQ.insert(p);
            } else {
                appointmentQueue.enqueue(p);
            }
            System.out.println("  Patient added: " + p);
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input - patient not added.");
        }
    }

    private void listAllPatients(Scanner sc) {
        int total = allPatients.size();
        System.out.println("  Total patients loaded: " + total);
        System.out.println();
        System.out.println("  How would you like to browse?");
        System.out.println("    1. Browse all   (20 per page)");
        System.out.println("    2. Sort first, then browse");
        System.out.println("    3. Quick summary (first 10 + stats)");
        System.out.println("    0. Back");
        System.out.print("  Choice: ");
        switch (sc.nextLine().trim()) {
            case "1":
                browsePaged(allPatients, sc, "All Patients");
                break;
            case "2":
                sortPatients(sc);
                break;
            case "3": {
                System.out.println("--- First 10 of " + total + " patients ---");
                for (int i = 0; i < Math.min(10, total); i++)
                    System.out.println("  " + allPatients.get(i));
                if (total > 10) System.out.println("  ... and " + (total - 10) + " more.");
                break;
            }
            default: break;
        }
    }

    /** Shows patients 20 at a time. Press Enter for next page, 0 to stop. */
    private void browsePaged(List<PatientRecord> list, Scanner sc, String title) {
        final int PAGE = 20;
        int total = list.size();
        if (total == 0) { System.out.println("  No patients to display."); return; }
        int page = 0;
        while (page * PAGE < total) {
            int from = page * PAGE;
            int to   = Math.min(from + PAGE, total);
            System.out.println();
            System.out.println("--- " + title + " [" + (from + 1) + "-" + to + " of " + total + "] ---");
            for (int i = from; i < to; i++)
                System.out.println("  " + list.get(i));
            if (to >= total) { System.out.println("  (end of list)"); break; }
            System.out.print("  [ENTER] next page   [0] stop: ");
            String inp = sc.nextLine().trim();
            if ("0".equals(inp)) break;
            page++;
        }
    }

    private void searchPatient(Scanner sc) {
        System.out.println("  Search by:");
        System.out.println("    1) ID (exact)        2) Name (partial)");
        System.out.println("    3) Diagnosis         4) Blood Type");
        System.out.println("    5) Hospital          6) Test Results");
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
                        System.out.printf("    Blood Type: %s | Hospital: %s | Room: %d%n", r.bloodType, r.hospital, r.roomNumber);
                        System.out.printf("    Admitted: %s | Discharged: %s%n", r.dateOfAdmission, r.dischargeDate);
                        System.out.printf("    Medication: %s | Test Results: %s%n", r.medication, r.testResults);
                        System.out.printf("    Insurance: %s%n", r.insuranceProvider);
                        Doctor d = search.findDoctorByName(r.doctorName);
                        if (d != null) System.out.println("    Assigned Doctor: " + d);
                    } else System.out.println("  Not found.");
                } catch (NumberFormatException e) { System.out.println("  Invalid ID."); }
                break;
            }
            case "2": {
                System.out.print("  Name (partial ok): ");
                List<PatientRecord> res = search.findPatientsByName(sc.nextLine().trim());
                printSearchResults(res, sc);
                break;
            }
            case "3": {
                System.out.print("  Diagnosis (partial ok): ");
                List<PatientRecord> res = search.findPatientsByDiagnosis(sc.nextLine().trim());
                printSearchResults(res, sc);
                break;
            }
            case "4": {
                System.out.print("  Blood Type (e.g. A+): ");
                List<PatientRecord> res = search.findPatientsByBloodType(sc.nextLine().trim());
                printSearchResults(res, sc);
                break;
            }
            case "5": {
                System.out.print("  Hospital name (partial ok): ");
                List<PatientRecord> res = search.findPatientsByHospital(sc.nextLine().trim());
                printSearchResults(res, sc);
                break;
            }
            case "6": {
                System.out.println("  Test Results: 1) Normal  2) Abnormal  3) Inconclusive");
                System.out.print("  Choice: ");
                String tr = sc.nextLine().trim();
                String keyword = tr.equals("1") ? "Normal" : tr.equals("2") ? "Abnormal" : "Inconclusive";
                List<PatientRecord> res = search.findPatientsByTestResults(keyword);
                printSearchResults(res, sc);
                break;
            }
            default: System.out.println("  Invalid option.");
        }
    }

    /** Print search results with paging if > 20 hits. */
    private void printSearchResults(List<PatientRecord> res, Scanner sc) {
        if (res.isEmpty()) { System.out.println("  No matches."); return; }
        System.out.println("  Found " + res.size() + " match(es).");
        if (res.size() > 20) {
            System.out.print("  [ENTER] browse paged   [A] show all: ");
            String inp = sc.nextLine().trim();
            if ("a".equalsIgnoreCase(inp)) res.forEach(r -> System.out.println("  " + r));
            else browsePaged(res, sc, "Search Results");
        } else {
            res.forEach(r -> System.out.println("  " + r));
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

    private void viewDoctors(Scanner sc) {
        List<Doctor> doctors = new ArrayList<>(doctorMap.getAllDoctors());
        doctors.sort((a, b) -> a.name.compareTo(b.name));
        if (doctors.isEmpty()) { System.out.println("  (no doctors on record)"); return; }
        System.out.println("  Total doctors: " + doctors.size());
        browseDoctorsPaged(doctors, sc);
    }

    private void browseDoctorsPaged(List<Doctor> doctors, Scanner sc) {
        final int PAGE = 20;
        int page = 0;
        int total = doctors.size();
        int totalPages = (total + PAGE - 1) / PAGE;
        while (true) {
            int from = page * PAGE;
            int to   = Math.min(from + PAGE, total);
            System.out.println();
            System.out.println("  --- Doctors " + (from + 1) + "-" + to + " of " + total
                    + "  (page " + (page + 1) + "/" + totalPages + ") ---");
            for (int i = from; i < to; i++) {
                System.out.println("  " + (i + 1) + ". " + doctors.get(i));
            }
            System.out.println();
            if (totalPages == 1) break;
            System.out.print("  [N]ext  [P]rev  [Q]uit listing > ");
            String cmd = sc.nextLine().trim().toLowerCase();
            if (cmd.equals("n") && page < totalPages - 1) page++;
            else if (cmd.equals("p") && page > 0) page--;
            else if (cmd.equals("q") || cmd.isEmpty()) break;
        }
    }

    private void addDoctor(Scanner sc) {
        System.out.print("  Name: ");               String name  = sc.nextLine().trim();
        System.out.print("  Specialization: ");     String spec  = sc.nextLine().trim();
        System.out.print("  Available Hours: ");    String hours = sc.nextLine().trim();
        Doctor d = new Doctor(name, spec, hours);
        doctorMap.addDoctor(d);
        System.out.println("  Doctor added: " + d);
    }

    private void removeDoctor(Scanner sc) {
        System.out.print("  Doctor name to remove: ");
        String name = sc.nextLine().trim();
        if (doctorMap.removeDoctor(name)) System.out.println("  Doctor '" + name + "' removed.");
        else System.out.println("  Doctor '" + name + "' not found.");
    }

    // ─── Appointment Queues ──────────────────────────────────────────────────────

    private void processNextAppointment() {
        if (!emergencyQ.isEmpty()) {
            PatientRecord p = emergencyQ.extractMin();
            System.out.println("  Serving [PRIORITY - " + p.admissionType.toUpperCase() + "]: " + p);
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
        // Test-result buckets
        int    normCount = 0, abnCount = 0, incCount = 0;
        double normTotal = 0, abnTotal = 0, incTotal = 0;
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
            String tr = p.testResults == null ? "" : p.testResults.toLowerCase();
            if      (tr.equals("normal"))       { normCount++; normTotal += p.billingAmount; }
            else if (tr.equals("abnormal"))     { abnCount++;  abnTotal  += p.billingAmount; }
            else                                { incCount++;  incTotal  += p.billingAmount; }
        }

        System.out.println("--- Billing & Admission Statistics ---");
        System.out.printf("  Total patients     : %d%n",           allPatients.size());
        System.out.printf("  Emergency          : %d%n",           emergency);
        System.out.printf("  Urgent             : %d%n",           urgent);
        System.out.printf("  Elective           : %d%n",           elective);
        System.out.printf("  Total billing      : $%,.2f%n",       total);
        System.out.printf("  Average billing    : $%,.2f%n",       total / allPatients.size());
        System.out.printf("  Highest billing    : $%,.2f  - %s%n", max, highestBill != null ? highestBill.name : "N/A");
        System.out.printf("  Lowest billing     : $%,.2f%n",       min);
        System.out.println();
        System.out.println("  --- Billing by Test Results ---");
        System.out.printf("  Normal      : %5d patients  |  avg $%,.2f%n", normCount, normCount > 0 ? normTotal / normCount : 0);
        System.out.printf("  Abnormal    : %5d patients  |  avg $%,.2f%n", abnCount,  abnCount  > 0 ? abnTotal  / abnCount  : 0);
        System.out.printf("  Inconclusive: %5d patients  |  avg $%,.2f%n", incCount,  incCount  > 0 ? incTotal  / incCount  : 0);
    }

    private void sortPatients(Scanner sc) {
        System.out.println("  Sort by:  1) Name   2) Age   3) Billing Amount");
        System.out.print("  Field: ");
        String fieldOpt = sc.nextLine().trim();
        String field = fieldOpt.equals("1") ? "name" : fieldOpt.equals("2") ? "age" : "billing";

        System.out.println("  Algorithm:  1) Bubble Sort   2) Merge Sort   3) Quick Sort");
        System.out.print("  Choice: ");
        String algOpt = sc.nextLine().trim();

        long start = System.nanoTime();
        List<PatientRecord> sorted;
        String algName;
        switch (algOpt) {
            case "1": sorted = SortingModule.bubbleSort(allPatients, field); algName = "Bubble Sort"; break;
            case "3": sorted = SortingModule.quickSort(allPatients,  field); algName = "Quick Sort";  break;
            default:  sorted = SortingModule.mergeSort(allPatients,  field); algName = "Merge Sort";  break;
        }
        long elapsed = System.nanoTime() - start;

        System.out.printf("%n  Sorted %,d patients by [%s] using %s in %,d ns%n",
                sorted.size(), field, algName, elapsed);
        browsePaged(sorted, sc, "Sorted by " + field);
    }
}
