package MainPackage;

import java.io.*;
import java.util.*;

public class sessionData {

    private static sessionData newSessionData = null;

    Hashtable<Physician, ArrayList<Patient>> linkedPhysicianToPatients;
    ArrayList<Patient> allPatients;
    ArrayList<String> patientsArchive;
    ArrayList<Patient> dischargedPatients;
    Hashtable<String, ArrayList<String>> specializations;


//    void addPatientForAdmin() {
//
//    }
    private Hashtable<Physician, ArrayList<Patient>> readFromLinksFile() throws IOException {

        File fileObj = Main.fileGenerator("links.properties");

        Hashtable<Physician, ArrayList<Patient>> tempLinkedPhysicianToPatients = new Hashtable<Physician, ArrayList<Patient>>();

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            Admin admin = new Admin();

            for (String physicianId : properties.stringPropertyNames()) {

                String[] patientsId = properties.get(physicianId).toString().split("\\.", -2);

                ArrayList<Patient> patients = new ArrayList<Patient>();
                Physician physician = (Physician) admin.getUserById(physicianId);

                for (String id : patientsId)
                    patients.add((Patient) admin.getUserById(id));

                tempLinkedPhysicianToPatients.put(physician, patients);

            }

        }

        return tempLinkedPhysicianToPatients;


    }

    private void readThreeArrays() throws IOException {

        File fileObj = Main.fileGenerator("dataArrays.properties");

//        Hashtable<String, ArrayList<Object>> threeArrays = new Hashtable<String, ArrayList<Object>>();

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            Admin admin = new Admin("admin", "admin");

            this.allPatients = new ArrayList<Patient>();

            for (User user : admin.allUsers) {
                if (user.type == UserType.Patient)
                    allPatients.add((Patient) user);
            }

            for (String type : properties.stringPropertyNames()) {



//                if (type.equals("allPatients")) {
//
//                    String[] patientsId = properties.get(type).toString().split("\\.", -2);
//
//                    for (String patientId : patientsId) {
//
//                        allPatients.add((Patient) admin.getUserById(patientId));
//
//                    }
//
//                }
                if (type.equals("patientsArchive")) {

                    String[] patientsArchiveArray = properties.get(type).toString().split("\\.", -2);

                    patientsArchive.addAll(Arrays.asList(patientsArchiveArray));

                } else {

                    String[] dischargedPatientsId = properties.get(type).toString().split("\\.", -2);

                    for (String dischargedPatientId : dischargedPatientsId) {

                        dischargedPatients.add((Patient) admin.getUserById(dischargedPatientId));

                    }

                }


            }

        }

    }

    private Hashtable<String, ArrayList<String>> readFromConfigFile() throws FileNotFoundException {

        File myFile = new File("config.txt");
        Scanner myReader = new Scanner(myFile);

        Hashtable<String, ArrayList<String>> specializations = new Hashtable<String, ArrayList<String>>();

        while (myReader.hasNext()) {
            String line = myReader.nextLine();

            String specialization;
            ArrayList<String> diseases = new ArrayList<String>();

            String[] twoDotSplit = line.split(":", -2);
            specialization = twoDotSplit[0];
            String[] tempDiseases = twoDotSplit[1].split(",", -2);

            for (String disease : tempDiseases) {
                diseases.add(disease.substring(1));
            }

            specializations.put(specialization, diseases);
        }

        return specializations;

    }

    private sessionData() throws IOException {

        specializations = this.readFromConfigFile();
        linkedPhysicianToPatients = this.readFromLinksFile();
        this.readThreeArrays();

    }

    private void saveToLinksFile() throws IOException {

        File fileObj = Main.fileGenerator("links.properties");

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            Hashtable<String, String> savableDic = new Hashtable<String,String>();

            for (Physician physician : linkedPhysicianToPatients.keySet()) {

                String stringOfStrings = "";

                for (Patient addingPatient : linkedPhysicianToPatients.get(physician))
                    stringOfStrings = stringOfStrings.concat(addingPatient.id + ".");

                savableDic.put(physician.id, stringOfStrings);

            }

//            for (String physicianId : properties.stringPropertyNames()) {

//                ArrayList<String> patientsId = new ArrayList<String>();


//                for (Physician addingPhysician : linkedPhysicianToPatients.keySet()) {




//                }


//                ArrayList<Patient> patients = new ArrayList<Patient>();
//                Physician physician = (Physician) admin.getUserById(physicianId);
//
//                for (String id : patientsId)
//                    patients.add((Patient) admin.getUserById(id));
//
//                tempLinkedPhysicianToPatients.put(physician, patients);

//            }

            properties.putAll(savableDic);

            properties.store(new FileOutputStream("links.properties"), null);

        }

    }

    private void saveThreeArrays() throws IOException {

        File fileObj = Main.fileGenerator("dataArrays.properties");

        if (fileObj != null) {

            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            Hashtable<String, String> savableDic = new Hashtable<String,String>();

            String stringOfStrings = "";

//            for (Patient addingPatient : allPatients) {
//
//                stringOfStrings = stringOfStrings.concat(addingPatient.id + ".");
//
//            }
//
//            savableDic.put("allPatients", stringOfStrings);
//
//            stringOfStrings = "";

            for (String patientArchive : patientsArchive) {

                stringOfStrings = stringOfStrings.concat(patientArchive + ".");

            }

            savableDic.put("patientsArchive", stringOfStrings);

            stringOfStrings = "";

            for (Patient addingPatient : dischargedPatients) {

                stringOfStrings = stringOfStrings.concat(addingPatient.id + ".");

            }

            savableDic.put("patientsArchive", stringOfStrings);


            properties.putAll(savableDic);


            properties.store(new FileOutputStream("dataArrays.properties"), null);


        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        this.saveToLinksFile();
        this.saveThreeArrays();

    }

    public static sessionData getSessionData() throws IOException {

        if (newSessionData == null) {
            newSessionData = new sessionData();
        }
        return newSessionData;
    }

}
