package com.payroll.system.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.payroll.system.model.EmployeeEvent;
import com.payroll.system.model.EmployeeExitDetailsDto;
import com.payroll.system.model.EmployeeFinancialReportDto;
import com.payroll.system.model.EmployeeJoinDetailsDto;
import com.payroll.system.model.MonthlyAmountReleasedReportDto;
import com.payroll.system.model.MonthlySalaryReportDto;
import com.payroll.system.model.YearlyFinancialReportDto;
import com.payroll.system.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			employeeService.processEmployeeFile(file);
			return ResponseEntity.ok("File uploaded and data saved successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error processing file: " + e.getMessage());
		}
	}

	@GetMapping
	public List<EmployeeEvent> getAllEmployees() {
		return employeeService.getAllEmployeeEvents();
	}

	@GetMapping("/count")
	public ResponseEntity<Long> getTotalEmployees() {
		long totalEmployees = employeeService.countTotalEmployees();
		return ResponseEntity.ok(totalEmployees);
	}

	@GetMapping("/monthly-joins")
	public ResponseEntity<Map<String, List<EmployeeJoinDetailsDto>>> getMonthlyJoins() {
		Map<String, List<EmployeeJoinDetailsDto>> monthlyJoins = employeeService.getMonthlyJoinDetails();
		return ResponseEntity.ok(monthlyJoins);
	}

	@GetMapping("/monthly-exits")
	public ResponseEntity<Map<String, List<EmployeeExitDetailsDto>>> getMonthlyExits() {
		Map<String, List<EmployeeExitDetailsDto>> monthlyExits = employeeService.getMonthlyExitDetails();
		return ResponseEntity.ok(monthlyExits);
	}

	@GetMapping("/monthly-salary-report")
	public ResponseEntity<Map<String, MonthlySalaryReportDto>> getMonthlySalaryReport() {
		Map<String, MonthlySalaryReportDto> salaryReport = employeeService.getMonthlySalaryReport();
		return ResponseEntity.ok(salaryReport);
	}

	@GetMapping("/employee-financial-report")
	public ResponseEntity<Map<String, EmployeeFinancialReportDto>> getEmployeeFinancialReport() {
		Map<String, EmployeeFinancialReportDto> financialReport = employeeService.getEmployeeFinancialReport();
		return ResponseEntity.ok(financialReport);
	}

	@GetMapping("/monthly-amount-released")
	public ResponseEntity<Map<String, MonthlyAmountReleasedReportDto>> getMonthlyAmountReleased() {
		Map<String, MonthlyAmountReleasedReportDto> amountReleasedReport = employeeService.getMonthlyAmountReleased();
		return ResponseEntity.ok(amountReleasedReport);
	}

	@GetMapping("/yearly-financial-report")
	public ResponseEntity<Map<Integer, List<YearlyFinancialReportDto>>> getYearlyFinancialReport() {
		Map<Integer, List<YearlyFinancialReportDto>> financialReport = employeeService.getYearlyFinancialReport();
		return ResponseEntity.ok(financialReport);
	}
}
