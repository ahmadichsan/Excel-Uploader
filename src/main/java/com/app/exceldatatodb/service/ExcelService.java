package com.app.exceldatatodb.service;

import org.springframework.web.multipart.MultipartFile;

import com.app.exceldatatodb.response.JsonResponse;

public interface ExcelService {

	public JsonResponse insertExcelToDb(MultipartFile file) throws Exception;
}
