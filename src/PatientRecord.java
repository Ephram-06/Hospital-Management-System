/**
 * Data model for a single patient record — 16 fields matching the Kaggle
 * healthcare dataset schema. IDs are auto-assigned sequentially on load
 * (the CSV does not include an ID column).
 */
public class PatientRecord {
    int    id;
    String name;
    int    age;
    String gender;
    String bloodType;
    String diagnosis;
    String dateOfAdmission;
    String doctorName;
    String hospital;
    String insuranceProvider;
    double billingAmount;
    int    roomNumber;
    String admissionType;
    String dischargeDate;
    String medication;
    String testResults;

    public PatientRecord(int id, String name, int age, String gender,
                         String bloodType, String diagnosis, String dateOfAdmission,
                         String doctorName, String hospital, String insuranceProvider,
                         double billingAmount, int roomNumber, String admissionType,
                         String dischargeDate, String medication, String testResults) {
        this.id                = id;
        this.name              = name;
        this.age               = age;
        this.gender            = gender;
        this.bloodType         = bloodType;
        this.diagnosis         = diagnosis;
        this.dateOfAdmission   = dateOfAdmission;
        this.doctorName        = doctorName;
        this.hospital          = hospital;
        this.insuranceProvider = insuranceProvider;
        this.billingAmount     = billingAmount;
        this.roomNumber        = roomNumber;
        this.admissionType     = admissionType;
        this.dischargeDate     = dischargeDate;
        this.medication        = medication;
        this.testResults       = testResults;
    }

    @Override
    public String toString() {
        return String.format("[#%d] %-20s | Age: %2d | %-6s | %-12s | %-10s | Dr. %-20s | $%,.2f",
                id, name, age, gender, diagnosis, admissionType, doctorName, billingAmount);
    }
}
