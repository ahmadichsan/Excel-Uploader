package com.app.exceldatatodb.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.exceldatatodb.dao.ExcelDao;
import com.app.exceldatatodb.domain.ExcelDomain;
import com.app.exceldatatodb.response.JsonResponse;
import com.app.exceldatatodb.service.ExcelService;

@RestController
@RequestMapping("/excel")
public class ExcelController {

	@Autowired
	ExcelService excelService;
	
	@Autowired
	ExcelDao excelDao;
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAll(){
		List<ExcelDomain> excelDomain = null;
		try {
			excelDomain = excelDao.findAll();
		} catch (Exception e) {
			List<Object> error = new ArrayList<>();
			error.add(1, e.getMessage());
			error.add(2, e.getStackTrace());
			error.add(3, e.getCause());
			return ResponseEntity.badRequest().body(error);
		}
		return new ResponseEntity<>(excelDomain, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> insertExcelToDb(@RequestParam ("file") MultipartFile file) throws IOException {
		JsonResponse jsonResponse;
		try {
			if (null == file) {
				throw new Exception("Please select a file to be uploaded");
			}
			
			Date startDate = new Date();
			
			jsonResponse = excelService.insertExcelToDb(file);
			
			System.out.println(startDate);
			System.out.println(new Date());
			
			return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
