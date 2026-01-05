package Rooms;

import Entities.Doctor;
import Entities.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TreatmentRoom extends Room implements MedicalRoom {

    private String treatmentPlan;
    private List<String> medications;
    private LocalDateTime treatmentStart;
    private LocalDateTime treatmentEnd;
    private Doctor assignedDoctor;
    private Patient currentPatient;

    public TreatmentRoom(int id, String roomNumber, int maxPatients) {
        super(id, roomNumber, maxPatients);
    }

    @Override
    public Patient getCurrentPatient() {
        return currentPatient;
    }

    @Override
    public Doctor getAssignedDoctor() {
        return assignedDoctor;
    }

    @Override
    public void enterRoom(Patient p, Doctor d) {
        if (!super.isOccupied()) {
            treatmentStart = LocalDateTime.now();
            this.medications = p.getMedications();
            this.treatmentPlan = p.getDiagnosis();
            this.currentPatient = p;
            this.assignedDoctor = d;
            super.setOccupied(true);
        }
    }

    @Override
    public void exitRoom() {

        treatmentEnd = LocalDateTime.now();
        this.medications = null;
        this.currentPatient = null;
        this.assignedDoctor = null;
        super.setOccupied(false);

    }

    public List<String> generateReports() {
        List<String> reports = new ArrayList<>();
        reports.add("Treatment of " + currentPatient.getName());
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
