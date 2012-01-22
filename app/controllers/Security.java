package controllers;

import models.User;
import play.mvc.*;

public class Security extends Controller {

	static boolean authenticate(String username, String password) {
		return User.connect(username, password) != null;
    }

}
