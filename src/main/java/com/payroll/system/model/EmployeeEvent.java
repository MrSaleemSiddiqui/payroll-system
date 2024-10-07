package com.payroll.system.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "employee_events")
public class EmployeeEvent {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "sequence_no", nullable = false)
 private Integer sequenceNo;

 @Column(name = "emp_id", nullable = false)
 private String empId;

 @Column(name = "emp_f_name", nullable = true)
 private String empFName;

 @Column(name = "emp_l_name", nullable = true)
 private String empLName;

 @Column(name = "designation", nullable = true)
 private String designation;

// @Column(name = "event", nullable = false)
// private String event; // Can also be an Enum for better type safety

 @Enumerated(EnumType.STRING) // Use EnumType.ORDINAL to store as integer
 @Column(name = "event", nullable = false)
 private EventType event;
 
 @Column(name = "value")
 private String value; // Nullable for ONBOARD and EXIT events

 @Column(name = "event_date", nullable = false)
 private LocalDate eventDate; // Date related to the specific event

 @Column(name = "notes")
 private String notes;

}
