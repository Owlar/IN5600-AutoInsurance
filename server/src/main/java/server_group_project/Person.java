//
// server for PUT@UiO Spring 2021
//

package server_group_project;

public class Person {

		String id;
	    String firstName;
	    String lastName;
	    String passClear;
	    String passHash;
	    String email; 
			
	    public Person() {
	      	id = "na";
	    	firstName = "na";
	    	lastName = "na";
	    	passClear = "na";
	        passHash = "na";
	        email = "na";
	     }
		    
	    public String getId(){return this.id;}
		    
	    public String getFirstName(){return this.firstName;}
		    
	    public String getLastName(){return this.lastName;}
		    
	    public String getPassClear(){return this.passClear;}
	    
	    public String getPassHash(){return this.passHash;}
		     
	    public String getEmail(){return this.email;}
		    
}


