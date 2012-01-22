package controllers;

import models.User;
import play.mvc.*;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		return User.connect(username, password) != null;
    }
	
	static boolean check(String profile) {
	    if("admin".equals(profile)) {
	        return User.find("byUserName", connected()).<User>first().isAdmin;
	    }
	    return false;
	}
	
	static void onDisconnected() {
	    Application.index();
	}
	
	static void onAuthenticated() {
	    Application.index();
	}

}
