package com.app.exceldatatodb.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "excel")
public class ExcelDomain {

	private Long id;
	private String empCode;
	private String empName;
	private String empOffice;
	private String empEmail;
	private Date createdDate;

	public ExcelDomain() {
		
	}

	public ExcelDomain(Long id, String empCode, String empName, String empOffice, String empEmail, Date createdDate) {
		super();
		this.id = id;
		this.empCode = empCode;
		this.empName = empName;
		this.empOffice = empOffice;
		this.empEmail = empEmail;
		this.createdDate = createdDate;
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "emp_code", nullable = false)
	public String getEmpCode() {
		return empCode;
	}

	public void setEmpCode(String empCode) {
		this.empCode = empCode;
	}

	@Column(name = "emp_name", nullable = false)
	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	@Column(name = "emp_office", nullable = false)
	public String getEmpOffice() {
		return empOffice;
	}

	public void setEmpOffice(String empOffice) {
		this.empOffice = empOffice;
	}

	@Column(name = "emp_email", nullable = false)
	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}

	@Column(name = "created_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
