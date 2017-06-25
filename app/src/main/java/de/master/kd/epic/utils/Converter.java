package de.master.kd.epic.utils;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by pentax on 25.06.17.
 */

public class Converter {
    private static final SimpleDateFormat dateFormat= new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
    private Converter(){}


    public static final String toString(Date date){
        return dateFormat.format(date);
    }

    public static Date toObject(String date) throws ParseException{
            return dateFormat.parse(date);
    }
}
