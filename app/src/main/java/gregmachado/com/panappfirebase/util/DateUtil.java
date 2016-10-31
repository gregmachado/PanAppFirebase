package gregmachado.com.panappfirebase.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by gregmachado on 15/10/16.
 */
public class DateUtil {

    public static String getToday() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(c.getTime());
    }

    public static String getTomorrowDay() {
        Calendar d = Calendar.getInstance();
        d.add(Calendar.DATE, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(d.getTime());
    }

    public static String getTodayDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss");
        return sdf.format(c.getTime());
    }
}
