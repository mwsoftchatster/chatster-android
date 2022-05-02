package nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatsterDateTimeUtil {

    public ChatsterDateTimeUtil() {
    }

    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String convertFromUtcToLocal(String utc){
        try {
            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utc);
            utcFormat.setTimeZone(TimeZone.getDefault());
            return utcFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUtcTime(){
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f.format(new Date());
    }

    public String getMessageCreated(String fullDate) {
        String date = "";
        String[] parts;
        String part1;
        String part2 = "";
        if (fullDate != null && !fullDate.equals("")) {
            parts = fullDate.split(" ");
            part1 = parts[0]; // 2017-05-27
            part2 = parts[1]; // 12:05:41
            String[] newParts = part2.split(":");
            part2 = newParts[0].concat(":").concat(newParts[1]);
            String[] dateParts = part1.split("-");
            String[] currDate = getDateTime().split(" ");
            String[] currDateParts = currDate[0].split("-");
            if (Integer.parseInt(currDateParts[2]) != Integer.parseInt(dateParts[2])) {
                date = part1;
            } else {
                date = part2;
            }
        }

        return date;
    }
}
