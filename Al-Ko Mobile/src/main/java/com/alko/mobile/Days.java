package com.alko.mobile;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 1/8/14.
 */
public class Days
{
    @JsonProperty
    private String DayID;

    @JsonProperty
    private String PayPeriod;

    @JsonProperty
    private String Employee;

    @JsonProperty
    private String Date;

    @JsonProperty
    private String StartTime;

    @JsonProperty
    private String EndTime;

    public String getDayID()
    {
        return this.DayID;
    }

    public String getPayperiod()
    {
        return this.PayPeriod;
    }

    public String getEmployee()
    {
        return this.Employee;
    }

    public String getDate()
    {
        return this.Date;
    }

    public void setDate(String inputDate)
    {
        this.Date = inputDate;
    }

    public String getStartTime()
    {
        return this.StartTime;
    }

    public void setStartTime(String inputStartTime)
    {
        this.StartTime = inputStartTime;
    }

    public String getEndTime()
    {
        return this.EndTime;
    }

    public void setEndTime(String inputEndTime)
    {
        this.EndTime = inputEndTime;
    }
}
