package com.sportstalk.models.common;

/**
 * The reason for reporting a comment or chat event.
 * Typical reason is "Abuse"
 */
public class ReportReason {

    private ReportType reportType;
    private String userId;

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
