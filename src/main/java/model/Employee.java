package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Employee {
    private String empId;
    private String name;
    private String fname;
    private Date dob;
    private BigDecimal salary;
    private String address;
    private String phone;
    private String email;
    private String education;
    private String designation;
    private String aadhar;

    // Constructor matching DashboardUI output exactly
    public Employee(String empId, String name, String fname, Date dob, BigDecimal salary, 
                    String address, String phone, String email, String education, 
                    String designation, String aadhar) {
        this.empId = empId;
        this.name = name;
        this.fname = fname;
        this.dob = dob;
        this.salary = salary;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.education = education;
        this.designation = designation;
        this.aadhar = aadhar;
    }

    // Getters
    public String getEmpId() { return empId; }
    public String getName() { return name; }
    public String getFname() { return fname; }
    public Date getDob() { return dob; }
    public BigDecimal getSalary() { return salary; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getEducation() { return education; }
    public String getDesignation() { return designation; }
    public String getAadhar() { return aadhar; }
}