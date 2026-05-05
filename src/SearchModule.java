import java.util.List;

/**
 * Facade that provides all search operations for the HMS.
 * Routes ID lookups through the linked list (linear, O(n)) and
 * offers a static binary search (O(log n)) for sorted-by-ID lists.
 */
public class SearchModule {

    private final PatientLinkedList patientList;
    private final DoctorHashMap doctorMap;

    public SearchModule(PatientLinkedList patientList, DoctorHashMap doctorMap) {
        this.patientList = patientList;
        this.doctorMap   = doctorMap;
    }

    // Linear search by exact ID through the linked list — O(n)
    public PatientRecord findPatientById(int id) {
        return patientList.searchById(id);
    }

    // Partial name match — O(n) linear scan
    public List<PatientRecord> findPatientsByName(String name) {
        return patientList.searchByName(name);
    }

    // Partial diagnosis match — O(n) linear scan
    public List<PatientRecord> findPatientsByDiagnosis(String diagnosis) {
        return patientList.searchByDiagnosis(diagnosis);
    }

    // Exact blood type match (e.g. "A+") — O(n) linear scan
    public List<PatientRecord> findPatientsByBloodType(String bloodType) {
        return patientList.searchByBloodType(bloodType);
    }

    // Partial hospital name match — O(n) linear scan
    public List<PatientRecord> findPatientsByHospital(String hospital) {
        return patientList.searchByHospital(hospital);
    }

    // Exact test result match (Normal / Abnormal / Inconclusive) — O(n) linear scan
    public List<PatientRecord> findPatientsByTestResults(String testResults) {
        return patientList.searchByTestResults(testResults);
    }

    /**
     * Binary search by ID on a sorted list. O(log n).
     * The list must already be sorted by ID ascending.
     */
    public static PatientRecord binarySearchById(List<PatientRecord> sorted, int id) {
        int lo = 0, hi = sorted.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) / 2;
            int midId = sorted.get(mid).id;
            if (midId == id)  return sorted.get(mid);
            if (midId < id)   lo = mid + 1;
            else              hi = mid - 1;
        }
        return null;
    }

    // Lookup doctor by exact name from the HashMap \u2014 O(1) average
    public Doctor findDoctorByName(String name) {
        return doctorMap.getDoctor(name);
    }
}
