package Entities;

import Exceptions.InvalidPhoneEx;
import Exceptions.InvalidSpecializationEx;

public class Doctor extends Entity {

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

    private boolean isTreating;
    private field specialisedField = null;
    private String phone;

    public Doctor(int id,String name, String specialization, String phone) throws InvalidSpecializationEx, InvalidPhoneEx {

        super(id, name);

        for (field f : field.values()) {
            if (f.name().equals(specialization)) {
                specialisedField = f;
                break;
            }
        }

        if (specialisedField == null) {
            throw new InvalidSpecializationEx("Specialization " + specialization + " is invalid!");
        }

        if (phone.length() > 11) {
            throw new InvalidPhoneEx(InvalidPhoneEx.Type.first);
        }

        try {
            Integer.parseInt(phone);
        } catch (NumberFormatException e) {
            throw new InvalidPhoneEx(InvalidPhoneEx.Type.second);
        }

        this.phone = phone;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
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
