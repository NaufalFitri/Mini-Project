package Rooms;

import Entities.Doctor;
import Entities.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TreatmentRoom extends Room {

    private String treatmentPlan;
    private List<String> medications;
    private LocalDateTime treatmentStart;
    private LocalDateTime treatmentEnd;

    public TreatmentRoom(int id, String roomNumber) {
        super(id, roomNumber);
    }

    public void startTreatment(Patient p, Doctor d, List<String> medications) {
        if (!super.isOccupied()) {
            treatmentStart = LocalDateTime.now();
            this.medications = medications;
            super.setCurrentPatient(p);
            super.setAssignedDoctor(d);
            super.setOccupied(true);
        }
    }

    public void finishTreatment() {
        treatmentEnd = LocalDateTime.now();
    }

    public List<String> generateReports() {
        List<String> reports = new ArrayList<>();
        reports.add("Treatment of " + super.getCurrentPatient().getName());
        reports.add("Treatment start at: " + treatmentStart.format(DateTimeFormatter.ISO_DATE_TIME));
        reports.add("Treatment finish at: " + treatmentEnd.format(DateTimeFormatter.ISO_DATE_TIME));
        reports.add("Treatment notes: " + treatmentPlan);
        reports.add("Treatment medications: " + medications);
        return reports;
    }

    public LocalDateTime getTreatmentStart() {
        return treatmentStart;
    }

}
