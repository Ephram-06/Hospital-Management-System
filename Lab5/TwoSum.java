package Lab5;

// Lab 5 - Part IV: Two Sum Problem
// Uses a HashMap to find two indices whose values sum to a target, solving the problem in O(n) time.

import java.util.Arrays;
import java.util.HashMap;

public class TwoSum {
    public static int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }

    public static void main(String[] args) {
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = twoSum(nums, target);
        System.out.println("Input: " + Arrays.toString(nums) + ", target: " + target);
        System.out.println("Indices: " + Arrays.toString(result));

        int[] nums2 = {3, 2, 4};
        int target2 = 6;
        int[] result2 = twoSum(nums2, target2);
        System.out.println("Input: " + Arrays.toString(nums2) + ", target: " + target2);
        System.out.println("Indices: " + Arrays.toString(result2));
    }
}
