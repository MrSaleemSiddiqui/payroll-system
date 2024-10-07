package com.payroll.system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.payroll.system.model.*;
import com.payroll.system.service.EmployeeService;

public class EmployeeControllerTest {

	@InjectMocks
	private EmployeeController employeeController;

	@Mock
	private EmployeeService employeeService;

	@Mock
	private MultipartFile file;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testUploadFile_Success() throws Exception {
		when(file.getOriginalFilename()).thenReturn("test.csv");

		ResponseEntity<String> response = employeeController.uploadFile(file);

		verify(employeeService, times(1)).processEmployeeFile(file);
		assert response.getStatusCodeValue() == 200;
		assert response.getBody().equals("File uploaded and data saved successfully.");
	}

	@Test
	public void testUploadFile_Failure() throws Exception {
		doThrow(new RuntimeException("File processing error")).when(employeeService).processEmployeeFile(file);

		ResponseEntity<String> response = employeeController.uploadFile(file);

		verify(employeeService, times(1)).processEmployeeFile(file);
		assert response.getStatusCodeValue() == 500;
		assert response.getBody().equals("Error processing file: File processing error");
	}

	@Test
	public void testGetAllEmployees() {
		List<EmployeeEvent> mockEvents = new ArrayList<>();
		when(employeeService.getAllEmployeeEvents()).thenReturn(mockEvents);

		List<EmployeeEvent> response = employeeController.getAllEmployees();

		verify(employeeService, times(1)).getAllEmployeeEvents();
		assert response == mockEvents;
	}

	@Test
	public void testGetTotalEmployees() {
		when(employeeService.countTotalEmployees()).thenReturn(5L);

		ResponseEntity<Long> response = employeeController.getTotalEmployees();

		verify(employeeService, times(1)).countTotalEmployees();
		assert response.getStatusCodeValue() == 200;
		assert response.getBody() == 5L;
	}

	@Test
	public void testGetMonthlyJoins() {
		Map<String, List<EmployeeJoinDetailsDto>> mockJoins = new HashMap<>();
		when(employeeService.getMonthlyJoinDetails()).thenReturn(mockJoins);

		ResponseEntity<Map<String, List<EmployeeJoinDetailsDto>>> response = employeeController.getMonthlyJoins();

		verify(employeeService, times(1)).getMonthlyJoinDetails();
		assert response.getStatusCodeValue() == 200;
		assert response.getBody() == mockJoins;
	}

	@Test
	public void testGetMonthlyExits() {
		Map<String, List<EmployeeExitDetailsDto>> mockExits = new HashMap<>();
		when(employeeService.getMonthlyExitDetails()).thenReturn(mockExits);

		ResponseEntity<Map<String, List<EmployeeExitDetailsDto>>> response = employeeController.getMonthlyExits();

		verify(employeeService, times(1)).getMonthlyExitDetails();
		assert response.getStatusCodeValue() == 200;
		assert response.getBody() == mockExits;
	}

	@Test
	public void testGetMonthlySalaryReport() {
		Map<String, MonthlySalaryReportDto> mockSalaryReport = new HashMap<>();
		when(employeeService.getMonthlySalaryReport()).thenReturn(mockSalaryReport);

		ResponseEntity<Map<String, MonthlySalaryReportDto>> response = employeeController.getMonthlySalaryReport();

		verify(employeeService, times(1)).getMonthlySalaryReport();
		assert response.getStatusCodeValue() == 200;
		assert response.getBody() == mockSalaryReport;
	}

	@Test
	public void testGetEmployeeFinancialReport() {
		Map<String, EmployeeFinancialReportDto> mockReport = new HashMap<>();
		when(employeeService.getEmployeeFinancialReport()).thenReturn(mockReport);

		ResponseEntity<Map<String, EmployeeFinancialReportDto>> response = employeeController
				.getEmployeeFinancialReport();

		verify(employeeService, times(1)).getEmployeeFinancialReport();
		assert response.getStatusCodeValue() == 200;
		assert response.getBody() == mockReport;
	}

	@Test
	public void testGetMonthlyAmountReleased() {
		Map<String, MonthlyAmountReleasedReportDto> mockReport = new HashMap<>();
		when(employeeService.getMonthlyAmountReleased()).thenReturn(mockReport);

		ResponseEntity<Map<String, MonthlyAmountReleasedReportDto>> response = employeeController
				.getMonthlyAmountReleased();

		verify(employeeService, times(1)).getMonthlyAmountReleased();
		assert response.getStatusCodeValue() == 200;
		assert response.getBody() == mockReport;
	}

	@Test
	public void testGetYearlyFinancialReport() {
		Map<Integer, List<YearlyFinancialReportDto>> mockReport = new HashMap<>();
		when(employeeService.getYearlyFinancialReport()).thenReturn(mockReport);

		ResponseEntity<Map<Integer, List<YearlyFinancialReportDto>>> response = employeeController
				.getYearlyFinancialReport();

		verify(employeeService, times(1)).getYearlyFinancialReport();
		assert response.getStatusCodeValue() == 200;
		assert response.getBody() == mockReport;
	}
}
