import java.util.Collection;
import java.util.HashMap;

public class DoctorHashMap {

    private final HashMap<Integer, Doctor> map = new HashMap<>();

    public void addDoctor(Doctor doctor) {
        map.put(doctor.doctorId, doctor);
    }

    public Doctor getDoctor(int doctorId) {
        return map.get(doctorId);
    }

    public boolean removeDoctor(int doctorId) {
        return map.remove(doctorId) != null;
    }

    public Collection<Doctor> getAllDoctors() { return map.values(); }

    public int size() { return map.size(); }

    public void printAll() {
        if (map.isEmpty()) { System.out.println("  (no doctors)"); return; }
        map.values().stream()
           .sorted((a, b) -> Integer.compare(a.doctorId, b.doctorId))
           .forEach(d -> System.out.println("  " + d));
    }
}
