package Lab5;

// Lab 5 - Part II: Frequency Counting Pattern
// Uses a HashMap to count the occurrences of each element in an integer array in O(n) time.

import java.util.HashMap;

public class FrequencyCount {
    public static HashMap<Integer, Integer> frequencyCount(int[] nums) {
        HashMap<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }
        return freq;
    }

    public static void main(String[] args) {
        int[] input = {1, 2, 2, 3, 1, 1, 4};
        HashMap<Integer, Integer> result = frequencyCount(input);
        System.out.println("Frequency count of " + java.util.Arrays.toString(input) + ":");
        System.out.println(result);
    }
}
