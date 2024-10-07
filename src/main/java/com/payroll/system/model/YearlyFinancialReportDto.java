package com.payroll.system.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyFinancialReportDto {

	private String event;
	private String empId;
	private LocalDate eventDate;
	private String eventValue;

}
