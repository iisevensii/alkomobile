package com.alko.mobile;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 1/3/14.
 */
public class PayPeriods
{
    @JsonProperty("PayPeriodId")
    private String PayPeriodId;
    @JsonProperty("PayPeriodStartDate")
    private String PayPeriodStartDate;
    @JsonProperty("PayPeriodEndDate")
    private String PayPeriodEndDate;

    public String getPayPeriodId()
    {
        return this.PayPeriodId;
    }

    public String getPayPeriodStartDate()
    {
        return this.PayPeriodStartDate;
    }

    public String getPayPeriodEndDate()
    {
        return this.PayPeriodEndDate;
    }
}
