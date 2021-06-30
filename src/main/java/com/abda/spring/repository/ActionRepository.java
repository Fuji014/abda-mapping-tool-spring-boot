package com.abda.spring.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.abda.spring.base.BaseClass;
import com.abda.spring.domain.ActionDomain;
import com.abda.spring.exception.AbdaToolException;
import com.abda.spring.lib.*;


@Repository
public class ActionRepository extends BaseClass {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionRepository.class);

	private List<ActionDomain> actionDomains = new ArrayList<>();
	private Action action = new Action();

	public List<ActionDomain> fetchAllActions() {
		if (actionDomains.size() == 0) {
			ActionDomain actionDomain = new ActionDomain();
			actionDomain.setRetaktIdLetter("");
			actionDomain.setProductIdLetter("");
			actionDomain.setMainAxisLetter("");
			actionDomain.setTddRetaktIdColumnName("");
			actionDomain.setProductMappingXls("");
			actionDomain.setTddXls("");
			actionDomains.add(actionDomain);
		}
		return actionDomains;
	}

	public void create(ActionDomain actionDomain) {
		long startTime = System.nanoTime();
		try {
			// Create reader object
			readerItemList = new ExcelReader(actionDomain.getProductMappingXls());
			readerXref = new ExcelReader(actionDomain.getXrefXls());
			readerTdd = new ExcelReader(actionDomain.getTddXls());

			// Initialize variable
			int rowPositionProductId = 0;
			int rowPositionMainAxis = 0;
			int totalRowCountTdd = 0;
			String productBaseId = "";
			String mainAxis = "";
			String abbrTrim = "";
			String retaktId = "";
			String parentTag = "";
			String fullMapped = "";
			boolean isRetaktExist = false;
			boolean isRetaktIDStrikeThrough = false;
			boolean isTddRetaktIdExist = false;

			// Get data from front end
			int colRetaktID = action.toNumber(actionDomain.getRetaktIdLetter()) - 1;
			int colRetaktTag = action.toNumber(actionDomain.getRetaktTagLetter()) - 1;
			int colProductID = action.toNumber(actionDomain.getProductIdLetter()) - 1;
			int colMainAxis = action.toNumber(actionDomain.getMainAxisLetter()) - 1;
			int colRetaktIdTdd = action.toNumber(actionDomain.getTddRetaktIdColumnName()) - 1;
			int colAbdaModelFieldTdd = action.toNumber(actionDomain.getTddAbdaModelField()) - 1;
			String productMappingTargetSheet = actionDomain.getProductMappingTargetSheet();
			String tddTargetSheet = actionDomain.getTddTargetSheet();

			// Index 0 is occupied by column name
			int temp = 1;

			// Get total number of row in tdd file
			totalRowCountTdd = readerTdd.getRowCount(tddTargetSheet);

			// Loop to all rows in tdd file
			for (int i = 0; i < readerTdd.getRowCount(tddTargetSheet); i++) {
				
				// Get retakt id in tdd file
				retaktId = readerTdd.getCellData(tddTargetSheet, colRetaktIdTdd, i) ;
				
				// Filter values using regex and by checking strikethrough
				if (Pattern.matches("(?=^LB)([a-zA-Z0-9]{6})", retaktId) && readerTdd.isCellDataStrikeThrough(tddTargetSheet, colRetaktIdTdd, i)) {

					// Increment temp value if condition satisfy
					temp++;

					// Find the position of retakt ID in the product mapping file.
					int rowPositionRetaktID = readerItemList.findRow(productMappingTargetSheet, retaktId);

					// Create variable and assign the value of retakt tag
					String retaktTag = readerItemList.getCellData(productMappingTargetSheet, colRetaktTag, rowPositionRetaktID);
					
					// Check if retakt tag is not empty
					if (!retaktTag.isEmpty()) {
						
						isRetaktExist = true;
						
						// Get retrakt product base id
						for (int j = rowPositionRetaktID; j > 0; j--) {
							if (!readerItemList.getCellData(productMappingTargetSheet, colProductID, j).isEmpty()) {
								rowPositionProductId = j;
								productBaseId = readerItemList.getCellData(productMappingTargetSheet, colProductID, j);
								break;
							}
						}

						// Get main axis
						for (int k = colMainAxis; k > 0; k--) {
							if (!readerItemList.getCellData(productMappingTargetSheet, k, rowPositionProductId).isEmpty()) {
								rowPositionMainAxis = (colMainAxis + 1) - k;
								abbrTrim = readerItemList.getCellData(productMappingTargetSheet, k, rowPositionProductId).substring(0, 3).replaceAll("\\s+", "");
								break;
							}
						}

						// Get main axis
						mainAxis = getMainAxis(rowPositionMainAxis);

						// Get parent tag using abbr
						String val = readerXref.getCellData(productMappingTargetSheet, mainAxis, readerXref.findRow(productMappingTargetSheet, action.transformText(abbrTrim)));
						
						// Check abbr again if value not exist in the previous step
						if (val.isEmpty()) {
							
							String leftRetakID = readerItemList.getCellData(productMappingTargetSheet, colRetaktID - 1, rowPositionProductId);
							String rightRetakID = readerItemList.getCellData(productMappingTargetSheet, colRetaktID + 1, rowPositionProductId);
							
							if (!leftRetakID.isEmpty() && leftRetakID.length() > 0) 
								val = readerItemList.getCellData(productMappingTargetSheet, colRetaktID - 1, rowPositionProductId).substring(0, 3).replaceAll("\\s+", "").replaceAll("[^a-zA-Z\\s]", "");
	
							val = readerXref.getCellData(productMappingTargetSheet, mainAxis, readerXref.findRow(productMappingTargetSheet, action.transformText(val)));
							
							if (val.isEmpty()) {
								if (!rightRetakID.isEmpty() && rightRetakID.length() > 0) 
									val = readerItemList.getCellData(productMappingTargetSheet, colRetaktID + 1, rowPositionProductId).substring(0, 3).replaceAll("\\s+", "").replaceAll("[^a-zA-Z\\s]", "");
							   else 
									val = "";
								val = readerXref.getCellData(productMappingTargetSheet, mainAxis, readerXref.findRow(productMappingTargetSheet, action.transformText(val)));
							}
						}

						// Main axis checker
						fullMapped = getFullMap(mainAxis, val, retaktTag);
						
						// Concatenate parent tag
						parentTag = val + "." + retaktTag;
						
						// Print details
						printDetails(retaktId, fullMapped, parentTag, productBaseId);
						
						// Check if abda model field is empty
						if (colAbdaModelFieldTdd > 0)
							readerTdd.setCellData(tddTargetSheet, colAbdaModelFieldTdd, i, val + "." + retaktTag + " " + '"' + productBaseId + '"');

					} 
					
					// Write data in excel file
					createMappedSheet(temp, retaktId, fullMapped, parentTag, productBaseId, isRetaktExist);
				}
			}
			
		} catch (Exception e) {
			
			throw new AbdaToolException(e.getMessage());
			
		}

		long endTime = System.nanoTime();
		
		System.out.println("Execute : {} ns " + startTime + "-" + endTime);

	}

}
