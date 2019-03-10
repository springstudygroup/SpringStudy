package com.study.spring.week2;

public class Company {
	// Company 객체는 printDepartment() 메서드에서 회사 정보를 출력하기 위해서 Department 객체의 필요하다.
	// Company 객체는 Department 객체의 의존(Dependency) 한다. 
	private Department department;
	
	public Company() {
		// 의존할 객체를 직접 생성하는 방식 (DI)
		this.department = new Department(); 
	}
	
	public Company(Department department) {		
		// 의존할 객체를 직접 외부로부터 주입받는 방식 (DI)
		this.department = department;
	}
	
	public void setDepartment(Department department) {
		// 의존할 객체를 직접 외부로부터 주입받는 방식 (DI)
		this.department = department;
	}
	
	public void printDepartmentInfo() {
		department.printDepartmentInfo();
	}
	
	
}
