package controllers;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import models.Milestone;
import models.User;
import play.Logger;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.*;
import play.mvc.Http.Request;

public class Users extends BaseController {
	
    public static void index() {
    	List<User> entities = User.all().fetch();
        render(entities);
    }
    
    public static void create(User entity) {
        render(entity);
    }
    
    public static void show(java.lang.Long id) {
        User entity = User.findById(id);
        render(entity);
    }
    
    public static void edit(java.lang.Long id) {
        User entity = User.findById(id);
        render(entity);
        
    }
    
    public static void profile(){
    	User userInfo = User.find("byUserName", Security.connected()).first();
    	render(userInfo);
    }
    
   /* @Check("admin")
    public static void list(){
    	List<User> users = User.findAll();
    	render(users);
    }
    
    @Check("admin")
    public static void edit(String userName){
    	if(userName != null) {
            User userInfo = User.find("byUserName", userName).first();
            render(userInfo);
        }
    	
    	// Error, userName null
    	index();
    }*/
    
    @Check("admin")
    public static void delete(java.lang.Long id) {
        User entity = User.findById(id);
        entity.delete();
        index();
    }
    
    @Check("admin")
    public static void save(@Valid User entity, String repassword) {
    	
    	if(!entity.password.equals(repassword)){
    		validation.addError("repassword", "Password does not match!");
        	render("@create", entity);
    	}
    	
        if (validation.hasErrors()) {
            //flash.error(Messages.get("scaffold.validation"));
            render("@create", entity);
        }
        entity.save();
        flash.success(Messages.get("scaffold.created", "User"));
        index();
    }
    /*public static void save(String userName, String fullName, String password, String repassword, boolean isAdmin){
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
    	index();
    }*/
    
    public static void update(User entity, String repassword, boolean chgPassword) {
        // Validate Password Change
    	Logger.info("chgPassword = %s / repassword = %s", chgPassword, repassword);
    	if(chgPassword){
    		
    		if(!entity.password.equals(repassword)){
    			validation.addError("repassword", "Password does not match!");
            	render("@edit", entity);
    		}
    		
    		if(entity.password.equals("")){
    			validation.addError("repassword", "Password cannot be empty!");
            	render("@edit", entity);
    		}
    		
    	}else {
    		User usr = User.findById(entity.id);
        	Logger.info("ID: [%s] - Set same password = %s", entity.id, usr.password);
        	entity.password = usr.password;
    	}
        
        validation.valid(entity);
        if (validation.hasErrors()) {
            //flash.error(Messages.get("scaffold.validation"));
            render("@edit", entity);
        }
        
        	entity = entity.merge();
        
        entity.save();
        flash.success(Messages.get("scaffold.updated", "User"));
        index();
    }
    
}
