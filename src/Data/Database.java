package Data;
import Entities.Doctor;
import Entities.Owner;
import Entities.Patient;
import Exceptions.InvalidGenderEx;
import Exceptions.InvalidPhoneEx;
import Exceptions.InvalidSpecializationEx;
import Rooms.DiagnoseRoom;
import Rooms.Room;
import Rooms.TreatmentRoom;
import Rooms.WardRoom;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final String driver;
    private final String dbName;
    private final String connectionURL;
    private final String username;
    private final String password;
    private Connection conn = null;

    private final List<Doctor> doctorList = new ArrayList<>();
    private final List<Patient> patientList = new ArrayList<>();
    private final List<DiagnoseRoom> diagnoseRooms = new ArrayList<>();
    private final List<TreatmentRoom> treatmentRooms = new ArrayList<>();
    private final List<WardRoom> wardRooms = new ArrayList<>();
    private final List<Owner> ownerList = new ArrayList<>();

    private void loadAllEntities(Statement stmt) throws SQLException {
        ResultSet res;

        List<String> statements = new ArrayList<>(4);
        String statementOwners = "SELECT * FROM owners";
        String statementDRoom = "SELECT * FROM rooms r JOIN room_types rt ON r.roomType_id = rt.roomType_id WHERE rt.type = 'Diagnose'";
        String statementTRoom = "SELECT * FROM rooms r JOIN room_types rt ON r.roomType_id = rt.roomType_id WHERE rt.type = 'Treatment'";
        String statementDoctor = "SELECT * FROM veterinarians";
        String statementPatient = "SELECT * FROM animals";
        String statementWRoom = "SELECT * FROM rooms r JOIN room_types rt ON r.roomType_id = rt.roomType_id WHERE rt.type = 'Ward'";
        statements.add(statementOwners);
        statements.add(statementDRoom);
        statements.add(statementTRoom);
        statements.add(statementDoctor);
        statements.add(statementPatient);
        statements.add(statementWRoom);

        int i = 1;
        for (String statement : statements){
            res = stmt.executeQuery(statement);
            while (res.next()) {
                switch (i) {
                    case 1:

                        try {
                            Owner o = new Owner(res.getInt(1), res.getString(2), res.getString(3), res.getString(4));
                            ownerList.add(o);
                        
                        } catch (InvalidPhoneEx e) {
                            e.printStackTrace();
                        }

                        break;
                    case 2:
                        diagnoseRooms.add(new DiagnoseRoom(res.getInt(1), res.getString(2), res.getInt(3)));
                        System.out.println(res.getString(2));
                        break;
                    case 3:
                        treatmentRooms.add(new TreatmentRoom(res.getInt(1), res.getString(2), res.getInt(3)));
                        System.out.println(res.getString(2));
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
                            Owner o = null;
                            for (Owner t : ownerList) {
                                if (t.getId() == res.getInt(9)) {
                                    o = t;
                                }
                            }
                            
                            if (o == null) {
                                continue;
                            }
                            Patient p = new Patient(res.getInt(1), res.getString(2), res.getString(3), res.getString(4), res.getInt(5), res.getString(6), o);

                            List<String> medications = new ArrayList<>();
                            String[] meds = res.getString(8).split(",");
                            for (String med : meds) {
                                medications.add(med.trim());
                            }

                            p.setDiagnose(res.getString(7));
                            p.setMedications(medications);
                            patientList.add(p);
                        } catch (InvalidGenderEx e) {
                            e.printStackTrace();
                        }

                        break;
                    case 6:
                        wardRooms.add(new WardRoom(res.getInt(1), res.getString(2), res.getInt(3)));
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

    public List<WardRoom> getWardRooms() {
        return wardRooms;
    }

    public List<Owner> getOwnersList() {
        return ownerList;
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
        String getID = "SELECT animal_id FROM `animals` WHERE `name` = '" + name + "' AND `owner_id` = '" + o.getId() + "'";

        int rows = stmt.executeUpdate(statement);
        if (rows > 0) {
            res = stmt.executeQuery(getID);
            if (res.next()) {
                try {
                    Patient p = new Patient(res.getInt(1), name, species, breed, age, gender,o);
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
        String getID = "SELECT owner_id FROM `owners` WHERE `owner_name` = '" + name + "', `phone` = '" + phone + "'";

        int rows = stmt.executeUpdate(statement);
        if (rows > 0) {
            res = stmt.executeQuery(getID);
            if (res.next()) {
                try {
                    Owner o = new Owner(res.getInt(1), name, phone, address);
                    ownerList.add(o);
                } catch (InvalidPhoneEx e) {
                    e.printStackTrace();
                }

            }
        }

        stmt.close();
        return rows + " row(s) updated.";

    }

    public String insertRoom(Statement stmt, String number, int maxPatients, int id) throws SQLException {

        ResultSet res;

        String statement = "INSERT INTO `rooms` (`room_id`, `room_number`, `roomType_id`, `maxcapacity`) VALUES (NULL, '" + number + "'," + id + "," + maxPatients + ")";
        String getID = "SELECT o.room_id, rt.type  FROM `owners` o JOIN room_types rt ON rt.roomType_id = o.roomType_id WHERE o.`room_number` = '" + number;

        int rows = stmt.executeUpdate(statement);
        if (rows > 0) {
            res = stmt.executeQuery(getID);
            if (res.next()) {
                switch (res.getString(2)) {
                    case "Diagnose":
                        DiagnoseRoom DRoom = new DiagnoseRoom(res.getInt(1), number, maxPatients);
                        diagnoseRooms.add(DRoom);
                        break;
                    case "Treatment":
                        TreatmentRoom TRoom = new TreatmentRoom(res.getInt(1), number, maxPatients);
                        treatmentRooms.add(TRoom);
                        break;
                    case "Ward":
                        WardRoom WRoom = new WardRoom(res.getInt(1), number, maxPatients);
                        wardRooms.add(WRoom);
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

        String statement = "UPDATE `veterinarians` SET `name` = '" + ud.getName() + "', `specialization` = '" + ud.getField().name() + "', `phone` = '" + ud.getPhone() + "' WHERE `veterinarians`.`vet_id` = " + ud.getId();
        int rows = stmt.executeUpdate(statement);

        return rows + " row(s) updated.";
    }

    public String updatePatient(Statement stmt, Patient up) throws SQLException {
        int i = 0;
        for (Patient p : patientList) {
            if (p.getId() == up.getId()) {
                patientList.set(i, up);
                break;
            }
            i++;
        }

        String statement = "UPDATE `animals` SET `name` = '" + up.getName() + "', `species` = '" + up.getSpecies() + "', `breed` = '" + up.getBreed() + "', `age` = " + up.getAge() + ", `gender` = '" + up.getGender().name() + "', `owner_id` = " + up.getOwner().getId() + ", `notes` = '" + up.getDiagnosis() + "', `medications` = '" + String.join(", ", up.getMedications()) + "' WHERE `animals`.`animal_id` = " + up.getId();
        int rows = stmt.executeUpdate(statement);

        return rows + " row(s) updated.";
    }

    public String updateOwner(Statement stmt, Owner uo) throws SQLException {
        int i = 0;
        for (Owner o : ownerList) {
            if (o.getId() == uo.getId()) {
                ownerList.set(i, uo);
                break;
            }
            i++;
        }

        String statement = "UPDATE `owners` SET `owner_name` = '" + uo.getName() + "', `phone` = '" + uo.getPhone() + "', `address` = '" + uo.getAddress() + "' WHERE `owners`.`owner_id` = " + uo.getId();
        int rows = stmt.executeUpdate(statement);

        return rows + " row(s) updated.";
    }

    public String updateRoom(Statement stmt, Room ur) throws SQLException {
        String roomtype = "";

        if (ur instanceof DiagnoseRoom udr) {
            int i = 0;
            for (DiagnoseRoom dr : diagnoseRooms) {
                if (dr.getRoomID() == udr.getRoomID()) {
                    diagnoseRooms.set(i, udr);
                    break;
                }
                i++;
            }

            roomtype = "Diagnose";

        } else if (ur instanceof TreatmentRoom utr) {
            int i = 0;
            for (TreatmentRoom tr : treatmentRooms) {
                if (tr.getRoomID() == utr.getRoomID()) {
                    treatmentRooms.set(i, utr);
                    break;
                }
                i++;
            }

            roomtype = "Treatment";
        }

        String statement = "UPDATE `rooms` r INNER JOIN room_types rt ON rt.type_name = '" + roomtype + "' SET r.`room_number` = '" + ur.getRoomNumber() + "', r.`roomType_id` = rt.`roomType_id` WHERE `rooms`.`room_id` = " + ur.getRoomID();
        int rows = stmt.executeUpdate(statement);

        return rows + " row(s) updated.";
    }

    public String deleteDoctor(Statement stmt, Doctor d) throws SQLException {

        doctorList.remove(d);
        String sqlStatement = "DELETE FROM veterinarians WHERE `veterinarians`.`vet_id` = " + d.getId();
        int rows = stmt.executeUpdate(sqlStatement);

        return rows + " row(s) updated.";
    }

    public String deletePatient(Statement stmt, Patient p) throws SQLException {

        patientList.remove(p);
        String sqlStatement = "DELETE FROM animals WHERE `animals`.`animal_id` = " + p.getId();
        int rows = stmt.executeUpdate(sqlStatement);

        return rows + " row(s) updated.";
    }

    public String deleteOwner(Statement stmt, Owner o) throws SQLException {

        ownerList.remove(o);
        String sqlStatement = "DELETE FROM owners WHERE `owners`.`owner_id` = " + o.getId();
        int rows = stmt.executeUpdate(sqlStatement);

        return rows + " row(s) updated.";

    }

    public String deleteRoom(Statement stmt, Room r) throws SQLException {

        if (r instanceof DiagnoseRoom dr) {
            diagnoseRooms.remove(dr);
        } else if (r instanceof TreatmentRoom tr) {
            treatmentRooms.remove(tr);
        }

        String sqlStatement = "DELETE FROM rooms WHERE `rooms`.`room_id` = " + r.getRoomID();
        int rows = stmt.executeUpdate(sqlStatement);

        return rows + " row(s) updated.";

    }

    public void addTreatment(Statement stmt, TreatmentRoom tr) throws SQLException {

        String statement = "INSERT INTO `treatments` (`treatment_id`, `vet_id`, `animal_id`, `room_id`, `date`, `diagnosis`, `notes`) VALUES (NULL, '" + tr.getAssignedDoctor().getId() + "', '" + tr.getCurrentPatient().getId() + "', '" + tr.getRoomID() + "', '" + tr.getTreatmentStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "', '" + tr.getCurrentPatient().getDiagnosis() + "', '" + String.join("\n", tr.generateReports()) + "')";
        int rows = stmt.executeUpdate(statement);

    }

}
