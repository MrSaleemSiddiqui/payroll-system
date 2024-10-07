package com.payroll.system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySalaryReportDto {


    private String monthYear;
    private double totalSalary;
    private int totalEmployees;

   
}