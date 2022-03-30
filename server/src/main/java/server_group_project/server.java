//
// server for PUT@UiO Spring 2021
//

package server_group_project;


import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.System.out;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.util.Base64;
import java.util.Scanner;

import javax.imageio.ImageIO;


public class server {
	
	String userIdWithChangedClaimStatus = null;
	
	Gson gson;
	
	static final int PERSON_ARRAY_SIZE = 4;
	Person personArray[];
	
	static final int CLAIMS_ARRAY_SIZE = 4;
	Claims claimArray[];
	
	// also defined in files Claims.java and serverController.java
	static final int CLAIMS_ITEMS_ARRAY_SIZE = 5;
	
	public server() {}
	
	public void start() {
	    // get the pid of this process
	    String vmName = ManagementFactory.getRuntimeMXBean().getName();
	    int p = vmName.indexOf("@");
	    String pid = vmName.substring(0, p);
	    System.out.println("pid of this processis:"+pid);
	    
	    // get the IP of this process
	    InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			 System.out.println("IP Address:- " + inetAddress.getHostAddress());
		     System.out.println("Host Name:- " + inetAddress.getHostName());
		} catch (UnknownHostException e) {
			System.out.println("FATAL ERROR when getting the IP address of this process");
			e.printStackTrace();
		}
       
		System.out.println("server.start> start");
		
		//Gson gson = new Gson();
		gson = new GsonBuilder().setPrettyPrinting().create();  //Toggle comment for Pretty print JSON
		
		// create in-memory data structure to store the users read to be read from person.json
		personArray = new Person[PERSON_ARRAY_SIZE];
		
		// read and parse the contents of the file person.json into personArray
		parseFilePerson("./data/person.json");
		
		// create in-memory data structure to store the users read to be read from claim.json
		claimArray = new Claims[CLAIMS_ARRAY_SIZE];
		
		// read and parse the contents of the file claim.json into claimnArray
		parseFileClaim("./data/claim.json");
		
		// debug_print_claimArray (claimArray, "called from server.start");
		
		getInputFromConsole();
		
		System.out.println("server.start> end");
		
	}
	
	// ================================================================================	
	// Called from server.java to get commands from
	// the console; in fact, there is only one command
	// being supported for the moment which is
	// to set the status of a claim.
	public void getInputFromConsole() {
       System.out.println("server.getInputFromConsole> start");

	   Scanner inputScanner = new Scanner(System.in);
	   String line;
	   boolean FLAG_EXIT = false;
	   while (!FLAG_EXIT && (line = inputScanner.nextLine().trim()) != null) {
	      if (line.equals("")) {
	         printCursor("> ");
	         continue;
	      }
	      String[] tokens = line.split(" ");
	      String username;
	      int claimId;
	      switch (tokens[0].trim()) {
	         case "help":
	            printSetStatusHelp();
	            break;
	            		
	         case "setStatus":
	            if (tokens.length < 4) {
	               System.out.println("Not enough arguments." + askHelpString());
	                  break;
	            }
	            username = tokens[1].trim();
	            try {
	               claimId = Integer.parseInt(tokens[2].trim());
	            } catch (NumberFormatException e) {
	               System.out.println("ERROR: claimId must be an integer." + askHelpString());
	               break;
	            }
	            String status = tokens[3].trim().toLowerCase();
	                   
	            // get userId given the username (which is the email)
	            String userId = getUserIdGivenTheUsername(username);
	            System.out.println("userId = " + userId);
	                    
	            int userIdInt = Integer.parseInt(userId);
	            claimArray[userIdInt].getClaimStatus(claimId);
	            userIdWithChangedClaimStatus = String.valueOf(userId);
	            claimArray[userIdInt].setClaimStatus(claimId, status); // claim status
	            // write all the claims from claimArray into the file
	    		writeFileClaim (claimArray,"./data/claim.json");
	    		
	    		System.out.println("getInputFromConsole.username=" + username);
	            System.out.println("getInputFromConsole.claimId =" + claimId);
	            System.out.println("getInputFromConsole.status  =" + status);
	            System.out.println("getInputFromConsole.userIdWithChangedClaimStatus  =" + userIdWithChangedClaimStatus);
	            
	            //String oldCaimStatus = claimArray[id].getClaimStatus(i);
	            //if (updateClaimSta.equals(oldCaimStatus)) changedClaimStatus = true;
	            //claimArray[id].setClaimStatus(i, updateClaimSta); // claim status
	            //userIdWithChangedClaimStatus = String.valueOf(userId);
	                    
	            break;
	                    
	         case "quit":
	            FLAG_EXIT = true;
	            break;
	                    
	         default:
	            System.out.println("Invalid command." + askHelpString());
	     }
	        
	     printCursor("> ");
      }

	     inputScanner.close();
	     System.out.println("Terminating server");
	     System.exit(0);

	     System.out.println("server.getInputFromConsole> end");
	}

	private void printCursor(String prompt) {
	     System.out.print(prompt);
	}

	private String askHelpString() {
	     return " Type \"help\" for more info.";
	}
	    
	private void printSetStatusHelp() {
		System.out.println("You can set claim statuses using this command line");
		System.out.println("Sintax: setStatus <int:username> <int:claimId> <String:status>");
		System.out.println("Example:");
		System.out.println("> setStatus joe@gmail.com 0 accepted");
	}
		    
	 
	// called from getInputFromConsole to get the userId from
	// the username entered which is in fact an email
	public String getUserIdGivenTheUsername(String username) {
	   String ret = null;
	
	   for (int i = 0; i < PERSON_ARRAY_SIZE; i++) {
		if (username.equals(personArray[i].getEmail())) {ret = personArray[i].getId(); return ret;}
	   }
	
	   return null;
	}
	
	// ================================================================================	
	// Called from serverController.java when a POST call
	// is done for a login in done in the smartphone in which
	// there is no local data (in the smartphone) available
	Person methodPostRemoteLogin (String em, String ph) {
		System.out.println("server.methodPostRemoteLogin> start");	
		Person pp = null;
		pp = getInMemoryPerson(em);
		if (pp == null) {System.out.println("server.methodPostRemoteLogin> FATAL ERROR: Person is null");}
		else { 
			String ppEmail = pp.getEmail();
			String ppPh = pp.getPassHash();
			if (ppEmail.equals(em) && ppPh.equals(ph)) {
				System.out.println("server.methodPostRemoteLogin> email and passwd are the same");
			}
			else {
				System.out.println("server.methodPostRemoteLogin> email and passwd are NOT the same");
				pp = null;
			}
		}
		System.out.println("server.methodPostRemoteLogin> start");	
		return pp;
	}
	
	// ================================================================================	
	// Called from serverController.java when a POST call
	// is done to change the passwd done in the smartphone 
	public String changePasswd(String em, String np, String ph) {
				
		System.out.println("server.changePasswd> start");	
		
		
	   	//String toBeWritten = "passClear:" + ppPc + "," + "passHash:" + ppPh;
	   	//System.out.println("server.changePasswd> toBeWritten="+toBeWritten);
	   	
	   	// update the in-memory data structure personArray with
	   	// the new passwd (both in clear and its hash code) 
	   	updateInMemoryPerson(em, np, ph);
	   	
	    // only for debug purposes 	
	   	//debugPrintPersonArray();
	   	
		// write the all personArray in the
		// file person.json corresponding 
	 	writeFilePerson (personArray,"./data/person.json");
		
		System.out.println("server.changePasswd> end");	
		
		return "OK";
	}
	
	
	// ================================================================================	
	// update the passwd in the personArray 
	// in-memory data structure
	public void updateInMemoryPerson(String email, String newPassClear, String newPassHash) {
			int i = 0;
			int max = personArray.length;
			
			System.out.println("server.updateInMemoryPerson> start");	
			
			for (i = 0; i<max; i++) {
				if (personArray[i].getEmail().equals(email) == true) {
					personArray[i].passClear = newPassClear;
					personArray[i].passHash = newPassHash;
					return;
				}
			}
			
			System.out.println("server.updateInMemoryPerson> end");	
		}
	
	
	// ================================================================================	
	// this method is called from getMethodMyClaims
	// in the serverControllerClass when there is
	// call from the smartphone to get the claims for
	// a specific user (the user who is logged in)
	public Claims getMyClaims(String id) {
				
		int i, index=-1;
		Claims cl = null;
		
		System.out.println("server.getMyClaims> start");	
			
		// find the claimArray entry corresponding to the user identified by id
		int max = claimArray.length;
		for (i=0; i < max;i++) {
			// equals() method compares the two given strings based on the content of the string
			// if any character is not matched, it returns false; if all characters are matched, it returns true.
			String indexFromClaimArray = claimArray[i].getId();
			if (id.contentEquals(indexFromClaimArray)) {
				index = i;
				break;
			} 
		}
				
		// if there are no claims for the user return immediately and print and
		// error as there should always be an entry for any existing user
		if (index == -1) {
			System.out.println("getMyClaims> FATAL ERROR: there are no claims for the user indicated="+index);
			return cl;
		}
				
		//index is the entry for the current user
		int numberOfClaims = Integer.valueOf(claimArray[index].getNumberOfClaims());
			
		String[] cIdAux = new String[CLAIMS_ITEMS_ARRAY_SIZE];
		String[] cDeAux = new String[CLAIMS_ITEMS_ARRAY_SIZE];
		String[] sTsAux = new String[CLAIMS_ITEMS_ARRAY_SIZE];
		String[] locAux = new String[CLAIMS_ITEMS_ARRAY_SIZE];
		String[] staAux = new String[CLAIMS_ITEMS_ARRAY_SIZE];
		
		// copy the claims for a specific user (identified by id)
		for (i=0; i<numberOfClaims;i++) {
			cIdAux[i] = claimArray[index].getClaimId(i); // claim identifiers
			cDeAux[i] = claimArray[index].getClaimDes(i); // claim descriptions
			sTsAux[i] = claimArray[index].getClaimPhoto(i); // claim photo
			locAux[i] = claimArray[index].getClaimLocation(i); // claim locations
			staAux[i] = claimArray[index].getClaimStatus(i); // claim status
		}
		cl = claimArray[index];	
		
		//debug_print_claimArray(claimArray, "called from getMyClaims");
		
		System.out.println("server.getMyClaims> end");	
		return cl;
	}
	
	// ================================================================================	
	// Called from serverController.java when a POST call
	// is done resulting from a registration of a new claim
	// done in the smartphone; indexUpdateClaim is in fact equal to nOc
	public String insertNewClaim (String userId, String indexUpdateClaim, String newClaimDes, String newClaimPho, String newClaimLoc, String newClaimSta) {
			
		System.out.println("server.insertNewClaim> start");	
			
		String nOc = indexUpdateClaim;
		int i = Integer.valueOf(nOc);
		int userIdInt = Integer.parseInt(userId);
		int maxClaimIdInt = maxClaimIdInt(userIdInt);
			
		if (i != maxClaimIdInt || i >= CLAIMS_ITEMS_ARRAY_SIZE) {
	   		System.out.println("server.postInsertNewClaim FATAL ERROR with wrong indexUpdateClaim");
	   		return "NOK";
	   	}
			
		int id = Integer.valueOf(userId);
		String auxNoC =  Integer.toString(i+1);
		    
			
		// insert new claim into the existing claimArray
		// in its entry corresponding to the entry which
		// is the user
		claimArray[id].setId(userId);
		claimArray[id].setNumberOfClaims(auxNoC);
		claimArray[id].setClaimId(i, indexUpdateClaim);
		claimArray[id].setClaimDes(i, newClaimDes); // claim descriptions
		claimArray[id].setClaimPhoto(i, newClaimPho); // claim photo
		claimArray[id].setClaimLocation(i, newClaimLoc); // claim location
		claimArray[id].setClaimStatus(i, newClaimSta); // claim status
		userIdWithChangedClaimStatus = String.valueOf(userId);
  
	    // write all the claims from claimArray into the file
		writeFileClaim (claimArray,"./data/claim.json");
				
		System.out.println("server.insertNewClaim> end");	
			
		return "OK";
				
	}
		
	// ================================================================================	
	// Called from serverController.java when a POST call
	// is done resulting from a update of an existing claim
	// done in the smartphone.
	public String updateClaim(String userId, String indexUpdateClaim, String updateClaimDes, String updateClaimPho, String updateClaimLoc, String updateClaimSta) {
				
		System.out.println("server.updateClaim> start");	
				
		int id = Integer.valueOf(userId);
		int i = Integer.valueOf(indexUpdateClaim);
		
		if (i >= CLAIMS_ITEMS_ARRAY_SIZE) {
			System.out.println("server.updateClaim FATAL ERROR with indexUpdateClaim equal or bigger than CLAIMS_ITEMS_ARRAY_SIZE");
			return "NOK";	   	
		}
			
		// update an existing claim in the claimArray
		// in its entry corresponding to the entry which
		// is the user
		claimArray[id].setId(userId);
		//claimArray[id].setNumberOfClaims(auxNoC);
		claimArray[id].setClaimId(i, indexUpdateClaim);
		claimArray[id].setClaimDes(i, updateClaimDes); // claim descriptions
		claimArray[id].setClaimPhoto(i, updateClaimPho); // claim descriptions
		claimArray[id].setClaimLocation(i, updateClaimLoc); // claim locations
		claimArray[id].setClaimStatus(i, updateClaimSta); // claim status
		userIdWithChangedClaimStatus = String.valueOf(userId);
		
		// debug_print_claimArray(claimArray, "called from updateClaim");
			
		// write all the claims from claimArray into the file
		writeFileClaim (claimArray,"./data/claim.json");
		
		System.out.println("server.updateClaim> end");	
		
		return "OK";
			
	}
	
	// ================================================================================	
	// store an image (last argument) into a file with
	// the name fileName; this method is invoked from 
	// postMethodUploadPhoto in serverController.java
	public String uploadPhoto (String userId, String claimId, String fileName, String imageStringBase64) {
		
		System.out.println("server.uploadPhoto> start");	
		
		System.out.println("server.uploadPhoto with userId="+userId);
		System.out.println("server.uploadPhoto with claimId="+claimId);
		System.out.println("server.uploadPhoto with fileName="+fileName);	
		System.out.println("server.uploadPhoto with imageStringBase64="+imageStringBase64);
		    
		// write the image into the file
		writeImageStringBase64IntoFile (fileName, imageStringBase64);
		
		System.out.println("server.uploadPhoto> end");
		
		return "OK";
		
	}
	
	// ===============================================================================================
    // write a chosen file locally into a file with a filename;
	// note that this method is used to write an image into a file
	// being the image received from the client as String
    public void writeImageStringBase64IntoFile(String fileName, String imageStringBase64) {
    	System.out.println("server.writeImageStringBase64IntoFile> start");	
       
    	try {
    		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
    	    writer.write(imageStringBase64);
    	    writer.close();
            System.out.println("server.writeImageStringBase64IntoFile> imageStringBase64="+imageStringBase64);
    	}          
        catch (IOException e) {
           System.out.println("server.writeImageStringBase64IntoFile> FATAL ERROR when writing imageStringBase64 in file");
           e.printStackTrace();
        }
    	
    	System.out.println("server.writeImageStringBase64IntoFile> end");	
    }
    
    
    // ================================================================================	
 	// get an image from the file with
 	// the name fileName; this method is invoked from 
 	// getMethodDownloadPhoto in serverController.java
    public String downloadPhoto(String fileName) {
    	String ret = null;
    	
    	System.out.println("server.downloadPhoto> start");	
    	
    	System.out.println("server.downloadPhoto with fileName="+fileName);	
    	
    	// read the contents of the file fileName
    	try {
			ret = readFileAsString(fileName);	
			System.out.println("server.downloadPhoto with ret="+ret);	
		 }
	     catch (JsonIOException       e) {e.printStackTrace();} 
	     catch (JsonSyntaxException   e) {e.printStackTrace();}
		 catch (Exception             e) {e.printStackTrace();}		
	
    	System.out.println("server.downloadPhoto> end");
    	
    	return ret;
    }
    
	
	////////////////////////////////////////////////////////////////////////////////
	// AUXILIARY METHODS
	///////////////////////////////////////////////////////////////////////////////
	
	// ================================================================================	
	// auxiliary method with an obvious functionality
	public int maxUserIdInt() {
		return PERSON_ARRAY_SIZE;
	}
	
	// ================================================================================	
	// auxiliary method with an obvious functionality
	public int maxClaimIdInt(int userIdInt) {
		String maxNumberOfClaims = claimArray[userIdInt].getNumberOfClaims();
		int maxNumberOfClaimsInt = Integer.parseInt(maxNumberOfClaims);
		return maxNumberOfClaimsInt;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// METHODS THAT READ THE EXISTING PERSON.JSON AND CLAIMS.JSON FILES
	// INTO A IN-MEMORY DATA STRUCTURE WHEN THE SERVER STARTS RUNNING
	/////////////////////////////////////////////////////////////////////////////////
	
	// ================================================================================	
	// read file ./data/person.json which contains
	// a list of persons in Json and create 
	// a in-memory data structure with the contentsstart> claimArray.length=
	// of the JSON file person.json (fileName)
	public void parseFilePerson(String fileName) {		       		
		System.out.println("server.parseFilePerson> start");
		try {
			String data = readFileAsString(fileName);				
			personArray = gson.fromJson(data, Person[].class); // Convert JSON String to personArray
		 }
	     catch (JsonIOException       e) {e.printStackTrace();} 
	     catch (JsonSyntaxException   e) {e.printStackTrace();}
		 catch (Exception             e) {e.printStackTrace();}		
		System.out.println("server.parseFilePerson> end");
	}
	
	// ================================================================================	
	// read a file with a name indicated in the
	// argument and returns its contents as a string
	public static String readFileAsString(String fileName) { 
		System.out.println("server.readFileAsString> start");
		String data = ""; 
		try {data = new String(Files.readAllBytes(Paths.get(fileName)));}
		catch (Exception e) {e.printStackTrace();}	
		System.out.println("server.readFileAsString> end");
	    return data; 
	} 
	
	// ================================================================================	
	// read file ./data/claim.json which contains
	// a list of claims in Json and create 
	// a in-memory data structure with the contents
	// of the JSON file claim.json (fileName)
	public void parseFileClaim(String fileName) {
		System.out.println("server.parseFileClaim> start");	
		try {
			String data = readFileAsString(fileName);
			claimArray = gson.fromJson(data, Claims[].class); // Convert JSON String to claimArray
		}
	    catch (JsonIOException       e) {e.printStackTrace();} 
	    catch (JsonSyntaxException   e) {e.printStackTrace();}
		catch (Exception             e) {e.printStackTrace();}	
		System.out.println("server.parseFileClaim> end");	
	}
	
	// ================================================================================	
	// write a string in a file;
	// file has name indicated in the
	// argument and string as well
	public static void writeStringIntoFile(String fileName, String data) {  	
		System.out.println("server.writeStringIntoFile> start with fileName="+fileName);	
		try {
			File file = new File(fileName);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(data);
			fileWriter.flush();
			fileWriter.close();
		}
		catch (IOException        e) {
			System.out.println("server.writeStringIntoFile> FATAL ERROR when writing filename="+fileName);	
			e.printStackTrace();
		} 
		System.out.println("server.writeStringIntoFile> end");	
	} 
	
	// ================================================================================	
	// write into file claim.json the contents
	// of the in-memory data structure claimArray
	// (which has been modified)
	private void writeFileClaim(Claims[] cl, String fileName) {			
		System.out.println("server.writeFileClaim> start");	
		try {
			String json;
			json = gson.toJson(cl) ; 
			writeStringIntoFile(fileName, json);
		}
			catch (JsonIOException       e) {e.printStackTrace();} 
			catch (JsonSyntaxException   e) {e.printStackTrace();}
			catch (Exception             e) {e.printStackTrace();}	
		System.out.println("server.writeFileClaim> end");	
	}
	
	
	// ================================================================================	
	// write into file person.json the contents
	// of the in-memory data structure personArray
	// (which has been modified with a new passwd)
		private void writeFilePerson(Person[] cl, String fileName) {			
			System.out.println("server.writeFilePerson> start");	
			try {
				String json;
				json = gson.toJson(cl) ; 
				writeStringIntoFile(fileName, json);
			}
				catch (JsonIOException       e) {e.printStackTrace();} 
				catch (JsonSyntaxException   e) {e.printStackTrace();}
				catch (Exception             e) {e.printStackTrace();}	
			System.out.println("server.writeFilePerson> end");	
		}
		
	
	// ================================================================================	
	// return the Person contents if the email passed
	// as argument is found the in-memory data structure
	public Person getInMemoryPerson(String email) {
		int i = 0, index = -1;
		int max = personArray.length;
		Person pp = null;
		
		System.out.println("server.getInMemoryPerson> start");	
		
		for (i = 0; i<max; i++) {
			if (personArray[i].getEmail().equals(email) == true) {
				index = i; 
				break;
			}
			if (index != -1) break;
		}
		
		
		if (index != -1) {
			pp = personArray[index];		
			System.out.println("server.getInMemoryPerson> end");	
			return pp;
		}
		else {
			System.out.println("getInMemoryPerson> FATAL ERROR: person not found in memory !");
			return null;
		}
	}
	
   ////////////////////////////////////////////////////////////////////////////////////////////////////
   //THE METHODS BELOW ARE JUST FOR DEBUG
   ////////////////////////////////////////////////////////////////////////////////////////////////////
   public void debugPrintPersonArray() {
	   int i = 0;
	   int max = personArray.length;
	   String ppId, ppFn, ppLn, ppPc, ppPh, ppEm;
	   
	   for (i = 0; i<max; i++) {
	      ppId = personArray[i].getId();
	      ppFn = personArray[i].getFirstName();
	      ppLn = personArray[i].getLastName();
	      ppPc = personArray[i].getPassClear();
	      ppPh = personArray[i].getPassHash();
          ppEm = personArray[i].getEmail();
          
          System.out.println("server.debugPrintPersonArray> ppId="+ppId);
      	  System.out.println("server.debugPrintPersonArray> ppFn="+ppFn);
      	  System.out.println("server.debugPrintPersonArray> ppLn="+ppLn);
      	  System.out.println("server.debugPrintPersonArray> ppPc="+ppPc);
      	  System.out.println("server.debugPrintPersonArray> ppPh="+ppPh);
      	  System.out.println("server.debugPrintPersonArray> ppEm="+ppEm);
   
	   }
    
	}
   
   	// debug method: show the contents of the
	// claimArray in-memory data structure
	public void debug_print_claimArray(Claims[] cl, String debug_sentence) {
					
		System.out.println("debug_print_claimArray> cl="+cl);
		System.out.println("debug_print_claimArray> ="+debug_sentence);
		
		for (int i=0; i<cl.length;i++) {
			System.out.println("server.debug_print_claimArray>i="+i);
			System.out.println("server.debug_print_claimArray> cl[i].getId()="+cl[i].getId()); 
			System.out.println("server.debug_print_claimArray> cl[i].getNumberOfClaims()="+cl[i].getNumberOfClaims()); 
				
			String noc = cl[i].getNumberOfClaims();
			if (noc == null) return;
			int numberOfClaims = Integer.valueOf(noc);
			for (int j=0; j<numberOfClaims;j++) {
				try {
					System.out.println("server.debug_print_claimArray> i =" +i); 
					System.out.println("server.debug_print_claimArray> j =" +j); 
					System.out.println("server.debug_print_claimArray> cl[i].getClaimId(j)       ="+cl[i].getClaimId(j)); 
					System.out.println("server.debug_print_claimArray> cl[i].getClaimDes(j)      ="+cl[i].getClaimDes(j)); 
					System.out.println("server.debug_print_claimArray> cl[i].getClaimPhoto(j)    ="+cl[i].getClaimPhoto(j)); 
					System.out.println("server.debug_print_claimArray> cl[i].getClaimLocation(j) ="+cl[i].getClaimLocation(j)); 
					System.out.println("server.debug_print_claimArray> cl[i].getClaimStatus(j)   ="+cl[i].getClaimStatus(j)); 
				}	
				catch (JsonIOException       e) {e.printStackTrace();} 
		        catch (JsonSyntaxException   e) {e.printStackTrace();}
				catch (Exception             e) {e.printStackTrace();}		
			}
		}	
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////
	//THE METHODS BELOW ARE JUST FOR TESTING
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// TESTING***********************************************************************
	// invoked from the method getMethodTesting
	public void getMethodTesting() { 
		out.println("server.getMethodTesting> OK");
	} 
		
	// TESTING***********************************************************************
	// invoked from the method postMethodTesting
	public void postMethodTesting() { 
		out.println("server.postMethodTesting> OK");
	} 

	
}
