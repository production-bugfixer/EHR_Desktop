/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.triloco.ehrmachine.models;


public class Appointment {
    private String time;
    private String patient;
    private String reason;
    private String status;

    public Appointment(String time, String patient, String reason, String status) {
        this.time = time;
        this.patient = patient;
        this.reason = reason;
        this.status = status;
    }

    public String getTime() { return time; }
    public String getPatient() { return patient; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
}
