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

/**
 * Created by IntelliJ IDEA. User: mush Date: 7/30/11 Time: 2:17 PM To change
 */
@Entity
public class Repository extends Model {
	private final static String BASE_DIR = Play.configuration.getProperty("git.repo", System.getProperty("user.home") + "/repo");
	public final static Pattern repositoryPattern = Pattern.compile("^[A-Za-z0-9_]+$");
	
	@Required
	@Column(unique=true, nullable=false)
	public String name;
	
	@Required
	public String owner;
	
	@ManyToMany(cascade=CascadeType.ALL)
    public Set<User> writeUsers = new HashSet();
	
	@ManyToMany(cascade=CascadeType.ALL)
    public Set<User> readUsers = new HashSet();
	
	public static Repository create(String name, String owner) throws RepositoryException {
		if (!repositoryPattern.matcher(name).matches()) {
			throw new RepositoryException("repository name not allowed");
		}
		if (User.find("byUserName", owner).fetch().size() != 1) {
			throw new RepositoryException("user doesn't exist");
		}
		
		File repoDir = new File(BASE_DIR + "/", name + ".git"); // File repoDir = new File(BASE_DIR + "/" + owner, name + ".git");
		if (repoDir.exists()) {
			throw new RepositoryException("repository exists");
		}
        Logger.debug("Creating repo: " + repoDir.getAbsolutePath());
		Git.init().setBare(true).setDirectory(repoDir).call();
		
		final Repository repository = new Repository();
		repository.name = name;
		repository.owner = owner;
		repository.writeUsers.add( (User) User.find("byUserName", owner).first());
		repository.save();
		return repository;
	}

	public static class RepositoryException extends Exception {
		public RepositoryException(String s) {
			super(s);
		}
	}

}
