package org.tech.poc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@RequestMapping(value = "/authorization_service", method = RequestMethod.POST)
	private String authorizationService(@RequestHeader String userName, @RequestHeader String password) {
		String response;
		if (userName.isEmpty()) {

			response = "Username field cannot be empty";
		} else if (password.isEmpty()) {

			response = "Password field cannot be empty";
		} else {
	        String privateKey = JwTokenHelper.getInstance().generatePrivateKey(userName, password);

			response = "You're authenticated successfully. Private key will be valid for 30 mins" +"\n"+" "+privateKey;
		}
		return response;

	}

}
