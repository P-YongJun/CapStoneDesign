package com.example.capstone.Search;

import com.google.firebase.Timestamp;

public class Bridge {
    private String alertLevel3;
    private String alertLevel3Nm;
    private String alertLevel4;
    private String alertLevel4Nm;
    private String timeDay;
    private String fludLevel;
    private String obsrTime;
    private String siteCode;
    private String siteName;
    private String sttus;
    private String sttusNm;
    private Timestamp createdAt;

    // 기본 생성자
    public Bridge() {
    }

    // Getter와 Setter 메서드들
    public String getAlertLevel3() {
        return alertLevel3;
    }

    public void setAlertLevel3(String alertLevel3) {
        this.alertLevel3 = alertLevel3;
    }

    public String getAlertLevel3Nm() {
        return alertLevel3Nm;
    }

    public void setAlertLevel3Nm(String alertLevel3Nm) {
        this.alertLevel3Nm = alertLevel3Nm;
    }

    public String getAlertLevel4() {
        return alertLevel4;
    }

    public void setAlertLevel4(String alertLevel4) {
        this.alertLevel4 = alertLevel4;
    }

    public String getAlertLevel4Nm() {
        return alertLevel4Nm;
    }

    public void setAlertLevel4Nm(String alertLevel4Nm) {
        this.alertLevel4Nm = alertLevel4Nm;
    }

    public String getTimeDay() {
        return timeDay;
    }

    public void setTimeDay(String timeDay) {
        this.timeDay = timeDay;
    }

    public String getFludLevel() {
        return fludLevel;
    }

    public void setFludLevel(String fludLevel) {
        this.fludLevel = fludLevel;
    }

    public String getObsrTime() {
        return obsrTime;
    }

    public void setObsrTime(String obsrTime) {
        this.obsrTime = obsrTime;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSttus() {
        return sttus;
    }

    public void setSttus(String sttus) {
        this.sttus = sttus;
    }

    public String getSttusNm() {
        return sttusNm;
    }

    public void setSttusNm(String sttusNm) {
        this.sttusNm = sttusNm;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Bridge{" +
                "alertLevel3='" + alertLevel3 + '\'' +
                ", alertLevel3Nm='" + alertLevel3Nm + '\'' +
                ", alertLevel4='" + alertLevel4 + '\'' +
                ", alertLevel4Nm='" + alertLevel4Nm + '\'' +
                ", timeDay='" + timeDay + '\'' +
                ", fludLevel='" + fludLevel + '\'' +
                ", obsrTime='" + obsrTime + '\'' +
                ", siteCode='" + siteCode + '\'' +
                ", siteName='" + siteName + '\'' +
                ", sttus='" + sttus + '\'' +
                ", sttusNm='" + sttusNm + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

}
