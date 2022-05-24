package custom.global
import com.atlassian.jira.component.ComponentAccessor
public class DateFunctions
{
    public static boolean DayAndMonthMatch(Calendar c1, Calendar c2)
    {
        return c1.time.format("yyyy-MM-dd") == c2.time.format("yyyy-MM-dd")
    }
    public static Calendar getLastWorkingDay(int MonthsToAdd = 0)
    {
        def date = new Date();
        def today = date.format('yyyy-MM-dd');      
        def day = date.format('dd');
        def year = date.format('yyyy');
        def month = date.format('MM');
        def addmonth = (Integer.parseInt(month)-1)+MonthsToAdd
        //To get in a variable the DATE_OF_THE_FIRST_MONDAY_OF_THE_CURRENT_MONTH
        Calendar cLastDay = Calendar.instance;
        cLastDay.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cLastDay.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);
        cLastDay.set(Calendar.MONTH, addmonth);
        cLastDay.set(Calendar.YEAR, Integer.parseInt(year));
        cLastDay.set(Calendar.HOUR_OF_DAY, 20);
        //wellllllllll I can't think of a better way to do this so :/
        //returns last saturday of month :/
        while(cLastDay.get(Calendar.MONTH) == addmonth)
        {
            cLastDay.add(Calendar.DAY_OF_WEEK_IN_MONTH,1);
        }
        cLastDay.add(Calendar.DAY_OF_WEEK_IN_MONTH,-1);
        return cLastDay;
    }
    public static Calendar getFirstWorkingDay(int MonthsToAdd = 0)
    {
        def date = new Date();
        def today = date.format('yyyy-MM-dd');      
        def day = date.format('dd');
        def year = date.format('yyyy');
        def month = date.format('MM');
        Calendar cFirstDay = Calendar.instance;
        def addmonth = (Integer.parseInt(month)-1)+MonthsToAdd
        //To get in a variable the DATE_OF_THE_FIRST_MONDAY_OF_THE_CURRENT_MONTH
        cFirstDay.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cFirstDay.set(Calendar.DAY_OF_WEEK_IN_MONTH, 3);
        cFirstDay.set(Calendar.MONTH, addmonth);
        cFirstDay.set(Calendar.YEAR, Integer.parseInt(year));
        def curweek = cFirstDay.get(Calendar.WEEK_OF_MONTH);
        cFirstDay.add(Calendar.WEEK_OF_MONTH,-curweek)
        cFirstDay.add(Calendar.DATE,1)
            
        //def FirstDay = cFirstDay.time.format("yyyy-MM-dd")
        return cFirstDay;
    }
}
