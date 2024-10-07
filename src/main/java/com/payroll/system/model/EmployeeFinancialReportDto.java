package com.payroll.system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFinancialReportDto {

	private String empId;
	private String empFName;
	private String empLName;
	private double totalAmountPaid;

}
