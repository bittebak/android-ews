package ews.types;

import java.util.Date;

/**
 * Created by marcprive on 05-31-15.
 */
public class CalendarItem {
    private String itemId;
    private String parentFolderId;
    private Date start;
    private Date end;
    private String organizer;
    private String subject;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;

    public String getItemId() {
        return itemId;
    }

    public String getParentFolderId() {
        return parentFolderId;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public String getOrganizer() {
        return organizer;
    }

    public String getSubject() {
        return subject;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
