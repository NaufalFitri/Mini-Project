package Rooms;

import Entities.Patient;
import Exceptions.FullroomException;

public interface StayRoom {

    public void addPatient(Patient p, int days) throws FullroomException;
    public void removePatient(Patient p);
    public int getPeriod(Patient p);

}
