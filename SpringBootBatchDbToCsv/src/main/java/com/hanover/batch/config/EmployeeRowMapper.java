package com.hanover.batch.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hanover.batch.model.Employee;

public class EmployeeRowMapper implements RowMapper<Employee> {

	@Override
	public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
		Employee emp = new Employee();
		emp.setEmpId(rs.getInt("empid"));
		emp.setEmpName(rs.getString("empname"));
		emp.setEmpSalary(rs.getString("empsalary"));
		return emp;
	}

}
