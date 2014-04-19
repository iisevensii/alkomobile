package com.alko.mobile;

import android.app.Application;

/**
 * Created by Administrator on 1/7/14.
 */
public class MyApplication extends Application
{
    private static MyApplication mInstance = null;
    private static String verifiedUserEmployeeID;
    private static String verifiedUserFullName;
    private static String currentPayPeriodID;
    private static String currentDayID;

    public MyApplication(){}

    public static synchronized MyApplication getInstance()
    {
        if(null == mInstance)
        {
            mInstance = new MyApplication();
        }
        return mInstance;
    }

    public static String getVerifiedUserEmployeeID()
    {
        return verifiedUserEmployeeID;
    }

    public static void setVerifiedUserEmployeeID(String inputVerifiedUserEmployeeID)
    {
       verifiedUserEmployeeID = inputVerifiedUserEmployeeID;
    }

    public static String getVerifiedUserFullName()
    {
        return verifiedUserFullName;
    }

    public static void setVerifiedUserFullName(String inputVerifiedUserFullName)
    {
        verifiedUserFullName = inputVerifiedUserFullName;
    }

    public static String getCurrentPayPeriodID()
    {
        return currentPayPeriodID;
    }

    public static void setCurrentPayPeriodID(String inputCurrentPayPeriodID)
    {
        currentPayPeriodID = inputCurrentPayPeriodID;
    }

    public static String getCurrentDayID()
    {
        return currentDayID;
    }

    public static void setCurrentDayID(String inputCurrentDay)
    {
        currentDayID = inputCurrentDay;
    }
}