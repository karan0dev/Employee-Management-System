package model;

import java.sql.Date;
import java.sql.Timestamp;

public class LeaveRequest {
    private int leaveId;
    private int employeeId;
    private String leaveType;
    private Date startDate;
    private Date endDate;
    private String reason;
    private String status;
    private Timestamp appliedAt;
    private String adminComment;

    public LeaveRequest(int leaveId, int employeeId, String leaveType,
                        Date startDate, Date endDate, String reason,
                        String status, Timestamp appliedAt, String adminComment) {
        this.leaveId = leaveId;
        this.employeeId = employeeId;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status;
        this.appliedAt = appliedAt;
        this.adminComment = adminComment;
    }

    public int getLeaveId() {
        return leaveId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getAppliedAt() {
        return appliedAt;
    }

    public String getAdminComment() {
        return adminComment;
    }
}