package models;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.*;

import org.eclipse.jgit.api.Git;

import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Repository extends Model {
	public final static String BASE_DIR = Play.configuration.getProperty("git.repo", System.getProperty("user.home") + "/repo");
	public final static Pattern repositoryPattern = Pattern.compile("^[A-Za-z0-9_]+$");
	
	@Required
	@Column(unique=true, nullable=false)
	public String name;
	
	@ManyToOne
	@JoinColumn (name="Owner")
	public User owner;
	
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="repository_writeusers") 
    public Set<User> writeUsers = new HashSet();
	
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(name="repository_readusers") 
	public Set<User> readUsers = new HashSet(); 
	
	public static Repository create(String name, String owner) throws RepositoryException {
		if (!repositoryPattern.matcher(name).matches()) {
			throw new RepositoryException("repository name not allowed");
		}
		User usrOwner = User.find("byUserName", owner).first();
		if (usrOwner == null) {
			throw new RepositoryException("user doesn't exist");
		}
		
		File repoDir = new File(BASE_DIR + "/", name + ".git"); // File repoDir = new File(BASE_DIR + "/" + owner, name + ".git");
		if (repoDir.exists()) {
			throw new RepositoryException("repository exists");
		}
        Logger.debug("Creating repo: " + repoDir.getAbsolutePath());
		Git.init().setBare(true).setDirectory(repoDir).call();
		
		Repository repository = new Repository();
		if(repository.writeUsers == null){
			repository.writeUsers = new HashSet();
		}
		repository.name = name;
		repository.owner = usrOwner;
		repository.writeUsers.add( usrOwner );
		
		repository.save();
		return repository;
	}

	public static class RepositoryException extends Exception {
		public RepositoryException(String s) {
			super(s);
		}
	}

}
