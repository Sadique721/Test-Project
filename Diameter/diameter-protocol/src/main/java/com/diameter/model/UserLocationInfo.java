package com.diameter.model;

public class UserLocationInfo {

    private String geoType;
    private String mcc;
    private String mnc;
    private String tac;
    private String eci;
    private String enodebId;
    private String cellId;
    
	public String getGeoType() {
		return geoType;
	}
	public void setGeoType(String geoType) {
		this.geoType = geoType;
	}
	public String getMcc() {
		return mcc;
	}
	public void setMcc(String mcc) {
		this.mcc = mcc;
	}
	public String getMnc() {
		return mnc;
	}
	public void setMnc(String mnc) {
		this.mnc = mnc;
	}
	public String getTac() {
		return tac;
	}
	public void setTac(String tac) {
		this.tac = tac;
	}
	public String getEci() {
		return eci;
	}
	public void setEci(String eci) {
		this.eci = eci;
	}
	public String getEnodebId() {
		return enodebId;
	}
	public void setEnodebId(String enodebId) {
		this.enodebId = enodebId;
	}
	public String getCellId() {
		return cellId;
	}
	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserLocationInfo [geoType=");
		builder.append(geoType);
		builder.append(", mcc=");
		builder.append(mcc);
		builder.append(", mnc=");
		builder.append(mnc);
		builder.append(", tac=");
		builder.append(tac);
		builder.append(", eci=");
		builder.append(eci);
		builder.append(", enodebId=");
		builder.append(enodebId);
		builder.append(", cellId=");
		builder.append(cellId);
		builder.append("]");
		return builder.toString();
	}
}
