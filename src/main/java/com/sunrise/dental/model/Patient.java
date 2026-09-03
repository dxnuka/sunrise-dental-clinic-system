package com.sunrise.dental.model;

public class Patient {
    private int patientId;
    private String patientName;
    private String address;
    private String contactNumber;
    private Integer birthYear;
    private String gender; // MALE, FEMALE, OTHER

    public Patient() {}

    public Patient(String patientName, String address, String contactNumber, Integer birthYear, String gender) {
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.birthYear = birthYear;
        this.gender = gender;
    }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public Integer getBirthYear() { return birthYear; }
    public void setBirthYear(Integer birthYear) { this.birthYear = birthYear; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
