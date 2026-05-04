import java.util.ArrayList;
import java.util.List;

public class PatientLinkedList {

    private static class Node {
        PatientRecord data;
        Node next;
        Node(PatientRecord data) { this.data = data; }
    }

    private Node head;
    private int size;

    public void insert(PatientRecord record) {
        Node newNode = new Node(record);
        newNode.next = head;
        head = newNode;
        size++;
    }

    public PatientRecord searchById(int id) {
        Node current = head;
        while (current != null) {
            if (current.data.id == id) return current.data;
            current = current.next;
        }
        return null;
    }

    public boolean deleteById(int id) {
        if (head == null) return false;
        if (head.data.id == id) {
            head = head.next;
            size--;
            return true;
        }
        Node current = head;
        while (current.next != null) {
            if (current.next.data.id == id) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public List<PatientRecord> searchByName(String name) {
        List<PatientRecord> results = new ArrayList<>();
        Node current = head;
        while (current != null) {
            if (current.data.name.toLowerCase().contains(name.toLowerCase()))
                results.add(current.data);
            current = current.next;
        }
        return results;
    }

    public List<PatientRecord> searchByDiagnosis(String diagnosis) {
        List<PatientRecord> results = new ArrayList<>();
        Node current = head;
        while (current != null) {
            if (current.data.diagnosis.toLowerCase().contains(diagnosis.toLowerCase()))
                results.add(current.data);
            current = current.next;
        }
        return results;
    }

    public void printAll() {
        Node current = head;
        if (current == null) { System.out.println("  (no patients)"); return; }
        while (current != null) {
            System.out.println("  " + current.data);
            current = current.next;
        }
    }

    public int size() { return size; }
}
