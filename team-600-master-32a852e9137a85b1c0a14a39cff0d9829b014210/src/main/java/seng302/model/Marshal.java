package seng302.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import seng302.model.person.*;
import seng302.model.person.Clinician;
import javafx.collections.ObservableList;

import static seng302.model.person.DonorReceiver.formatDateTimeToString;


public class Marshal {


    /**
     * A collection of file name strings which document all the user json files that were not imported correctly into the database.
     */
    private Collection<String> failedImports;

    /**
     * A collection of file name strings which document all the user json files that were imported correctly into the database.
     */
    private Collection<String> successfulImports;

    /**
     * A collection of file name strings which document all the user json files that were not exported correctly to file.
     */
    private Collection<String> failedExports;

    /**
     * A collection of file name strings which document all the user json files that were exported correctly to file.
     */
    private Collection<String> successfulExports;

    private static ArrayList<String> marshalLog = new ArrayList<String>();


    /**
     * Basic constructor for the Marshal class to instantiate import/export lists to document successful/failed import/exporting.
     */
    public Marshal () {

        failedImports = new ArrayList<>();

        failedExports = new ArrayList<>();

        successfulImports = new ArrayList<>();

        successfulExports = new ArrayList<>();


    }

    public Collection<String> getFailedImports() {
        return failedImports;
    }

    public Collection<String> getSuccessfulImports() {
        return successfulImports;
    }

    public Collection<String> getFailedExports() {
        return failedExports;
    }

    public Collection<String> getSuccessfulExports() {
        return successfulExports;
    }

    public void setFailedExports(Collection<String> failedExports) {
        this.failedExports = failedExports;
    }

    public void setFailedImports(Collection<String> failedImports) {
        this.failedImports = failedImports;
    }

    public void setSuccessfulExports(Collection<String> successfulExports) {
        this.successfulExports = successfulExports;
    }

    public void setSuccessfulImports(Collection<String> successfulImports) {
        this.successfulImports = successfulImports;
    }


    /**
     * Empties the marshall export and import array lists.
     */
    public void clearImportAndExportLists() {
        failedImports.clear();
        failedExports.clear();
        successfulExports.clear();
        successfulImports.clear();
    }


    /**
     * Takes the name of a file as a String and determines if a JSON
     * file with that name exists in the base directory.
     * @param fileName The name of the file.
     * @return True if the file exists, false otherwise.
     */
    public static boolean fileExists(String fileName) {

        File file = new File(fileName + ".json");
        return file.isFile();

    }





    /**
     * Creates the directory given by the string dir if it does not already exist.
     * @param dir A string of the directory to be made. Will be on of "administrator', 'clinicians', 'donorReceivers', or 'systemLog'.
     */
    private static void createDirectory(String dir) {

        File directory = new File(dir);

        // Create directory if no file exists with the name, or if it exists and is not a directory.
        if (!directory.exists() || (directory.exists() && !directory.isDirectory())) {

            directory.mkdir();

        }

    }


    /**
     * Transforms an ArrayList of donorReceivers into a series of JSON files, each
     * one corresponding to an individual acount.
     * @param donorReceivers The ArrayList to be exported.
     * @throws IOException Occurs when Jackson or Java IO fails.
     */
    public static void exportAccounts(ArrayList<DonorReceiver> donorReceivers) throws IOException {

        ObjectMapper mapper = createMapper();
        String exportPath = "donorReceivers" + File.separator;
        createDirectory("donorReceivers"); // If it does not already exist.

        // For each account, write to an independent file in the donorReceivers directory.
        for (int index = 0; index < donorReceivers.size(); index++) {

            DonorReceiver donorReceiver = donorReceivers.get(index);
            File file = new File(exportPath + donorReceiver.getUserName() + ".json");
            mapper.writeValue(file, donorReceiver);


            // Log export.
            String time = formatDateTimeToString(LocalDateTime.now());
            marshalLog.add("Exported donorReceiver '" + donorReceiver.getUserName() + "' to " +
                    "file. Change made at: " + time);

        }

    }


    /**
     * Imports all files located within the accounts directory.
     * @return The accounts stored in an ArrayList format.
     * @throws IOException When the account files could not be imported.
     */
    public static ArrayList<DonorReceiver> importAccounts()
            throws IOException {

        // Retrieve list of files in donorReceivers directory.
        File directory = new File("donorReceivers");
        File[] accountFiles = directory.listFiles();

        // For each account file, import the data to donorReceivers.
        ObjectMapper mapper = createMapper();
        ArrayList<DonorReceiver> donorReceivers = new ArrayList<DonorReceiver>();

        // Handle case where no files have been exported before.
        if (accountFiles != null) {

            for (int index = 0; index < accountFiles.length; index++) {

                String fileName = "";

                try {

                    File accountFile = accountFiles[index];
                    fileName = accountFile.getName();
                    DonorReceiver importedDonorReceiver = mapper.readValue(accountFile, DonorReceiver.class);
                    importedDonorReceiver.populateIllnessLists();
                    donorReceivers.add(importedDonorReceiver);

                    // Log import.
                    String time = formatDateTimeToString(LocalDateTime.now());
                    marshalLog.add("Imported donorReceiver '" + importedDonorReceiver.getUserName() + "' from " +
                            "file. Change made at: " + time);

                } catch (IOException exception) {
                    System.out.println(exception.getMessage());
                    System.out.println("ERROR: Could not import '" + fileName + "'. It may not be a valid account save file.");
                }
            }
        }
        return donorReceivers;

    }


    /**
     * Saves the system log to file.
     * @param systemLog The ArrayList of log strings to be exported.
     * @throws IOException Occurs when Jackson or Java IO fails.
     */
    public static void exportSystemLog(ObservableList systemLog) throws IOException {

        ObjectMapper mapper = createMapper();
        String exportPath = "systemLog" + File.separator;
        createDirectory("systemLog"); // If it does not already exist.
        File file = new File(exportPath + "systemLog.json");
        mapper.writeValue(file, systemLog);
        // Log export.
        String time = formatDateTimeToString(LocalDateTime.now());
        marshalLog.add("Exported system log to file. Change made at: " + time);
    }


    /**
     * Imports the System Log from the systemlog directory.
     * @return An Array list of log strings.
     * @throws IOException When the system log could not be imported.
     */
    public static ArrayList<LogEntry> importSystemLog()
            throws IOException {

        // Retrieve the system log file in systemLog directory.
        File file = new File("systemLog/systemLog.json");

        // Import the System log.
        ObjectMapper mapper = createMapper();
        ArrayList<LogEntry> importedSystemLog = new ArrayList<>();
        // Handle case where no file have been exported before.
        if (file != null) {
                try {
                    JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, LogEntry.class);
                    importedSystemLog = mapper.readValue(file, type);
                    // Log import.
                    String time = formatDateTimeToString(LocalDateTime.now());
                    System.out.println("System log successfully imported.");
                    marshalLog.add("Imported DonorReceiver Manager system log from file. Change made at: " + time);

                } catch (IOException exception) {
                    System.out.println(exception.getMessage());
                    System.out.println("ERROR: Could not import " + "systemLog/systemLog.json" + ". It may not be a valid DonorReceiver Manager save file.");
                }
            }
        return importedSystemLog;
    }



    /*
        // Retrieve the system log file in systemLog directory.
        File directory = new File("systemlog");
        File[] systemLogList = directory.listFiles();

        // For each account file, import the data to accounts.
        ObjectMapper mapper = createMapper();
        ArrayList<AccountManager> accountManagers = new ArrayList<AccountManager>();

        // Handle case where no files have been exported before.
        if (systemLogList != null) {

            for (int index = 0; index < systemLogList.length; index++) {

                String fileName = "";

                try {

                    File accountManagerFile = systemLogList[index];
                    fileName = accountManagerFile.getName();
                    AccountManager importedAccountManager = mapper.readValue(accountManagerFile, AccountManager.class);
                    accountManagers.add(importedAccountManager);

                    // Log import.
                    String time = formatDateTimeToString(LocalDateTime.now());
                    marshalLog.add("Imported DonorReceiver Manager system log from " +
                            "file. Change made at: " + time);

                } catch (IOException exception) {
                    System.out.println(exception.getMessage());

                    System.out.println("ERROR: Could not import '" + fileName + "'. It may not be a valid Account Manager save file.");
                }
            }
        }
        return accountManagers.get(0);
    }
    */

    /**
     * Creates an ObjectMapper for converting between JSON and Java objects,
     * @return The new ObjectMapper instance.
     */
    private static ObjectMapper createMapper() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return mapper;

    }



    /**
     * Imports all files located within the clinicians directory.
     * @return The clinicians stored in an ArrayList format.
     * @throws IOException When the clinician files could not be imported.
     */
    public static ArrayList<Clinician> importClinicians()
            throws IOException {

        // Retrieve list of files in clinicians directory.
        File directory = new File("clinicians");
        File[] clinicianFiles = directory.listFiles();

        // For each clinician file, import the data to clinicians.
        ObjectMapper mapper = createMapper();
        ArrayList<Clinician> clinicians = new ArrayList<Clinician>();

        // Handle case where no files have been exported before.
        if (clinicianFiles != null) {

            for (int index = 0; index < clinicianFiles.length; index++) {

                String fileName = "";

                try {

                    File clinicianFile = clinicianFiles[index];
                    fileName = clinicianFile.getName();
                    Clinician importedClinician = mapper.readValue(clinicianFile, Clinician.class);
                    clinicians.add(importedClinician);

                    // Log import.
                    String time = formatDateTimeToString(LocalDateTime.now());
                    marshalLog.add("Imported clinician '" + importedClinician.getUserName() + "' from " +
                            "file. Change made at: " + time);

                } catch (IOException exception) {

                    System.out.println("ERROR: Could not import '" + fileName + "'. It may not be a valid clinician save file.");

                }
            }
        }
        return clinicians;
    }


    /**
     * Transforms an ArrayList of clinicians into a series of JSON files, each
     * one corresponding to an individual clinician.
     * @param clinicians The ArrayList to be exported.
     * @throws IOException Occurs when Jackson or Java IO fails.
     */
    public static void exportClinicians(ArrayList<Clinician> clinicians) throws IOException {

        ObjectMapper mapper = createMapper();
        String exportPath = "clinicians" + File.separator;
        createDirectory("clinicians"); // If it does not already exist.

        // For each clinician, write to an independent file in the clinicians directory.
        for (int index = 0; index < clinicians.size(); index++) {

            Clinician clinician = clinicians.get(index);
            File file = new File(exportPath + clinician.getUserName() + ".json");
            mapper.writeValue(file, clinician);


            // Log export.
            String time = formatDateTimeToString(LocalDateTime.now());
            marshalLog.add("Exported clinician '" + clinician.getUserName() + "' to " +
                    "file. Change made at: " + time);
        }
    }


    /**
     * Transforms an ArrayList of administrators into a series of JSON files, each
     * one corresponding to an individual admin.
     * @param admins The ArrayList to be exported.
     * @throws IOException Occurs when Jackson or Java IO fails.
     */
    public static void exportAdministrators(ArrayList<Administrator> admins) throws IOException {

        ObjectMapper mapper = createMapper();
        String exportPath = "administrators" + File.separator;
        createDirectory("administrators"); // If it does not already exist.

        // For each admin, write to an independent file in the administrators directory.
        for (int index = 0; index < admins.size(); index++) {

            Administrator admin = admins.get(index);
            File file = new File(exportPath + admin.getUserName() + ".json");
            mapper.writeValue(file, admin);

            // Log export.
            String time = formatDateTimeToString(LocalDateTime.now());
            marshalLog.add("Exported admin '" + admin.getUserName() + "' to " +
                    "file. Change made at: " + time);
        }
    }


    /**
     * Imports all files located within the admins directory.
     * @return The admins stored in an ArrayList format.
     * @throws IOException When the administrator files could not be imported.
     */
    public static ArrayList<Administrator> importAdministrators()
            throws IOException {

        // Retrieve list of files in clinicians directory.
        File directory = new File("administrators");
        File[] adminFiles = directory.listFiles();

        // For each clinician file, import the data to clinicians.
        ObjectMapper mapper = createMapper();
        ArrayList<Administrator> admins = new ArrayList<Administrator>();

        // Handle case where no files have been exported before.
        if (adminFiles != null) {

            for (int index = 0; index < adminFiles.length; index++) {

                String fileName = "";

                try {

                    File adminFile = adminFiles[index];
                    fileName = adminFile.getName();
                    Administrator importedAdmin = mapper.readValue(adminFile, Administrator.class);
                    admins.add(importedAdmin);

                    // Log import.
                    String time = formatDateTimeToString(LocalDateTime.now());
                    marshalLog.add("Imported admin '" + importedAdmin.getUserName() + "' from " +
                            "file. Change made at: " + time);

                } catch (IOException exception) {

                    System.out.println("ERROR: Could not import '" + fileName + "'. It may not be a valid admin save file.");

                }
            }
        }
        return admins;
    }


    public ArrayList<User> ImportSelectedUsersFromFile(File directory, String type) {

        // Retrieve list of files in the given directory type.
        File[] userFiles = directory.listFiles();

        // For each .json file, import the data into the App.
        ObjectMapper mapper = createMapper();
        ArrayList<User> users = new ArrayList<>();

        // Handle case where no files have been exported before.
        if (userFiles != null) {

            for (int index = 0; index < userFiles.length; index++) {
                String fileName = "";
                try {
                    File userFile = userFiles[index];
                    fileName = userFile.getName();
                    if (type.equals("donorReceivers")) {
                        DonorReceiver importedDonor = mapper.readValue(userFile, DonorReceiver.class);
                        users.add(importedDonor);
                    } else if (type.equals("clinicians")) {
                        Clinician importedClinician = mapper.readValue(userFile, Clinician.class);
                        users.add(importedClinician);
                    } else {
                        Administrator importedAdmin = mapper.readValue(userFile, Administrator.class);
                        users.add(importedAdmin);
                    }

                    // Log import.
                    String time = formatDateTimeToString(LocalDateTime.now());
                    marshalLog.add("Imported user '" +  users.get(users.size() -1).getUserName()  + "' from " +
                            "file. Change made at: " + time);

                    successfulImports.add(fileName);



                } catch (IOException exception) {
                    //TODO handle user selecting wrong file types
                    System.out.println("ERROR: Could not import '" + fileName + "'. It may not be a valid user save file.");
                    failedImports.add(fileName);
                }
            }
        }
        return users;
    }

}
