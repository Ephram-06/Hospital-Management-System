import java.util.List;

/**
 * FIFO queue for regular (Elective) patient appointments.
 * Uses a singly linked list with front and rear pointers so both
 * enqueue and dequeue run in O(1) — no shifting needed.
 */
public class AppointmentQueue {

    // Each node holds one patient and a pointer to the next in line
    private static class Node {
        PatientRecord data;
        Node next;
        Node(PatientRecord data) { this.data = data; }
    }

    private Node front, rear; // front = next to be served, rear = last in line
    private int size;

    // Add patient to the back of the queue — O(1)
    public void enqueue(PatientRecord record) {
        Node newNode = new Node(record);
        if (rear == null) {
            front = rear = newNode; // first patient in the queue
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    // Remove and return the patient at the front (next to be served) — O(1)
    public PatientRecord dequeue() {
        if (front == null) return null;
        PatientRecord data = front.data;
        front = front.next;
        if (front == null) rear = null; // queue is now empty
        size--;
        return data;
    }

    // Peek at the next patient without removing them — O(1)
    public PatientRecord peek() { return front != null ? front.data : null; }

    public boolean isEmpty() { return front == null; }

    public int size() { return size; }

    // Print all patients currently in the queue, front to back
    public void printAll() {
        Node current = front;
        if (current == null) { System.out.println("  (queue is empty)"); return; }
        int pos = 1;
        while (current != null) {
            System.out.println("  " + pos++ + ". " + current.data);
            current = current.next;
        }
    }

    // Return all records as a List for paged browsing
    public List<PatientRecord> toList() {
        List<PatientRecord> list = new java.util.ArrayList<>(size);
        Node current = front;
        while (current != null) { list.add(current.data); current = current.next; }
        return list;
    }
}
