import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class sessionData {

    private static sessionData newSessionData = null;

    Hashtable<Physician, ArrayList<Patient>> linkedPhysicianToPatients;
    ArrayList<Patient> allPatients = new ArrayList<Patient>();
    Hashtable<Patient, Date> allPatientsDate;
    ArrayList<String> patientsArchive = new ArrayList<String>();
    ArrayList<Patient> dischargedPatients = new ArrayList<Patient>();
    Hashtable<String, ArrayList<String>> specializations;
    Hashtable<String, Hashtable<String, String>> receivedMessages;


    private Hashtable<Physician, ArrayList<Patient>> readFromLinksFile() throws IOException, ParseException {

        File fileObj = Main.fileGenerator("links.properties");

        Hashtable<Physician, ArrayList<Patient>> tempLinkedPhysicianToPatients = new Hashtable<Physician, ArrayList<Patient>>();

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            Admin admin = new Admin("admin", "admin");

            for (String physicianId : properties.stringPropertyNames()) {

                String[] patientsId = properties.get(physicianId).toString().split("\\.", -2);

                ArrayList<Patient> patients = new ArrayList<Patient>();
                Physician physician = (Physician) admin.getUserById(physicianId);

                for (String id : patientsId)
                    if (!id.isEmpty())
                        patients.add((Patient) admin.getUserById(id));

                tempLinkedPhysicianToPatients.put(physician, patients);

            }

        }

        return tempLinkedPhysicianToPatients;


    }

    private Hashtable<Patient, Date> readFromDatesFile() throws IOException, ParseException {

        File fileObj = Main.fileGenerator("dates.properties");

        Hashtable<Patient, Date> tempPatientsDate = new Hashtable<Patient, Date>();

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            Admin admin = new Admin("admin", "admin");

            for (String patientId : properties.stringPropertyNames()) {

                Patient patient = (Patient) admin.getUserById(patientId);

                tempPatientsDate.put(patient, new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse((String) properties.get(patientId)));

            }

        }

        return tempPatientsDate;


    }

    private void readThreeArrays() throws IOException, ParseException {

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

            this.patientsArchive = new ArrayList<String>();
            this.dischargedPatients = new ArrayList<Patient>();

            for (String type : properties.stringPropertyNames()) {

                if (type.equals("patientsArchive")) {

                    String[] patientsArchiveArray = properties.get(type).toString().split("\\.", -2);

                    for (String patientArchive : patientsArchiveArray) {

                        if (!patientArchive.isEmpty())
                            patientsArchive.add(patientArchive);
                    }

//                    patientsArchive.addAll(Arrays.asList(patientsArchiveArray));

                } else {

                    String[] dischargedPatientsId = properties.get(type).toString().split("\\.", -2);

                    for (String dischargedPatientId : dischargedPatientsId) {

                        if (!dischargedPatientId.isEmpty())
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

    private Hashtable<String, Hashtable<String, String>> readFromReceivedMsgFile() throws IOException {

        File fileObj = Main.fileGenerator("receivedMsg.properties");

        Hashtable<String, Hashtable<String, String>> tempReceivedMsg = new Hashtable<String, Hashtable<String, String>>();

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            for (String userId : properties.stringPropertyNames()) {

                String[] userMessagesAndSenderIdArray = properties.get(userId).toString().split("@@@", -2);

                Hashtable<String, String> userMessagesAndSenderIdTable = new Hashtable<String, String>();

                for (String messageAndSenderId : userMessagesAndSenderIdArray)
                    if (!messageAndSenderId.isEmpty()) {

                        String message = messageAndSenderId.split("##", -2)[0];
                        String senderId = messageAndSenderId.split("##", -2)[1];

                        userMessagesAndSenderIdTable.put(senderId, message);

                    }

                tempReceivedMsg.put(userId, userMessagesAndSenderIdTable);

            }

        }

        return tempReceivedMsg;

    }

    private sessionData() throws IOException, ParseException {

        this.readFiles();

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

            properties.clear();
            properties.putAll(savableDic);

            properties.store(new FileOutputStream("links.properties"), null);

        }

    }

    private void saveToDatesFile() throws IOException {

        File fileObj = Main.fileGenerator("dates.properties");

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            Hashtable<String, String> savableDic = new Hashtable<String,String>();

            for (Patient patient : allPatientsDate.keySet()) {

                savableDic.put(patient.id, allPatientsDate.get(patient).toString());

            }

            properties.clear();
            properties.putAll(savableDic);

            properties.store(new FileOutputStream("dates.properties"), null);

        }

    }

    private void saveThreeArrays() throws IOException {

        File fileObj = Main.fileGenerator("dataArrays.properties");

        if (fileObj != null) {

            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            Hashtable<String, String> savableDic = new Hashtable<String,String>();

            String stringOfStrings = "";

            if (patientsArchive != null) {

                for (String patientArchive : patientsArchive) {

                    stringOfStrings = stringOfStrings.concat(patientArchive + ".");

                }

                savableDic.put("patientsArchive", stringOfStrings);

            }


            stringOfStrings = "";

            if (dischargedPatients != null) {

                for (Patient addingPatient : dischargedPatients) {

                    stringOfStrings = stringOfStrings.concat(addingPatient.id + ".");

                }

                savableDic.put("dischargedPatients", stringOfStrings);

            }


            properties.clear();
            properties.putAll(savableDic);


            properties.store(new FileOutputStream("dataArrays.properties"), null);


        }

    }

    private void saveToReceivedMsgFile() throws IOException {

        File fileObj = Main.fileGenerator("receivedMsg.properties");

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            Hashtable<String, String> savableDic = new Hashtable<String,String>();

            for (String userId : receivedMessages.keySet()) {

                String userMessages = "";

                for (String senderId : receivedMessages.get(userId).keySet()) {

                    userMessages = userMessages.concat(receivedMessages.get(userId).get(senderId) + "##" + senderId + "@@@");

                }

                savableDic.put(userId, userMessages);

//                for (String message : receivedMessages.get(userId))
//                    stringOfStrings = stringOfStrings.concat(message + "@" + m);

//                savableDic.put(userId, stringOfStrings);

            }

            properties.clear();
            properties.putAll(savableDic);

            properties.store(new FileOutputStream("receivedMsg.properties"), null);

        }

    }

    void readFiles() throws IOException, ParseException {

        specializations = this.readFromConfigFile();
        linkedPhysicianToPatients = this.readFromLinksFile();
        allPatientsDate = this.readFromDatesFile();
        receivedMessages = this.readFromReceivedMsgFile();
        this.readThreeArrays();

    }

    void saveFiles() throws IOException {

        this.saveToLinksFile();
        this.saveThreeArrays();
        this.saveToDatesFile();
        this.saveToReceivedMsgFile();

    }


    public static sessionData getSessionData() throws IOException, ParseException {

        if (newSessionData == null) {
            newSessionData = new sessionData();
        }
        return newSessionData;
    }

}
