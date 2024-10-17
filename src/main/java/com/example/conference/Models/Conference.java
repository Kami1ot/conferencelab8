package com.example.conference.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
public class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String conferenceName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime conferenceDate;
    private String moderatorFullName;
    private String leadSpeakerFullName;

    public Conference() {
    }

    public Conference(String conferenceName, LocalDateTime conferenceDate, String moderatorFullName, String leadSpeakerFullName) {
        this.conferenceName = conferenceName;
        this.conferenceDate = conferenceDate;
        this.moderatorFullName = moderatorFullName;
        this.leadSpeakerFullName = leadSpeakerFullName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    public LocalDateTime getConferenceDate() {
        return conferenceDate;
    }

    public void setConferenceDate(LocalDateTime conferenceDate) {
        this.conferenceDate = conferenceDate;
    }

    public String getModeratorFullName() {
        return moderatorFullName;
    }

    public void setModeratorFullName(String moderatorFullName) {
        this.moderatorFullName = moderatorFullName;
    }

    public String getLeadSpeakerFullName() {
        return leadSpeakerFullName;
    }

    public void setLeadSpeakerFullName(String leadSpeakerFullName) {
        this.leadSpeakerFullName = leadSpeakerFullName;
    }

}
