package Rooms;

import Entities.Doctor;
import Entities.Patient;

public class Room {

    public enum Type {
        Diagnose,
        Treatment
    }

    private String RoomID;
    private Doctor assignedDoctor;
    private Patient currentPatient;
    private boolean isOccupied;

    public void setAssignedDoctor(Doctor doctor) {
        assignedDoctor = doctor;
    }

    public Doctor getAssignedDoctor() {
        return assignedDoctor;
    }

    public void setRoomID(String roomID) {
        RoomID = roomID;
    }

    public String getRoomID() {
        return RoomID;
    }

    public void setCurrentPatient(Patient currentPatient) {
        this.currentPatient = currentPatient;
    }

    public Patient getCurrentPatient() {
        return currentPatient;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public boolean isOccupied() {
        return isOccupied;
    }
}
