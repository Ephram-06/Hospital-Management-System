package Lab5;

// Lab 5 - Part I: Fundamental Hash Map Operations
// Demonstrates basic HashMap usage: insert, retrieve, update, and delete key-value pairs.

import java.util.HashMap;

public class BasicHashMapExample {
    public static void main(String[] args) {
        HashMap<Integer, String> students = new HashMap<>();

        // Insert
        students.put(101, "Nick");
        students.put(102, "Ephram");
        students.put(103, "Nia");

        // Access
        System.out.println("Student 101: " + students.get(101));

        // Update
        students.put(102, "Mohamed");
        System.out.println("Updated student 102: " + students.get(102));

        // Delete
        students.remove(103);
        System.out.println("After removing 103, map contains 103? " + students.containsKey(103));

        System.out.println("Final map: " + students);
    }
}
