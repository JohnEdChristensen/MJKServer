
import java.util.Calendar;


public class TimeCheck  {
    public static String shiftCheck() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour == 14) {
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            if(minute>30)
                return "Night";
            else
                return "Day";
        }else{
            if(hour >14)
                return "Night";
            else
                return "Day";
        }
    }

}
