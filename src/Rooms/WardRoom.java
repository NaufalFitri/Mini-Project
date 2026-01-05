package Rooms;

import Entities.Patient;
import Exceptions.FullroomException;

import java.util.HashMap;
import java.util.Map;

public class WardRoom extends Room implements StayRoom {

    private final Map<Patient, Integer> patients = new HashMap<>();

    public WardRoom(int id, String number, int maxPatients) {
        super(id, number, maxPatients);
    }

    @Override
    public void addPatient(Patient p, int days) throws FullroomException {
        if (patients.size() >= super.getMaxEntities()) {
            throw new FullroomException("The ward room is full");
        }

        patients.put(p, days);
    }

    @Override
    public void removePatient(Patient p) {
        patients.remove(p);
    }

    @Override
    public int getPeriod(Patient p) {
        return patients.get(p);
    }
}
