import java.io.*;
import javax.swing.*;

import Entities.Doctor;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Doctor d = new Doctor("D001", "Naufal");
        d.setField(Doctor.field.SURGERY);
    }
}