# payroll-system

All Api curls for test the application 


1. File upload and save records.

curl --location 'http://localhost:8095/api/employees/upload' \
--form 'file=@"postman-cloud:///1ef81d0d-f0c7-4150-944c-18b4c52bd9b5"'

2. Get all Employees. 

curl --location 'http://localhost:8095/api/employees' \
--data ''

3. Total number of employees in an organization.

curl --location 'http://localhost:8095/api/employees/count' \
--data ''

4. Month wise following details  
a. Total number of employees joined the organization with employee details like emp id, 
designation, name, surname. 

curl --location 'http://localhost:8095/api/employees/monthly-exits' \
--data ''

b. Total number of employees exit organization with employee details like name, surname. 

curl --location 'http://localhost:8095/api/employees/monthly-joins' \
--data ''

5. Monthly salary report in following format 
a. Month, Total Salary, Total employees

curl --location 'http://localhost:8095/api/employees/monthly-salary-report' \
--data ''

6. Employee wise financial report in the following format 
a. Employee Id, Name, Surname, Total amount paid

curl --location 'http://localhost:8095/api/employees/employee-financial-report' \
--data ''

7. Monthly amount released in following format 
a. Month, Total Amount (Salary + Bonus + REIMBURSEMENT), Total employees 

curl --location 'http://localhost:8095/api/employees/monthly-amount-released' \
--data ''

8. Yearly financial report in the following format 
a. Event, Emp Id, Event Date, Event value

curl --location 'http://localhost:8095/api/employees/yearly-financial-report' \
--data ''
