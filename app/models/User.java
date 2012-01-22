package models;

import play.*;
import play.data.validation.*;
import play.db.jpa.*;

import javax.persistence.*;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;

import java.util.*;

@Entity
public class User extends Model {
    
	@Required
	public String userName;
	
	@Required
	public String fullName;
	
	@Required
	public String password;
	
	@Required
    public boolean isAdmin;
	
	public User(String userName, String fullName, boolean isAdmin)
	{
	    this.userName = userName;
	    this.fullName = fullName;
	    this.isAdmin = isAdmin;
	}
	
	public static User connect(String userName, String password) {
	    return find("byUserNameAndPassword", userName, password).first();
	}
	
	public static User getByUserName(String userName)
    {
        return find("byUserName", userName).first();
    }
	
    @Override
    public String toString()
    {
        return this.userName;
    }
	
}
