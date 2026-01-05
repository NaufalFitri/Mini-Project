package Entities;

import Exceptions.InvalidGenderEx;

import java.util.ArrayList;
import java.util.List;

public class Patient extends Entity {

    public enum Gender {
        Male,
        Female,
    }

    private String diagnose;
    private String species;
    private String breed;
    private int age;
    private Gender gender = null;
    private Owner o = null;

    private List<String> medications = new ArrayList<>();

    public Patient(int id, String name, String species, String breed, int age, String gender, Owner o) throws InvalidGenderEx {
        super(id, name);

        for (Gender gen : Gender.values()) {
            if (gen.name().equals(gender)) {
                this.gender = gen;
                break;
            }
        }
        if (this.gender == null) {
            throw new InvalidGenderEx("Gender " + gender + " is not valid!");
        }

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

    public void setMedications(List<String> m) {
        medications = m;
    }

    public List<String> getMedications() {
        return medications;
    }

    public String getDiagnosis() {
        return diagnose;
    }

    public Owner getOwner() {
        return o;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
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

}
