package Exceptions;

public class InvalidPhoneEx extends Exception {

    public enum Type {
        first("Phone number length must be between 10 and 11!"),
        second("Phone number is not a number!");

        private final String description;

        Type(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public InvalidPhoneEx(Type type) {
        super(type.getDescription());
    }

}
