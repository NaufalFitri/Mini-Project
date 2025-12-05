package Entities;

import java.lang.reflect.Field;

public class Doctor {

    public static enum field {
        Surgery,
        InternalMedicine,
        Dermatology,
        Ophthalmology,
        Dentistry,
        Cardiology,
        Oncology,
        Anesthesiology,
    }

    private String id;
    private String name;
    private boolean isTreating;
    private field specialisedField;


    public Doctor(String id,String name)
    {
        this.id = id;
        this.name = name;
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
