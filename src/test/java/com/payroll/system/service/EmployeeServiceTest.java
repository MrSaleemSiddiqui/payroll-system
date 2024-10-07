package com.payroll.system.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.payroll.system.model.*;
import com.payroll.system.repo.EmployeeEventRepository;

class EmployeeServiceTest {

	@InjectMocks
	private EmployeeService employeeService;

	@Mock
	private EmployeeEventRepository employeeEventRepository;

	@Mock
	private MultipartFile file;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testProcessEmployeeFile_Success() throws Exception {
		String fileContent = "1,EMP001,John,Doe,Developer,ONBOARD,1000,01-01-2023,Notes";
		ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
		when(file.getInputStream()).thenReturn(inputStream);

		employeeService.processEmployeeFile(file);

		verify(employeeEventRepository, times(1)).saveAll(any());
	}

	@Test
	void testProcessEmployeeFile_InvalidEventType() throws Exception {
		String fileContent = "1,EMP001,John,Doe,Developer,INVALID_TYPE,1000,01-01-2023,Notes";
		ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
		when(file.getInputStream()).thenReturn(inputStream);

		try {
			employeeService.processEmployeeFile(file);
		} catch (IllegalArgumentException e) {
			assert e.getMessage().equals("Unknown event type: INVALID_TYPE");
		}

		verify(employeeEventRepository, never()).saveAll(any());
	}

	@Test
	void testGetAllEmployeeEvents() {
		List<EmployeeEvent> mockEvents = new ArrayList<>();
		when(employeeEventRepository.findAll()).thenReturn(mockEvents);

		List<EmployeeEvent> result = employeeService.getAllEmployeeEvents();

		verify(employeeEventRepository, times(1)).findAll();
		assert result.equals(mockEvents);
	}

	@Test
	void testCountTotalEmployees() {
		List<EmployeeEvent> mockEvents = Arrays.asList(
				createEmployeeEvent(1, "EMP001", "John", "Doe", "Developer", EventType.ONBOARD, null, LocalDate.now(),
						"Joined the team."),
				createEmployeeEvent(2, "EMP002", "Jane", "Smith", "Manager", EventType.ONBOARD, null, LocalDate.now(),
						"Joined the team."),
				createEmployeeEvent(3, "EMP001", "John", "Doe", "Developer", EventType.EXIT, null, LocalDate.now(),
						"Left the company.") // Duplicate
		);
		when(employeeEventRepository.findAll()).thenReturn(mockEvents);

		long totalEmployees = employeeService.countTotalEmployees();

		assert totalEmployees == 2; // Only unique IDs should be counted
	}

	@Test
	void testGetMonthlyJoinDetails() {
		List<EmployeeEvent> mockEvents = Arrays.asList(
				createEmployeeEvent(1, "EMP001", "John", "Doe", "Developer", EventType.ONBOARD, null,
						LocalDate.of(2023, 1, 10), "Joined the team."),
				createEmployeeEvent(2, "EMP002", "Jane", "Smith", "Manager", EventType.ONBOARD, null,
						LocalDate.of(2023, 1, 20), "Joined the team."));
		when(employeeEventRepository.findAll()).thenReturn(mockEvents);

		Map<String, List<EmployeeJoinDetailsDto>> result = employeeService.getMonthlyJoinDetails();

		assert result.size() == 1; // Should return one month
		assert result.get("2023-01").size() == 2; // Two employees joined in January
	}

	@Test
	void testGetMonthlyExitDetails() {
		List<EmployeeEvent> mockEvents = Arrays.asList(
				createEmployeeEvent(1, "EMP001", "John", "Doe", "Developer", EventType.ONBOARD, null,
						LocalDate.of(2023, 1, 10), "Joined the team."),
				createEmployeeEvent(2, "EMP001", "", "", "", EventType.EXIT, null, LocalDate.of(2023, 1, 25),
						"Left the company."),
				createEmployeeEvent(3, "EMP002", "", "", "", EventType.EXIT, null, LocalDate.of(2023, 1, 30),
						"Left the company."));
		when(employeeEventRepository.findAll()).thenReturn(mockEvents);

		Map<String, List<EmployeeExitDetailsDto>> result = employeeService.getMonthlyExitDetails();

		assert result.size() == 1; // Should return one month
		assert result.get("2023-01").size() == 2; // Two employees exited in January
	}

	@Test
	void testGetMonthlySalaryReport() {
		List<EmployeeEvent> mockEvents = Arrays.asList(
				createEmployeeEvent(1, "EMP001", "", "", "", EventType.SALARY, "5000", LocalDate.of(2023, 1, 10),
						"Salary for January."),
				createEmployeeEvent(2, "EMP002", "", "", "", EventType.SALARY, "6000", LocalDate.of(2023, 1, 20),
						"Salary for January."));
		when(employeeEventRepository.findAll()).thenReturn(mockEvents);

		Map<String, MonthlySalaryReportDto> result = employeeService.getMonthlySalaryReport();

		assert result.size() == 1; // Should return one month
		assert result.get("2023-01").getTotalSalary() == 11000; // Total salary for January
	}

	@Test
	void testGetEmployeeFinancialReport() {
		List<EmployeeEvent> mockEvents = Arrays.asList(
				createEmployeeEvent(1, "EMP001", "John", "Doe", "Developer", EventType.ONBOARD, null,
						LocalDate.of(2023, 1, 10), "Joined the team."),
				createEmployeeEvent(2, "EMP001", "", "", "", EventType.SALARY, "5000", LocalDate.of(2023, 2, 10),
						"Salary for February."),
				createEmployeeEvent(3, "EMP001", "", "", "", EventType.BONUS, "1000", LocalDate.of(2023, 3, 10),
						"Bonus for February."));
		when(employeeEventRepository.findAll()).thenReturn(mockEvents);

		Map<String, EmployeeFinancialReportDto> result = employeeService.getEmployeeFinancialReport();

		assert result.size() == 1; // Only one employee
		assert result.get("EMP001").getTotalAmountPaid() == 6000; // Total amount for EMP001
	}

	@Test
	void testGetMonthlyAmountReleased() {
		List<EmployeeEvent> mockEvents = Arrays.asList(
				createEmployeeEvent(1, "EMP001", "", "", "", EventType.SALARY, "5000", LocalDate.of(2023, 1, 10),
						"Salary for January."),
				createEmployeeEvent(2, "EMP001", "", "", "", EventType.BONUS, "2000", LocalDate.of(2023, 1, 15),
						"Bonus for January."),
				createEmployeeEvent(3, "EMP002", "", "", "", EventType.SALARY, "6000", LocalDate.of(2023, 1, 20),
						"Salary for January."));
		when(employeeEventRepository.findAll()).thenReturn(mockEvents);

		Map<String, MonthlyAmountReleasedReportDto> result = employeeService.getMonthlyAmountReleased();

		assert result.size() == 1; // Should return one month
		assert result.get("2023-01").getTotalAmount() == 13000; // Total amount released in January
		assert result.get("2023-01").getTotalEmployees() == 2; // Total unique employees in January
	}

	@Test
	void testGetYearlyFinancialReport() {
		List<EmployeeEvent> mockEvents = Arrays.asList(
				createEmployeeEvent(1, "EMP001", "", "", "", EventType.SALARY, "5000", LocalDate.of(2023, 1, 10),
						"Salary for January."),
				createEmployeeEvent(2, "EMP002", "", "", "", EventType.BONUS, "2000", LocalDate.of(2023, 1, 15),
						"Bonus for January."),
				createEmployeeEvent(3, "EMP001", "", "", "", EventType.REIMBURSEMENT, "1000", LocalDate.of(2023, 2, 20),
						"Reimbursement for February."));
		when(employeeEventRepository.findAll()).thenReturn(mockEvents);

		Map<Integer, List<YearlyFinancialReportDto>> result = employeeService.getYearlyFinancialReport();

		assert result.size() == 1; // Should return one year (2023)
		assert result.get(2023).size() == 3; // Three events in total for 2023
	}

	// Utility method to create an EmployeeEvent object
	private EmployeeEvent createEmployeeEvent(Integer sequenceNo, String empId, String empFName, String empLName,
			String designation, EventType eventType, String value, LocalDate eventDate, String notes) {
		EmployeeEvent event = new EmployeeEvent();
		event.setSequenceNo(sequenceNo);
		event.setEmpId(empId);
		event.setEmpFName(empFName);
		event.setEmpLName(empLName);
		event.setDesignation(designation);
		event.setEvent(eventType);
		event.setValue(value);
		event.setEventDate(eventDate);
		event.setNotes(notes);
		return event;
	}
}
