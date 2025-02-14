package MainPackage;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.*;

class loginReturn {

    User user;
    boolean userFound;
    boolean wrongPassword;
    String username;

    loginReturn(User user, boolean userFound, boolean wrongPassword, String username) {
        this.user = user;
        this.userFound = userFound;
        this.wrongPassword = wrongPassword;
        this.username = username;
    }

}

public class Main {

    static sessionData sessionData;

    static Scanner scanner = new Scanner(System.in);

    static Admin adminUser;

    static {
        try {
            adminUser = new Admin("admin", "admin");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static File fileGenerator(String fileName) {

        try {

            File myFileObj = new File(fileName);
            myFileObj.createNewFile();

            return myFileObj;

        } catch (IOException e) {


            JOptionPane.showMessageDialog(null, "An error occurred.", "Error", JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();

            return null;
        }

    }

    static String convertUserToFileDottedData(User user){

        String finalData = "";
        String[] allData = user.getUserInformationArray();

        for (String data : allData) {
            finalData = finalData.concat(data+".");
        }

        return finalData;
    }

    static User convertFileDottedDataToUser(String username, String dottedData) throws IOException, ParseException {

        String[] allData;
        allData = dottedData.split("\\.", -2);
        String name = allData[0];
        String lastName = allData[1];
        String sex = allData[2];
        String id = allData[3];
        String type = allData[4];

        if (type.equals("Physician")) {
            String record = allData[5];
            String field = allData[6];
            return new Physician(username, "", name, lastName, sex, id, field, record);
        } else if (type.equals("Nurse")) {
            String record = allData[5];
            return new Nurse(username, "", record, name, lastName, sex, id);

        } else if (type.equals("Patient")) {

            String age = allData[5];
            String disease = allData[6];
            String mode = allData[7];

            return new Patient(username, "", age, disease, mode, name, lastName, sex, id);
        }

        return new Admin();

    }

    static ArrayList<User> getUsersFromFile() throws IOException, ParseException {

        File fileObj = Main.fileGenerator("usersInformation.properties");

        ArrayList<User> allUsers = new ArrayList<User>();

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));


            for (String key : properties.stringPropertyNames()) {


                allUsers.add(convertFileDottedDataToUser(key, properties.get(key).toString()));

            }

        }


        return allUsers;

    }

    public static Hashtable<String, String[]> getUserCredentials() throws IOException {

        Hashtable<String, String> userCredentialsDic = new Hashtable<String, String>();

        File fileObj = fileGenerator("userCredentials.properties");

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));


            for (String key : properties.stringPropertyNames()) {
                userCredentialsDic.put(key, properties.get(key).toString());
            }

        }

        Hashtable<String, String[]> userCredentialsDicSplit = new Hashtable<String, String[]>();

        for (String username : userCredentialsDic.keySet()) {
            userCredentialsDicSplit.put(username, userCredentialsDic.get(username).split(" ", -1));
        }

        return userCredentialsDicSplit;

    }


    public static void twoFilesGenerator() {

        try {
            // File initialization segment
            File crFileObj = new File("userCredentials.properties");
            File infFileObj = new File("usersInformation.properties");

            // Check files segment
            if (crFileObj.length() == 0){
                FileWriter writer = new FileWriter("userCredentials.properties");
                writer.write("admin=admin Admin");
                writer.close();
            }
            if (infFileObj.length() == 0){
                FileWriter writer = new FileWriter("usersInformation.properties");
                writer.write("admin=admin.admin.admin.000.Admin.");
                writer.close();
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "An error occurred.", "Error", JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        }

    }

    public static loginReturn login() throws IOException, ParseException {

        twoFilesGenerator();

        Hashtable<String, String[]> userCredentialsDicSplit = getUserCredentials();

        JTextField Username = new JTextField();
        JTextField Password = new JPasswordField();
        Object[] message = {
                "Username: ", Username,
                "Password: ", Password
        };

        String username;
        String password;
        ImageIcon loginIcon = new ImageIcon("loginIcon.png");

        while (true) {
            int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, loginIcon);
            if (option == JOptionPane.OK_OPTION) {
                username = Username.getText();
                password = Password.getText();
                break;
            } else {
                int exit = JOptionPane.showConfirmDialog(null, "Do you want to exit?", "Exit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (exit == 0) {
                    System.exit(0);
                }
            }

        }

        User user = null;
        boolean userFound = false;
        boolean wrongPassword = false;


            for (String checkUsername : userCredentialsDicSplit.keySet()) {
                if (checkUsername.equals(username)) {
                    userFound = true;
                    String userPassword = userCredentialsDicSplit.get(checkUsername)[0];

                    if (userPassword.equals(password)) {

                        if (username.equals("admin")) {
                            user = new Admin(username, password);
                        } else {
                            user = new Admin("admin", "").getUserObj(username);
                        }
                        break;

                    } else wrongPassword = true;

                }
            }
//        }

        return new loginReturn(user, userFound, wrongPassword, username);

    }

    static void goBack() {
        System.out.print("<-Press Enter to go back->");
        Main.scanner.nextLine();
    }

    static String getPassword() {

        String password = " ";

        while (password.contains(" ") || Admin.checkPassword(password)) {
            password = JOptionPane.showInputDialog(null, "Please enter Password: ", "Password of new user", JOptionPane.PLAIN_MESSAGE);
            if (password == null)
                return password;
            if (password.contains(" ") || Admin.checkPassword(password)){
                String passwordError = "password shouldn't include whitespaces and must contains\n" +
                        "at least one of these characters!: @#$%&*";
                JOptionPane.showMessageDialog(null, passwordError, "Invalid Password", JOptionPane.ERROR_MESSAGE);
            }

        }

        return password;
    }

    public static void main(String[] args) throws IOException, ParseException {

        try {
            Main.sessionData = MainPackage.sessionData.getSessionData();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


        Hashtable<String, ArrayList<Object>> triedUsers = new Hashtable<String, ArrayList<Object>>();

        User newUser;

        while (true) {

            Main.sessionData.readFiles();

            loginReturn loginReturn = login();

            if (loginReturn.username.isEmpty())
                break;

            if (!loginReturn.userFound) {
                JOptionPane.showMessageDialog(null, "No user found with this username!", "Invalid Username", JOptionPane.WARNING_MESSAGE);
            } else if (loginReturn.wrongPassword || (triedUsers.containsKey(loginReturn.username) && (Integer) triedUsers.get(loginReturn.username).get(0) >= 3)) {

                if (triedUsers.containsKey(loginReturn.username)) {

                    if (loginReturn.wrongPassword)
                        JOptionPane.showMessageDialog(null, "Wrong password!", "Invalid Password", JOptionPane.WARNING_MESSAGE);

                    triedUsers.get(loginReturn.username).set(0, (Integer) triedUsers.get(loginReturn.username).get(0) + 1);

                    if ((Integer) triedUsers.get(loginReturn.username).get(0) == 3) {

                        triedUsers.get(loginReturn.username).set(1, System.currentTimeMillis());

                        JOptionPane.showMessageDialog(null, "Your account has been locked for 2 minutes.", "Locked Account", JOptionPane.WARNING_MESSAGE);

                    } else if ((Integer) triedUsers.get(loginReturn.username).get(0) > 3) {

                        long currentTime = System.currentTimeMillis();

                        long timeDifference = (currentTime - (Long) triedUsers.get(loginReturn.username).get(1));

                        if ( timeDifference / 1000 >= 120) {
                            triedUsers.remove(loginReturn.username);
                        } else {
                            String messageLocked = "Please try " + (120 - (timeDifference / 1000)) + " second later.";
                            JOptionPane.showMessageDialog(null, messageLocked, "Locked Account", JOptionPane.WARNING_MESSAGE);
                        }
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Wrong password!", "Invalid Password", JOptionPane.WARNING_MESSAGE);
                    ArrayList<Object> list = new ArrayList<Object>();
                    list.add(1);
                    list.add(System.currentTimeMillis());
                    triedUsers.put(loginReturn.username, list);
                }
            } else {
                triedUsers.clear();
                Main.sessionData = MainPackage.sessionData.getSessionData();
                newUser = loginReturn.user;
                newUser.Menu();
            }

        }

    }


}
