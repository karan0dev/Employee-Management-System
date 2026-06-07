package model;

import java.sql.Date;
import java.sql.Time;

public class Attendance {
    private int attendanceId;
    private int employeeId;
    private Date attendanceDate;
    private String status;
    private Time checkInTime;
    private String remarks;

    public Attendance(int attendanceId, int employeeId, Date attendanceDate,
                      String status, Time checkInTime, String remarks) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.checkInTime = checkInTime;
        this.remarks = remarks;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public Time getCheckInTime() {
        return checkInTime;
    }

    public String getRemarks() {
        return remarks;
    }
}