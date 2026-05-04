public class Doctor {
    int doctorId;
    String name;
    String specialization;
    String availableHours;

    public Doctor(int doctorId, String name, String specialization, String availableHours) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.availableHours = availableHours;
    }

    @Override
    public String toString() {
        return String.format("[Dr.#%d] %-22s | %-20s | Hours: %s",
                doctorId, name, specialization, availableHours);
    }
}
