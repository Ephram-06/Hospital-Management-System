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

    public Doctor findDoctorById(int doctorId) {
        return doctorMap.getDoctor(doctorId);
    }
}
