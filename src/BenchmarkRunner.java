import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BenchmarkRunner {

    private static final int   RUNS  = 5;
    private static final int[] SIZES = {1000, 2000, 5000, 10000};

    private static final String[] ADM_TYPES = {"Emergency", "Urgent", "Elective"};

    // ─── Called from the menu ────────────────────────────────────────────────────

    public static void run(PatientLinkedList list, List<PatientRecord> allRecords) {
        System.out.println("\n  Running benchmarks across 4 data sizes (" + RUNS + " runs each)...");
        System.out.println("  Time complexity: O(n)=linear  O(1)=constant  O(log n)=logarithmic\n");

        long[][] llResults   = new long[SIZES.length][3]; // Insert, Search, Delete
        long[][] arrResults  = new long[SIZES.length][3]; // Insert, Search, Delete
        long[][] htResults   = new long[SIZES.length][3]; // Insert, Lookup, Delete
        long[][] qResults    = new long[SIZES.length][4]; // Enqueue, Dequeue, PQ Insert, PQ Extract
        long[][] sortResults = new long[SIZES.length][3]; // Bubble, Merge, Quick
        long[][] binResults  = new long[SIZES.length][2]; // Linear Search, Binary Search

        for (int s = 0; s < SIZES.length; s++) {
            List<PatientRecord> dataset = generateDataset(SIZES[s]);
            int midId = dataset.get(SIZES[s] / 2).id;
            System.out.printf("  Benchmarking %,d records...%n", SIZES[s]);

            llResults[s][0]  = avgLLInsert(dataset);
            llResults[s][1]  = avgLLSearch(dataset, midId);
            llResults[s][2]  = avgLLDelete(dataset, midId);

            arrResults[s][0] = avgArrayInsert(dataset);
            arrResults[s][1] = avgArraySearch(dataset, midId);
            arrResults[s][2] = avgArrayDelete(dataset, midId);

            htResults[s][0]  = avgHashInsert(dataset);
            htResults[s][1]  = avgHashLookup(dataset, midId);
            htResults[s][2]  = avgHashDelete(dataset, midId);

            qResults[s][0]   = avgQueueEnqueue(dataset);
            qResults[s][1]   = avgQueueDequeue(dataset);
            qResults[s][2]   = avgPQInsert(dataset);
            qResults[s][3]   = avgPQExtract(dataset);

            sortResults[s][0] = avgBubbleSort(dataset);
            sortResults[s][1] = avgMergeSort(dataset);
            sortResults[s][2] = avgQuickSort(dataset);

            // Binary search requires a pre-sorted list (sorted by ID = natural order here)
            List<PatientRecord> sorted = SortingModule.mergeSort(dataset, "id-not-a-field");
            // dataset is generated with sequential IDs so it is already sorted by ID
            binResults[s][0]  = avgLinearSearch(dataset, midId);
            binResults[s][1]  = avgBinarySearch(dataset, midId);
        }

        StringBuilder sb = new StringBuilder();
        appendTable(sb, "LINKED LIST",
                new String[]{"Insert O(1)", "Search O(n)", "Delete O(n)"}, llResults);
        appendTable(sb, "ARRAY (ArrayList)",
                new String[]{"Insert O(1)*", "Search O(n)", "Delete O(n)"}, arrResults);
        appendTable(sb, "HASH TABLE (HashMap)",
                new String[]{"Insert O(1)", "Lookup O(1)", "Delete O(1)"}, htResults);
        appendTable(sb, "QUEUE / PRIORITY QUEUE",
                new String[]{"Enqueue O(1)", "Dequeue O(1)", "PQ Insert O(log n)", "PQ Extract O(log n)"}, qResults);
        appendTable(sb, "SORTING ALGORITHMS",
                new String[]{"Bubble O(n^2)", "Merge O(n log n)", "Quick O(n log n)*"}, sortResults);
        appendTable(sb, "BINARY vs LINEAR SEARCH",
                new String[]{"Linear O(n)", "Binary O(log n)"}, binResults);

        String footer = "  * Each value = average of " + RUNS + " runs (nanoseconds)\n"
                + "  * Array Insert amortized O(1) | Quick Sort worst-case O(n^2)";
        sb.append(footer).append("\n");

        System.out.print(sb);
        exportToFile(sb.toString());
    }

    // ─── Linked List ─────────────────────────────────────────────────────────────

    private static long avgLLInsert(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            PatientLinkedList temp = new PatientLinkedList();
            long start = System.nanoTime();
            for (PatientRecord p : dataset) temp.insert(p);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgLLSearch(List<PatientRecord> dataset, int targetId) {
        PatientLinkedList ll = new PatientLinkedList();
        for (PatientRecord p : dataset) ll.insert(p);
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            ll.searchById(targetId);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgLLDelete(List<PatientRecord> dataset, int targetId) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            PatientLinkedList copy = new PatientLinkedList();
            for (PatientRecord p : dataset) copy.insert(p);
            long start = System.nanoTime();
            copy.deleteById(targetId);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    // ─── Array (ArrayList) ───────────────────────────────────────────────────────

    private static long avgArrayInsert(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            List<PatientRecord> arr = new ArrayList<>();
            long start = System.nanoTime();
            for (PatientRecord p : dataset) arr.add(p);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgArraySearch(List<PatientRecord> dataset, int targetId) {
        List<PatientRecord> arr = new ArrayList<>(dataset);
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            for (PatientRecord p : arr) { if (p.id == targetId) break; }
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgArrayDelete(List<PatientRecord> dataset, int targetId) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            List<PatientRecord> copy = new ArrayList<>(dataset);
            long start = System.nanoTime();
            copy.removeIf(p -> p.id == targetId);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    // ─── Hash Table (HashMap) ────────────────────────────────────────────────────

    private static long avgHashInsert(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            HashMap<Integer, PatientRecord> map = new HashMap<>();
            long start = System.nanoTime();
            for (PatientRecord p : dataset) map.put(p.id, p);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgHashLookup(List<PatientRecord> dataset, int targetId) {
        HashMap<Integer, PatientRecord> map = new HashMap<>();
        for (PatientRecord p : dataset) map.put(p.id, p);
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            map.get(targetId);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgHashDelete(List<PatientRecord> dataset, int targetId) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            HashMap<Integer, PatientRecord> copy = new HashMap<>();
            for (PatientRecord p : dataset) copy.put(p.id, p);
            long start = System.nanoTime();
            copy.remove(targetId);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    // ─── Queue / Priority Queue ──────────────────────────────────────────────────

    private static long avgQueueEnqueue(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            AppointmentQueue q = new AppointmentQueue();
            long start = System.nanoTime();
            for (PatientRecord p : dataset) q.enqueue(p);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgQueueDequeue(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            AppointmentQueue q = new AppointmentQueue();
            for (PatientRecord p : dataset) q.enqueue(p);
            long start = System.nanoTime();
            while (!q.isEmpty()) q.dequeue();
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgPQInsert(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            EmergencyPriorityQueue pq = new EmergencyPriorityQueue();
            long start = System.nanoTime();
            for (PatientRecord p : dataset) pq.insert(p);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgPQExtract(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            EmergencyPriorityQueue pq = new EmergencyPriorityQueue();
            for (PatientRecord p : dataset) pq.insert(p);
            long start = System.nanoTime();
            while (!pq.isEmpty()) pq.extractMin();
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    // ─── Sorting Algorithms ───────────────────────────────────────────────────────

    private static long avgBubbleSort(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            SortingModule.bubbleSort(dataset, "name");
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgMergeSort(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            SortingModule.mergeSort(dataset, "name");
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgQuickSort(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            SortingModule.quickSort(dataset, "name");
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    // ─── Binary vs Linear Search ─────────────────────────────────────────────────

    private static long avgLinearSearch(List<PatientRecord> dataset, int targetId) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            for (PatientRecord p : dataset) { if (p.id == targetId) break; }
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgBinarySearch(List<PatientRecord> dataset, int targetId) {
        // dataset generated with sequential IDs so already sorted by ID
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            SearchModule.binarySearchById(dataset, targetId);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    // ─── Table builder ───────────────────────────────────────────────────────────

    private static void appendTable(StringBuilder sb, String title,
                                    String[] opNames, long[][] results) {
        int cols = opNames.length;
        StringBuilder sep = new StringBuilder("+----------");
        for (int i = 0; i < cols; i++) sep.append("+----------------------");
        sep.append("+");

        sb.append("\n  [ ").append(title).append(" ]\n");
        sb.append("  ").append(sep).append("\n");
        sb.append("  | ").append(String.format("%-8s", "Size"));
        for (String op : opNames) sb.append(" | ").append(String.format("%-20s", op));
        sb.append(" |\n");
        sb.append("  ").append(sep).append("\n");
        for (int s = 0; s < SIZES.length; s++) {
            sb.append("  | ").append(String.format("%,8d", SIZES[s]));
            for (int o = 0; o < cols; o++)
                sb.append(" | ").append(String.format("%,20d", results[s][o]));
            sb.append(" |\n");
        }
        sb.append("  ").append(sep).append("\n");
    }

    // ─── Export to file ───────────────────────────────────────────────────────────

    private static void exportToFile(String content) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter("data/benchmark_results.txt"));
            pw.println("Hospital Management System - Benchmark Results");
            pw.println("Generated: " + new java.util.Date());
            pw.println();
            pw.print(content);
            pw.close();
            System.out.println("\n  Results saved to data/benchmark_results.txt");
        } catch (IOException e) {
            System.out.println("  (Could not save results to file: " + e.getMessage() + ")");
        }
    }

    // ─── Synthetic dataset generator ─────────────────────────────────────────────

    private static List<PatientRecord> generateDataset(int size) {
        List<PatientRecord> list = new ArrayList<>(size);
        String[] diagnoses = {"Hypertension", "Diabetes", "Pneumonia", "Appendicitis",
                              "Fracture", "Asthma", "Migraine", "Kidney Stones"};
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        String[] hospitals  = {"Cedar Sinai", "St. Mary Hospital", "General Hospital", "Mercy Medical"};
        String[] insurers   = {"Aetna", "Blue Cross", "Cigna", "Medicare", "UnitedHealthcare"};
        String[] meds       = {"Aspirin", "Ibuprofen", "Paracetamol", "Penicillin", "Lipitor"};
        String[] results    = {"Normal", "Abnormal", "Inconclusive"};
        String[] doctors    = {"Dr. Ephram Mbapte", "Dr. Mohamed Kargbo", "Dr. Nia Allen", "Dr. Nicholas Armenta"};
        for (int i = 1; i <= size; i++) {
            list.add(new PatientRecord(
                    i,
                    "Patient" + i,
                    20 + (i % 60),
                    i % 2 == 0 ? "Male" : "Female",
                    bloodTypes[i % bloodTypes.length],
                    diagnoses[i % diagnoses.length],
                    "01/01/2024",
                    doctors[i % doctors.length],
                    hospitals[i % hospitals.length],
                    insurers[i % insurers.length],
                    1000.0 + (i % 50000),
                    100 + (i % 400),
                    ADM_TYPES[i % ADM_TYPES.length],
                    "01/10/2024",
                    meds[i % meds.length],
                    results[i % results.length]
            ));
        }
        return list;
    }
}
