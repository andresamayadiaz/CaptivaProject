package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.list.TreeList;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.gitective.core.CommitFinder;
import org.gitective.core.CommitUtils;
import org.gitective.core.TreeUtils;
import org.gitective.core.filter.commit.CommitFilter;
import org.gitective.core.filter.commit.CommitLimitFilter;
import org.gitective.core.filter.commit.CommitListFilter;

import com.google.gson.Gson;

import models.Issue;
import models.RefModel;
import models.Repository;
import models.User;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import controllers.PathModel.PathChangeModel;
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
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		List<RevCommit> commits = null;
		
		// EXPERIMENTAL aad Feb 2012
		File repoDir = new File(Repository.BASE_DIR + "/", entity.name + ".git");
		try {
			org.eclipse.jgit.lib.Repository repo = builder.setGitDir(repoDir).readEnvironment().findGitDir().build();			
			
			if(JGitUtils.hasCommits(repo)) {
				
				// TESTEANDO
				
				for(RefModel ref : JGitUtils.getLocalBranches(repo, true, 10)){
					Logger.info("Branch: %s", ref.displayName);
				}
				
				for(RefModel ref : JGitUtils.getRemoteBranches(repo, true, 10)){
					Logger.info("Remote Branch: %s", ref.displayName);
				}
				
				for(RevCommit com : JGitUtils.getRevLog(repo, 10)){
					Logger.info("COMMIT: %s", com.getName() + " MSG: " +com.getFullMessage());
					
					for(PathChangeModel file : JGitUtils.getFilesInCommit(repo, com)){
						Logger.info("File Name: "+ file.name + " PATH: " + file.path + " isTree: " + file.isTree());
					}
				}
				
				// END TESTEANDO
				
				CommitListFilter filter = new CommitListFilter();
				CommitFinder service = new CommitFinder(repo);
				service.setFilter(filter);
				service.find();
				// Limit to 20 Max Commits
				int size = (filter.getCommits().size() > 20) ? 20 : filter.getCommits().size();
				commits = filter.getCommits().subList(0, size);
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// END EXPERIMENTAL aad Feb 2012
		
		// Repo URL Access
		String repoUrl = Play.configuration.getProperty("git.user", "git") + "@" + Play.configuration.getProperty("git.url", "project.captivatecnologia.info") + ":" + entity.name + ".git";
		
		render(entity, commits, repoUrl);
	}
	
	@Check("admin")
	public static void add(String name) {
		try {
			Repository.create(name, Security.connected());
		} catch (Repository.RepositoryException e) {
			error(500, e.getMessage());
		}
        index();
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
