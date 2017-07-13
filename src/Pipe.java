import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class Pipe {
    private String ID, branch_plant,spoolNo,orderNo,weight,area,paintSystem,inboundLoadNum,
            internalBlast,cleaningMethod,dateReceived, surfacePreparation,OSNDDetail,loadListNum,trailerNum,
            invoiced,location,dateUnloaded,shiftUnloaded,userUnloaded,dateBlasted,shiftBlasted,userBlasted,datePainted,
            shiftPainted,userPainted,dateTouchedUp,shiftTouchedUp,userTouchedUp,dateLoaded,shiftLoaded,userLoaded;
    private long unloadInstant, blastInstant, paintInstant,touchupInstant,loadInstant;
    private String pipeLine;


    Pipe(String pipeLine_in){
        pipeLine = pipeLine_in;
        if(!pipeLine.equals("null")) {
            pipeLine += ",END";
            String[] parameterList = pipeLine.split(",");
            ID = parameterList[0];
            branch_plant = parameterList[1];
            spoolNo = parameterList[2];
            orderNo = parameterList[3];
            weight = parameterList[4];
            area = parameterList[5];
            paintSystem = parameterList[6];
            inboundLoadNum = parameterList[7];
            internalBlast = parameterList[8];
            cleaningMethod = parameterList[9];
            dateReceived = parameterList[10];
            surfacePreparation = parameterList[11];
            OSNDDetail = parameterList[12];
            loadListNum = parameterList[13];
            trailerNum = parameterList[14];
            invoiced = parameterList[15];
            location = parameterList[16];
            dateUnloaded = parameterList[17];
            shiftUnloaded = parameterList[18];
            userUnloaded = parameterList[19];
            dateBlasted = parameterList[20];
            shiftBlasted = parameterList[21];
            userBlasted = parameterList[22];
            datePainted = parameterList[23];
            shiftPainted = parameterList[24];
            userPainted = parameterList[25];
            dateTouchedUp = parameterList[26];
            shiftTouchedUp = parameterList[27];
            userTouchedUp = parameterList[28];
            dateLoaded = parameterList[29];
            shiftLoaded = parameterList[30];
            userLoaded = parameterList[31];
        }
    }
    public String pipeToString() {
        String pipeToLine;
        if(!this.pipeLine.equals("null")) {
            pipeToLine = ID + "," + branch_plant + "," + spoolNo + "," + orderNo + "," + weight + "," + area + "," +
                    paintSystem + "," + inboundLoadNum + "," + internalBlast + "," + cleaningMethod + "," +
                    dateReceived + "," + surfacePreparation + "," + OSNDDetail + "," + loadListNum + "," +
                    trailerNum + "," + invoiced + "," + location + "," + dateUnloaded + "," + shiftUnloaded + "," + userUnloaded + "," +
                    dateBlasted + "," + shiftBlasted + "," + userBlasted + "," + datePainted + "," + shiftPainted + "," +
                    userPainted + "," + dateTouchedUp + "," + shiftTouchedUp + "," + userTouchedUp + "," +
                    dateLoaded + "," + shiftLoaded + "," + userLoaded;
            return pipeToLine;
        }
        else{
            return "null";
        }
    }
    public File findImage(){

        String projectNum = this.branch_plant.substring(branch_plant.length() - 6, branch_plant.length());
        String spoolNum = this.spoolNo.substring(0, 5);
        return new File(Main.jpgFileLocation+ "/" + projectNum + "-" + spoolNum + ".jpg");


    }

    public String getID() {
        return ID;
    }

    public String getPipeLine() {
        return pipeLine;
    }




    // Date Setters/User Setters
    public void setDateUnloaded(String _dateUnloaded, long _unloadInstant, String _userUnloaded) {
        // System.out.println("Current Time: " + dateUnloaded);
        this.dateUnloaded = _dateUnloaded;
        this.unloadInstant = _unloadInstant;
        this.userUnloaded = _userUnloaded;
        this.location = "Unload";
        setShiftUnloaded(TimeCheck.shiftCheck());
        dateUpdate("Date Unloaded","Shift Unloaded","User Unloaded", this.dateUnloaded,this.shiftUnloaded,this.userUnloaded);
    }

    public void setDateBlasted(String _dateBlasted, long _blastInstant, String _userBlasted) {
        this.dateBlasted = _dateBlasted;
        this.blastInstant = _blastInstant;
        this.userBlasted = _userBlasted;
        this.location = "Blast";
        setShiftBlasted(TimeCheck.shiftCheck());
        dateUpdate("Date Blasted","Shift Blasted","User Blasted", this.dateBlasted,this.shiftBlasted,this.userBlasted);

    }

    public void setDatePainted(String _datePainted, long _paintInstant, String _userPainted) {
        this.datePainted = _datePainted;
        this.paintInstant = _paintInstant;
        this.userPainted = _userPainted;
        this.location = "Paint";
        setShiftPainted(TimeCheck.shiftCheck());
        dateUpdate("Date Painted","Shift Painted","User Painted", this.datePainted,this.shiftPainted,this.userPainted);
    }

    public void setDateTouchedUp(String _dateTouchedUp, long _touchupInstant, String _userTouchedUp) {
        this.dateTouchedUp = _dateTouchedUp;
        this.touchupInstant = _touchupInstant;
        this.userTouchedUp = _userTouchedUp;
        this.location = "Touch Up";
        setShiftTouchedUp(TimeCheck.shiftCheck());
        dateUpdate("Date Touched Up","Shift Touched Up","User Touched Up", this.dateTouchedUp,this.shiftTouchedUp,this.userTouchedUp);
    }

    public void setDateLoaded(String _dateLoaded, long _loadInstant,String _userLoaded,String _loadListNum, String _trailerNum) {
        this.dateLoaded = _dateLoaded;
        this.loadInstant = _loadInstant;
        this.userLoaded = _userLoaded;
        this.loadListNum = _loadListNum;
        this.trailerNum = _trailerNum;
        this.location = "Load";
        setShiftLoaded(TimeCheck.shiftCheck());
        dateUpdate("Date Loaded","Shift Loaded","User Loaded", this.dateLoaded,this.shiftLoaded,this.userLoaded);
        setTrailerLoad();
    }

    //Shift Setters
    public void setShiftUnloaded(String shiftUnloaded) {
        this.shiftUnloaded = shiftUnloaded;
    }

    public void setShiftBlasted(String shiftBlasted) {
        this.shiftBlasted = shiftBlasted;
    }

    public void setShiftPainted(String shiftPainted) {
        this.shiftPainted = shiftPainted;
    }

    public void setShiftTouchedUp(String shiftTouchedUp) {
        this.shiftTouchedUp = shiftTouchedUp;
    }

    public void setShiftLoaded(String shiftLoaded) {
        this.shiftLoaded = shiftLoaded;
    }

    public String getDateUnloaded() {
        return dateUnloaded;
    }

    public String getDateBlasted() {
        return dateBlasted;
    }

    public String getDatePainted() {
        return datePainted;
    }

    public String getDateTouchedUp() {
        return dateTouchedUp;
    }

    public String getDateLoaded() {
        return dateLoaded;
    }

    public void dateUpdate(String dateLocation, String shiftLocation, String userLocation, String date, String shift, String user){
        try {
            Connection conn = DriverManager.getConnection(Main.dataBaseLocation);

            Statement st = conn.createStatement();
            String sql = "Update [Spool Data] " +
                    "Set ["+dateLocation+"] = #"+date+"#,["+shiftLocation+"] = '"+shift+"' ," +
                    "["+userLocation+"] = '"+user+"', " +
                    "Location = '"+this.location+"'" +
                    "where ID = '"+this.ID+"'";
            st.execute(sql);
        } catch (SQLException e) {

            e.printStackTrace();
        }

    }
    public void setTrailerLoad(){
        try {
            Connection conn = DriverManager.getConnection(Main.dataBaseLocation);

            Statement st = conn.createStatement();
            String sql = "Update [Spool Data] " +
                    "Set [Load List #] = "+this.loadListNum+",[Trailer #] = '"+this.trailerNum+"'" +
                    "where ID = '"+this.ID+"'";
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setOSNDDetail(String OSNDDetail) {
        this.OSNDDetail = OSNDDetail;
        try {
            Connection conn = DriverManager.getConnection(Main.dataBaseLocation);

            Statement st = conn.createStatement();
            String sql = "Update [Spool Data] " +
                    "Set [OS&D Detail] = '"+OSNDDetail+"' " +
                    "where ID = '"+this.ID+"'";
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
