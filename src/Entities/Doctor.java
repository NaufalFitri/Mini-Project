package Entities;

import Exceptions.InvalidPhoneEx;
import Exceptions.InvalidSpecializationEx;

public class Doctor {

    public enum field {
        Surgery,
        InternalMedicine,
        Dermatology,
        Ophthalmology,
        Dentistry,
        Cardiology,
        Oncology,
        Anesthesiology,
    }

    private int id;
    private String name;
    private boolean isTreating;
    private field specialisedField = null;


    public Doctor(int id,String name, String specialization, String phone) throws InvalidSpecializationEx, InvalidPhoneEx {

        for (field f : field.values()) {
            if (f.name().equals(specialization)) {
                specialisedField = f;
                break;
            }
        }

        if (specialisedField == null) {
            throw new InvalidSpecializationEx("Specialization " + specialization + " is invalid!");
        }

        if (phone.length() >= 11) {
            throw new InvalidPhoneEx(InvalidPhoneEx.Type.first);
        }

        try {
            Integer.parseInt(phone);
        } catch (NumberFormatException e) {
            throw new InvalidPhoneEx(InvalidPhoneEx.Type.second);
        }

        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isTreating()
    {
        return isTreating;
    }

    public field getField()
    {
        return specialisedField;
    }

    public void setField(field f)
    {
        specialisedField = f;
    }


}
