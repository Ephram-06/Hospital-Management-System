import java.util.List;

public class BenchmarkRunner {

    private static final int RUNS = 5;

    public static void run(PatientLinkedList list, List<PatientRecord> allRecords) {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║                  BENCHMARK RESULTS                  ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.printf( "║  Dataset size: %-5d records, %d runs per operation   ║%n", allRecords.size(), RUNS);
        System.out.println("╠══════════════════════════════════════════════════════╣");
        benchmarkInsert(allRecords);
        benchmarkSearch(list, allRecords);
        benchmarkDelete(allRecords);
        benchmarkPriorityQueue(allRecords);
        System.out.println("╚══════════════════════════════════════════════════════╝");
    }

    private static void benchmarkInsert(List<PatientRecord> records) {
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            PatientLinkedList temp = new PatientLinkedList();
            long start = System.nanoTime();
            for (PatientRecord p : records) temp.insert(p);
            total += System.nanoTime() - start;
        }
        System.out.printf("║  Linked List Insert (%d records)  avg: %,9d ns    ║%n", records.size(), total / RUNS);
    }

    private static void benchmarkSearch(PatientLinkedList list, List<PatientRecord> records) {
        if (records.isEmpty()) return;
        int targetId = records.get(records.size() / 2).id;
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long start = System.nanoTime();
            list.searchById(targetId);
            total += System.nanoTime() - start;
        }
        System.out.printf("║  Linked List Search (by ID)        avg: %,9d ns    ║%n", total / RUNS);
    }

    private static void benchmarkDelete(List<PatientRecord> records) {
        if (records.isEmpty()) return;
        int targetId = records.get(records.size() / 2).id;
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            PatientLinkedList copy = new PatientLinkedList();
            for (PatientRecord p : records) copy.insert(p);
            long start = System.nanoTime();
            copy.deleteById(targetId);
            total += System.nanoTime() - start;
        }
        System.out.printf("║  Linked List Delete (by ID)        avg: %,9d ns    ║%n", total / RUNS);
    }

    private static void benchmarkPriorityQueue(List<PatientRecord> records) {
        if (records.isEmpty()) return;
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            EmergencyPriorityQueue pq = new EmergencyPriorityQueue();
            long start = System.nanoTime();
            for (PatientRecord p : records) pq.insert(p);
            total += System.nanoTime() - start;
        }
        System.out.printf("║  Priority Queue Insert (%d records) avg: %,9d ns    ║%n", records.size(), total / RUNS);
    }
}
