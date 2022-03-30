//
// server for PUT@UiO Spring 2021
//

package server_group_project;

public class Claims {
	
	// also defined in file server.java
	static final int CLAIMS_ITEMS_ARRAY_SIZE = 5;
	
	String id;
	String numberOfClaims;
	String claimId[];
	String claimDes[];
	String claimPhoto[];
	String claimLocation[];
	String claimStatus[];
	
	public Claims() {
		claimId = new String[CLAIMS_ITEMS_ARRAY_SIZE]; 
		claimDes = new String[CLAIMS_ITEMS_ARRAY_SIZE];
		claimPhoto = new String[CLAIMS_ITEMS_ARRAY_SIZE];
		claimLocation = new String[CLAIMS_ITEMS_ARRAY_SIZE];
		claimStatus = new String[CLAIMS_ITEMS_ARRAY_SIZE];
		for (int i=0; i < CLAIMS_ITEMS_ARRAY_SIZE; i++) {
			claimId[i] = "na";
			claimDes[i] = "na";
			claimId[i] = "na";
			claimPhoto[i] = "na";
			claimLocation[i] = "na";
			claimStatus[i] = "na";
		}
	}
	
	public String getId(){return this.id;}
	
	public String getNumberOfClaims(){return this.numberOfClaims;}
	
	public String getClaimId(int index){return this.claimId[index];}
	
	public String getClaimDes(int index){return this.claimDes[index];}
	
	public String getClaimPhoto(int index){return this.claimPhoto[index];}
	
	public String getClaimLocation(int index){return this.claimLocation[index];}
	
	public String getClaimStatus(int index){return this.claimStatus[index];}
	
	public void setId(String i){this.id = i;}
	
	public void setNumberOfClaims(String nOc){this.numberOfClaims = nOc;}
	
	public void setClaimId(int index, String i){this.claimId[index] = i;}
			
	public void setClaimDes(int index, String des){this.claimDes[index] = des;}
	
	public void setClaimPhoto(int index, String pho){this.claimPhoto[index] = pho;}
	
	public void setClaimLocation(int index, String loc){this.claimLocation[index] = loc;}
	
	public void setClaimStatus(int index, String sta){this.claimStatus[index] = sta;}
	
}
