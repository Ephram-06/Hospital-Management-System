/**
 * Represents a doctor in the hospital system.
 * Loaded from data/doctors.csv at startup; also auto-registered when
 * a patient record references a doctor name not already in the HashMap.
 */
public class Doctor {
    String name;
    String specialization;
    String availableHours;

    public Doctor(String name, String specialization, String availableHours) {
        this.name          = name;
        this.specialization = specialization;
        this.availableHours = availableHours;
    }

    @Override
    public String toString() {
        return String.format("%-25s | %-20s | Hours: %s", name, specialization, availableHours);
    }
}
