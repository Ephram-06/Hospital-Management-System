import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunner {

    private static final int   RUNS  = 5;
    private static final int[] SIZES = {1000, 2000, 5000, 10000};

    // Admission types cycled to generate synthetic data
    private static final String[] ADM_TYPES = {"Emergency", "Urgent", "Elective"};

    // ─── Called from the menu ────────────────────────────────────────────────────

    public static void run(PatientLinkedList list, List<PatientRecord> allRecords) {
        System.out.println("\n  Running benchmarks across 4 data sizes (" + RUNS + " runs each) ...");

        // Results table: [sizeIndex][operation]  0=insert 1=search 2=delete 3=pqInsert
        long[][] results = new long[SIZES.length][4];

        for (int s = 0; s < SIZES.length; s++) {
            List<PatientRecord> dataset = generateDataset(SIZES[s]);
            System.out.printf("  Testing %,d records ... %n", SIZES[s]);

            // Build a list once for search (search doesn't mutate)
            PatientLinkedList searchList = new PatientLinkedList();
            for (PatientRecord p : dataset) searchList.insert(p);
            int midId = dataset.get(SIZES[s] / 2).id;

            results[s][0] = avgInsert(dataset);
            results[s][1] = avgSearch(searchList, midId);
            results[s][2] = avgDelete(dataset, midId);
            results[s][3] = avgPriorityQueue(dataset);
        }

        printSummaryTable(results);
    }

    // ─── Individual averaged operations ─────────────────────────────────────────

    private static long avgInsert(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            PatientLinkedList temp = new PatientLinkedList();
            long start = System.nanoTime();
            for (PatientRecord p : dataset) temp.insert(p);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgSearch(PatientLinkedList list, int targetId) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            list.searchById(targetId);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    private static long avgDelete(List<PatientRecord> dataset, int targetId) {
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

    private static long avgPriorityQueue(List<PatientRecord> dataset) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            EmergencyPriorityQueue pq = new EmergencyPriorityQueue();
            long start = System.nanoTime();
            for (PatientRecord p : dataset) pq.insert(p);
            total += System.nanoTime() - start;
        }
        return total / RUNS;
    }

    // ─── Summary table ───────────────────────────────────────────────────────────

    private static void printSummaryTable(long[][] results) {
        String sep = "+----------+--------------------+--------------------+--------------------+--------------------+";
        System.out.println("\n" + sep);
        System.out.printf("| %-8s | %-18s | %-18s | %-18s | %-18s |%n",
                "Size", "LL Insert (ns)", "LL Search (ns)", "LL Delete (ns)", "PQ Insert (ns)");
        System.out.println(sep);
        for (int s = 0; s < SIZES.length; s++) {
            System.out.printf("| %,8d | %,18d | %,18d | %,18d | %,18d |%n",
                    SIZES[s], results[s][0], results[s][1], results[s][2], results[s][3]);
        }
        System.out.println(sep);
        System.out.println("  * Each value is the average of " + RUNS + " runs  |  LL = Linked List  |  PQ = Priority Queue");
    }

    // ─── Synthetic dataset generator ────────────────────────────────────────────

    private static List<PatientRecord> generateDataset(int size) {
        List<PatientRecord> list = new ArrayList<>(size);
        String[] diagnoses = {"Hypertension", "Diabetes", "Pneumonia", "Appendicitis",
                              "Fracture", "Asthma", "Migraine", "Kidney Stones"};
        for (int i = 1; i <= size; i++) {
            list.add(new PatientRecord(
                    i,
                    "Patient" + i,
                    20 + (i % 60),
                    i % 2 == 0 ? "Male" : "Female",
                    diagnoses[i % diagnoses.length],
                    ADM_TYPES[i % ADM_TYPES.length],
                    (i % 5) + 1,
                    1000.0 + (i % 50000)
            ));
        }
        return list;
    }
}
