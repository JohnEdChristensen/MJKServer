
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import java.sql.*;

public class Main {

    private static final int portNumber = 60101;
    private static int currentVersionCode = 9;
    private static Vector<Pipe>inventory = new Vector<>(1,1);
    private static Vector<User>userList = new Vector<>(1,1);
    private static ObjectInputStream inputStream;
    private static ObjectOutputStream outputStream;
    private static ServerSocket serverSocket;
    private static Socket socket;
    public static String dataBaseLocation = "jdbc:ucanaccess://T:\\PROJECT MANAGEMENT-Aaron\\CB&I\\Spool Data\\CB&I Pipe Spool Data.accdb";
    public static String userFileLocation = "T:\\Android App/UserList.csv";
    public static String backupDataLocation = "C://Users/MJK Server/Desktop/SERVER/Backup/";
    public static String jpgFileLocation = "T:\\Android App\\Drawings\\JPG";



    public static void main(String[] args) {

        String command;
        String outputMessage = "Failed to Initiate";
        boolean serverEnd = false;
        //format files
        final File folder = new File(jpgFileLocation);
        formatImages(jpgFileLocation,folder);
        //pipe create
        System.out.println("Reading Database...");
        getPipeInventory();
        System.out.println("Done Reading Database");
        getUserList();
        writePipeInventory();

        try {
            //starting server
            System.out.println("Server starting at port number: " + portNumber);
            serverSocket = new ServerSocket(portNumber);
            do {
                //client connecting
                System.out.println("Waiting for clients to connect");
                socket = serverSocket.accept();
                System.out.println("Client has connected.");

                //Receive message from client
                inputStream = new ObjectInputStream(socket.getInputStream());
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                command = (String) inputStream.readObject();
                System.out.println("Command from the client: " + command);
                //determine command/process
                switch (command){
                    case "getPipeData":
                        outputMessage = getPipeData();
                        break;
                    case "validateCredentials":
                        outputMessage = validateCredentials();
                        break;
                    case "updateLocation":
                        outputMessage = updateLocation();
                        break;
                    case "sendImage":
                        sendImage();
                        continue;
                    case "sendReport":
                        outputMessage = OSNDReport();
                        break;
                    case "SearchQuery":
                        outputMessage = pipeQuery();
                        break;
                    case "Test":
                        outputMessage = checkVersionCode();
                        break;
                    case "END SERVER":
                        serverEnd = true;
                        break;
                    default:
                        outputMessage = "Invalid Command";
                }

                if(!serverEnd) {
                    //Send message to the client

                    outputStream.writeObject(outputMessage);
                    outputStream.flush();
                    System.out.println("Sent client a message: " + outputMessage);
                }
                System.out.println("Connection has ended");
            }while(!serverEnd);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server has ended");
    }



    private static String checkVersionCode(){
        try {
            int userVersionCode = inputStream.readInt();
            if(userVersionCode >= currentVersionCode){
                return "Up To Date";
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return "Not Up To Date";

    }
    private static String getPipeData() {
        try {
            long pipeID = inputStream.readLong();
            int pipeIndex = findPipe(pipeID, inventory);
            if(pipeIndex == -1){
                return "Pipe Not Found";
            }
            return inventory.get(pipeIndex).pipeToString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Server Error";
    }

    private static String validateCredentials() {
        //get username and password
        // look for username
        String username ="";
        String password ="";
        try {
            username = (String) inputStream.readObject();
            password = (String) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        for(int i = 0;i<userList.size();i++){
            String userIUsername = userList.get(i).getUserName();
            String userIPassword = userList.get(i).getPassword();
            if(username.equals(userIUsername)&&password.equals(userIPassword)){
                return userList.get(i).getAccess();
            }
        }
        return "false";
    }
    private static String updateLocation(){
        try {
            long data;
            data = inputStream.readLong();
            long pipeID = data;
            int pipeIndex = findPipe(pipeID, inventory);
            if(pipeIndex == -1){
                return "Pipe Not Found";
            }
            Pipe updatePipe = inventory.get(pipeIndex);
            String location = (String) inputStream.readObject();
            String user = (String) inputStream.readObject();
            switch (location){
                case "Unload":
                    if(updatePipe.getDateUnloaded().equals("null"))
                        updatePipe.setDateUnloaded(new SimpleDateFormat("MM/dd/yyyy\nHH:mm:ss").format(Calendar.getInstance().getTime()), System.currentTimeMillis(),user);
                    else return "Pipe has already been scanned at " + location;
                    break;
                case "Blast":
                    if(updatePipe.getDateBlasted().equals("null"))
                        updatePipe.setDateBlasted(new SimpleDateFormat("MM/dd/yyyy\nHH:mm:ss").format(Calendar.getInstance().getTime()),System.currentTimeMillis(),user);
                    else return "Pipe has already been scanned at " + location;
                    break;
                case "Paint":
                    if(updatePipe.getDatePainted().equals("null"))
                        updatePipe.setDatePainted(new SimpleDateFormat("MM/dd/yyyy\nHH:mm:ss").format(Calendar.getInstance().getTime()),System.currentTimeMillis(),user);
                    else return "Pipe has already been scanned at " + location;
                    break;
                case "Touch Up":
                    if(updatePipe.getDateTouchedUp().equals("null"))
                        updatePipe.setDateTouchedUp(new SimpleDateFormat("MM/dd/yyyy\nHH:mm:ss").format(Calendar.getInstance().getTime()),System.currentTimeMillis(),user);
                    else return "Pipe has already been scanned at " + location;
                    break;
                case "Load":
                    String loadListNum = (String) inputStream.readObject();
                    String TrailerNum = (String) inputStream.readObject();
                    if(updatePipe.getDateLoaded().equals("null"))
                        updatePipe.setDateLoaded(new SimpleDateFormat("MM/dd/yyyy\nHH:mm:ss").format(Calendar.getInstance().getTime()),System.currentTimeMillis(),user,loadListNum,TrailerNum);
                    else return "Pipe has already been scanned at " + location;
                    break;
            }
            System.out.println(data + " arrived in " + location + " by " + user);
            return data + " arrived in " + location + " by " + user;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "Server Error";
    }
    private static void sendImage(){


        byte[] bytes;
        BufferedInputStream bis;
        ObjectOutputStream outputStream = null;
        try {
            long pipeID = inputStream.readLong();
            int pipeIndex = findPipe(pipeID, inventory);
            if(pipeIndex == -1){
                throw new IOException();
            }
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            Pipe pipe = inventory.get(pipeIndex);
            File imageFile = pipe.findImage();
            if(!imageFile.exists()){
                outputStream.writeObject("false");
                throw new FileNotFoundException();
            }
            bytes = new byte[(int) imageFile.length()];
            bis = new BufferedInputStream(new FileInputStream(imageFile));
            bis.read(bytes, 0, bytes.length);
            outputStream.writeObject("true");
            outputStream.writeObject(bytes);
            outputStream.flush();
            socket.close();
            System.out.println("Sent Image");
        }catch (FileNotFoundException e){
            System.out.println("Could not find Image File");
        }catch(IOException e) {
            e.printStackTrace();
        }

    }
    private static String OSNDReport(){
        try {
            long pipeID = inputStream.readLong();
            int pipeIndex = findPipe(pipeID, inventory);
            if(pipeIndex == -1){
                throw new IOException();
            }
            Pipe pipe = inventory.get(pipeIndex);
            String OSNDDetail = (String) inputStream.readObject();
            pipe.setOSNDDetail(OSNDDetail);
            return "OS&D Successfully Reported";

        }catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return "OS&D Report Failed";
    }
    private static String pipeQuery(){
        Vector<String>pipeLineList = new Vector<>(0,1);
        try{
            String searchMode = (String) inputStream.readObject();
            String searchText = (String) inputStream.readObject();

            Connection con = DriverManager.getConnection(dataBaseLocation);
            Statement st = con.createStatement();
            String sqlCommand = "Select * from [Spool Data]" +
                    "where '" +searchMode+ "' = '" +searchText+ "'" ;
            ResultSet resultSet = st.executeQuery(sqlCommand);
            while(resultSet.next()) {
               String pipeLine = resultSet.getString("ID") + "," + resultSet.getString("Branch/Plant")
                        + "," + resultSet.getString("Spool No") + "," + resultSet.getString("Order No")
                        + "," + resultSet.getString("Weight") + "," + resultSet.getString("Area")
                        + "," + resultSet.getString("Paint System") + "," + resultSet.getString("Inbound Load #")
                        + "," + resultSet.getString("Internal Blast") + "," + resultSet.getString("Cleaning Method")
                        + "," + resultSet.getString("Date Received") + "," + resultSet.getString("Surface Preparation")
                        + "," + resultSet.getString("OS&D Detail") + "," + resultSet.getString("Load List #")
                        + "," + resultSet.getString("Trailer #") + "," + resultSet.getString("Invoiced")
                        + "," + resultSet.getString("Location")
                        + "," + resultSet.getString("Date Unloaded") + "," + resultSet.getString("Shift Unloaded")
                        + "," + resultSet.getString("User Unloaded") + "," + resultSet.getString("Date Blasted")
                        + "," + resultSet.getString("Shift Blasted") + "," + resultSet.getString("User Blasted")
                        + "," + resultSet.getString("Date Painted") + "," + resultSet.getString("Shift Painted")
                        + "," + resultSet.getString("User Painted") + "," + resultSet.getString("Date Touched Up")
                        + "," + resultSet.getString("Shift Touched Up") + "," + resultSet.getString("User Touched Up")
                        + "," + resultSet.getString("Date Loaded") + "," + resultSet.getString("Shift Loaded") + ","
                        + "," + resultSet.getString("User Loaded");
               pipeLineList.add(pipeLine);
            }
            outputStream.writeInt(pipeLineList.size());
            for (String aPipeLineList : pipeLineList) {
                outputStream.writeObject(aPipeLineList);
            }
            return "search finished";

        }catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            outputStream.writeInt(pipeLineList.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "search failed";

    }








    private static void getUserList() {


        String userLine;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        try{
            fileReader = new FileReader(userFileLocation);
            bufferedReader = new BufferedReader(fileReader);
            while((userLine = bufferedReader.readLine()) != null) {
                userList.add(new User(userLine));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Could not find UserList");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error Reading UserList File");
        }finally {
            try {
                if(bufferedReader!=null) {
                    bufferedReader.close();
                }
                if(fileReader!=null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private static void getPipeInventory() {
        String pipeLine;
        try {
            Connection con = DriverManager.getConnection(dataBaseLocation);
            Statement st = con.createStatement();
            String sqlCommand = "Select * from [Spool Data]";
            ResultSet resultSet = st.executeQuery(sqlCommand);
            while(resultSet.next()){
                pipeLine = resultSet.getString("ID") + ","+ resultSet.getString("Branch/Plant")
                        + "," + resultSet.getString("Spool No") + "," + resultSet.getString("Order No")
                        + "," + resultSet.getString("Weight") + "," + resultSet.getString("Area")
                        + "," + resultSet.getString("Paint System") + "," + resultSet.getString("Inbound Load #")
                        + "," + resultSet.getString("Internal Blast") + "," + resultSet.getString("Cleaning Method")
                        + "," + resultSet.getString("Date Received") + "," + resultSet.getString("Surface Preparation")
                        + "," + resultSet.getString("OS&D Detail") + "," + resultSet.getString("Load List #")
                        + "," + resultSet.getString("Trailer #")+ "," + resultSet.getString("Invoiced")
                        + "," + resultSet.getString("Location")
                        + "," + resultSet.getString("Date Unloaded") + "," + resultSet.getString("Shift Unloaded")
                        + "," + resultSet.getString("User Unloaded") + "," + resultSet.getString("Date Blasted")
                        + "," + resultSet.getString("Shift Blasted") + "," + resultSet.getString("User Blasted")
                        + "," + resultSet.getString("Date Painted") + "," + resultSet.getString("Shift Painted")
                        + "," + resultSet.getString("User Painted") + "," + resultSet.getString("Date Touched Up")
                        + "," + resultSet.getString("Shift Touched Up") + "," + resultSet.getString("User Touched Up")
                        + "," + resultSet.getString("Date Loaded") + "," + resultSet.getString("Shift Loaded") + ","
                        + "," + resultSet.getString("User Loaded");
                inventory.add(new Pipe(pipeLine));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    private static void writePipeInventory(){
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(backupDataLocation +"BackupSpoolData"+ new SimpleDateFormat("MM_dd_yyyy HH_mm_ss").format(Calendar.getInstance().getTime()) +".txt");
            bufferedWriter = new BufferedWriter(fileWriter);
            for(int i =0; i<inventory.size(); i++){
                bufferedWriter.write(inventory.get(i).pipeToString() + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(bufferedWriter!=null) {
                    bufferedWriter.close();
                }
                if(fileWriter!=null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static int findPipe(long pipeID, Vector <Pipe>inventory) {

        for(int i =0; i<inventory.size(); i++){
            try {
                if (pipeID == Integer.parseInt(inventory.get(i).getID())) {
                    return i;
                }
            }catch(NumberFormatException e){
                System.out.println("DATABASE ID ERROR");

            }

        }
        return -1;
    }

    private static void formatImages(String folderLocation,final File folder) {
        File toDelete = new File(folderLocation);
        for (final File fileEntry : folder.listFiles()) {
            if(fileEntry.isDirectory()) {
                formatImages(folderLocation, fileEntry);
            }else {

                String oldFilename = fileEntry.getName();
                if (oldFilename.length() != 16 && (!oldFilename.equals("Thumbs.db"))) {
                    String newFileName = oldFilename.substring(0, 12);
                    newFileName = folderLocation + "/" + newFileName + ".jpg";
                    fileEntry.renameTo(new File(newFileName));
                    System.out.println("Renamed " + oldFilename + " to " + newFileName);
                }
            }

        }
        deleteDirectories(toDelete);
    }
    private static void deleteDirectories(File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                fileEntry.delete();
            }
        }
    }

}
