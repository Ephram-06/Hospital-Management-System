package Lab5;

// Lab 5 - Part V: Roman to Integer
// Uses a HashMap to map Roman numeral symbols to values, then converts a Roman numeral string to an integer in O(n) time.

import java.util.HashMap;

public class RomanToInteger {
    public static int romanToInt(String s) {
        HashMap<Character, Integer> values = new HashMap<>();
        values.put('I', 1);
        values.put('V', 5);
        values.put('X', 10);
        values.put('L', 50);
        values.put('C', 100);
        values.put('D', 500);
        values.put('M', 1000);

        int total = 0;
        for (int i = 0; i < s.length(); i++) {
            int curr = values.get(s.charAt(i));
            int next = (i + 1 < s.length()) ? values.get(s.charAt(i + 1)) : 0;
            if (curr < next) {
                total -= curr;
            } else {
                total += curr;
            }
        }
        return total;
    }

    public static void main(String[] args) {
        String[] tests = {"III", "IV", "IX", "LVIII", "MCMXCIV"};
        int[] expected = {3, 4, 9, 58, 1994};
        for (int i = 0; i < tests.length; i++) {
            int result = romanToInt(tests[i]);
            System.out.println(tests[i] + " = " + result + (result == expected[i] ? " ✓" : " ✗"));
        }
    }
}
