package com.app.exceldatatodb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.exceldatatodb.domain.ExcelDomain;

@Repository
public interface ExcelDao extends JpaRepository<ExcelDomain, Long> {

	
}
