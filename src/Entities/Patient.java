package Entities;

import Exceptions.InvalidGenderEx;

import java.util.Arrays;

public class Patient {

    private enum Gender {
        Male,
        Female,
    }

    private String name;
    private int id;
    private String diagnose;
    private Gender gender = null;
    private Owner o = null;

    public Patient(int id, String name, String species, String breed, int age, String gender) throws InvalidGenderEx
    {
        for (Gender gen : Gender.values()) {
            if (gen.name().equals(gender)) {
                this.gender = gen;
                break;
            }
        }
        if (this.gender == null) {
            throw new InvalidGenderEx("Gender " + gender + " is not valid!");
        }

        this.name = name;
        this.id = id;
    }

    public void setOwner(Owner o) {
        this.o = o;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Owner getOwner() {
        return o;
    }

    public String getName() {
        return name;
    }
    
//    public isBeingTreated()
//    {
//        return isBeingTreated;
//    }
//
//    public getRoomID()
//    {
//        return roomID;
//    }
//
//    public getCurrentPatient()
//    {
//        return getCurrentPatient;
//    }
}
