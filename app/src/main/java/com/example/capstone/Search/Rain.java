package com.example.capstone.Search;

import com.google.firebase.Timestamp;

public class Rain {
    private String clientId;
    private String clientName;
    private String lastRainDt;
    private String accRain;
    private String timeDay;
    private String level6;
    private String level12;
    private String accRainDt;
    private Timestamp createdAt;

    // 기본 생성자
    public Rain() {
    }

    // getter 및 setter 메서드
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getLastRainDt() {
        return lastRainDt;
    }

    public void setLastRainDt(String lastRainDt) {
        this.lastRainDt = lastRainDt;
    }

    public String getAccRain() {
        return accRain;
    }

    public void setAccRain(String accRain) {
        this.accRain = accRain;
    }

    public String getTimeDay() {
        return timeDay;
    }

    public void setTimeDay(String timeDay) {
        this.timeDay = timeDay;
    }

    public String getLevel6() {
        return level6;
    }

    public void setLevel6(String level6) {
        this.level6 = level6;
    }

    public String getLevel12() {
        return level12;
    }

    public void setLevel12(String level12) {
        this.level12 = level12;
    }

    public String getAccRainDt() {
        return accRainDt;
    }

    public void setAccRainDt(String accRainDt) {
        this.accRainDt = accRainDt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // toString 메서드
    @Override
    public String toString() {
        return "Rain{" +
                "clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", lastRainDt='" + lastRainDt + '\'' +
                ", accRain='" + accRain + '\'' +
                ", timeDay='" + timeDay + '\'' +
                ", level6='" + level6 + '\'' +
                ", level12='" + level12 + '\'' +
                ", accRainDt='" + accRainDt + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}