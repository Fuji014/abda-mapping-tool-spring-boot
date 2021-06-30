package com.abda.spring.base;

import com.abda.spring.lib.ExcelReader;

public class BaseClass {
	public ExcelReader readerItemList;
	public ExcelReader readerXref;
	public ExcelReader readerTdd;

	public String getMainAxis(int rowPositionMainAxis) {
		String mainAxis = "";
		if (rowPositionMainAxis <= 2) {
			mainAxis = "Sub Coverage";
		} else if (rowPositionMainAxis <= 4) {
			mainAxis = "Coverage";
		} else if (rowPositionMainAxis <= 6) {
			mainAxis = "Contract";
		} else if (rowPositionMainAxis <= 8) {
			mainAxis = "Policy";
		} else {
			mainAxis = "";

		}
		return mainAxis;
	}

	public String getFullMap(String mainAxis, String val, String retaktTag) {
		String fullMapped = "";
		switch (mainAxis.toLowerCase()) {
		case "policy":
			fullMapped = "ABDAPOLICY." + val + "." + retaktTag;
			break;
		case "contract":
			fullMapped = "ABDAPOLPR." + val + "." + retaktTag;
			break;
		case "coverage":
			fullMapped = "ABDAPOLPR." + "ABDACOV." + val + "." + retaktTag;
			break;
		case "sub coverage":
			fullMapped = "ABDAPOLPR." + "ABDACOV." + "ABDACOVCPCO." + val + "." + retaktTag;
			break;
		}
		return fullMapped;
	}

	public void createMappedSheet(int temp, String retaktId, String fullMapped, String parentTag, String productBaseId, boolean isRetaktExist) {
		if (!readerTdd.isSheetExist("Mapped")) {
			readerTdd.addSheet("Mapped");
			readerTdd.addColumn("Mapped", "RETAKTID");
			readerTdd.addColumn("Mapped", "FULL_MAPPED");
			readerTdd.addColumn("Mapped", "PARENT_TAG.TAG_NAME");
			readerTdd.addColumn("Mapped", "PRODUCT_BASE_ID");
			
		}
		readerTdd.setCellDataFillColor("Mapped", "RETAKTID", temp, retaktId, isRetaktExist);
		readerTdd.setCellData("Mapped", "FULL_MAPPED", temp, fullMapped);
		readerTdd.setCellData("Mapped", "PARENT_TAG.TAG_NAME", temp, parentTag);
		readerTdd.setCellData("Mapped", "PRODUCT_BASE_ID", temp, productBaseId);

	}
	
	public void printDetails(String retaktId, String fullMapped, String parentTag, String productBaseId) {
		System.out.println("=========================================================================");
		System.out.println("RETAKT_ID : " + retaktId);
		System.out.println("FULL_MAPPED : " + fullMapped);
		System.out.println("PARENT_TAG.TAG_NAME : " + parentTag);
		System.out.println("PRODUCT_BASE_ID : " + productBaseId);
		System.out.println("=========================================================================");
	}
}
