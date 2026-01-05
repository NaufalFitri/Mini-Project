package Rooms;

import Entities.Doctor;
import Entities.Patient;

public interface MedicalRoom {
    Patient getCurrentPatient();
    Doctor getAssignedDoctor();

    void enterRoom(Patient p, Doctor d);
    void exitRoom();
}
