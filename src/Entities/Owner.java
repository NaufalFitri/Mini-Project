package Entities;

import Exceptions.InvalidPhoneEx;

public class Owner {

    private int id;
    private final String name;
    private final String phone;
    private final String address;

    public Owner(int id, String name, String phone, String address) throws InvalidPhoneEx {

        if (phone.length() > 11) {
            throw new InvalidPhoneEx(InvalidPhoneEx.Type.first);
        }

        try {
            Integer.parseInt(phone);
        } catch (NumberFormatException e) {
            throw new InvalidPhoneEx(InvalidPhoneEx.Type.second);
        }

        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
