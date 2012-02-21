package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.lib.*;

import models.Issue;
import models.Repository;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import controllers.Security;

@With(Security.class)
@Check("any")
public class Repositories extends BaseController {
	
	@Check("admin")
	public static void index(){
		List<Repository> entities = Repository.all().fetch();
        render(entities);
	}
	
	@Check("any")
	public static void show(java.lang.Long id){
		Repository entity = Repository.findById(id);
		notFoundIfNull(entity);
		
		// EXPERIMENTAL aad Feb 2012
		File repoDir = new File(Repository.BASE_DIR + "/", entity.name + ".git");
		try {
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			org.eclipse.jgit.lib.Repository repo = builder.setGitDir(repoDir).readEnvironment().findGitDir().build();
			
			RevWalk walk = new RevWalk(repo);
			Logger.info("Walk: %s", walk.toString());
			for (RevCommit commit : walk) {
				
				Logger.info("commit: %s", commit.toString());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// END EXPERIMENTAL aad Feb 2012
		
		render(entity);
	}
	
	@Check("admin")
	public static void add(String name) {
		try {
			Repository.create(name, Security.connected());
		} catch (Repository.RepositoryException e) {
			error(500, e.getMessage());
		}
        Application.index();
    }
	
	@Check("admin")
	public static void addWriteUser(java.lang.Long repoId, java.lang.Long userId){
		User user = User.findById(userId);
		notFoundIfNull(user);
		
		Repository repo = Repository.findById(repoId);
		notFoundIfNull(user);
		
		repo.writeUsers.add(user);
		repo.save();
		
		show(repoId);
	}
	
	@Check("admin")
	public static void addReadUser(java.lang.Long repoId, java.lang.Long userId){
		User user = User.findById(userId);
		notFoundIfNull(user);
		
		Repository repo = Repository.findById(repoId);
		notFoundIfNull(user);
		
		repo.readUsers.add(user);
		repo.save();
		
		show(repoId);
	}
	
	@Check("admin")
	public static void removeWriteUser(java.lang.Long repoId, java.lang.Long userId){
		User user = User.findById(userId);
		notFoundIfNull(user);
		
		Repository repo = Repository.findById(repoId);
		notFoundIfNull(user);
		
		repo.writeUsers.remove(user);
		repo.save();
		
		show(repoId);
	}
	
	@Check("admin")
	public static void removeReadUser(java.lang.Long repoId, java.lang.Long userId){
		User user = User.findById(userId);
		notFoundIfNull(user);
		
		Repository repo = Repository.findById(repoId);
		notFoundIfNull(user);
		
		repo.readUsers.remove(user);
		repo.save();
		
		show(repoId);
	}
    
}
