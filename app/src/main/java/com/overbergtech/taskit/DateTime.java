package com.overbergtech.taskit;

import java.util.Calendar;

class DateTime {

    public String getDateTime(){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String getYear = Integer.toString(year);
        String getMonth = Integer.toString(month+1);
        String getDay = Integer.toString(day);

        String full_date = (getDay + "/" + getMonth + "/" + getYear);
        return full_date;
    }
}
