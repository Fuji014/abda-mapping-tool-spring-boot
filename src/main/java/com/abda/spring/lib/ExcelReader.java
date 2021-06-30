package com.abda.spring.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abda.spring.exception.AbdaToolException;

public class ExcelReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReader.class);

	public String path;
	public FileInputStream fileInput = null;
	public FileOutputStream fileOut = null;
	private XSSFWorkbook workbook = null;
	private XSSFSheet sheet = null;
	private XSSFRow row = null;
	private XSSFCell cell = null;

	public ExcelReader(String path) {
		this.path = path;
		File file = FileUtil.getFile(path);
		try {
			fileInput = new FileInputStream(file);
			workbook = new XSSFWorkbook(fileInput);
//			sheet = workbook.getSheetAt(0);
			fileInput.close();
		} catch (FileNotFoundException e) {
			LOGGER.error("File Not found {}", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error("IOException {}", e.getMessage());
			e.printStackTrace();
		}
	}

	// returns the row count in a sheet
	public int getRowCount(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1) {
			LOGGER.error("Sheet Not found " + sheetName);
			throw new AbdaToolException("Sheet Name Not found " + sheetName);
		} else {
			sheet = workbook.getSheetAt(index);
			int number = sheet.getLastRowNum() + 1;
			return number;
		}
	}

	// returns the data from a cell
	public String getCellData(String sheetName, String colName, int rowNum) {
		try {
			if (rowNum <= 0)
				return "";

			int index = workbook.getSheetIndex(sheetName);
			int col_Num = -1;
			if (index == -1)
				return "";

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(0);

			for (int i = 0; i < row.getLastCellNum(); i++) {
				if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
					col_Num = i;
			}
			if (col_Num == -1)
				return "";

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(rowNum - 1);
			if (row == null)
				return "";
			cell = row.getCell(col_Num);

			if (cell == null)
				return "";

			if (cell.getCellType().name().equals("STRING"))
				return cell.getStringCellValue();

			else if ((cell.getCellType().name().equals("NUMERIC")) || (cell.getCellType().name().equals("FORMULA"))) {

				String cellText = String.valueOf(cell.getNumericCellValue());
				if (DateUtil.isCellDateFormatted(cell)) {
					double d = cell.getNumericCellValue();
					Calendar cal = Calendar.getInstance();
					cal.setTime(DateUtil.getJavaDate(d));
					cellText = (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
					cellText = cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + 1 + "/" + cellText;
				}

				return cellText;
			} else if (cell.getCellType().BLANK != null)
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());
		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}
	}

	// returns the data from a cell
	public String getCellData(String sheetName, int colNum, int rowNum) {
		try {
			if (rowNum <= 0)
				return "";

			int index = workbook.getSheetIndex(sheetName);

			if (index == -1)
				return "";

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				return "";
			
			cell = row.getCell(colNum);
			
			if (cell == null)
				return "";

			
			if (cell.getCellType().name().equals("STRING"))
				return cell.getStringCellValue();
			
			else if ((cell.getCellType().name().equals("NUMERIC")) || (cell.getCellType().name().equals("FORMULA"))) {

				String cellText = String.valueOf(cell.getNumericCellValue());
				if (DateUtil.isCellDateFormatted(cell)) {
					// format in form of M/D/YY
					double d = cell.getNumericCellValue();

					Calendar cal = Calendar.getInstance();
					cal.setTime(DateUtil.getJavaDate(d));
					cellText = (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
					cellText = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cellText;

				}

				return cellText;
			} else if (cell.getCellType().BLANK != null)
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());
		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}
	}

	public boolean setCellData(String sheetName, int colNum, int rowNum, String data) {
		try {
			int index = workbook.getSheetIndex(sheetName);
			sheet = workbook.getSheetAt(index);

			Row r = sheet.getRow(rowNum - 1);
			if (r == null) {
				// First cell in the row, create
				r = sheet.createRow(rowNum - 1);
			}

			Cell c = r.getCell(colNum);
			if (c == null) {
				// New cell
				c = r.createCell(3, CellType.STRING);
			}
			c.setCellValue(data);

			fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}
		return true;
	}

	// returns true if data is set successfully else false
	public boolean setCellDataFillColor(String sheetName, String colName, int rowNum, String data, boolean isRetaktExist) {
		try {
			if (rowNum <= 0)
				throw new AbdaToolException("Invalid Target Row " + sheetName);

			int index = workbook.getSheetIndex(sheetName);
			int colNum = -1;

			if (index == -1)
				throw new AbdaToolException("Sheet Name Not found " + sheetName);

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				if (row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(colName))
					colNum = i;
			}

			if (colNum == -1)
				throw new AbdaToolException("Invalid Target Column " + sheetName);

			sheet.autoSizeColumn(colNum);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				row = sheet.createRow(rowNum - 1);

			cell = row.getCell(colNum);
			if (cell == null)
				cell = row.createCell(colNum);

			CellStyle style = workbook.createCellStyle();
			style = workbook.createCellStyle();
			
			if (!isRetaktExist) {
				style.setFillForegroundColor(IndexedColors.RED.getIndex());
			} else {
				style.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
			}	

			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			cell.setCellStyle(style);
			cell.setCellValue(data);

			fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}
		return true;

	}

	public boolean setCellData(String sheetName, String colName, int rowNum, String data) {
		try {
			if (rowNum <= 0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			int colNum = -1;
			if (index == -1)
				return false;

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				// System.out.println(row.getCell(i).getStringCellValue().trim());
				if (row.getCell(i).getStringCellValue().trim().equalsIgnoreCase(colName))
					colNum = i;
			}

			if (colNum == -1)
				return false;

			sheet.autoSizeColumn(colNum);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				row = sheet.createRow(rowNum - 1);

			cell = row.getCell(colNum);
			if (cell == null)
				cell = row.createCell(colNum);
			
			cell.setCellValue(data);

			fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

			fileOut.close();

		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}
		return true;
	}

	// returns true if sheet is created successfully else false
	public boolean addSheet(String sheetname) {

		FileOutputStream fileOut;
		try {
			workbook.createSheet(sheetname);
			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// returns true if sheet is removed successfully else false if sheet does
	// not exist
	public boolean removeSheet(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1)
			return false;

		FileOutputStream fileOut;
		try {
			workbook.removeSheetAt(index);
			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}
		return true;
	}

	// returns true if column is created successfully
	public boolean addColumn(String sheetName, String colName) {
		// System.out.println("**************addColumn*********************");

		try {
			int index = workbook.getSheetIndex(sheetName);
			if (index == -1)
				return false;

			XSSFCellStyle style = workbook.createCellStyle();

			sheet = workbook.getSheetAt(index);

			row = sheet.getRow(0);
			if (row == null)
				row = sheet.createRow(0);
			
			if (row.getLastCellNum() == -1)
				cell = row.createCell(0);
			else
				cell = row.createCell(row.getLastCellNum());

			cell.setCellValue(colName);
			cell.setCellStyle(style);

			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}

		return true;

	}

	// remove a column and all the contents
	public boolean removeColumn(String sheetName, int colNum) {
		try {
			if (!isSheetExist(sheetName))
				return false;
			sheet = workbook.getSheet(sheetName);
			XSSFCellStyle style = workbook.createCellStyle();
			// style.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
			XSSFCreationHelper createHelper = workbook.getCreationHelper();
			// style.setFillPattern(XSSFCellStyle.NO_FILL);
			for (int i = 0; i < getRowCount(sheetName); i++) {
				row = sheet.getRow(i);
				if (row != null) {
					cell = row.getCell(colNum);
					if (cell != null) {
						cell.setCellStyle(style);
						row.removeCell(cell);
					}
				}
			}
			fileOut = new FileOutputStream(path);
			workbook.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	// find whether sheets exists
	public boolean isSheetExist(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		if (index == -1) {
			index = workbook.getSheetIndex(sheetName.toUpperCase());
			if (index == -1)
				return false;
			else
				return true;
		} else
			return true;
	}

	// returns number of columns in a sheet
	public int getColumnCount(String sheetName) {
		// check if sheet exists
		if (!isSheetExist(sheetName))
			return -1;

		sheet = workbook.getSheet(sheetName);
		row = sheet.getRow(0);

		if (row == null)
			return -1;

		return row.getLastCellNum();

	}

	public int getCellRowNum(String sheetName, String colName, String cellValue) {

		for (int i = 2; i <= getRowCount(sheetName); i++) {
			if (getCellData(sheetName, colName, i).equalsIgnoreCase(cellValue)) {
				return i;
			}
		}
		return -1;

	}

	public int findRow(String sheetName, String cellContent) {
		int index = workbook.getSheetIndex(sheetName);
		sheet = workbook.getSheetAt(index);
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() == CellType.STRING) {
					if (cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
						return row.getRowNum() + 1;
					}
				}
			}
		}
		return 0;
	}

	public String getCellDataBlankCell(String sheetName, int colNum, int rowNum) {
		try {
			if (rowNum <= 0)
				return "";

			int index = workbook.getSheetIndex(sheetName);

			if (index == -1)
				return "";

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				return "";
			cell = row.getCell(colNum);
			if (cell == null)
				return "";

			if (cell.getCellType().name().equals("STRING"))
				return cell.getRawValue();

			else if ((cell.getCellType().name().equals("NUMERIC")) || (cell.getCellType().name().equals("FORMULA"))) {

				String cellText = String.valueOf(cell.getNumericCellValue());
				if (DateUtil.isCellDateFormatted(cell)) {
					double d = cell.getNumericCellValue();

					Calendar cal = Calendar.getInstance();
					cal.setTime(DateUtil.getJavaDate(d));
					cellText = (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
					cellText = cal.get(Calendar.MONTH) + 1 + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cellText;

				}

				return cellText;
			} else if (cell.getCellType().BLANK != null)
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());
		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}
	}

	// Check if cell is blank
	public boolean isCellDataStrikeThrough(String sheetName, int colNum, int rowNum) {
		try {
			if (rowNum <= 0)
				return false;

			int index = workbook.getSheetIndex(sheetName);

			if (index == -1)
				return false;

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				return false;
			cell = row.getCell(colNum);
			if (cell == null)
				return false;
			if (cell.getCellType().name().equals("STRING"))
				return cell.getCellStyle().getFont().getStrikeout();
			else if (cell.getCellType().BLANK != null)
				return false;
			else
				return false;
		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}
	}

	public boolean isCellDataStrikeThrough(String sheetName, String colName, int rowNum) {
		try {
			if (rowNum <= 0)
				return false;

			int index = workbook.getSheetIndex(sheetName);
			int col_Num = -1;
			if (index == -1)
				return false;

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
					col_Num = i;
			}
			if (col_Num == -1)
				return false;

			sheet = workbook.getSheetAt(index);
			row = sheet.getRow(rowNum - 1);
			if (row == null)
				return false;
			cell = row.getCell(col_Num);

			if (cell == null)
				return false;

			if (cell.getCellType().name().equals("STRING"))
				return cell.getCellStyle().getFont().getStrikeout();
			else if (cell.getCellType().BLANK != null)
				return false;
			else
				return false;

		} catch (Exception e) {
			throw new AbdaToolException(e.getMessage());
		}
	}

	public void removeEmptyRows(String sheetName) {
		int index = workbook.getSheetIndex(sheetName);
		sheet = workbook.getSheetAt(index);

		Boolean isRowEmpty = Boolean.FALSE;
		for (int i = 0; i <= sheet.getLastRowNum(); i++) {
			if (sheet.getRow(i) == null) {
				isRowEmpty = true;
				sheet.shiftRows(i + 1, sheet.getLastRowNum() + 1, -1);
				i--;
				continue;
			}
			for (int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {
				if (sheet.getRow(i).getCell(j) == null || sheet.getRow(i).getCell(j).toString().trim().equals("")) {
					isRowEmpty = true;
				} else {
					isRowEmpty = false;
					break;
				}
			}
			if (isRowEmpty == true) {
				sheet.shiftRows(i + 1, sheet.getLastRowNum() + 1, -1);
				i--;
			}
		}
	}

}
