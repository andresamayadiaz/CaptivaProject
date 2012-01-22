package controllers;

import java.util.List;

import models.User;
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
    
    public static void edit(String username){
    	render();
    }
    
    public static void delete(String userName){
    	render();
    }
    
}
