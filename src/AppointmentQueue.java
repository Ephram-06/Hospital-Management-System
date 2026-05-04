public class AppointmentQueue {

    private static class Node {
        PatientRecord data;
        Node next;
        Node(PatientRecord data) { this.data = data; }
    }

    private Node front, rear;
    private int size;

    public void enqueue(PatientRecord record) {
        Node newNode = new Node(record);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    public PatientRecord dequeue() {
        if (front == null) return null;
        PatientRecord data = front.data;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return data;
    }

    public PatientRecord peek() { return front != null ? front.data : null; }

    public boolean isEmpty() { return front == null; }

    public int size() { return size; }

    public void printAll() {
        Node current = front;
        if (current == null) { System.out.println("  (queue is empty)"); return; }
        int pos = 1;
        while (current != null) {
            System.out.println("  " + pos++ + ". " + current.data);
            current = current.next;
        }
    }
}
