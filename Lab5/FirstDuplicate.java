package Lab5;

// Lab 5 - Part III: Duplicate Detection
// Uses a HashSet to find and return the first duplicate element in an integer array in O(n) time.

import java.util.HashSet;

public class FirstDuplicate {
    public static Integer firstDuplicate(int[] nums) {
        HashSet<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (seen.contains(num)) {
                return num;
            }
            seen.add(num);
        }
        return null;
    }

    public static void main(String[] args) {
        int[] input = {3, 1, 4, 2, 5, 3};
        Integer result = firstDuplicate(input);
        System.out.println("Input: " + java.util.Arrays.toString(input));
        System.out.println("First duplicate: " + result);
    }
}
