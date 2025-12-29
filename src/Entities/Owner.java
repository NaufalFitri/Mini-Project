package Entities;

import Exceptions.InvalidPhoneEx;

public class Owner {

    private int id;
    private String name;
    private String phone;
    private String address;

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

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) throws InvalidPhoneEx {
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

    public void setAddress(String address) {
        this.address = address;
    }

}
