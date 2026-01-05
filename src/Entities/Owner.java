package Entities;

import Exceptions.InvalidPhoneEx;

public class Owner extends Entity {

    private String phone;
    private String address;

    public Owner(int id, String name, String phone, String address) throws InvalidPhoneEx {
        super(id, name);

        if (phone.length() > 11) {
            throw new InvalidPhoneEx(InvalidPhoneEx.Type.first);
        }

        try {
            Integer.parseInt(phone);
        } catch (NumberFormatException e) {
            throw new InvalidPhoneEx(InvalidPhoneEx.Type.second);
        }

        this.phone = phone;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        super.name = name;
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
