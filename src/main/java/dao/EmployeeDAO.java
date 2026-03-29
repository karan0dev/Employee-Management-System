package dao;

import model.Employee;
import utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    
    public boolean addEmployee(Employee emp) {
        String query = "INSERT INTO employees (emp_id, name, fname, dob, salary, address, phone, email, education, designation, aadhar) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, emp.getEmpId());
            pstmt.setString(2, emp.getName());
            pstmt.setString(3, emp.getFname());
            pstmt.setDate(4, emp.getDob());
            pstmt.setBigDecimal(5, emp.getSalary());
            pstmt.setString(6, emp.getAddress());
            pstmt.setString(7, emp.getPhone());
            pstmt.setString(8, emp.getEmail());
            pstmt.setString(9, emp.getEducation());
            pstmt.setString(10, emp.getDesignation());
            pstmt.setString(11, emp.getAadhar());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in addEmployee: " + e.getMessage());
            return false;
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        String query = "SELECT * FROM employees";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Employee emp = new Employee(
                    rs.getString("emp_id"), rs.getString("name"), rs.getString("fname"),
                    rs.getDate("dob"), rs.getBigDecimal("salary"), rs.getString("address"),
                    rs.getString("phone"), rs.getString("email"), rs.getString("education"),
                    rs.getString("designation"), rs.getString("aadhar")
                );
                employeeList.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error fetching employees: " + e.getMessage());
        }
        return employeeList;
    }

    public boolean deleteEmployee(String empId) {
        String query = "DELETE FROM employees WHERE emp_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, empId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error deleting employee: " + e.getMessage());
            return false;
        }
    }

    // NEW METHOD: Update an existing employee
    public boolean updateEmployee(Employee emp) {
        String query = "UPDATE employees SET name=?, fname=?, dob=?, salary=?, address=?, phone=?, email=?, education=?, designation=?, aadhar=? WHERE emp_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, emp.getName());
            pstmt.setString(2, emp.getFname());
            pstmt.setDate(3, emp.getDob());
            pstmt.setBigDecimal(4, emp.getSalary());
            pstmt.setString(5, emp.getAddress());
            pstmt.setString(6, emp.getPhone());
            pstmt.setString(7, emp.getEmail());
            pstmt.setString(8, emp.getEducation());
            pstmt.setString(9, emp.getDesignation());
            pstmt.setString(10, emp.getAadhar());
            pstmt.setString(11, emp.getEmpId()); // ID is used strictly to find the right row
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error updating employee: " + e.getMessage());
            return false;
        }
    }
}