package MainPackage;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Scanner;


class loginReturn {

    User user = null;
    boolean userFound;
    boolean wrongPassword;
    String username = null;

    loginReturn(User user, boolean userFound, boolean wrongPassword, String username) {
        this.user = user;
        this.userFound = userFound;
        this.wrongPassword = wrongPassword;
        this.username = username;
    }

}

public class Main {


    static Scanner scanner = new Scanner(System.in);

    public static File fileGenerator(String fileName) {

        try {

            File myFileObj = new File(fileName);
            myFileObj.createNewFile();

            return myFileObj;

        } catch (IOException e) {

            System.out.println("An error occurred.");
            e.printStackTrace();

            return null;
        }

    }

//    static void saveUsersInformationToFile(String username, User user) throws IOException {
//
//        File fileObj = Main.fileGenerator("usersInformation.properties");
//
//        Hashtable<String, String> allInformationDicArray = new Hashtable<String, String>();
//        Hashtable<String, Hashtable<String, String>> allInformationDic = getUsersInformationFromFile();
//
//        for (String key: allInformationDic.keySet()) {
//
//            if (!key.equals("admin"))
//                allInformationDicArray.put(key, convertDicToFileDottedData(key));
//            else allInformationDicArray.put("admin", "admin.admin.admin.111.Admin.");
//
//        }
//        String[] userInformationArray = user.getUserInformationArray();
//        String allData = "";
//
//        for (String data: userInformationArray) {
//            allData = allData.concat(data+".");
//        }
//        allInformationDicArray.put(username, allData);
//        if (fileObj != null) {
//
//
//            Properties properties = new Properties();
//
//            properties.putAll(allInformationDicArray);
//
//            properties.store(new FileOutputStream("usersInformation.properties"), null);
//
//
//        }
//
//    }

    static String convertUserToFileDottedData(User user) throws IOException {

//        Hashtable<String, String> userDic = user.getUserInformationDic();
        String finalData = "";
        String[] allData = user.getUserInformationArray();

        for (String data : allData) {
            finalData = finalData.concat(data+".");
        }

        return finalData;
    }

    static User convertFileDottedDataToUser(String username, String dottedData) throws IOException {

        User user = null;
        String[] allData;
        allData = dottedData.split("\\.", -2);
//        Hashtable<String, String> userDic = new Hashtable<String, String>();
        String name = allData[0];
        String lastName = allData[1];
        String sex = allData[2];
        String id = allData[3];
        String type = allData[4];

//        userDic.put("name", allData[0]);
//        userDic.put("lastName", allData[1]);
//        userDic.put("sex", allData[2]);
//        userDic.put("id", allData[3]);
//        userDic.put("type", allData[4]);

        if (type.equals("Physician")) {
            String record = allData[5];
            String field = allData[6];
            return new Physician(username, "", name, lastName, sex, id, field, record);
//            userDic.put("record", allData[5]);
//            userDic.put("field", allData[6]);
        } else if (type.equals("Nurse")) {
            String record = allData[5];
//            userDic.put("record", allData[5]);
            return new Nurse(username, "", record, name, lastName, sex, id);

        } else if (type.equals("Patient")) {
//            userDic.put("age", allData[5]);
//            userDic.put("disease", allData[6]);
//            userDic.put("mode", allData[7]);

            String age = allData[5];
            String disease = allData[6];
            String mode = allData[7];

            return new Patient(username, "", age, disease, mode, name, lastName, sex, id);
        }

        return new Admin();

    }

    static ArrayList<User> getUsersFromFile() throws IOException {

        File fileObj = Main.fileGenerator("usersInformation.properties");

        ArrayList<User> allUsers = new ArrayList<User>();

//        Hashtable<String, Hashtable<String, String>> allInformationDic = new Hashtable<String, Hashtable<String, String>>();

//        boolean isThereAdmin = false;

        if (fileObj != null) {


            Properties properties = new Properties();
            properties.load(new FileInputStream(fileObj));


            for (String key : properties.stringPropertyNames()) {

//                if (key.equals("admin")) isThereAdmin = true;

                allUsers.add(convertFileDottedDataToUser(key, properties.get(key).toString()));

            }

        }

//        if (!isThereAdmin) {
////            Hashtable<String, String> adminDic = new Hashtable<String, String>();
//
//            allUsers.add(new Admin("admin", ""));
//
////            adminDic.put("name", "admin");
////            adminDic.put("lastName", "admin");
////            adminDic.put("sex", "admin");
////            adminDic.put("id", "111");
////            adminDic.put("type", "Admin");
////
////            allInformationDic.put("admin", adminDic);
//        }


        return allUsers;

    }
//    static Hashtable<String, Hashtable<String, String>> getUsersInformationFromFile() throws IOException {
//
//        File fileObj = Main.fileGenerator("usersInformation.properties");
//
//        Hashtable<String, Hashtable<String, String>> allInformationDic = new Hashtable<String, Hashtable<String, String>>();
//
//        boolean isThereAdmin = false;
//
//        if (fileObj != null) {
//
//
//            Properties properties = new Properties();
//            properties.load(new FileInputStream(fileObj));
//
//
//            for (String key : properties.stringPropertyNames()) {
//
//                if (key.equals("admin")) isThereAdmin = true;
//
//                allInformationDic.put(key, convertFileDottedDataToDic(properties.get(key).toString()));
//
//            }
//
//        }
//
//        if (!isThereAdmin) {
//            Hashtable<String, String> adminDic = new Hashtable<String, String>();
//
//            adminDic.put("name", "admin");
//            adminDic.put("lastName", "admin");
//            adminDic.put("sex", "admin");
//            adminDic.put("id", "111");
//            adminDic.put("type", "Admin");
//
//            allInformationDic.put("admin", adminDic);
//        }
//
//
//        return allInformationDic;
//    }

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

//        userCredentialsDicSplit.put("admin", new String[]{"admin", "Admin"});

        return userCredentialsDicSplit;

    }


//    public static void saveUserCredentials(Hashtable<String, String[]> allData) throws IOException {
//
//
//        File fileObj = fileGenerator("userCredentials.properties");
//
//        Hashtable<String, String> savableDic = new Hashtable<String, String>();
//
//        if (fileObj != null) {
//
//
//            Properties properties = new Properties();
//            properties.load(new FileInputStream(fileObj));
//
//            boolean isThereAdmin = false;
//
//
//            for (String key : allData.keySet()) {
//
//                if (key.equals("admin")) isThereAdmin = true;
//
//                String passType = "";
//                passType = passType.concat(allData.get(key)[0] + " " + allData.get(key)[1]);
//                savableDic.put(key, passType);
//            }
//
//            if (!isThereAdmin) savableDic.put("admin", "admin Admin");
//
//            properties.putAll(savableDic);
//
//            properties.store(new FileOutputStream("userCredentials.properties"), null);
//
//
//        }
//
//    }

    public static void twoFilesGenerator() {

        try {
            File crFileObj = new File("userCredentials.properties");


            if (!crFileObj.exists()){
                crFileObj.createNewFile();
                FileWriter writer = new FileWriter("userCredentials.properties");
                writer.write("admin=admin Admin");
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            File infFileObj = new File("usersInformation.properties");


            if (!infFileObj.exists()){
                infFileObj.createNewFile();
                FileWriter writer = new FileWriter("usersInformation.properties");
                writer.write("admin=admin.admin.admin.111.Admin.");
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }





    }


    public static loginReturn login() throws IOException {

        twoFilesGenerator();

        Hashtable<String, String[]> userCredentialsDicSplit = getUserCredentials();


        System.out.println("Welcome :)\n" +
                            "Please Enter Username: ");

        String username = scanner.nextLine();

        System.out.println("Please enter password: ");

        String password = scanner.nextLine();

        User user = null;
        boolean userFound = false;
        boolean wrongPassword = false;


        if (username.equals("admin")) {

            userFound = true;

            if (password.equals("admin")) {
                user = new Admin(username, password);
            } else wrongPassword = true;

        }

        else {
            for (String checkUsername : userCredentialsDicSplit.keySet()) {
                if (checkUsername.equals(username)) {
                    userFound = true;
                    String userPassword = userCredentialsDicSplit.get(checkUsername)[0];

                    String userType = userCredentialsDicSplit.get(checkUsername)[1];

                    if (userPassword.equals(password)) {

//                        user = User.getUserObjFromFile(username, UserType.valueOf(userType));
                        user = new Admin("admin", "").getUserObj(username);

                    } else wrongPassword = true;

                }
            }
        }

        return new loginReturn(user, userFound, wrongPassword, username);

    }

    public static void main(String[] args) throws IOException {

        Hashtable<String, ArrayList<Object>> triedUsers = new Hashtable<String, ArrayList<Object>>();

        User newUser = null;

        while (true) {

            loginReturn loginReturn = login();

            if (!loginReturn.userFound) {
                System.out.println("No user found with this username!");
            } else if (loginReturn.wrongPassword) {
                if (triedUsers.containsKey(loginReturn.username)) {
                    if ((Integer) triedUsers.get(loginReturn.username).get(0) == 3) {
                        long currentTime = System.currentTimeMillis();
                        if ( ((Long) triedUsers.get(loginReturn.username).get(1) - currentTime) / 1000 >= 120) {
                            triedUsers.remove(loginReturn.username);
                        } else {
                            System.out.println("Please try again later.");
                        }
                    }
                } else {
                    ArrayList<Object> list = new ArrayList<Object>();
                    list.add(1);
                    list.add(System.currentTimeMillis());
                    triedUsers.put(loginReturn.username, list);
                }
            } else {
                newUser = loginReturn.user;
                break;
            }

        }

        if (newUser.type == UserType.Admin){
            Admin user = (Admin) newUser;
            user.Menu();
        }

//            wrongPassInt++;
//        if (triedUsers.containsKey(loginReturn.username)) {
//
//        }
//        if (loginReturn.userFound) {
//
//        } else if (loginReturn.wrongPassword)
//            wrongPassInt++;
//
//        if (lockedUsers.containsKey(loginReturn.username)) {
//            long currentTime = System.currentTimeMillis();
//
//            if ( (lockedUsers.get(loginReturn.username) - currentTime) / 1000 >= 120) {
//                lockedUsers.remove(loginReturn.username);
//            }
//
//        }
//        if (wrongPassInt == 3) {
//            long startTime = System.currentTimeMillis();
//            lockedUsers.put(loginReturn.username, startTime);
//// wait for activity here
//            long endTime = System.currentTimeMillis();
//            long seconds = (endTime - startTime) / 1000;
//
//        }

//        Hashtable<String, String> userCredentialsDic = new Hashtable<String, String>();
//
//        userCredentialsDic.put("ali", "ali Nurse");
//        userCredentialsDic.put("admin", "admin Admin");
//        userCredentialsDic.put("amir", "amir Patient");
//
//        File fileObj = fileGenerator("userCredentials.properties");
//
//        if (fileObj != null) {
//
//
//            Properties properties = new Properties();
//            properties.putAll(userCredentialsDic);
//
//            properties.store(new FileOutputStream("userCredentials.properties"), null);
//
//        }
    }


}
