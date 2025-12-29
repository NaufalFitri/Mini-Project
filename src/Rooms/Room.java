package Rooms;

import Entities.Doctor;
import Entities.Patient;

public class Room {

    public enum Type {
        Diagnose,
        Treatment
    }

    private int RoomID;
    private String roomNumber;
    private Doctor assignedDoctor;
    private Patient currentPatient;
    private boolean isOccupied;

    public Room(int id, String number) {
        this.RoomID = id;
        this.roomNumber = number;
    }

    public void setAssignedDoctor(Doctor doctor) {
        assignedDoctor = doctor;
    }

    public Doctor getAssignedDoctor() {
        return assignedDoctor;
    }

    public void setRoomNumber(String number) { roomNumber = number; }

    public String getRoomNumber() { return roomNumber; }

    public int getRoomID() {
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
