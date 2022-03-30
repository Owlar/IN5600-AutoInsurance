//
// server for PUT@UiO Spring 2021
//

package server_group_project;

import static java.lang.System.out;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import server_group_project.Application;

@RestController
public class serverController {
	
	// ================================================================================	
	// this methodPostRemoteLogin is mapped to hostname:port/methodPostRemoteLogin
	// @RequestMapping(value="/methodPostRemoteLogin", method=RequestMethod.POST) 
	// it receives as arguments the email and the password hash corresponding to 
	// the authentication data that was inserted in the login activity in the client
	//@RequestMapping(value="/methodPostRemoteLogin", method=RequestMethod.POST) 
	@PostMapping(value="/methodPostRemoteLogin")
	@ResponseBody
	public Person methodPostRemoteLogin(@RequestParam String em, @RequestParam String ph) { 
		Person p;
	   	if (em.equals(null)  || ph.equals(null)) {
	   		System.out.println("serverController.methodPostRemoteLogin FATAL ERROR with at least one of the arguments null");
	   		return null;
	   	}
	   	p = Application.s.methodPostRemoteLogin(em, ph); 
	   	if (p == null) {System.out.println("serverController.methodPostRemoteLogin FATAL ERROR with p=null");
	   	}
	   	return p;
	}
	
	
	// ================================================================================	
	// this methodPostChangePasswd is mapped to hostname:port/methodPostRemoteLogin
	// @RequestMapping(value="/methodPostChangePasswd", method=RequestMethod.POST) 
	// it receives as arguments the email and both the new password and its hash 
	// corresponding to the new passwd that was inserted in the ChangePasswd activity 
	// in the client
	//@RequestMapping(value="/methodPostChangePasswd", method=RequestMethod.POST) 
	@PostMapping(value="/methodPostChangePasswd")
	@ResponseBody
	public String methodPostChangePasswd(@RequestParam String em, @RequestParam String np, @RequestParam String ph) { 
		String ret = "NOK";		
	   	if (em.equals(null)  || np.equals(null)|| ph.equals(null)) {
	   		System.out.println("serverController.methodPostChangePasswd FATAL ERROR with at least one of the arguments null");
	   		return "NOK";
	   	}
	   	
	   	System.out.println("serverController.methodPostChangePasswd> em="+em);
	   	System.out.println("serverController.methodPostChangePasswd> np="+np);
	   	System.out.println("serverController.methodPostChangePasswd> ph="+ph);
	   	
	   	ret = Application.s.changePasswd(em, np, ph); 
	   	if (ret.equals("NOK")) {
	   		System.out.println("serverController.methodPostChangePasswd FATAL ERROR");
	   	   return "NOK";
	   	}
	   	return "OK";
	}
			
			
	
	// ================================================================================		
	//this getMethod is mapped to hostname:port/getMethodMyClaims
	//@RequestMapping(value="/getMethodMyClaims", method=RequestMethod.GET) 
	@GetMapping(value="/getMethodMyClaims")
	public String  getMethodMyClaims(@RequestParam String id) {
		Claims cl;
		if (id.equals(null)) {
			out.println("serverController.getMethodMyClaims> FATAL ERROR with null id");
			return null;
		}
	    cl = Application.s.getMyClaims(id);		    
	    Gson gson = new Gson(); 
		String clJson = gson.toJson(cl);
	    return clJson;
	}			
	
	// ================================================================================	
	//this postInsertNewClaim is mapped to hostname:port/postInsertNewClaim
	//@RequestMapping(value="/postInsertNewClaim", method=RequestMethod.POST) 
	@PostMapping(value="/postInsertNewClaim")
	@ResponseBody
	public String postInsertNewClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String newClaimDes, @RequestParam String newClaimPho, @RequestParam String newClaimLoc, @RequestParam String newClaimSta) {
		String ret = null;
		if (userId.equals(null) || indexUpdateClaim.equals(null)  || newClaimDes.equals(null) || newClaimPho.equals(null) || newClaimLoc.equals(null) || newClaimSta.equals(null)) {
	   		System.out.println("serverController.postInsertNewClaim FATAL ERROR with at least one null argument");
	   		return null;
	   	}
		ret = Application.s.insertNewClaim(userId, indexUpdateClaim, newClaimDes, newClaimPho, newClaimLoc, newClaimSta);
		return ret;
	}
	
	// ================================================================================	
	//this postUpdateClaim is mapped to hostname:port/postUpdateClaim
	//@RequestMapping(value="/postUpdateClaim", method=RequestMethod.POST) 
	@PostMapping(value="/postUpdateClaim")
	@ResponseBody
	public String postUpdateClaim(@RequestParam String userId, @RequestParam String indexUpdateClaim, @RequestParam String updateClaimDes, @RequestParam String updateClaimPho, @RequestParam String updateClaimLoc, @RequestParam String updateClaimSta) {
		String ret = null;
		if (userId.equals(null) || indexUpdateClaim.equals(null)) {
	   		System.out.println("serverController.postUpdateClaim FATAL ERROR with either userId or indexUpdateClaim null");
	   		return null;
	   	}			
		ret = Application.s.updateClaim(userId, indexUpdateClaim, updateClaimDes, updateClaimPho, updateClaimLoc, updateClaimSta);
		return ret;
	}
	
	
	// ================================================================================	
	//this postMethodUploadPhoto is mapped to hostname:port/postMethodUploadPhoto
	//@RequestMapping(value="/postMethodUploadPhoto", method=RequestMethod.POST) 
	@PostMapping(value="/postMethodUploadPhoto")
	@ResponseBody
	public String postMethodUploadPhoto(@RequestParam String userId, @RequestParam String claimId, @RequestParam String fileName, @RequestParam String imageStringBase64) {
		String ret = null;
		if (fileName.equals(null) || userId.equals(null) || claimId.equals(null) || imageStringBase64.equals(null)) {
	   		System.out.println("serverController.postMethodUploadPhoto FATAL ERROR with either userId or claimId or fileName or imageStringBase64 null");
	   		return ret;
	   	}
		System.out.println("serverController.postMethodUploadPhoto with imageStringBase64="+imageStringBase64);
		ret = Application.s.uploadPhoto(userId, claimId, fileName, imageStringBase64);
		return ret;
	}
	
	// ================================================================================		
	//this getMethod is mapped to hostname:port/getMethodDownloadPhoto
	//@RequestMapping(value="/getMethodDownloadPhoto", method=RequestMethod.GET) 
	@GetMapping(value="/getMethodDownloadPhoto")
	public String  getMethodDownloadPhoto(@RequestParam String fileName) {
		String ret = null;
		if (fileName.equals(null)) {
			System.out.println("serverController.getMethodDownloadPhoto FATAL ERROR with fileName null");
	   		return ret;
		}
		ret = Application.s.downloadPhoto(fileName);
		return ret;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	// The methods below are just for testing/debug purposes
	////////////////////////////////////////////////////////////////////////////////
	
	// TESTING***********************************************************************
	//this getMethodTesting is mapped to hostname:port/getMethodTesting
	//@RequestMapping(value="/getMethodTesting", method=RequestMethod.GET) 
	@GetMapping(value="/getMethodTesting")
	public String getMethodTesting() {
		out.println("serverController.getMethodTesting> start ");
		Application.s.getMethodTesting();
		return "OK";
	}
			
	// TESTING***********************************************************************
	//this postMethodTesting is mapped to hostname:port/postMethodTesting
	//@RequestMapping(value="/postMethodTesting", method=RequestMethod.POST) 
	@PostMapping(value="/postMethodTesting")
	public String postMethodTesting() {
		out.println("serverController.postMethodTesting> start ");
		Application.s.postMethodTesting();
		return "OK";
	}

}
