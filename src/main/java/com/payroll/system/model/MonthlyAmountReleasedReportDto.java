package com.payroll.system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAmountReleasedReportDto {

	private String monthYear;
	private double totalAmount;
	private int totalEmployees;

}
