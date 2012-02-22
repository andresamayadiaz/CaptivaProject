package models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.*;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import jobs.AuthorizedKeysGenerator;

@Entity
public class Key extends Model {
	
	@Required
	@Column(unique=true)
	public String name;
	
	@Required
	@MaxSize(1000)
	public String sshkey;
	
	@Required
	@ManyToOne
	@JoinColumn (name="User")
	public User User;
	
	@Transient
    public final static Pattern keyPattern = Pattern.compile("^(ssh-[a-z]+ +[A-Za-z0-9+/=]+).*$", Pattern.DOTALL | Pattern.MULTILINE);
	
	@Transient
    public final static AuthorizedKeysGenerator authorizedKeysGenerator = new AuthorizedKeysGenerator();
    
	public static String extractKey(String key) throws SshKeyException {
		final Matcher matcher = keyPattern.matcher(key);
		if (!matcher.matches()) {
			throw new SshKeyException("key is not valid");
		}
		return matcher.group(1);
	}

	public static class SshKeyException extends Exception {
		public SshKeyException(String s) {
			super(s);
		}
	}
}
