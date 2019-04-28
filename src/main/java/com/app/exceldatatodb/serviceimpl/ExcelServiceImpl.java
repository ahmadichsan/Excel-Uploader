package com.app.exceldatatodb.serviceimpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.app.exceldatatodb.response.JsonResponse;
import com.app.exceldatatodb.service.ExcelService;

@Service
public class ExcelServiceImpl implements ExcelService {

	@Override
	@Transactional(rollbackFor = {Exception.class})
	public JsonResponse insertExcelToDb(MultipartFile file) throws Exception {
		Connection conn = null;
		PreparedStatement sql_statement = null;
		File newFile = null;
		OPCPackage opc = null;
		try {
			newFile = convert(file);
			
			conn = getCon();
			
			if (null == conn) {
				throw new Exception("No connection established");
			}
			
			String query = "INSERT INTO excel (emp_code, emp_name, emp_office, emp_email, created_date) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?);";
			
			conn.setAutoCommit(false);
			sql_statement = conn.prepareStatement(query);
			
			try {
				opc = OPCPackage.open(newFile.getAbsolutePath(), PackageAccess.READ);
				ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(opc);
				XSSFReader xssfReader = new XSSFReader(opc);
				MappingFromXml is = new MappingFromXml(conn, sql_statement);
				StylesTable styles = xssfReader.getStylesTable();
				XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator)xssfReader.getSheetsData();
				int countFound = 0;
				
				while (iter.hasNext()) {
					try (InputStream stream = iter.next()) {
						String sheetName = iter.getSheetName();
						if (sheetName.contentEquals("SHEET_NAME") && countFound == 0) {
							processSheet(styles, strings, new MappingFromXml(conn, sql_statement), stream);
							is.endSheet();
						} else if (!iter.hasNext() && countFound == 0) {
							throw new Exception("Sheet not found");
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new Exception("Error when iterating sheet: " + e.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			if (null != sql_statement) {
				sql_statement.close();
			}
			
			if (null != opc) {
				opc.close();
			}
			
			if (null != conn) {
				conn.close();
			}
			
			if (newFile.exists()) {
				newFile.delete();
			}
		}
		JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setMessage("clear");
		return jsonResponse;
	}
	
	private static Connection getCon() {
		Connection connection = null;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5432/excelreader",
					"postgres",
					"ichsan13");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	private static File convert(MultipartFile file) throws IOException {
		File convFile = null;
		try {
			convFile = new File(file.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
			return convFile;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}
	
	private static void processSheet(StylesTable styles, ReadOnlySharedStringsTable strings, MappingFromXml mappingFromXml, InputStream sheetInputStream) throws Exception, IOException, SAXException {
		try {
			DataFormatter formatter = new DataFormatter();
			InputSource sheetSource = new InputSource(sheetInputStream);
			try {
				XMLReader sheetParser = SAXHelper.newXMLReader();
				ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings, mappingFromXml, formatter, false);
				
				sheetParser.setContentHandler(handler);
				sheetParser.parse(sheetSource);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
