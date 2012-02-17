package models;

import play.*;
import play.data.validation.*;
import play.data.binding.*;
import play.db.jpa.*;
import play.libs.Codec;

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
	@NoBinding("updateProfile")
    public boolean isAdmin;
	
	@OneToMany (mappedBy="User")
	public List<Key> sshkeys;
	
	/*
	@ManyToMany(mappedBy="writeUsers")
    public Set<Repository> writeRepos = new HashSet<Repository>();
	
	@ManyToMany(mappedBy="readUsers")
	public Set<Repository> readRepos = new HashSet<Repository>();
	*/
	
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
	
	public void addKey(String name, String keyString) throws Key.SshKeyException {
        Key key = new Key();
        key.name = name;
        key.sshkey = Key.extractKey(keyString);
        key.User = this;
        key.save();
        //sshkeys.put(UUID.randomUUID().toString(), key);
        //sshkeys.add(key);
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
