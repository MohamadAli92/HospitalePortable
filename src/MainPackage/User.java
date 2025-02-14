package MainPackage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.JapaneseDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class User {

    String name;
    String lastName;
    String sex;
    String id;
    UserType type;
    String username;
    String password;
    Hashtable<User, String> messages;

    User(String username, String password, UserType type, String name, String lastName, String sex, String id){

        this.type = type;
        this.name = name;
        this.lastName = lastName;
        this.sex = sex;
        this.id = id;
        this.username = username;
        this.password = password;
        this.messages = new Hashtable<User, String>();

        if (Main.sessionData != null) {

            for (String userId : Main.sessionData.receivedMessages.keySet()) {

                if (userId.equals(this.id)) {

                    for (String senderId : Main.sessionData.receivedMessages.get(userId).keySet()) {

                        this.messages.put(Main.adminUser.getUserById(senderId), Main.sessionData.receivedMessages.get(userId).get(senderId));

                    }

                }

            }

        }

    }

    abstract void Menu() throws IOException, ParseException;

    abstract String[] getUserInformationArray();

    void getMessage(User sourceUser, String message) throws IOException {

//        if (messages.contains(sourceUser)) {
//
//        } else {
//            ArrayList<String> messagesList = new ArrayList<String>();
//            messagesList.add(message);
//            messages.put(sourceUser, messagesList);
//        }

        messages.put(sourceUser, message);

        if (Main.sessionData.receivedMessages.containsKey(this.id))
            Main.sessionData.receivedMessages.get(this.id).put(sourceUser.id, message);
        else {
            Hashtable<String, String> messages = new Hashtable<String, String>();
            messages.put(sourceUser.id, message);
            Main.sessionData.receivedMessages.put(this.id, messages);
        }

        Main.sessionData.saveFiles();

    }

    Integer showMessages() {

        if (messages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "There is no message!", "No message found!", JOptionPane.PLAIN_MESSAGE);
            return 0;
        }
        String[] allMessages = new String[1000];
        String title = ("Please enter one of these numbers: ");

        int n = 0;

        for (User user : messages.keySet()) {

            String info = (n+1 + " - " + user.name + " " + user.lastName + " : " + messages.get(user));
            allMessages[n] = info;
            n++;

        }
        Object[] message = {
                title, allMessages
        };
        String choice = JOptionPane.showInputDialog(null, message, "Messages", JOptionPane.PLAIN_MESSAGE);
        if (choice == null)
            return 0;
        return (Integer.parseInt(choice));

    }

    protected void sendMessage(User destUser, String message) throws IOException {

        destUser.getMessage(this, message);

    }

}

class Admin extends User {

    Hashtable<String, String[]> allUserCredentials;
    ArrayList<User> allUsers;

    Admin(String username, String password) throws IOException, ParseException {
        super(username, password, UserType.Admin, "admin", "admin", "admin", "000");

        allUserCredentials = Main.getUserCredentials();
        allUsers = Main.getUsersFromFile();

    }

    Admin() {
        super("admin", "admin", UserType.Admin, "admin", "admin", "admin", "000");

    }

    User getUserById(String id) {

        User foundUser = null;

        for (User user : allUsers) {
            if (user.id.equals(id)){
                foundUser = user;
                break;
            }
        }

        return foundUser;

    }

    void changeUserPassword(String username, String newPass) {

        for (String checkUsername : allUserCredentials.keySet()) {
            if (checkUsername.equals(username))
                allUserCredentials.get(checkUsername)[0] = newPass;
        }

    }

    void addUserPassword(String username, String newPass, String userType) {

        if (userType.equals("1"))
            allUserCredentials.put(username, new String[]{newPass, String.valueOf(UserType.Physician)});
        else if (userType.equals("2"))
            allUserCredentials.put(username, new String[]{newPass, String.valueOf(UserType.Nurse)});
        else if (userType.equals("3"))
            allUserCredentials.put(username, new String[]{newPass, String.valueOf(UserType.Patient)});

    }


    void updateCredentialsFile() throws IOException {

        File fileObj = Main.fileGenerator("userCredentials.properties");

        Hashtable<String, String> savableDic = new Hashtable<String, String>();

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));

            for (String key : allUserCredentials.keySet()) {

                String passType = "";
                passType = passType.concat(allUserCredentials.get(key)[0] + " " + allUserCredentials.get(key)[1]);
                savableDic.put(key, passType);
            }

            properties.clear();

            properties.putAll(savableDic);

            properties.store(new FileOutputStream("userCredentials.properties"), null);

        }

    }


    void addUserInformation(User user) {

        allUsers.add(user);

    }

    void updateInformationFile() throws IOException {

        File fileObj = Main.fileGenerator("usersInformation.properties");

        Hashtable<String, String> allInformationDicArray = new Hashtable<String, String>();

        for (User user: allUsers) {

             allInformationDicArray.put(user.username, Main.convertUserToFileDottedData(user));

        }

        if (fileObj != null) {


            Properties properties = new Properties();

            properties.putAll(allInformationDicArray);

            properties.store(new FileOutputStream("usersInformation.properties"), null);


        }

    }


    private void listAllUsers(){

        String numOfUser = "There are " + allUsers.size() + " users.";
        String[] allUsersArray = new String[1000];

        int i = 0;
        for (User user : allUsers) {
            String userInfo = "Name: " + user.name + " " + user.lastName + "\n" + "Type: " + user.type + "\n" + "Id:" + user.id + "\n--------\n";
            allUsersArray[i] = userInfo;
            i++;
        }

        Object[] message = {
                numOfUser,
                allUsersArray,
        };

        ImageIcon allUsersIcon = new ImageIcon("allUsersIcon.png");
        JOptionPane.showMessageDialog(null, message, "All Users", JOptionPane.PLAIN_MESSAGE, allUsersIcon);

    }

    public static boolean checkPassword(String newPass) {
        String[] characters = {"!", "@", "#", "$", "%", "&", "*"};


        for (String character : characters) {
            if (newPass.contains(character))
                return false;
        }

        return true;

    }

    private void searchUser(String sample) {

        int n = 1;

        String[] allUsersArray = new String[1000];


        for (User user : allUsers) {
            if (user.lastName.toLowerCase().contains(sample.toLowerCase())) {
                String userInfo = n + "- " + user.name + " " + user.lastName;
                allUsersArray[n] = userInfo;
                n++;
            }
        }

        String numOfUser = "Found " + (n-1) + " users:\n";

        Object[] message = {
                numOfUser,
                allUsersArray,
        };

        ImageIcon allUsersIcon = new ImageIcon("allUsersIcon.png");
        JOptionPane.showMessageDialog(null, message, "Searching for user", JOptionPane.PLAIN_MESSAGE, allUsersIcon);

    }

    private void addUser(String userType) throws IOException, ParseException {


        String username = " ";
        while (username.contains(" ")) {

            username = JOptionPane.showInputDialog(null, "Please enter Username: ", "Username of new user", JOptionPane.PLAIN_MESSAGE);

            if (username == null)
                return;
            if (username.contains(" "))
                JOptionPane.showMessageDialog(null, "username shouldn't include whitespaces!", "Invalid Username", JOptionPane.ERROR_MESSAGE);
        }

        String password = Main.getPassword();
        if (password == null)
            return;

        this.addUserPassword(username, password, String.valueOf(Integer.parseInt(userType)+1));
        this.updateCredentialsFile();

        String name = JOptionPane.showInputDialog(null, "Please enter name: ", "Name of new user", JOptionPane.PLAIN_MESSAGE);

        if (name == null)
            return;

        String lastName = JOptionPane.showInputDialog(null, "Please enter lastname: ", "Name of new user", JOptionPane.PLAIN_MESSAGE);

        if (lastName == null)
            return;

        Random r = new Random();
        int low = 100;
        int high = 1000;
        String id = Integer.toString(r.nextInt(high-low) + low);

        while (true) {
            boolean uniqueId = true;
            for (User user : allUsers) {
                if (user.id.equals(id)){
                    uniqueId = false;
                    break;
                }
            }
            if (uniqueId) break;
            else id = Integer.toString(r.nextInt(high-low) + low);
        }

        String[] buttonsUserType = { "Man", "Woman", "Cancel"};

        int genderChoice = JOptionPane.showOptionDialog(null, "Select gender", "Gender",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttonsUserType, 2);


        if (genderChoice == 0) {
            sex = "Man";
        } else if (genderChoice == 1) {
            sex = "Woman";
        } else return;

        User newUser = null;
        if (userType.equals("0")) {
            String field = JOptionPane.showInputDialog(null, "Please enter field: ", "Name of new user", JOptionPane.PLAIN_MESSAGE);

            if (field == null)
                return;

            String record = JOptionPane.showInputDialog(null, "Please enter record: ", "Name of new user", JOptionPane.PLAIN_MESSAGE);

            if (record == null)
                return;


            newUser = new Physician(username, password, name, lastName, sex, id, field, record);

        } else if (userType.equals("1")) {
            String record = JOptionPane.showInputDialog(null, "Please enter record: ", "Name of new user", JOptionPane.PLAIN_MESSAGE);

            if (record == null)
                return;


            newUser = new Nurse(username, password, record, name, lastName, sex, id);

        } else if (userType.equals("2")) {
            String age;
            while (true) {
                age = JOptionPane.showInputDialog(null, "Please enter age: ", "Age of new user", JOptionPane.PLAIN_MESSAGE);

                String regex = "^[a-zA-Z]+$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(age);
                if (age == null)
                    return;
                if (!matcher.matches())
                    break;

            }

            String disease = JOptionPane.showInputDialog(null, "Please enter Disease: ", "Disease of new user", JOptionPane.PLAIN_MESSAGE);

            if (disease == null)
                return;

            String mode = " ";

            String[] modeUsers = { "Vip", "Normal", "Insurance", "Cancel"};

            int modeChoice = JOptionPane.showOptionDialog(null, "Select mode", "Mode",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, modeUsers, 3);


            if (modeChoice == 0) {
                mode = "Vip";
            } else if (modeChoice == 1) {
                mode = "Normal";
            } else if (modeChoice == 2) {
                mode = "Insurance";
            } else return;

            newUser = new Patient(username, password, age, disease, mode, name, lastName, sex, id);

        }

        this.addUserInformation(newUser);
//        this.addUserPassword(username, password, userType);
        this.updateInformationFile();
//        this.updateCredentialsFile();

    }

    private void deleteUser(String id) throws IOException {

        for (Iterator<User> it = allUsers.iterator(); it.hasNext(); ){

            User deletingUser = it.next();

            if (deletingUser.id.equals(id)) {
                String username = deletingUser.username;

                // Delete user information
                it.remove();

                // Delete user credentials
                for (String checkingUsername : allUserCredentials.keySet()) {
                    if (checkingUsername.equals(username)){
                        allUserCredentials.remove(username);
                        break;
                    }
                }

                // Delete for Physician
                if (deletingUser.type == UserType.Physician) {

                    for (Physician deletingPhysician : Main.sessionData.linkedPhysicianToPatients.keySet()) {
                        if (deletingPhysician.id.equals(deletingUser.id)){
                            Main.sessionData.linkedPhysicianToPatients.remove(deletingPhysician);
                            break;
                        }
                    }

                }

                // Delete for Patient
                if (deletingUser.type == UserType.Patient) {

                    for (Physician checkingPhysician : Main.sessionData.linkedPhysicianToPatients.keySet()) {
                        for (Patient deletingPatient : Main.sessionData.linkedPhysicianToPatients.get(checkingPhysician)) {
                            if (deletingPatient.id.equals(deletingUser.id)){
                                Main.sessionData.linkedPhysicianToPatients.get(checkingPhysician).remove(deletingPatient);
                                break;
                            }
                        }
                    }

                    for (Patient deletingPatient : Main.sessionData.allPatients) {
                        if (deletingPatient.id.equals(deletingUser.id)){
                            Main.sessionData.allPatients.remove(deletingPatient);
                            break;
                        }
                    }

                    for (Patient deletingPatient : Main.sessionData.allPatientsDate.keySet()) {
                        if (deletingPatient.id.equals(deletingUser.id)){
                            Main.sessionData.allPatientsDate.remove(deletingPatient);
                            break;
                        }
                    }

                    for (Patient deletingPatient : Main.sessionData.dischargedPatients) {
                        if (deletingPatient.id.equals(deletingUser.id)){
                            Main.sessionData.dischargedPatients.remove(deletingPatient);
                            break;
                        }
                    }

                }

                // Delete messages for all Types
                for (Iterator<String> innerIt = Main.sessionData.receivedMessages.keySet().iterator(); innerIt.hasNext(); ){
                    String deletingId = innerIt.next();
                    if (deletingId.equals(deletingUser.id)){
                        innerIt.remove();
                        continue;
                    }
                    for (String messageId : Main.sessionData.receivedMessages.get(deletingId).keySet()){
                        if (messageId.equals(deletingUser.id)){
                            Main.sessionData.receivedMessages.get(deletingId).remove(messageId);
                            break;
                        }
                    }
                    if (Main.sessionData.receivedMessages.get(deletingId).isEmpty())
                        innerIt.remove();
                }

                break;
            }
        }

        Main.sessionData.saveFiles();
    }

    public User getUserObj(String username) {

        for (User checkUser : allUsers)
            if (checkUser.username.equals(username)) {
                return checkUser;
            }

        return null;
    }

    @Override
    void Menu() throws IOException, ParseException {

        while (true) {

            String[] buttons = { "list all users", "search user by last name", "add user", "delete user",
                    "change password", "Exit to login menu"};

            int choiceN = JOptionPane.showOptionDialog(null, "Select one of these options", this.type + "'s Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, 5);

            String choice = String.valueOf(choiceN);

            if (choice.equals("0")) {

                this.listAllUsers();

            } else if (choice.equals("1")) {

                String searchSample = JOptionPane.showInputDialog(null, "Enter search sample: ", "Search user", JOptionPane.PLAIN_MESSAGE);

                if (searchSample == null)
                    return;

                this.searchUser(searchSample);
            } else if (choice.equals("2")) {

                String[] buttonsUserType = { "Physician", "Nurse", "Patient", "Cancel"};

                int choiceUsersN = JOptionPane.showOptionDialog(null, "Select new user type", "Adding user type",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttonsUserType, 3);

                String userType = String.valueOf(choiceUsersN);
                if (choiceUsersN != 3)
                    this.addUser(userType);


            } else if (choice.equals("3")) {
                String deletingId = JOptionPane.showInputDialog(null, "Enter search sample: ", "Search user", JOptionPane.PLAIN_MESSAGE);

//                if (deletingId == null)
//                    return;

                this.deleteUser(deletingId);
                this.updateInformationFile();
                this.updateCredentialsFile();
            } else if (choice.equals("4")) {

                String newPass = Main.getPassword();
                this.changeUserPassword(username, newPass);
                this.updateCredentialsFile();

            } else if (choice.equals("5")) {
                break;
            }

        }




    }

    @Override
    String[] getUserInformationArray() {
        return new String[]{this.name, this.lastName, this.sex, this.id, String.valueOf(this.type)};
    }

}

class Physician extends User {

    String field;
    String record;
    Hashtable<Patient, Date> patients;

    Physician(String username, String password, String name, String lastName, String sex, String id, String field, String record) throws IOException, ParseException {
        super(username, password, UserType.Physician, name, lastName, sex, id);

        this.field = field;
        this.record = record;
        this.patients = getPatients();

    }

    private Hashtable<Patient, Date> getPatients() throws IOException, ParseException {

        Hashtable<Patient, Date> tempPatients = new Hashtable<Patient, Date>();

        if (Main.sessionData != null) {
            for (Physician checkingPhys : Main.sessionData.linkedPhysicianToPatients.keySet()) {
                if (checkingPhys.id.equals(this.id)) {
                    for (Patient addingPatient : Main.sessionData.linkedPhysicianToPatients.get(checkingPhys)) {
                        for (Patient datePatient : Main.sessionData.allPatientsDate.keySet()) {
                            if (datePatient.id.equals(addingPatient.id)) {
                                tempPatients.put(addingPatient, Main.sessionData.allPatientsDate.get(datePatient));
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            return tempPatients;
        } else return null;

    }

    private void pickPatient() throws IOException {

        while (true) {

            ArrayList<Patient> availablePatients = new ArrayList<Patient>();

            for (Patient checkingPatient : Main.sessionData.allPatients) {

                boolean isFree = true;

                for (Physician checkingPhysician : Main.sessionData.linkedPhysicianToPatients.keySet()) {

                    ArrayList<String> ids = new ArrayList<String>();

                    for (Patient patient : Main.sessionData.linkedPhysicianToPatients.get(checkingPhysician)) {
                        ids.add(patient.id);
                    }

                    if (ids.contains(checkingPatient.id)) {
                        isFree = false;
                        break;
                    }

                }

                if (isFree)
                    if (Main.sessionData.specializations.get(this.field).contains(checkingPatient.disease))
                        availablePatients.add(checkingPatient);

            }

            int n = 0;

            if (availablePatients.isEmpty()) {
                JOptionPane.showMessageDialog(null, "There isn't any patient!", "Not Found", JOptionPane.WARNING_MESSAGE);
                break;
            }
            String[] usersInfo = new String[1000];

            for (Patient patient : availablePatients) {
                String info = (n+1) + " - " + patient.name + " " + patient.lastName + '\n';
                usersInfo[n] = info;
                n++;
            }

            Object[] message = {
                    "Please enter number of a patient: ",
                    usersInfo
            };

            String choice = JOptionPane.showInputDialog(null, message, "Pick Patient", JOptionPane.PLAIN_MESSAGE);

            if (choice == null)
                return;

            int choiceInt;
            try {
                choiceInt = Integer.parseInt(choice)-1;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid choice!", "Invalid choice", JOptionPane.WARNING_MESSAGE);
                break;
            }

            if (choiceInt < 0 || choiceInt >= availablePatients.size()) {
                JOptionPane.showMessageDialog(null, "Invalid choice!", "Invalid choice", JOptionPane.WARNING_MESSAGE);
                break;
            } else {
                Patient addingPatient = availablePatients.get(choiceInt);
                this.patients.put(addingPatient, new Date());
                Main.sessionData.allPatientsDate.putAll(this.patients);

                boolean userFound = false;

                assert Main.sessionData.linkedPhysicianToPatients != null;
                for (Physician physician : Main.sessionData.linkedPhysicianToPatients.keySet()) {
                    if (physician.id.equals(this.id)) {
                        userFound = true;
                        Main.sessionData.linkedPhysicianToPatients.get(physician).add(addingPatient);
                    }
                } if (!userFound){
                    ArrayList<Patient> addingList = new ArrayList<Patient>();
                    addingList.add(addingPatient);
                    Main.sessionData.linkedPhysicianToPatients.put(this, addingList);
                }

            }
        }

        Main.sessionData.saveFiles();

    }

    private void listAllPatients() {

        int n = 0;

        if (patients.isEmpty()){
            JOptionPane.showMessageDialog(null, "No patient has been added!", "Not Found", JOptionPane.WARNING_MESSAGE);
        } else {
            String[] allPatients = new String[1000];
            for (Patient patient : patients.keySet()) {
                String patientInfo = patient.name + " " + patient.lastName + '\n';
                patientInfo += "    disease: " + patient.disease + '\n';
                patientInfo += "    id: " + patient.id + '\n';
                patientInfo += "-----------------";
                allPatients[n] = patientInfo;
                n++;
            }
            JOptionPane.showMessageDialog(null, allPatients, "Picked patients", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void viewPatientInfo(String sample) {

        ArrayList<Patient> foundPatients = new ArrayList<Patient>();

        if (sample.contains("-")) {

            String[] lastNameId = sample.split("-", -2);
            String lastName = lastNameId[0];
            String id = lastNameId[1];

            for (Patient patient : Main.sessionData.allPatients) {

                if (patient.lastName.equals(lastName) && patient.id.equals(id)) {

                    foundPatients.add(patient);

                }

            }

        } else {

            for (Patient patient : Main.sessionData.allPatients) {

                if (patient.name.equals(sample)) {

                    foundPatients.add(patient);

                }

            }

        }
        String[] allPatients = new String[1000];
        String foundNums = "Found " + foundPatients.size() + " results";
        int n = 0;
        for (Patient patient : foundPatients) {
            String userInfo = (patient.name + "/" +
                                patient.lastName + "/" +
                                patient.disease + "/" +
                                patient.age + "/" +
                                patient.mode);
            n++;
            allPatients[n] = userInfo;
        }
        Object[] message = {
                foundNums, allPatients
        };

        JOptionPane.showMessageDialog(null, message, "Picked patients", JOptionPane.PLAIN_MESSAGE);


    }

    private void writeMedicine() throws IOException {

        while (true) {

            Integer choice = this.showMessages();
            if (choice == 0) {
                break;
            } else {
                List<User> users = new ArrayList<User>(messages.keySet());
                User destUser = users.get(choice-1);

                if (destUser.type == UserType.Nurse) {

                    String medicines = JOptionPane.showInputDialog(null, "Please write medicines:", "Write medicine", JOptionPane.PLAIN_MESSAGE);

                    this.sendMessage(destUser, medicines);

                    this.messages.remove(destUser);

                }
            }
        }
    }

    private void dischargePatient(String id) throws IOException, ParseException {

        for (Patient dischargingPatient : patients.keySet()) {
            if (dischargingPatient.id.equals(id)) {

                String archiveString = "Patient " + "(" + dischargingPatient.name + " " + dischargingPatient.lastName + ")" +
                        " that was entered at (" + patients.get(dischargingPatient) + ") with problem (" +
                        dischargingPatient.disease + ") " + "discharged at (" + new Date() + ") and was a (" +
                        dischargingPatient.mode + ") patient;";

                JOptionPane.showMessageDialog(null, archiveString, "Archieved", JOptionPane.PLAIN_MESSAGE);

                patients.remove(dischargingPatient);

                for (Physician physician : Main.sessionData.linkedPhysicianToPatients.keySet()) {
                    if (physician.id.equals(this.id)){
                        for (Patient patient : Main.sessionData.linkedPhysicianToPatients.get(physician)) {
                            if (patient.id.equals(dischargingPatient.id)) {
                                Main.sessionData.linkedPhysicianToPatients.get(physician).remove(patient);
                                break;
                            }
                        }
                    }
                }

                Main.sessionData.dischargedPatients.add(dischargingPatient);
                Main.sessionData.patientsArchive.add(archiveString);
                Main.sessionData.allPatientsDate.putAll(this.patients);
                Main.sessionData.allPatients.remove(dischargingPatient);

                Main.sessionData.saveFiles();

                break;
            }
        }
    }


    @Override
    void Menu() throws IOException, ParseException {

        while (true) {

            String[] buttons = { "Pick Patient", "List All Patients", "View Patient Info", "Write Medicine",
                    "Discharge Patient", "Change Password", "Exit to login menu"};

            int choiceN = JOptionPane.showOptionDialog(null, "Select one of these options", this.type + "'s Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, 5);

            String choice = String.valueOf(choiceN);


            if (choice.equals("0")) {

                this.pickPatient();

            } else if (choice.equals("1")) {

                this.listAllPatients();

            } else if (choice.equals("2")) {

                String searchSample = JOptionPane.showInputDialog(null, "Enter patient's name or enter lastname-id: ", "Search user", JOptionPane.PLAIN_MESSAGE);

                if (searchSample == null)
                    continue;

                this.viewPatientInfo(searchSample);

            } else if (choice.equals("3")) {

                this.writeMedicine();

            } else if (choice.equals("4")) {

                String patientId = JOptionPane.showInputDialog(null, "Enter patient's id: ", "Search user", JOptionPane.PLAIN_MESSAGE);

                if (patientId == null)
                    continue;

                this.dischargePatient(patientId);

            } else if (choice.equals("5")) {

                String password = Main.getPassword();

                Admin newAdmin = new Admin("admin", "admin");
                newAdmin.changeUserPassword(this.username, password);
                newAdmin.updateCredentialsFile();

            } else if (choice.equals("6")) {
                break;
            }

        }

    }

    @Override
    String[] getUserInformationArray() {
        return new String[]{this.name, this.lastName, this.sex, this.id, String.valueOf(this.type), this.record, this.field};
    }


}

class Nurse extends User {

    String record;

    Nurse(String username, String password, String record, String name, String lastName, String sex, String id) {
        super(username, password, UserType.Nurse, name, lastName, sex, id);

        this.record = record;
    }

    private void noDoctorAssigned() {

        ArrayList<Patient> notAssignedPatients = new ArrayList<Patient>();

        for (Patient pubPatient : Main.sessionData.allPatients) {

            boolean notAssigned = true;

            for (Patient assignedPatient : Main.sessionData.allPatientsDate.keySet()) {

                if (pubPatient.id.equals(assignedPatient.id)){
                    notAssigned = false;
                    break;
                }

            }

            if (notAssigned) {
                notAssignedPatients.add(pubPatient);
            }

        }

        int n =0;

        String[] allUsersInfo = new String[1000];

        for (Patient patient : notAssignedPatients) {

            String userInfo = (n+1 + " - " + patient.name + " " + patient.lastName + '\n');
            allUsersInfo[n] = userInfo;
            n++;

        }
        if (notAssignedPatients.isEmpty())
            JOptionPane.showMessageDialog(null, "No patients found!", "No patient found", JOptionPane.WARNING_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, allUsersInfo, "All patients info", JOptionPane.PLAIN_MESSAGE);
    }

    private void checkedIn() throws ParseException {

        JTextField EnterDate = new JTextField();
        JTextField OutDate = new JTextField();
        Object[] message = {
                "Please enter a date in format DD-MM-YYYY: ", EnterDate,
                "Please enter another date in format DD-MM-YYYY: ", OutDate
        };

        int selected = JOptionPane.showConfirmDialog(null, message, "Checked In", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

        String firstDateString = EnterDate.getText();
        String secondDateString = OutDate.getText();

        if (selected != JOptionPane.OK_OPTION)
            return;

        Date firstDate = new SimpleDateFormat("dd-MM-yyyy").parse(firstDateString);
        Date secondDate = new SimpleDateFormat("dd-MM-yyyy").parse(secondDateString);

        int n = 0;

        String[] allUsersInfo = new String[1000];

        for (Patient patient : Main.sessionData.allPatientsDate.keySet()) {

            if (Main.sessionData.allPatientsDate.get(patient).after(firstDate) &&
                Main.sessionData.allPatientsDate.get(patient).before(secondDate)) {

                String userInfo = (n+1 + " - " + patient.name + " " + patient.lastName + '\n');

                allUsersInfo[n] = userInfo;

                n++;

            }

        }

        if (Main.sessionData.allPatientsDate.isEmpty())
            JOptionPane.showMessageDialog(null, "No patient found", "No patients", JOptionPane.WARNING_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, allUsersInfo, "All checked in users", JOptionPane.PLAIN_MESSAGE);

    }

    private void getPrescription() throws IOException {

        while (true) {

            String menu = ("Select one of options:\n" +
                    "**Be careful that this message will be replaced with your last message to each user**\n" +
                    "1-Send message to all doctors to write medicine for their patients\n" +
                    "2-Reply to messages\n");

            String choiceMenu = JOptionPane.showInputDialog(null, menu, "Menu", JOptionPane.PLAIN_MESSAGE);

            if (choiceMenu == null) return;

            if (choiceMenu.equals("1")) {

                for (Physician doctor : Main.sessionData.linkedPhysicianToPatients.keySet()) {

                    for (Patient patient : Main.sessionData.linkedPhysicianToPatients.get(doctor)) {

                        String message = "Please write medicines for (" + patient.name + " " + patient.lastName + ").";

                        this.sendMessage(doctor, message);

                    }
                }

            } else if (choiceMenu.equals("2")) {

                while (true) {
                    Integer choice = this.showMessages();
                    if (choice == 0) {
                        break;
                    } else {
                        List<User> users = new ArrayList<User>(messages.keySet());
                        User destUser = users.get(choice-1);

                        if (destUser.type == UserType.Physician) {

                            String medicines = JOptionPane.showInputDialog(null, "Please write reply to message: ", "Reply", JOptionPane.PLAIN_MESSAGE);

                            if (medicines == null)
                                return;
                            this.sendMessage(destUser, medicines);

                            this.messages.remove(destUser);

                        }
                    }
                }

            } else if (choiceMenu.equals("0")) {
                break;
            }

        }

    }

    private void discharge() {

        int n = 0;

        String[] allDischarged = new String[1000];

        for (Patient dischargedPatient : Main.sessionData.dischargedPatients) {

            allDischarged[n] = n+1 + " - " + dischargedPatient.name + " " + dischargedPatient.lastName;

            n++;
        }

        if (Main.sessionData.dischargedPatients.isEmpty())
            JOptionPane.showMessageDialog(null, "No user found!", "No user", JOptionPane.WARNING_MESSAGE);
        else
            JOptionPane.showMessageDialog(null, allDischarged, "All users", JOptionPane.PLAIN_MESSAGE);

    }

    private void checkState() throws ParseException, IOException {

        while (true) {

            String[] buttons = { "No doctor assigned", "Checked in", "Get prescription", "Discharge",
                    "Exit to main menu"};

            int choiceN = JOptionPane.showOptionDialog(null, "Select one of these options", "Check state",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, 4);

            String choice = String.valueOf(choiceN);


            if (choice.equals("0")) {

                this.noDoctorAssigned();

            } else if (choice.equals("1")) {

                this.checkedIn();

            } else if (choice.equals("2")) {

                this.getPrescription();

            } else if (choice.equals("3")) {

                this.discharge();

            } else if (choice.equals("4")) {
                break;
            }

        }

    }

    @Override
    void Menu() throws IOException, ParseException {

        while (true) {

            String[] buttons = { "Check the patient state", "Change Password", "Exit to login menu"};

            int choiceN = JOptionPane.showOptionDialog(null, "Select one of these options", this.type + "'s Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, 2);

            String choice = String.valueOf(choiceN);

            if (choice.equals("0")) {

                this.checkState();

            } else if (choice.equals("1")) {

                String password = Main.getPassword();
                Admin newAdmin = new Admin("admin", "admin");
                newAdmin.changeUserPassword(this.username, password);
                newAdmin.updateCredentialsFile();

            } else if (choice.equals("2")) {
                break;
            }

        }

    }

    @Override
    String[] getUserInformationArray() {
        return new String[]{this.name, this.lastName, this.sex, this.id, String.valueOf(this.type), this.record};
    }

}

class Patient extends User {

    String age;
    String disease;
    Mode mode;

    Patient(String username, String password, String age, String disease, String mode, String name, String lastName, String sex, String id) {
        super(username, password, UserType.Patient, name, lastName, sex, id);

        this.age = age;
        this.disease = disease;
        this.mode = Mode.valueOf(mode);

    }

    private void checkOut() {

        boolean isDischarged = false;

        for (User dischargedPatient : Main.sessionData.dischargedPatients) {

            if (dischargedPatient.id.equals(this.id)) {
                isDischarged = true;
                break;
            }

        }

        if (!isDischarged) {
            JOptionPane.showMessageDialog(null, "You are not discharged yet!", "Not discharged yet!", JOptionPane.PLAIN_MESSAGE);
        } else {

            long checkOutPrice = 0;
            long daysInHis = 0;

            for (Patient user : Main.sessionData.allPatientsDate.keySet()) {
                if (user.id.equals(this.id)) {
                    long diffInMs = new Date().getTime() - Main.sessionData.allPatientsDate.get(user).getTime();
                    daysInHis = TimeUnit.DAYS.convert(diffInMs, TimeUnit.MILLISECONDS);
                }
            }

            if (this.mode == Mode.Vip) {
                checkOutPrice = (daysInHis+1) * 120;
            } else if (this.mode == Mode.Normal) {
                checkOutPrice = (daysInHis+1) * 70;
            } else if (this.mode == Mode.Insurance){
                checkOutPrice = (daysInHis+1) * 35;
            }

            String[] buttons = { "Confirm", "Cancel"};

            Object[] message = {
                    ("Your bill is " + checkOutPrice + "$"),
                    "Do you want to pay?",
            };

            JOptionPane.showOptionDialog(null, message, "Pay bill", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, 0);

        }
    }

    @Override
    void Menu() throws IOException, ParseException {

        while (true) {

            String[] buttons = { "Check out", "Change Password", "Exit to login menu"};

            int choiceN = JOptionPane.showOptionDialog(null, "Select one of these options", this.type + "'s Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, 5);

            String choice = String.valueOf(choiceN);

            if (choice.equals("0")) {

                this.checkOut();

            } else if (choice.equals("1")) {

                String password = Main.getPassword();
                Admin newAdmin = new Admin("admin", "admin");
                newAdmin.changeUserPassword(this.username, password);
                newAdmin.updateCredentialsFile();

            } else if (choice.equals("2")) {
                break;
            }

        }

    }

    @Override
    String[] getUserInformationArray() {
        return new String[]{this.name, this.lastName, this.sex, this.id, String.valueOf(this.type), this.age, this.disease, String.valueOf(this.mode)};
    }

}