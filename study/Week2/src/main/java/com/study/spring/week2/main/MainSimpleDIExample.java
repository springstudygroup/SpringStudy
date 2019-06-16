package com.study.spring.week2.main;

import com.study.spring.week2.Company;
import com.study.spring.week2.Department;
import com.study.spring.week2.HumanResourceDept;
import com.study.spring.week2.WebApplicationDept;

public class MainSimpleDIExample {

	public static void main(String[] args) {
		
		Company com = new Company();
		
		com.printDepartmentInfo();
		
		HumanResourceDept hrDept = new HumanResourceDept();
		com = new Company(hrDept);
		com.printDepartmentInfo();
		
		WebApplicationDept webDept = new WebApplicationDept();
		com.setDepartment(webDept);
		com.printDepartmentInfo();
		
		
	}

}
