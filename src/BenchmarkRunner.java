import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BenchmarkRunner {

    // ─── ANSI Colors ─────────────────────────────────────────────────────────────
    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";
    private static final String DIM    = "\u001B[2m";
    private static final String CYAN   = "\u001B[36m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String WHITE  = "\u001B[97m";

    private static final int   RUNS  = 5;
    private static final int[] SIZES = {1000, 2000, 5000, 10000};

    private static final String[] ADM_TYPES = {"Emergency", "Urgent", "Elective"};

    // ─── Called from the menu ────────────────────────────────────────────────────

    public static void run(PatientLinkedList list, List<PatientRecord> allRecords) {
        // ── Loading screen ───────────────────────────────────────────────────────
        System.out.println();
        System.out.println(CYAN + BOLD + "  ╔══════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + BOLD + "  ║" + RESET + WHITE + BOLD + "       PERFORMANCE BENCHMARK SUITE            " + RESET + CYAN + BOLD + "║" + RESET);
        System.out.println(CYAN + BOLD + "  ╠══════════════════════════════════════════════╣" + RESET);
        System.out.println(CYAN + BOLD + "  ║" + RESET + DIM  + "   4 data sizes  |  " + RUNS + " runs each  |  ns timing " + RESET + CYAN + BOLD + "║" + RESET);
        System.out.println(CYAN + BOLD + "  ╚══════════════════════════════════════════════╝" + RESET);
        System.out.println();

        String[] stages = {
            "Linked List  ", "Array        ", "Hash Table   ",
            "Queue / PQ   ", "Sorting      ", "Search       "
        };
        int totalStages = stages.length * SIZES.length;
        int done = 0;
        int barWidth = 30;

        System.out.println(YELLOW + "  Initializing benchmark engine..." + RESET);
        try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        System.out.println();

        long[][] llResults   = new long[SIZES.length][3];
        long[][] arrResults  = new long[SIZES.length][3];
        long[][] htResults   = new long[SIZES.length][3];
        long[][] qResults    = new long[SIZES.length][4];
        long[][] sortResults = new long[SIZES.length][3];
        long[][] binResults  = new long[SIZES.length][2];

        for (int s = 0; s < SIZES.length; s++) {
            List<PatientRecord> dataset = generateDataset(SIZES[s]);
            int midId = dataset.get(SIZES[s] / 2).id;

            // Linked List
            llResults[s][0] = avgLLInsert(dataset);
            llResults[s][1] = avgLLSearch(dataset, midId);
            llResults[s][2] = avgLLDelete(dataset, midId);
            done++; printBar(stages[0], SIZES[s], done, totalStages, barWidth);

            // Array
            arrResults[s][0] = avgArrayInsert(dataset);
            arrResults[s][1] = avgArraySearch(dataset, midId);
            arrResults[s][2] = avgArrayDelete(dataset, midId);
            done++; printBar(stages[1], SIZES[s], done, totalStages, barWidth);

            // Hash Table
            htResults[s][0] = avgHashInsert(dataset);
            htResults[s][1] = avgHashLookup(dataset, midId);
            htResults[s][2] = avgHashDelete(dataset, midId);
            done++; printBar(stages[2], SIZES[s], done, totalStages, barWidth);

            // Queue
            qResults[s][0] = avgQueueEnqueue(dataset);
            qResults[s][1] = avgQueueDequeue(dataset);
            qResults[s][2] = avgPQInsert(dataset);
            qResults[s][3] = avgPQExtract(dataset);
            done++; printBar(stages[3], SIZES[s], done, totalStages, barWidth);

            // Sorting
            sortResults[s][0] = avgBubbleSort(dataset);
            sortResults[s][1] = avgMergeSort(dataset);
            sortResults[s][2] = avgQuickSort(dataset);
            done++; printBar(stages[4], SIZES[s], done, totalStages, barWidth);

            // Search
            binResults[s][0] = avgLinearSearch(dataset, midId);
            binResults[s][1] = avgBinarySearch(dataset, midId);
            done++; printBar(stages[5], SIZES[s], done, totalStages, barWidth);
        }

        System.out.println("\r  " + GREEN + BOLD + "[DONE] All benchmarks complete!" + RESET
                + "                                        ");
        System.out.println();
        try { Thread.sleep(400); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

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

        String footer = GREEN + "  * Each value = average of " + RUNS + " runs (nanoseconds)" + RESET + "\n"
                + DIM + "  * Array Insert amortized O(1) | Quick Sort worst-case O(n^2)" + RESET;
        sb.append(footer).append("\n");

        System.out.print(sb);
        exportToFile(sb.toString().replaceAll("\\u001B\\[[\\d;]*m", ""));
    }

    // ─── Live progress bar ────────────────────────────────────────────────────────

    private static void printBar(String stage, int size, int done, int total, int barWidth) {
        int filled = (int)((double) done / total * barWidth);
        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filled; i++) bar.append('█');
        for (int i = filled; i < barWidth; i++) bar.append('░');
        int pct = (int)((double) done / total * 100);
        System.out.printf("\r  " + CYAN + "[" + GREEN + "%s" + CYAN + "]" + RESET
                + "  %s  " + DIM + "n=%,d" + RESET + "  %3d%%",
                bar, stage, size, pct);
        System.out.flush();
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

        sb.append("\n  " + CYAN + BOLD + "[ " + WHITE + title + CYAN + " ]" + RESET + "\n");
        sb.append("  " + CYAN).append(sep).append(RESET + "\n");
        sb.append("  " + CYAN + "|" + RESET + YELLOW + " ").append(String.format("%-8s", "Size"));
        for (String op : opNames) sb.append(CYAN + " | " + RESET + YELLOW).append(String.format("%-20s", op));
        sb.append(CYAN + " |" + RESET + "\n");
        sb.append("  " + CYAN).append(sep).append(RESET + "\n");
        for (int s = 0; s < SIZES.length; s++) {
            sb.append("  " + CYAN + "|" + RESET + WHITE + " ").append(String.format("%,8d", SIZES[s]));
            for (int o = 0; o < cols; o++)
                sb.append(CYAN + " | " + RESET).append(String.format("%,20d", results[s][o]));
            sb.append(CYAN + " |" + RESET + "\n");
        }
        sb.append("  " + CYAN).append(sep).append(RESET + "\n");
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
            System.out.println("\n  " + GREEN + "Results saved to data/benchmark_results.txt" + RESET);
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
