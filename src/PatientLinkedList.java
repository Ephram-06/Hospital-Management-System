import java.util.ArrayList;
import java.util.List;

/**
 * Custom singly linked list for storing PatientRecord objects.
 * Chosen over an array because patients are frequently admitted (insert)
 * and discharged (delete) — linked lists handle these in O(1) / O(n)
 * without shifting elements like an array would require.
 */
public class PatientLinkedList {

    // Each node holds one patient and a pointer to the next node
    private static class Node {
        PatientRecord data;
        Node next;
        Node(PatientRecord data) { this.data = data; }
    }

    private Node head; // front of the list
    private int size;

    // Insert at head — O(1), no traversal needed
    public void insert(PatientRecord record) {
        Node newNode = new Node(record);
        newNode.next = head;
        head = newNode;
        size++;
    }

    // Linear search by ID — O(n), must traverse until found
    public PatientRecord searchById(int id) {
        Node current = head;
        while (current != null) {
            if (current.data.id == id) return current.data;
            current = current.next;
        }
        return null; // not found
    }

    // Delete by ID — O(n) to find the node, then O(1) to unlink it
    public boolean deleteById(int id) {
        if (head == null) return false;
        // Special case: target is the head node
        if (head.data.id == id) {
            head = head.next;
            size--;
            return true;
        }
        // Traverse until we find the node just before the target
        Node current = head;
        while (current.next != null) {
            if (current.next.data.id == id) {
                current.next = current.next.next; // unlink target node
                size--;
                return true;
            }
            current = current.next;
        }
        return false; // id not found
    }

    // Case-insensitive partial match on patient name — O(n)
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

    // Case-insensitive partial match on diagnosis — O(n)
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

    // Exact match on blood type (e.g. "A+") — O(n)
    public List<PatientRecord> searchByBloodType(String bloodType) {
        List<PatientRecord> results = new ArrayList<>();
        Node current = head;
        while (current != null) {
            if (current.data.bloodType.equalsIgnoreCase(bloodType))
                results.add(current.data);
            current = current.next;
        }
        return results;
    }

    // Case-insensitive partial match on hospital name — O(n)
    public List<PatientRecord> searchByHospital(String hospital) {
        List<PatientRecord> results = new ArrayList<>();
        Node current = head;
        while (current != null) {
            if (current.data.hospital.toLowerCase().contains(hospital.toLowerCase()))
                results.add(current.data);
            current = current.next;
        }
        return results;
    }

    // Exact match on test results (Normal / Abnormal / Inconclusive) — O(n)
    public List<PatientRecord> searchByTestResults(String testResults) {
        List<PatientRecord> results = new ArrayList<>();
        Node current = head;
        while (current != null) {
            if (current.data.testResults.equalsIgnoreCase(testResults))
                results.add(current.data);
            current = current.next;
        }
        return results;
    }

    // Convert the linked list to an ArrayList (used for sorting and binary search)
    public List<PatientRecord> toList() {
        List<PatientRecord> list = new ArrayList<>();
        Node current = head;
        while (current != null) { list.add(current.data); current = current.next; }
        return list;
    }

    // Print every patient in the list
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
