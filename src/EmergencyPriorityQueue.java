import java.util.Arrays;

/**
 * Min-heap priority queue for Emergency and Urgent patients.
 * Lower priority number = higher urgency, so Emergency (1) is always
 * extracted before Urgent (2) and Elective (3), regardless of arrival order.
 * Insert and extract both run in O(log n).
 */
public class EmergencyPriorityQueue {

    private PatientRecord[] heap; // array-backed binary min-heap
    private int size;

    public EmergencyPriorityQueue() {
        heap = new PatientRecord[16]; // initial capacity, grows as needed
    }

    // Map admission type to a numeric priority (lower = served first)
    private int priority(PatientRecord r) {
        switch (r.admissionType.toLowerCase()) {
            case "emergency": return 1; // highest urgency
            case "urgent":    return 2;
            default:          return 3; // elective — lowest urgency
        }
    }

    // Add a patient and restore the heap property — O(log n)
    public void insert(PatientRecord record) {
        if (size == heap.length) grow(); // double capacity if full
        heap[size] = record;
        siftUp(size); // bubble up to correct position
        size++;
    }

    // Remove and return the highest-priority patient (lowest number) — O(log n)
    public PatientRecord extractMin() {
        if (size == 0) return null;
        PatientRecord min = heap[0];      // root is always the min
        heap[0] = heap[--size];           // replace root with last element
        heap[size] = null;                // clear old slot
        if (size > 0) siftDown(0);        // restore heap property
        return min;
    }

    // Peek at the highest-priority patient without removing — O(1)
    public PatientRecord peek() { return size > 0 ? heap[0] : null; }

    public boolean isEmpty() { return size == 0; }

    public int size() { return size; }

    // Bubble a node up until the heap property is restored — O(log n)
    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (priority(heap[i]) < priority(heap[parent])) {
                swap(i, parent); // child has higher priority than parent, swap
                i = parent;
            } else break;
        }
    }

    // Push a node down until the heap property is restored — O(log n)
    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1, right = 2 * i + 2, smallest = i;
            if (left < size  && priority(heap[left])  < priority(heap[smallest])) smallest = left;
            if (right < size && priority(heap[right]) < priority(heap[smallest])) smallest = right;
            if (smallest == i) break; // heap property satisfied
            swap(i, smallest);
            i = smallest;
        }
    }

    private void swap(int a, int b) {
        PatientRecord tmp = heap[a]; heap[a] = heap[b]; heap[b] = tmp;
    }

    // Double the heap array capacity when full
    private void grow() {
        heap = Arrays.copyOf(heap, heap.length * 2);
    }

    public void printAll() {
        if (size == 0) { System.out.println("  (priority queue is empty)"); return; }
        PatientRecord[] copy = Arrays.copyOf(heap, size);
        Arrays.sort(copy, (a, b) -> Integer.compare(priority(a), priority(b)));
        for (int i = 0; i < copy.length; i++)
            System.out.println("  " + (i + 1) + ". [Priority " + priority(copy[i]) + "] " + copy[i]);
    }
}
