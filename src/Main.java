import Entities.Doctor;
import Entities.Patient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Database db = new Database();
        Connection con = null;
        try {

            if (db.getConnection() != null) {
                db.initialize();
                con = db.getConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (con == null) {
            return;
        }

        try {
            db.insertDoctor(con.createStatement(), "Naufal", Doctor.field.Cardiology, "0187786707");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        GUI gui = new GUI(db);
        gui.show();
    }
}