package com.alko.mobile;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Administrator on 1/7/14.
 */
public class Employees
{
    @JsonProperty
    private String EmployeeID;
    @JsonProperty
    private String FirstName;
    @JsonProperty
    private String LastName;
    @JsonProperty
    private String CombinedName;
    @JsonProperty
    private String Login;
    @JsonProperty
    private String Password;

    public String getEmployeeID() { return this.EmployeeID; }
    public String getFirstName() { return this.FirstName; }
    public String getLastName() { return this.LastName; }
    public String getCombinedName() { return this.CombinedName; }
    public String getLogin() { return this.Login; }
    public String getPassword() { return this.Password; }
}
