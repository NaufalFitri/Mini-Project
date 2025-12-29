package Entities;

import Exceptions.InvalidGenderEx;

public class Patient {

    public enum Gender {
        Male,
        Female,
    }

    private final String name;
    private int id;
    private String diagnose;
    private final String species;
    private final String breed;
    private final int age;
    private Gender gender = null;
    private Owner o = null;

    public Patient(int id, String name, String species, String breed, int age, String gender, Owner o) throws InvalidGenderEx
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
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.o = o;
    }

    public void setOwner(Owner o) {
        this.o = o;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getDiagnosis() {
        return diagnose;
    }

    public Owner getOwner() {
        return o;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getSpecies() {
        return this.species;
    }

    public String getBreed() {
        return this.breed;
    }

    public int getAge() {
        return age;
    }

    public Gender getGender() {
        return gender;
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
