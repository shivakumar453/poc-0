package com.hanover.batch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.hanover.batch.model.Employee;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee>{

	@Override
	public Employee process(Employee emp) throws Exception {
		System.err.println(emp.getEmpId());
		System.err.println(emp.getEmpName());
		System.err.println(emp.getEmpSalary());
		return emp;
	}
}
