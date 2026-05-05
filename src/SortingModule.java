import java.util.ArrayList;
import java.util.List;

/**
 * SortingModule — Bubble Sort, Merge Sort, Quick Sort on PatientRecord lists.
 * Sort fields: "name" (alphabetical), "age" (ascending), "billing" (ascending).
 * All methods return a new sorted list; the original is not modified.
 */
public class SortingModule {

    // ─── Public entry points ──────────────────────────────────────────────────

    public static List<PatientRecord> bubbleSort(List<PatientRecord> input, String field) {
        List<PatientRecord> list = new ArrayList<>(input);
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (compare(list.get(j), list.get(j + 1), field) > 0) {
                    PatientRecord tmp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, tmp);
                }
            }
        }
        return list;
    }

    public static List<PatientRecord> mergeSort(List<PatientRecord> input, String field) {
        if (input.size() <= 1) return new ArrayList<>(input);
        int mid = input.size() / 2;
        List<PatientRecord> left  = mergeSort(input.subList(0, mid), field);
        List<PatientRecord> right = mergeSort(input.subList(mid, input.size()), field);
        return merge(left, right, field);
    }

    public static List<PatientRecord> quickSort(List<PatientRecord> input, String field) {
        List<PatientRecord> list = new ArrayList<>(input);
        quickSortHelper(list, 0, list.size() - 1, field);
        return list;
    }

    // ─── Internal helpers ─────────────────────────────────────────────────────

    private static List<PatientRecord> merge(List<PatientRecord> left,
                                             List<PatientRecord> right,
                                             String field) {
        List<PatientRecord> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            if (compare(left.get(i), right.get(j), field) <= 0)
                result.add(left.get(i++));
            else
                result.add(right.get(j++));
        }
        while (i < left.size())  result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }

    private static void quickSortHelper(List<PatientRecord> list, int low, int high, String field) {
        if (low < high) {
            int pi = partition(list, low, high, field);
            quickSortHelper(list, low, pi - 1, field);
            quickSortHelper(list, pi + 1, high, field);
        }
    }

    private static int partition(List<PatientRecord> list, int low, int high, String field) {
        PatientRecord pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (compare(list.get(j), pivot, field) <= 0) {
                i++;
                PatientRecord tmp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, tmp);
            }
        }
        PatientRecord tmp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, tmp);
        return i + 1;
    }

    private static int compare(PatientRecord a, PatientRecord b, String field) {
        switch (field.toLowerCase()) {
            case "age":     return Integer.compare(a.age, b.age);
            case "billing": return Double.compare(a.billingAmount, b.billingAmount);
            default:        return a.name.compareToIgnoreCase(b.name);
        }
    }
}
