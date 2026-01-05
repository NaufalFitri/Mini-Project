package Rooms;

import Entities.Doctor;
import Entities.Patient;
import java.time.LocalDateTime;

public class DiagnoseRoom extends Room implements MedicalRoom {

    private LocalDateTime diagnosisStart;
    private LocalDateTime diagnosisEnd;
    private Doctor assignedDoctor;
    private Patient currentPatient;

    public DiagnoseRoom(int id, String roomNumber, int maxPatients) {
        super(id, roomNumber, maxPatients);
    }

    @Override
    public void enterRoom(Patient p, Doctor d) {
        if (!super.isOccupied()) {
            diagnosisStart = LocalDateTime.now();

            this.assignedDoctor = d;
            this.currentPatient = p;
            super.setOccupied(true);
        }
    }

    @Override
    public void exitRoom() {
        diagnosisEnd = LocalDateTime.now();
        currentPatient = null;
        assignedDoctor = null;
        super.setOccupied(false);
    }

    @Override
    public Patient getCurrentPatient() {
        return currentPatient;
    }

    @Override
    public Doctor getAssignedDoctor() {
        return assignedDoctor;
    }
}
