package models;

import play.*;
import play.data.validation.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class User extends Model {
    
	@Required(message = "User Name is requiered")
	@Column(unique=true)
	@Email
	public String userName;
	
	@Required(message = "Full Name is requiered")
	public String fullName;
	
	@Required(message = "Password is requiered")
	public String password;
	
	@Required(message = "is Admin is requiered")
    public boolean isAdmin;
	
	public User(String userName, String fullName, String password, boolean isAdmin)
	{
	    this.userName = userName;
	    this.fullName = fullName;
	    this.password = password;
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
