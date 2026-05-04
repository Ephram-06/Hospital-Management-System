public class PatientRecord {
    int id;
    String name;
    int age;
    String gender;
    String diagnosis;
    String admissionType;
    int doctorId;
    double billingAmount;

    public PatientRecord(int id, String name, int age, String gender,
                         String diagnosis, String admissionType, int doctorId, double billingAmount) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.diagnosis = diagnosis;
        this.admissionType = admissionType;
        this.doctorId = doctorId;
        this.billingAmount = billingAmount;
    }

    @Override
    public String toString() {
        return String.format("[#%d] %-20s | Age: %2d | %-6s | %-22s | %-10s | Dr.#%d | $%,.2f",
                id, name, age, gender, diagnosis, admissionType, doctorId, billingAmount);
    }
}
