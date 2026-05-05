import java.util.Date;

/**
 * LIFO stack for logging treatment actions with timestamps.
 * A stack is the natural fit for undo functionality — the most recent
 * action is always on top and can be popped in O(1).
 */
public class TreatmentStack {

    // Each node stores an action string and the time it was logged
    private static class Node {
        String action;
        long timestamp;
        Node next;
        Node(String action) {
            this.action = action;
            this.timestamp = System.currentTimeMillis(); // record when action was logged
        }
    }

    private Node top; // most recently logged action
    private int size;

    // Push a new treatment action onto the top of the stack — O(1)
    public void push(String action) {
        Node newNode = new Node(action);
        newNode.next = top;
        top = newNode;
        size++;
    }

    // Pop the most recent action (undo) — O(1)
    public String pop() {
        if (top == null) return null;
        String action = top.action;
        top = top.next; // move top down to previous action
        size--;
        return action;
    }

    // Peek at the most recent action without removing it — O(1)
    public String peek() { return top != null ? top.action : null; }

    public boolean isEmpty() { return top == null; }

    public int size() { return size; }

    // Print all logged actions from most recent to oldest, with timestamps
    public void printAll() {
        Node current = top;
        if (current == null) { System.out.println("  (no treatment logs)"); return; }
        int i = 1;
        while (current != null) {
            System.out.println("  " + i++ + ". " + current.action + "  [" + new Date(current.timestamp) + "]");
            current = current.next;
        }
    }
}
