package MainPackage;

import java.io.*;
//import java.lang.reflect.Array;
import java.util.*;

abstract public class User {

    String name;
    String lastName;
    String sex;
    String id;
    UserType type;
    String username;
    String password;
    Hashtable<User, String> messages;


//    static Hashtable<String, String> getUserInformationFromFile(String username) throws IOException {
//
//        File fileObj = Main.fileGenerator("usersInformation.properties");
//
//        if (fileObj != null) {
//
//
//            Properties properties = new Properties();
//            properties.load(new FileInputStream(fileObj));
//
//
//            for (String key : properties.stringPropertyNames()) {
//                if (key.equals(username)) {
//                    return Main.convertFileDottedDataToDic(properties.get(key).toString());
//                }
//
//            }
//
//        }
//
//        return null;
//    }

//    public User getUserObjFromFile(UserType type) throws IOException {
////        Hashtable<String, String> userInformation = User.getUserInformationFromFile(username);
//
////        String name = this.name;
////        String lastName = this.lastName;
////        String sex = this.sex;
////        String id = this.id;
//
//        if (type == UserType.Physician) {
//            return new Physician(username, password, name, lastName, sex, id, userInformation.get("field"), userInformation.get("record"));
//        } else if (type == UserType.Nurse) {
//            return new Nurse(userInformation.get("record"), name, lastName, sex, id);
//        } else if (type == UserType.Patient) {
//            return new Patient(userInformation.get("age"), userInformation.get("disease"), userInformation.get("mode"), name, lastName, sex, id);
//        }
//
//        return null;
//    }

    User(String username, String password, UserType type, String name, String lastName, String sex, String id) {

        this.type = type;
        this.name = name;
        this.lastName = lastName;
        this.sex = sex;
        this.id = id;
        this.username = username;
        this.password = password;
        this.messages = new Hashtable<User, String>();

    }

//    private String findUserUsername() throws IOException {
//
//        Hashtable<String, Hashtable<String, String>> allData = Main.getUsersInformationFromFile();
//
//        for (String username : allData.keySet()) {
//
//            if ((this.id).equals(allData.get(username).get("id")))
//                return username;
//        }
//
//        return null;
//
//    }

//    private Hashtable<String, String[]> updateUserPassword(Hashtable<String, String[]> allData, String newPass) throws IOException {
//
//        String username = this.findUserUsername();
//
//        for (String checkUsername : allData.keySet()) {
//
//            if (checkUsername.equals(username)) {
//                allData.get(checkUsername)[0] = newPass;
//            }
//
//        }
//
//        return allData;
//
//    }

//    Hashtable<String, String[]> addUserPassword(Hashtable<String, String[]> allData, String newPass, String username){
//
//        allData.put(username, new String[]{newPass, String.valueOf(this.type)});
//
//        return allData;
//
//    }

//    Hashtable<String, String[]> changePassword(String newPass) throws IOException {
//
//        return updateUserPassword(Main.getUserCredentials(), newPass);
//
//    }

    abstract void Menu() throws IOException;

    abstract String[] getUserInformationArray();

    abstract Hashtable<String, String> getUserInformationDic();

    void getMessage(User sourceUser, String message) {

        messages.put(sourceUser, message);

    }

    protected void sendMessage(User destUser, String message) {

        destUser.getMessage(this, message);
    }

}

class Admin extends User {

    Hashtable<String, String[]> allUserCredentials;
//    Hashtable<String, Hashtable<String, String>> allUserInformation;
    ArrayList<User> allUsers;

    Admin(String username, String password) throws IOException {
        super(username, password, UserType.Admin, "admin", "admin", "admin", "000");

        allUserCredentials = Main.getUserCredentials();
//        allUserInformation = Main.getUsersInformationFromFile();
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
//        allUserInformation.put(user.username, user.getUserInformationDic());

    }

    void updateInformationFile() throws IOException {

        File fileObj = Main.fileGenerator("usersInformation.properties");

        Hashtable<String, String> allInformationDicArray = new Hashtable<String, String>();

        for (User user: allUsers) {

             allInformationDicArray.put(user.username, Main.convertUserToFileDottedData(user));

        }
//        String[] userInformationArray = user.getUserInformationArray();
//        String allData = "";
//
//        for (String data: userInformationArray) {
//            allData = allData.concat(data+".");
//        }
//        allInformationDicArray.put(username, allData);
        if (fileObj != null) {


            Properties properties = new Properties();

            properties.putAll(allInformationDicArray);

            properties.store(new FileOutputStream("usersInformation.properties"), null);


        }

    }

//    private void

    private void listAllUsers(){

//        Hashtable<String, Hashtable<String, String>> allUsers = Main.getUsersInformationFromFile();

        int n = 1;

        for (User user : allUsers) {
            System.out.println(n + "- " + user.name + " " + user.lastName);
            n++;
        }
    }

    public static boolean checkPassword(String newPass) {
        String[] characters = {"!", "@", "#", "$", "%", "&", "*"};


        for (String character : characters) {
            if (newPass.contains(character))
                return true;
        }

        return false;

    }

    private void searchUser(String sample) {

//        Hashtable<String, Hashtable<String, String>> allUsers = Main.getUsersInformationFromFile();

        int n = 1;

        for (User user : allUsers) {
            if (user.name.toLowerCase().contains(sample.toLowerCase())) {
                System.out.println(n + "- " + user.name + " " + user.lastName);
                n++;
            }
        }
    }

    private void addUser(String userType) throws IOException {

        String username = " ";
        while (username.contains(" ")) {
            System.out.println("Please enter Username:");
            username = Main.scanner.nextLine();
            if (username.contains(" "))
                System.out.println("username shouldn't include whitespaces!");
        }

        String password = " ";
        while (password.contains(" ") || !checkPassword(password)) {
            System.out.println("Please enter Password:");
            password = Main.scanner.nextLine();
            if (password.contains(" ") || !checkPassword(password))
                System.out.println("password shouldn't include whitespaces and must contains\n" +
                                   "at least one of these characters!: @#$%&*");
        }


        this.addUserPassword(username, password, userType);
        this.updateCredentialsFile();

        System.out.println("Please enter name:");
        String name = Main.scanner.nextLine();
        System.out.println("Please enter lastName:");
        String lastName = Main.scanner.nextLine();

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

        System.out.println("Please enter sex:");
        String sex = Main.scanner.nextLine();

        User newUser = null;
        if (userType.equals("1")) {
            System.out.println("Please enter field:");
            String field = Main.scanner.nextLine();
            System.out.println("Please enter record:");
            String record = Main.scanner.nextLine();

            newUser = new Physician(username, password, name, lastName, sex, id, field, record);

        } else if (userType.equals("2")) {
            System.out.println("Please enter record:");
            String record = Main.scanner.nextLine();

            newUser = new Nurse(username, password, record, name, lastName, sex, id);

        } else if (userType.equals("3")) {
            System.out.println("Please enter age:");
            String age = Main.scanner.nextLine();
            System.out.println("Please enter disease:");
            String disease = Main.scanner.nextLine();

            String mode = " ";
            ArrayList<String> modes = new ArrayList<String>();
            modes.add("Vip");
            modes.add("Normal");
            modes.add("Insurance");

            while (!modes.contains(mode)) {
                System.out.println("Please enter mode:");
                mode = Main.scanner.nextLine();
                if (!modes.contains(mode))
                    System.out.println("invalid mode!");
            }

            newUser = new Patient(username, password, age, disease, mode, name, lastName, sex, id);

        }

//        assert newUser != null;
//        Main.saveUserCredentials(newUser.addUserPassword(Main.getUserCredentials(), password, username));
        assert newUser != null;
        this.addUserInformation(newUser);
        this.updateInformationFile();

    }

    private void deleteUser(String id) {

//        Hashtable<String, Hashtable<String, String>> allUsers = Main.getUsersInformationFromFile();

//        for (String key : allUsers.keySet()) {
//            if (allUsers.get(key).get("id").equals(id))
//                allUsers.remove(key);
//        }

        for (User deletingUser : allUsers) {

            if (deletingUser.id.equals(id)) {
                String username = deletingUser.username;
                allUsers.remove(deletingUser);
                for (String checkingUsername : allUserCredentials.keySet()) {
                    if (checkingUsername.equals(username)){
                        allUserCredentials.remove(username);
                        break;
                    }

                }
                break;
            }

        }

    }

    public User getUserObj(String username) {

        for (User checkUser : allUsers)
            if (checkUser.username.equals(username)) {
                return checkUser;
            }

        return null;
    }

    @Override
    void Menu() throws IOException {

        while (true) {

            System.out.println("Select one of options:\n" +
                    "1-list all users\n" +
                    "2-search user by last name\n" +
                    "3-add user\n" +
                    "4-delete user\n" +
                    "5-change password\n" +
                    "0-Exit to login menu");

            String choice = Main.scanner.nextLine();

            if (choice.equals("1")) {
                this.listAllUsers();
            } else if (choice.equals("2")) {
                System.out.println("Enter search sample:");
                this.searchUser(Main.scanner.nextLine());
            } else if (choice.equals("3")) {

                System.out.println("Select one of these user types:\n" +
                        "1-Physician\n" +
                        "2-Nurse\n" +
                        "3-Patient\n");

                String userType = Main.scanner.nextLine();

                this.addUser(userType);
            } else if (choice.equals("4")) {
                System.out.println("Enter an id number:");
                this.deleteUser(Main.scanner.nextLine());
                this.updateInformationFile();
                this.updateCredentialsFile();
            } else if (choice.equals("5")) {

                String password = " ";
                while (password.contains(" ") || !checkPassword(password)) {
                    System.out.println("Please enter Password:");
                    password = Main.scanner.nextLine();
                    if (password.contains(" ") || !checkPassword(password))
                        System.out.println("password shouldn't include whitespaces and must contains\n" +
                                "at least one of these characters!: @#$%&*");
                }

                this.changeUserPassword(username, password);
                this.updateCredentialsFile();

            } else if (choice.equals("0")) {
                break;
            } else {
                System.out.println("Invalid input!");
            }

        }




    }

    @Override
    String[] getUserInformationArray() {
        return new String[]{this.name, this.lastName, this.sex, this.id, String.valueOf(this.type)};
    }

    @Override
    Hashtable<String, String> getUserInformationDic() {
        Hashtable<String, String> informationDic = new Hashtable<String, String>();

        informationDic.put("name", this.name);
        informationDic.put("lastName", this.lastName);
        informationDic.put("sex", this.sex);
        informationDic.put("id", this.id);
        informationDic.put("type", String.valueOf(this.type));

        return informationDic;
    }

}

class Physician extends User {

    String field;
    String record;
    Hashtable<Patient, Date> patients;

    Physician(String username, String password, String name, String lastName, String sex, String id, String field, String record){
        super(username, password, UserType.Physician, name, lastName, sex, id);

        this.field = field;
        this.record = record;
        if (Main.sessionData != null)
            patients = Main.sessionData.allPatientsDate;
        else patients = null;

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

            int n = 1;

            System.out.println("Please enter one of these numbers or enter 0 to exit:");
            for (Patient patient : availablePatients) {
                System.out.println(n + "- " + patient.name + " " + patient.lastName);
                n++;
            }
            String choice = Main.scanner.nextLine();
            if (choice.equals("0")) {
                break;
            } else {
                int choiceInt = Integer.parseInt(choice)-1;
                Patient addingPatient = availablePatients.get(choiceInt);
                this.patients.put(addingPatient, new Date());
                this.patients.putAll(Main.sessionData.allPatientsDate);

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

        int n = 1;

        if (Main.sessionData.allPatients.isEmpty()){
            System.out.println("No patient has been added!");
        } else {
            for (Patient patient : patients.keySet()) {
                System.out.println(n + " - " + patient.name + " " + patient.lastName);
                System.out.println("    " + patient.disease);
                System.out.println("    " + patients.get(patient));
                n++;
            }
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

        System.out.println("Found " + foundPatients.size() + " results");
        for (Patient patient : foundPatients) {
            System.out.println(patient.name + "/" +
                                patient.lastName + "/" +
                                patient.disease + "/" +
                                patient.age + "/" +
                                patient.mode);
        }

    }

    private void writeMedicine() {

        int n = 1;

        while (true) {
            System.out.println("Please enter one of these numbers or enter 0 to exit:");
            for (String message : messages.values()) {
                System.out.println(n + "- " + message);
                n++;
            }
            String choice = Main.scanner.nextLine();
            if (choice.equals("0")) {
                break;
            } else {
                List<User> users = new ArrayList<User>(messages.keySet());
                User destUser = users.get(Integer.getInteger(choice));

                if (destUser.type == UserType.Nurse) {

                    System.out.println("Please write medicines:");
                    String medicines = Main.scanner.nextLine();

                    this.sendMessage(destUser, medicines);

                    this.messages.remove(destUser);

                }

//                Patient addingPatient = availablePatients.get(Integer.getInteger(choice));
////                this.patients.add(addingPatient);
//                this.patients.put(addingPatient, new Date());
//                sessionData.linkedPhysicianToPatients.put(this, addingPatient);

            }
        }

//        for (String message : messages.values()) {
//
//        }
    }

    private void dischargePatient(String id) throws IOException {

        for (Patient dischargingPatient : patients.keySet()) {
            if (dischargingPatient.id.equals(id)) {

                String archiveString = "Patient " + "(" + dischargingPatient.name + " " + dischargingPatient.lastName + ")" +
                        " that was entered at (" + patients.get(dischargingPatient) + ") with problem (" +
                        dischargingPatient.disease + ") " + "discharged at (" + new Date() + ") and was a (" +
                        dischargingPatient.mode + ") patient;";

                System.out.println(archiveString);

//                Main.sessionData.patientsArchive.add(archiveString);

//                sessionData.allPatients.remove(dischargingPatient);

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
//                        Main.sessionData.linkedPhysicianToPatients.get(physician).remove(dischargingPatient);
                }
//                Main.sessionData.linkedPhysicianToPatients.get(this).remove(dischargingPatient);

                Main.sessionData.dischargedPatients.add(dischargingPatient);
                Main.sessionData.patientsArchive.add(archiveString);
                this.patients.putAll(Main.sessionData.allPatientsDate);

                Main.sessionData.saveFiles();

                break;
            }
        }
    }


    @Override
    void Menu() throws IOException {

        while (true) {

            System.out.println("Select one of options:\n" +
                    "1-Pick Patient\n" +
                    "2-List All Patients\n" +
                    "3-View Patient Info\n" +
                    "4-Write Medicine\n" +
                    "5-Discharge Patient\n" +
                    "6-Change Password\n" +
                    "0-Exit to login menu");

            String choice = Main.scanner.nextLine();

            if (choice.equals("1")) {

                this.pickPatient();

            } else if (choice.equals("2")) {

                this.listAllPatients();

            } else if (choice.equals("3")) {

                System.out.println("Enter patient's name or enter lastname-id:");
                this.viewPatientInfo(Main.scanner.nextLine());

            } else if (choice.equals("4")) {

                this.writeMedicine();

            } else if (choice.equals("5")) {

                System.out.println("Enter patient's id:");
                this.dischargePatient(Main.scanner.nextLine());

            } else if (choice.equals("6")) {

                String password = " ";
                while (password.contains(" ") || !Admin.checkPassword(password)) {
                    System.out.println("Please enter Password:");
                    password = Main.scanner.nextLine();
                    if (password.contains(" ") || !Admin.checkPassword(password))
                        System.out.println("password shouldn't include whitespaces and must contains\n" +
                                "at least one of these characters!: @#$%&*");
                }

                Admin newAdmin = new Admin("admin", "admin");
                newAdmin.changeUserPassword(this.username, password);
                newAdmin.updateCredentialsFile();

            } else if (choice.equals("0")) {
                break;
            } else {
                System.out.println("Invalid input!\n");
            }

        }

    }

    @Override
    String[] getUserInformationArray() {
        return new String[]{this.name, this.lastName, this.sex, this.id, String.valueOf(this.type), this.record, this.field};
    }

    @Override
    Hashtable<String, String> getUserInformationDic() {
        Hashtable<String, String> informationDic = new Hashtable<String, String>();

        informationDic.put("name", this.name);
        informationDic.put("lastName", this.lastName);
        informationDic.put("sex", this.sex);
        informationDic.put("id", this.id);
        informationDic.put("type", String.valueOf(this.type));
        informationDic.put("record", this.record);
        informationDic.put("field", this.field);

        return informationDic;
    }


}

class Nurse extends User {

    String record;

    Nurse(String username, String password, String record, String name, String lastName, String sex, String id){
        super(username, password, UserType.Nurse, name, lastName, sex, id);

        this.record = record;
    }

    @Override
    void Menu() {

    }

    @Override
    String[] getUserInformationArray() {
        return new String[]{this.name, this.lastName, this.sex, this.id, String.valueOf(this.type), this.record};
    }

    @Override
    Hashtable<String, String> getUserInformationDic() {
        Hashtable<String, String> informationDic = new Hashtable<String, String>();

        informationDic.put("name", this.name);
        informationDic.put("lastName", this.lastName);
        informationDic.put("sex", this.sex);
        informationDic.put("id", this.id);
        informationDic.put("type", String.valueOf(this.type));
        informationDic.put("record", this.record);

        return informationDic;
    }

}

class Patient extends User {

    String age;
    String disease;
    Mode mode;

    Patient(String username, String password, String age, String disease, String mode, String name, String lastName, String sex, String id){
        super(username, password, UserType.Patient, name, lastName, sex, id);

        this.age = age;
        this.disease = disease;
        this.mode = Mode.valueOf(mode);

    }

    @Override
    void Menu() {

    }

    @Override
    String[] getUserInformationArray() {
        return new String[]{this.name, this.lastName, this.sex, this.id, String.valueOf(this.type), this.age, this.disease, String.valueOf(this.mode)};
    }

    @Override
    Hashtable<String, String> getUserInformationDic() {
        Hashtable<String, String> informationDic = new Hashtable<String, String>();

        informationDic.put("name", this.name);
        informationDic.put("lastName", this.lastName);
        informationDic.put("sex", this.sex);
        informationDic.put("id", this.id);
        informationDic.put("type", String.valueOf(this.type));
        informationDic.put("age", this.age);
        informationDic.put("disease", this.disease);
        informationDic.put("mode", String.valueOf(this.mode));

        return informationDic;
    }

}