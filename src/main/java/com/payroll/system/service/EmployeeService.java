package com.payroll.system.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.payroll.system.model.EmployeeEvent;
import com.payroll.system.model.EmployeeExitDetailsDto;
import com.payroll.system.model.EmployeeFinancialReportDto;
import com.payroll.system.model.EmployeeJoinDetailsDto;
import com.payroll.system.model.EventType;
import com.payroll.system.model.MonthlyAmountReleasedReportDto;
import com.payroll.system.model.MonthlySalaryReportDto;
import com.payroll.system.model.YearlyFinancialReportDto;
import com.payroll.system.repo.EmployeeEventRepository;

@Service
public class EmployeeService {

	@Autowired
	private EmployeeEventRepository employeeEventRepository;

	public void processEmployeeFile(MultipartFile file) throws IOException {
		List<EmployeeEvent> events = new ArrayList<>();

		// Date format for event dates: "dd-MM-yyyy"
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d-M-yyyy");

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;

			while ((line = reader.readLine()) != null) {
				// Split the line by commas, accounting for potential quotes
				String[] data = line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)");

				EmployeeEvent event = new EmployeeEvent();
				event.setSequenceNo(Integer.parseInt(data[0].trim())); // Common for all
				event.setEmpId(data[1].trim()); // Common for all

				// Check the event type based on the expected index for each type
				String eventTypeString = data.length > 6 ? data[5].trim() : data[2].trim();
				EventType eventType;

				try {
					// Convert the string to EventType enum
					eventType = EventType.valueOf(eventTypeString);
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException("Unknown event type: " + eventTypeString);
				}

				switch (eventType) {
				case ONBOARD:
					event.setEmpFName(data[2].trim());
					event.setEmpLName(data[3].trim());
					event.setDesignation(data[4].trim());
					event.setEvent(eventType); 
					event.setValue(data[6].trim()); // Onboarding date
					event.setEventDate(LocalDate.parse(data[7].trim(), dateFormatter)); // Event date
					event.setNotes(data[8].trim().replaceAll("^\"|\"$", ""));
					break;

				case SALARY:
					event.setEvent(eventType); // Set the event using the enum
					event.setValue(data[3].trim()); // Salary amount
					event.setEventDate(LocalDate.parse(data[4].trim(), dateFormatter)); // Event date
					event.setNotes(data[5].trim().replaceAll("^\"|\"$", ""));
					break;

				case BONUS:
					event.setEvent(eventType); // Set the event using the enum
					event.setValue(data[3].trim()); // Bonus amount
					event.setEventDate(LocalDate.parse(data[4].trim(), dateFormatter)); // Event date
					event.setNotes(data[5].trim().replaceAll("^\"|\"$", ""));
					break;

				case REIMBURSEMENT:
					event.setEvent(eventType); // Set the event using the enum
					event.setValue(data[3].trim()); // Reimbursement amount
					event.setEventDate(LocalDate.parse(data[4].trim(), dateFormatter)); // Event date
					event.setNotes(data[5].trim().replaceAll("^\"|\"$", ""));
					break;

				case EXIT:
					event.setEvent(eventType); // Set the event using the enum
					event.setValue(data[3].trim()); // Exit date
					event.setEventDate(LocalDate.parse(data[4].trim(), dateFormatter)); // Event date
					event.setNotes(data[5].trim().replaceAll("^\"|\"$", ""));
					break;

				default:
					throw new IllegalArgumentException("Unknown event type: " + eventTypeString);
				}

				events.add(event);
			}
		}

		// Save all events to the database in one go
		employeeEventRepository.saveAll(events);
	}

	public List<EmployeeEvent> getAllEmployeeEvents() {
		return employeeEventRepository.findAll();
	}

	public long countTotalEmployees() {
		List<EmployeeEvent> events = employeeEventRepository.findAll();
		Set<String> uniqueEmpIds = new HashSet<>();

		for (EmployeeEvent event : events) {
			uniqueEmpIds.add(event.getEmpId());
		}

		return uniqueEmpIds.size();
	}

	public Map<String, List<EmployeeJoinDetailsDto>> getMonthlyJoinDetails() {
		return employeeEventRepository.findAll().stream().filter(event -> EventType.ONBOARD.equals(event.getEvent()))
				.collect(Collectors.groupingBy(
						event -> String.format("%d-%02d", event.getEventDate().getYear(),
								event.getEventDate().getMonthValue()),
						Collectors.mapping(event -> new EmployeeJoinDetailsDto(event.getEmpId(), event.getEmpFName(),
								event.getEmpLName(), event.getDesignation()), Collectors.toList())));
	}

	public Map<String, List<EmployeeExitDetailsDto>> getMonthlyExitDetails() {
		List<EmployeeEvent> events = employeeEventRepository.findAll();

		// Create a map to quickly access onboarding records
		Map<String, EmployeeEvent> onboardingRecords = events.stream()
				.filter(event -> EventType.ONBOARD.equals(event.getEvent()))
				.collect(Collectors.toMap(EmployeeEvent::getEmpId, event -> event));

		return events.stream().filter(event -> EventType.EXIT.equals(event.getEvent())).collect(Collectors.groupingBy(
				event -> String.format("%d-%02d", event.getEventDate().getYear(), event.getEventDate().getMonthValue()),
				Collectors.mapping(event -> {
					EmployeeEvent onboardingEvent = onboardingRecords.get(event.getEmpId());
					return onboardingEvent != null
							? new EmployeeExitDetailsDto(onboardingEvent.getEmpId(), onboardingEvent.getEmpFName(),
									onboardingEvent.getEmpLName())
							: null;
				}, Collectors.toList())));
	}

	public Map<String, MonthlySalaryReportDto> getMonthlySalaryReport() {
		List<EmployeeEvent> events = employeeEventRepository.findAll();

		Map<String, MonthlySalaryReportDto> monthlySalaryReports = new HashMap<>();

		for (EmployeeEvent event : events) {
			if (EventType.SALARY.equals(event.getEvent())) {
				String monthYear = event.getEventDate().getYear() + "-"
						+ String.format("%02d", event.getEventDate().getMonthValue());
				double salary = Double.parseDouble(event.getValue());

				MonthlySalaryReportDto report = monthlySalaryReports.getOrDefault(monthYear,
						new MonthlySalaryReportDto(monthYear, 0.0, 0));
				report.setTotalSalary(report.getTotalSalary() + salary);
				report.setTotalEmployees(report.getTotalEmployees() + 1);

				monthlySalaryReports.put(monthYear, report);
			}
		}

		return monthlySalaryReports;
	}

	public Map<String, EmployeeFinancialReportDto> getEmployeeFinancialReport() {
		List<EmployeeEvent> events = employeeEventRepository.findAll();

		// Map to store onboarding details
		Map<String, EmployeeEvent> onboardingRecords = new HashMap<>();

		// Collect onboarding records
		for (EmployeeEvent event : events) {
			if (EventType.ONBOARD.equals(event.getEvent())) {
				onboardingRecords.put(event.getEmpId(), event);
			}
		}

		// Map to store financial reports
		Map<String, EmployeeFinancialReportDto> financialReports = new HashMap<>();

		for (EmployeeEvent event : events) {
			if (EventType.SALARY.equals(event.getEvent()) || EventType.BONUS.equals(event.getEvent())
					|| EventType.REIMBURSEMENT.equals(event.getEvent())) {
				String empId = event.getEmpId();
				double amount = Double.parseDouble(event.getValue());

				// Get onboarding details
				EmployeeEvent onboardingEvent = onboardingRecords.get(empId);
				String empFName = onboardingEvent != null ? onboardingEvent.getEmpFName() : "Unknown";
				String empLName = onboardingEvent != null ? onboardingEvent.getEmpLName() : "Unknown";

				// Create or update the financial report for this employee
				EmployeeFinancialReportDto report = financialReports.getOrDefault(empId,
						new EmployeeFinancialReportDto(empId, empFName, empLName, 0.0));
				report.setTotalAmountPaid(report.getTotalAmountPaid() + amount);

				financialReports.put(empId, report);
			}
		}

		return financialReports;
	}

	public Map<String, MonthlyAmountReleasedReportDto> getMonthlyAmountReleased() {
		List<EmployeeEvent> events = employeeEventRepository.findAll();

		// Map to hold monthly reports
		Map<String, MonthlyAmountReleasedReportDto> monthlyReports = new HashMap<>();

		// Set to hold unique employees per month
		Map<String, Set<String>> uniqueEmployeesPerMonth = new HashMap<>();

		for (EmployeeEvent event : events) {
			String monthYear = event.getEventDate().getYear() + "-"
					+ String.format("%02d", event.getEventDate().getMonthValue());
			double amount = 0.0;

			// Determine the amount based on the event type
			if (EventType.SALARY.equals(event.getEvent()) || EventType.BONUS.equals(event.getEvent())
					|| EventType.REIMBURSEMENT.equals(event.getEvent())) {
				amount = Double.parseDouble(event.getValue());

				// Update the monthly report
				MonthlyAmountReleasedReportDto report = monthlyReports.getOrDefault(monthYear,
						new MonthlyAmountReleasedReportDto(monthYear, 0.0, 0));
				report.setTotalAmount(report.getTotalAmount() + amount);
				monthlyReports.put(monthYear, report);

				// Track unique employees per month
				uniqueEmployeesPerMonth.computeIfAbsent(monthYear, k -> new HashSet<>()).add(event.getEmpId());
			}
		}

		// Now update the total employees in each monthly report
		for (String monthYear : uniqueEmployeesPerMonth.keySet()) {
			MonthlyAmountReleasedReportDto report = monthlyReports.get(monthYear);
			if (report != null) {
				report.setTotalEmployees(uniqueEmployeesPerMonth.get(monthYear).size());
			}
		}

		return monthlyReports;
	}

	public Map<Integer, List<YearlyFinancialReportDto>> getYearlyFinancialReport() {
		List<EmployeeEvent> events = employeeEventRepository.findAll();

		return events.stream()
				.map(event -> new YearlyFinancialReportDto(event.getEvent().name(), event.getEmpId(),
						event.getEventDate(), event.getValue()))
				.collect(Collectors.groupingBy(eventDto -> eventDto.getEventDate().getYear()));
	}

}
