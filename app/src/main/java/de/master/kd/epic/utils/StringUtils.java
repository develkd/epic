package de.master.kd.epic.utils;

/**
 * Created by pentax on 25.06.17.
 */

public class StringUtils {

    private StringUtils(){

    }


    public static final String cut(String value, int index){
        if(null == value){
            return "";
        }

        int size = value.length();
        if(size > index){
            return value.substring(0,index);
        }

        return value;
    }

    public static boolean isEmpty(String value){
        return (null == value || value.trim().length() == 0);
    }

}
