package com.payroll.system.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payroll.system.model.EmployeeEvent;

@Repository
public interface EmployeeEventRepository extends JpaRepository<EmployeeEvent, Long> {
 // You can define custom query methods here if needed
}
