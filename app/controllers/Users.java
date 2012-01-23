package controllers;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import models.User;
import play.Logger;
import play.mvc.*;

public class Users extends BaseController {
	
    public static void index() {
        render();
    }
    
    public static void profile(){
    	User userInfo = User.find("byUserName", Security.connected()).first();
    	render(userInfo);
    }
    
    @Check("admin")
    public static void list(){
    	List<User> users = User.findAll();
    	render(users);
    }
    
    @Check("admin")
    public static void edit(String userName){
    	render();
    }
    
    @Check("admin")
    public static void delete(String userName){
    	render();
    }
    
    public static void form(String userName){
    	if(userName != null) {
            User user = User.find("byUserName", userName).first();
            render(user);
        }
    	render();
    }
    
    @Check("admin")
    public static void save(String userName, String fullName, String password, String repassword, boolean isAdmin){
    	User user = new User(userName, fullName, password, isAdmin);
    	Logger.info("isAdmin: "+isAdmin);
    	
    	// Validate
    	validation.valid(user);
    	if(validation.hasErrors()){	
    		render("@form", user);
    	}
    	if(User.count("byUserName", userName) > 0){
    		validation.addError("user.userName", "User Name is already taken");
    		render("@form", user);
    	}
    	if(!password.equals(repassword)){
    		validation.addError("user.repassword", "Password Does not match!!");
    		render("@form", user);
    	}
    	
    	// Save
    	user.save();
    	list();
    }
    
}
