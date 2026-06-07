package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.LeaveRequest;
import utils.DBConnection;

public class LeaveDAO {

    public boolean applyLeave(int employeeId, String leaveType,
                              Date startDate, Date endDate, String reason) {

        String sql = """
                INSERT INTO leaves (employee_id, leave_type, start_date, end_date, reason)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ps.setString(2, leaveType);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);
            ps.setString(5, reason);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<LeaveRequest> getAllLeaves() {
        List<LeaveRequest> list = new ArrayList<>();

        String sql = """
                SELECT * FROM leaves
                ORDER BY applied_at DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new LeaveRequest(
                        rs.getInt("leave_id"),
                        rs.getInt("employee_id"),
                        rs.getString("leave_type"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getTimestamp("applied_at"),
                        rs.getString("admin_comment")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<LeaveRequest> getLeavesByEmployee(int employeeId) {
        List<LeaveRequest> list = new ArrayList<>();

        String sql = """
                SELECT * FROM leaves
                WHERE employee_id = ?
                ORDER BY applied_at DESC
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new LeaveRequest(
                        rs.getInt("leave_id"),
                        rs.getInt("employee_id"),
                        rs.getString("leave_type"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getTimestamp("applied_at"),
                        rs.getString("admin_comment")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateLeaveStatus(int leaveId, String status, int adminId, String comment) {
        String sql = """
                UPDATE leaves
                SET status = ?, approved_by = ?, admin_comment = ?
                WHERE leave_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, adminId);
            ps.setString(3, comment);
            ps.setInt(4, leaveId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}