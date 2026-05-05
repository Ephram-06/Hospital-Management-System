import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchModule {

    private final PatientLinkedList patientList;
    private final DoctorHashMap doctorMap;

    public SearchModule(PatientLinkedList patientList, DoctorHashMap doctorMap) {
        this.patientList = patientList;
        this.doctorMap   = doctorMap;
    }

    public PatientRecord findPatientById(int id) {
        return patientList.searchById(id);
    }

    public List<PatientRecord> findPatientsByName(String name) {
        return patientList.searchByName(name);
    }

    public List<PatientRecord> findPatientsByDiagnosis(String diagnosis) {
        return patientList.searchByDiagnosis(diagnosis);
    }

    public List<PatientRecord> findPatientsByBloodType(String bloodType) {
        return patientList.searchByBloodType(bloodType);
    }

    public List<PatientRecord> findPatientsByHospital(String hospital) {
        return patientList.searchByHospital(hospital);
    }

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

    public Doctor findDoctorByName(String name) {
        return doctorMap.getDoctor(name);
    }
}
