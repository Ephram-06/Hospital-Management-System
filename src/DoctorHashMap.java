import java.util.Collection;
import java.util.HashMap;

/**
 * Wraps a HashMap<String, Doctor> keyed by doctor name.
 * Provides O(1) average-case insert, lookup, and delete — critical when
 * checking doctor assignments across 55,000+ patient records on load.
 */
public class DoctorHashMap {

    private final HashMap<String, Doctor> map = new HashMap<>();

    // Add or overwrite a doctor entry — O(1) average
    public void addDoctor(Doctor doctor) {
        map.put(doctor.name, doctor);
    }

    // Look up a doctor by exact name — O(1) average, null if not found
    public Doctor getDoctor(String name) {
        return map.get(name);
    }

    // Remove a doctor by name — returns true if found and removed, O(1) average
    public boolean removeDoctor(String name) {
        return map.remove(name) != null;
    }

    // Returns all doctor values (unordered) — O(n) to iterate
    public Collection<Doctor> getAllDoctors() { return map.values(); }

    public int size() { return map.size(); }

    public void printAll() {
        if (map.isEmpty()) { System.out.println("  (no doctors)"); return; }
        map.values().stream()
           .sorted((a, b) -> a.name.compareTo(b.name))
           .forEach(d -> System.out.println("  " + d));
    }
}
