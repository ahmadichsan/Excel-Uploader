package com.app.exceldatatodb.serviceimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

public class MappingFromXml implements SheetContentsHandler {

	private Date today = new Date();
	private java.sql.Timestamp now = new java.sql.Timestamp(today.getTime());
	
	private int lineNumber = 0;
	private int countHead = 0;
	private int col0 = -1;
	private int col1 = -1;
	private int col2 = -1;
	private int col3 = -1;
	private boolean isDataExist = true;
	private Connection conn = null;
	private PreparedStatement sql_statement = null;
	
	public MappingFromXml(Connection conn, PreparedStatement sql_statement) {
		this.conn = conn;
		this.sql_statement = sql_statement;
	}
	
	@Override
	public void startRow(int rowNum) {
		try {
			isDataExist = true;
			
			col0 = -1;
			col1 = -1;
			col2 = -1;
			col3 = -1;
			
			lineNumber = rowNum;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in start row with message: " + e.getMessage());
		}
		
	}

	@Override
	public void endRow(int rowNum) {
		if (rowNum == 0 && countHead < 4) {
			throw new RuntimeException("File missed one/more column header. Please check again the column header in your file.");
		}
		
		if (rowNum > 0) {
			try {
				if (col0 == -1 && col1 == -1 && col2 == -1 && col3 == -1) {
					isDataExist = false;
				}
				
				if (isDataExist) {
					sql_statement.setTimestamp(5, now);
					
					if (col0 == -1) {
						sql_statement.setString(1, "");
					}
						
					if (col1 == -1) {
						sql_statement.setString(2, "");
					}
					
					if (col2 == -1) {
						sql_statement.setString(3, "");
					}
					
					if (col3 == -1) {
						sql_statement.setString(4, "");
					}
					
					sql_statement.addBatch();
				}
				
				if (rowNum % 1000 == 0) {
					try {
						sql_statement.executeBatch();
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException("Error in execute batch with message: " + e.getMessage());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Error in add batch with message: " + e.getMessage());
			}
		}
	}

	@Override
	public void cell(String cellReference, String formattedValue, XSSFComment comment) {
		try {
			int columnIndex = (new CellReference(cellReference)).getCol();
			
			if (lineNumber == 0) {
				++countHead;
				switch (columnIndex) {
					case 0:
						if (!formattedValue.contentEquals("EMP_CODE")) {
							throw new RuntimeException("Column A is supposed to be EMP_CODE instead of: " + formattedValue);
						}
						break;
					case 1:
						if (!formattedValue.contentEquals("EMP_NAME")) {
							throw new RuntimeException("Column B is supposed to be EMP_NAME instead of: " + formattedValue);
						}
						break;
					case 2:
						if (!formattedValue.contentEquals("EMP_OFFICE")) {
							throw new RuntimeException("Column C is supposed to be EMP_OFFICE instead of: " + formattedValue);
						}
						break;
					case 3:
						if (!formattedValue.contentEquals("EMP_EMAIL")) {
							throw new RuntimeException("Column D is supposed to be EMP_EMAIL instead of: " + formattedValue);
						}
						break;
				}
			}
			
			if (lineNumber > 0) {
				switch (columnIndex) {
					case 0:
						col0 = 1;
						sql_statement.setString(1, formattedValue);
						break;
					case 1:
						col1 = 1;
						sql_statement.setString(2, formattedValue);
						break;
					case 2:
						col2 = 1;
						sql_statement.setString(3, formattedValue);
						break;
					case 3:
						col3 = 1;
						sql_statement.setString(4, formattedValue);
						break;
				}
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e.getMessage());
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void headerFooter(String text, boolean isHeader, String tagName) {
		// TODO Auto-generated method stub
		
	}
	
	void endSheet() throws SQLException {
		try {
			sql_statement.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error in executing remaining data, with error message: " + e.getMessage());
		}
	}

}
