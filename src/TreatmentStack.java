import java.util.Date;

public class TreatmentStack {

    private static class Node {
        String action;
        long timestamp;
        Node next;
        Node(String action) {
            this.action = action;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private Node top;
    private int size;

    public void push(String action) {
        Node newNode = new Node(action);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public String pop() {
        if (top == null) return null;
        String action = top.action;
        top = top.next;
        size--;
        return action;
    }

    public String peek() { return top != null ? top.action : null; }

    public boolean isEmpty() { return top == null; }

    public int size() { return size; }

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
