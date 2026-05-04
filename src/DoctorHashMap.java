import java.util.Collection;
import java.util.HashMap;

public class DoctorHashMap {

    private final HashMap<String, Doctor> map = new HashMap<>();

    public void addDoctor(Doctor doctor) {
        map.put(doctor.name, doctor);
    }

    public Doctor getDoctor(String name) {
        return map.get(name);
    }

    public boolean removeDoctor(String name) {
        return map.remove(name) != null;
    }

    public Collection<Doctor> getAllDoctors() { return map.values(); }

    public int size() { return map.size(); }

    public void printAll() {
        if (map.isEmpty()) { System.out.println("  (no doctors)"); return; }
        map.values().stream()
           .sorted((a, b) -> a.name.compareTo(b.name))
           .forEach(d -> System.out.println("  " + d));
    }
}
