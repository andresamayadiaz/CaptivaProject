package models;

import play.*;
import play.data.validation.*;
import play.db.jpa.*;

import javax.persistence.*;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;

import java.util.*;

@Entity
public class User extends Model implements RoleHolder {
    
	@Required
	public String userName;
	
	@Required
	public String fullName;
	
	@Required
	public String email;
	
	@Required
	public String password;
	
	@Required
	public String comments;
	
	@Required
    @ManyToOne
    public ApplicationRole role;
	
	public User(String userName, String fullName, String email, String comments, ApplicationRole role)
	{
	    this.userName = userName;
	    this.fullName = fullName;
	    this.email = email;
	    this.comments = comments;
	    this.role = role;
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
    
    public List<? extends Role> getRoles()
    {
        return Arrays.asList(role);
    }
	
}
