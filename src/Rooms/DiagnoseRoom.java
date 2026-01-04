package Rooms;

import Entities.Doctor;
import Entities.Patient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DiagnoseRoom extends Room implements MedicalRoom {

    private LocalDateTime diagnosisStart;
    private LocalDateTime diagnosisEnd;

    public DiagnoseRoom(int id, String roomNumber) {
        super(id, roomNumber);
    }

    @Override
    public void enterRoom(Patient p, Doctor d) {
        if (!super.isOccupied()) {
            diagnosisStart = LocalDateTime.now();

            super.setCurrentPatient(p);
            super.setAssignedDoctor(d);
            super.setOccupied(true);
        }
    }

    public void finishDiagnosis() {
        diagnosisEnd = LocalDateTime.now();
        super.setCurrentPatient(null);
        super.setAssignedDoctor(null);
        super.setOccupied(false);
    }

    @Override
    public void exitRoom() {

    }
}
