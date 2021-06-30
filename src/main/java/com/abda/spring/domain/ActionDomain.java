package com.abda.spring.domain;

public class ActionDomain {

	private String productMappingXls;
	private String tddXls;
	private String xrefXls;
	private String retaktIdLetter;
	private String retaktTagLetter;
	private String productIdLetter;
	private String mainAxisLetter;
	private String tddRetaktIdColumnName;
	private String productMappingTargetSheet;
	private String tddTargetSheet;
	private String tddAbdaModelField;
	private String error;
	
	
	public String getXrefXls() {
		return xrefXls;
	}

	public void setXrefXls(String xrefXls) {
		this.xrefXls = xrefXls;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getTddAbdaModelField() {
		return tddAbdaModelField;
	}

	public void setTddAbdaModelField(String tddAbdaModelField) {
		this.tddAbdaModelField = tddAbdaModelField;
	}

	public String getProductMappingXls() {
		return productMappingXls;
	}

	public void setProductMappingXls(String productMappingXls) {
		this.productMappingXls = productMappingXls;
	}

	public String getTddXls() {
		return tddXls;
	}

	public void setTddXls(String tddXls) {
		this.tddXls = tddXls;
	}

	public String getRetaktTagLetter() {
		return retaktTagLetter;
	}

	public String getProductMappingTargetSheet() {
		return productMappingTargetSheet;
	}

	public void setProductMappingTargetSheet(String productMappingTargetSheet) {
		this.productMappingTargetSheet = productMappingTargetSheet;
	}

	public String getTddTargetSheet() {
		return tddTargetSheet;
	}

	public void setTddTargetSheet(String tddTargetSheet) {
		this.tddTargetSheet = tddTargetSheet;
	}

	public void setRetaktTagLetter(String retaktTagLetter) {
		this.retaktTagLetter = retaktTagLetter;
	}

	public String getRetaktIdLetter() {
		return retaktIdLetter;
	}

	public void setRetaktIdLetter(String retaktIdLetter) {
		this.retaktIdLetter = retaktIdLetter;
	}

	public String getProductIdLetter() {
		return productIdLetter;
	}

	public void setProductIdLetter(String productIdLetter) {
		this.productIdLetter = productIdLetter;
	}

	public String getMainAxisLetter() {
		return mainAxisLetter;
	}

	public void setMainAxisLetter(String mainAxisLetter) {
		this.mainAxisLetter = mainAxisLetter;
	}

	public String getTddRetaktIdColumnName() {
		return tddRetaktIdColumnName;
	}

	public void setTddRetaktIdColumnName(String tddRetaktIdColumnName) {
		this.tddRetaktIdColumnName = tddRetaktIdColumnName;
	}

}
