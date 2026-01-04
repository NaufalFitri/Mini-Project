package Rooms;
import Entities.Doctor;
import Entities.Patient;

public interface MedicalRoom {
    void enterRoom(Patient p, Doctor d);
    void exitRoom();
}
