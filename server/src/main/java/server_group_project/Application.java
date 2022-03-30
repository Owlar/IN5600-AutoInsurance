//
// server for PUT@UiO Spring 2021
//


package server_group_project;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static final server s = new server();
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
    	s.start();
	}	

}
