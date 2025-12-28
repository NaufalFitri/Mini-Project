import Entities.Doctor;
import Entities.Owner;
import Entities.Patient;
import Exceptions.InvalidGenderEx;
import Exceptions.InvalidPhoneEx;
import Exceptions.InvalidSpecializationEx;
import Rooms.DiagnoseRoom;
import Rooms.Room;
import Rooms.TreatmentRoom;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final String driver;
    private final String dbName;
    private final String connectionURL;
    private final String username;
    private final String password;
    private Connection conn = null;

    private List<Doctor> doctorList = new ArrayList<>();
    private List<Patient> patientList = new ArrayList<>();
    private List<DiagnoseRoom> diagnoseRooms = new ArrayList<>();
    private List<TreatmentRoom> treatmentRooms = new ArrayList<>();
    private List<Owner> ownersList = new ArrayList<>();

    private void loadAllEntities(Statement stmt) throws SQLException {
        ResultSet res;

        List<String> statements = new ArrayList<>(4);
        String statementOwners = "SELECT * FROM owners";
        String statementDRoom = "SELECT * FROM rooms r JOIN room_types rt ON rt.type = 'Diagnose'";
        String statementTRoom = "SELECT * FROM rooms r JOIN room_types rt ON rt.type = 'Treatment'";
        String statementDoctor = "SELECT * FROM veterinarians";
        String statementPatient = "SELECT * FROM animals";
        statements.add(statementOwners);
        statements.add(statementDRoom);
        statements.add(statementTRoom);
        statements.add(statementDoctor);
        statements.add(statementPatient);

        int i = 1;
        for (String statement : statements){
            res = stmt.executeQuery(statement);
            while (res.next()) {
                switch (i) {
                    case 1:

                        try {
                            Owner o = new Owner(res.getInt(1), res.getString(2), res.getString(3), res.getString(4));
                            ownersList.add(o);
                        } catch (InvalidPhoneEx e) {
                            e.printStackTrace();
                        }

                        break;
                    case 2:
                        diagnoseRooms.add(new DiagnoseRoom());
                        break;
                    case 3:
                        treatmentRooms.add(new TreatmentRoom());
                        break;
                    case 4:

                        try {
                            Doctor d = new Doctor(res.getInt(1), res.getString(2), res.getString(3), res.getString(4));
                            doctorList.add(d);
                        } catch (InvalidPhoneEx | InvalidSpecializationEx e) {
                            e.printStackTrace();
                        }

                        break;
                    case 5:

                        try {
                            Patient p = new Patient(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), res.getInt(5), res.getString(6));

                            for (Owner o : ownersList) {
                                if (o.getId() == res.getInt(7)) {
                                    p.setOwner(o);
                                }
                            }

                            patientList.add(p);
                        } catch (InvalidGenderEx e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }
            i++;
        }

        stmt.close();

    }

    public Database() {
        driver = "com.mysql.cj.jdbc.Driver";
        connectionURL = "jdbc:mysql://localhost:3306/";
        dbName = "miniproject";
        username = "root";
        password = "";
    }

    public Connection getConnection() throws Exception {
        Class.forName(driver);
        conn = DriverManager.getConnection(connectionURL+dbName,username,password);
        return conn;
    }

    public void initialize() throws SQLException {
        loadAllEntities(conn.createStatement());
    }

    public List<Doctor> getDoctorList() {
        return doctorList;
    }

    public List<Patient> getPatientList() {
        return patientList;
    }

    public List<DiagnoseRoom> getDiagnoseRooms() {
        return diagnoseRooms;
    }

    public List<TreatmentRoom> getTreatmentRooms() {
        return treatmentRooms;
    }

    public String insertDoctor(Statement stmt, String name, Doctor.field field, String phone) throws SQLException {

        ResultSet res;

        String statement = "INSERT INTO `veterinarians` (`vet_id`, `name`, `specialization`, `phone`) VALUES (NULL, '" + name + "', '" + field.name() + "', '" + phone + "')";
        String getID = "SELECT vet_id FROM `veterinarians` WHERE `name` = '" + name + "'";

        int rows = stmt.executeUpdate(statement);
        if (rows > 0) {
            res = stmt.executeQuery(getID);
            if (res.next()) {
                try {
                    Doctor d = new Doctor(res.getInt(1) , name, field.name(), phone);
                    doctorList.add(d);
                } catch (InvalidPhoneEx | InvalidSpecializationEx e) {
                    e.printStackTrace();
                }

            }
        }

        stmt.close();
        return rows + " row(s) updated.";
    }

    public String insertPatient(Statement stmt, String name, String species, String breed, int age, String gender, Owner o) throws SQLException {

        ResultSet res;

        String statement = "INSERT INTO `animals` (`animal_id`, `name`, `species`, `breed`, `age`, `gender`, `owner_id`) VALUES (NULL, '" + name + "','" + species + "','" + breed + "', " + age + ",'" + gender + "', " + o.getId() + ")";
        String getID = "SELECT animal_id FROM `veterinarians` WHERE `name` = '" + name + "', `owner_id` = " + o.getId();

        int rows = stmt.executeUpdate(statement);
        if (rows > 0) {
            res = stmt.executeQuery(getID);
            if (res.next()) {
                try {
                    Patient p = new Patient(res.getInt(1), name, species, breed, age, gender);
                    patientList.add(p);
                } catch (InvalidGenderEx e) {
                    e.printStackTrace();
                }

            }
        }

        stmt.close();
        return rows + " row(s) updated.";

    }

    public String insertOwner(Statement stmt, String name, String phone, String address) throws SQLException {

        ResultSet res;

        String statement = "INSERT INTO `owners` (`owner_id`, `owner_name`, `phone`, `address`) VALUES (NULL, '" + name + "','" + phone + "','" + address + "')";
        String getID = "SELECT owner_id FROM `owners` WHERE `owner_name` = '" + name + "', `phone` = " + phone;

        int rows = stmt.executeUpdate(statement);
        if (rows > 0) {
            res = stmt.executeQuery(getID);
            if (res.next()) {
                try {
                    Owner o = new Owner(res.getInt(1), name, phone, address);
                    ownersList.add(o);
                } catch (InvalidPhoneEx e) {
                    e.printStackTrace();
                }

            }
        }

        stmt.close();
        return rows + " row(s) updated.";

    }

    public String insertRoom(Statement stmt, String number, int id) throws SQLException {

        ResultSet res;

        String statement = "INSERT INTO `rooms` (`room_id`, `room_number`, `roomType_id`) VALUES (NULL, '" + number + "'," + id + ")";
        String getID = "SELECT o.room_id, rt.type  FROM `owners` o JOIN room_types rt ON rt.roomType_id = o.roomType_id WHERE o.`room_number` = '" + number;

        int rows = stmt.executeUpdate(statement);
        if (rows > 0) {
            res = stmt.executeQuery(getID);
            if (res.next()) {
                switch (res.getString(2)) {
                    case "Diagnose":
                        DiagnoseRoom DRoom = new DiagnoseRoom();
                        diagnoseRooms.add(DRoom);
                        break;
                    case "Treatment":
                        TreatmentRoom TRoom = new TreatmentRoom();
                        treatmentRooms.add(TRoom);
                        break;
                }
            }
        }

        stmt.close();
        return rows + " row(s) updated.";

    }

    public String updateDoctor(Statement stmt, Doctor ud) throws SQLException {
        int i = 0;
        for (Doctor d : doctorList) {
            if (d.getId() == ud.getId()) {
                doctorList.set(i, ud);
                break;
            }
            i++;
        }

        return "";
    }

}
