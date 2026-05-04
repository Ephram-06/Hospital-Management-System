import java.util.Arrays;

public class EmergencyPriorityQueue {

    private PatientRecord[] heap;
    private int size;

    public EmergencyPriorityQueue() {
        heap = new PatientRecord[16];
    }

    private int priority(PatientRecord r) {
        switch (r.admissionType.toLowerCase()) {
            case "emergency": return 1;
            case "urgent":    return 2;
            default:          return 3;
        }
    }

    public void insert(PatientRecord record) {
        if (size == heap.length) grow();
        heap[size] = record;
        siftUp(size);
        size++;
    }

    public PatientRecord extractMin() {
        if (size == 0) return null;
        PatientRecord min = heap[0];
        heap[0] = heap[--size];
        heap[size] = null;
        if (size > 0) siftDown(0);
        return min;
    }

    public PatientRecord peek() { return size > 0 ? heap[0] : null; }

    public boolean isEmpty() { return size == 0; }

    public int size() { return size; }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (priority(heap[i]) < priority(heap[parent])) {
                swap(i, parent);
                i = parent;
            } else break;
        }
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1, right = 2 * i + 2, smallest = i;
            if (left < size  && priority(heap[left])  < priority(heap[smallest])) smallest = left;
            if (right < size && priority(heap[right]) < priority(heap[smallest])) smallest = right;
            if (smallest == i) break;
            swap(i, smallest);
            i = smallest;
        }
    }

    private void swap(int a, int b) {
        PatientRecord tmp = heap[a]; heap[a] = heap[b]; heap[b] = tmp;
    }

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
