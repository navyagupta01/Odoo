// com.example.dto.AdminDashboardStats.java
package com.example.dto;

public class AdminDashboardStats {
    private long totalUsers;
    private long activeSwaps;
    private long pendingSwaps;
    private long completedSwaps;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveSwaps() {
        return activeSwaps;
    }

    public void setActiveSwaps(long activeSwaps) {
        this.activeSwaps = activeSwaps;
    }

    public long getPendingSwaps() {
        return pendingSwaps;
    }

    public void setPendingSwaps(long pendingSwaps) {
        this.pendingSwaps = pendingSwaps;
    }

    public long getCompletedSwaps() {
        return completedSwaps;
    }

    public void setCompletedSwaps(long completedSwaps) {
        this.completedSwaps = completedSwaps;
    }
}