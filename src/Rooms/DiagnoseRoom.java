package Rooms;

import Entities.Doctor;
import Entities.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DiagnoseRoom extends Room {

    private String diagnosisNotes = null;
    private LocalDateTime diagnosisStart;
    private LocalDateTime diagnosisEnd;
    private Patient p;

    public DiagnoseRoom(int id, String roomNumber) {
        super(id, roomNumber);
    }

    public void startDiagnosis(Patient p, Doctor d) {
        if (!super.isOccupied()) {
            diagnosisStart = LocalDateTime.now();

            super.setCurrentPatient(p);
            super.setAssignedDoctor(d);
            super.setOccupied(true);
        }
    }

    public void recordDiagnosis(String note) {
        if (!super.isOccupied()) return;

        diagnosisNotes = note;
    }

    public String getDiagnosisNotes() { return diagnosisNotes; }

    public void finishDiagnosis() {
        diagnosisEnd = LocalDateTime.now();

        super.getCurrentPatient().setDiagnose(diagnosisNotes);

        diagnosisNotes = null;
        super.setCurrentPatient(null);
        super.setAssignedDoctor(null);
        super.setOccupied(false);
    }

    public List<String> generateReports() {
        if (!super.isOccupied()) return null;

        List<String> reports = new ArrayList<>();
        reports.add("Diagnosis of " + super.getCurrentPatient().getName());
        reports.add("Diagnosis start at: " + diagnosisStart.format(DateTimeFormatter.ISO_DATE_TIME));
        reports.add("Diagnosis finish at: " + diagnosisEnd.format(DateTimeFormatter.ISO_DATE_TIME));
        reports.add("Diagnosis notes: " + diagnosisNotes);
        return reports;
    }

}
