package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import model.Attendance;
import utils.DBConnection;

public class AttendanceDAO {

    public boolean markAttendance(int employeeId, String status, String remarks) {
        String sql = """
                INSERT INTO attendance (employee_id, attendance_date, status, remarks)
                VALUES (?, CURDATE(), ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setString(2, status);
            ps.setString(3, remarks);

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Attendance already marked today.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasMarkedToday(int employeeId) {
        String sql = """
                SELECT attendance_id FROM attendance
                WHERE employee_id = ? AND attendance_date = CURDATE()
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Attendance> getTodayAttendance() {
        List<Attendance> list = new ArrayList<>();

        String sql = """
                SELECT * FROM attendance
                WHERE attendance_date = CURDATE()
                ORDER BY check_in_time DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Attendance(
                        rs.getInt("attendance_id"),
                        rs.getInt("employee_id"),
                        rs.getDate("attendance_date"),
                        rs.getString("status"),
                        rs.getTime("check_in_time"),
                        rs.getString("remarks")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}