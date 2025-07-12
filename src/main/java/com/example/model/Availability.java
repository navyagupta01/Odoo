// com.example.model.Availability.java
package com.example.model;

import jakarta.persistence.Embeddable;
import java.util.List;

@Embeddable
public class Availability {
    private String days;       // Store as comma-separated string
    private String timeSlot;
    private String timezone;

    public Availability() {}

    public Availability(List<String> days, String timeSlot, String timezone) {
        this.days = String.join(",", days); // convert to string
        this.timeSlot = timeSlot;
        this.timezone = timezone;
    }

    public List<String> getDays() {
        return days != null && !days.isEmpty()
                ? List.of(days.split(","))
                : List.of(); // return empty list
    }


    public void setDays(List<String> days) {
        this.days = String.join(",", days);
    }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}
