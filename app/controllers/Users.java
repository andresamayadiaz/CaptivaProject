package controllers;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import models.Milestone;
import models.User;
import play.Logger;
import play.data.binding.As;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.*;
import play.mvc.Http.Request;

@With(Security.class)
public class Users extends BaseController {
	
	@Check("admin")
    public static void index() {
    	List<User> entities = User.all().fetch();
        render(entities);
    }
    
	@Check("admin")
    public static void create(User entity) {
        render(entity);
    }
    
	@Check("admin")
    public static void show(java.lang.Long id) {
        User entity = User.findById(id);
        render(entity);
    }
    
	@Check("admin")
    public static void edit(java.lang.Long id) {
        User entity = User.findById(id);
        render(entity);
        
    }
    
	@Check("any")
    public static void profile(){
    	User entity = User.find("byUserName", Security.connected()).first();
    	render(entity);
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
        
        entity.password = Codec.hexSHA1(entity.password);
        entity.save();
        flash.success(Messages.get("scaffold.created", "User"));
        index();
    }
    
    @Check("admin")
    public static void update(User entity, String repassword, boolean chgPassword) {
    	
        // Validate Password Change
    	if(chgPassword){
    		
    		if(!entity.password.equals(repassword)){
    			validation.addError("repassword", "Password does not match!");
            	render("@edit", entity);
    		}
    		
    		if(entity.password.equals("")){
    			validation.addError("entity.password", "Password cannot be empty!");
            	render("@edit", entity);
    		}
    		
    		entity.password = Codec.hexSHA1(entity.password);
    	}
        
        validation.valid(entity);
        if (validation.hasErrors()) {
            render("@edit", entity);
        }
        
        	entity = entity.merge();
        
        entity.save();
        flash.success(Messages.get("scaffold.updated", "User"));
        index();
    }
    
    @Check("any")
    public static void updateProfile(@As("updateProfile") User entity, String repassword, boolean chgPassword){
    	// Validate Password Change
    	if(chgPassword){
    		
    		if(!entity.password.equals(repassword)){
    			validation.addError("repassword", "Password does not match!");
            	render("@profile", entity);
    		}
    		
    		if(entity.password.equals("")){
    			validation.addError("entity.password", "Password cannot be empty!");
            	render("@profile", entity);
    		}
    		
    	}
        
        validation.valid(entity);
        if (validation.hasErrors()) {
            render("@profile", entity);
        }
        
        	entity = entity.merge();
        
        entity.save();
        flash.success(Messages.get("scaffold.updated", "User"));
        Application.home();
    }
    
}
