package models;

import play.*;
import play.data.validation.*;
import play.data.binding.*;
import play.db.jpa.*;
import play.libs.Codec;

import javax.persistence.*;

import notifiers.Mails;

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
	@NoBinding("updateProfile")
    public boolean isAdmin;
	
	@OneToMany (mappedBy="User")
	public List<SSHKey> sshkeys;
	
	public User(String userName, String fullName, String password, boolean isAdmin)
	{
	    this.userName = userName;
	    this.fullName = fullName;
	    this.password = password;
	    this.isAdmin = isAdmin;
	}
	
	public static User connect(String userName, String password) {
	    return find("byUserNameAndPassword", userName, Codec.hexSHA1(password)).first();
	}
	
	public static User getByUserName(String userName)
    {
        return find("byUserName", userName).first();
    }
	
	public void addKey(String name, String keyString) throws SSHKey.SshKeyException {
		SSHKey key = new SSHKey();
        key.name = name;
        key.sshkey = SSHKey.extractKey(keyString);
        key.User = this;
        key.save();
        //sshkeys.put(UUID.randomUUID().toString(), key);
        //sshkeys.add(key);
    }
	
	@PostPersist
	public void UserCreated() {
		//Mails mails = new Mails();
		//mails.welcome(this);
	}
	
    @Override
    public String toString()
    {
        return this.userName;
    }
    
    /*@Override
    public User save(){
    	Logger.info("Save Password: %s", password);
    	if(!password.isEmpty()){
    		password = Codec.hexSHA1(password);
    	}
    	
    	return super.save();
    }*/
	
}
