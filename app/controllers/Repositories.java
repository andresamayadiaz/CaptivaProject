package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.list.TreeList;
import org.eclipse.jgit.lib.ObjectId;
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
		String branch = null;
		
		// EXPERIMENTAL aad Feb 2012
		File repoDir = new File(Repository.BASE_DIR + "/", entity.name + ".git");
		try {
			org.eclipse.jgit.lib.Repository repo = builder.setGitDir(repoDir).readEnvironment().findGitDir().build();			
			
			if(JGitUtils.hasCommits(repo)) {
				
				renderArgs.put("branches", JGitUtils.getLocalBranches(repo, true, 10));
				if(params.get("branchId") != null){
					branch = params.get("branchId");
				}else {
					try {
						branch = JGitUtils.getDefaultBranch(repo).name();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				/*for(RefModel ref : JGitUtils.getLocalBranches(repo, true, 10)){
					Logger.info("Branch: %s", ref.displayName + " ref.getReferencedObjectId(): " + ref.getReferencedObjectId().getName());
				}*/
				
				/*for(RefModel ref : JGitUtils.getRemoteBranches(repo, true, 10)){
					Logger.info("Remote Branch: %s", ref.displayName);
				}*/
				
				commits = JGitUtils.getRevLog(repo, branch, 0, 20);
				
				/*for(RevCommit com : commits){
					Date time = new Date();
					time.setTime(com.getCommitTime());
					Logger.info("COMMIT ID: %s", com.getId() + " NAME: " + com.getName() + " MSG: " +com.getFullMessage() + " TIME: " + time.toString());
					
					//for(PathChangeModel file : JGitUtils.getFilesInCommit(repo, com)){
					//	Logger.info("Commit File Name: "+ file.name + " PATH: " + file.path + " isTree: " + file.isTree());
					//}
					
				}*/
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// END EXPERIMENTAL aad Feb 2012
		
		// Repo URL Access
		String repoUrl = Play.configuration.getProperty("git.user", "git") + "@" + Play.configuration.getProperty("git.url", "project.captivatecnologia.info") + ":" + entity.name + ".git";
		
		render(entity, commits, repoUrl, branch);
	}
	
	@Check("any")
	public static void showCommit(java.lang.Long repoId, String commit, String pathStr){
		Repository entity = Repository.findById(repoId);
		notFoundIfNull(entity);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		List<PathModel> path = new ArrayList<PathModel>();
		RevCommit com = null;
		File repoDir = new File(Repository.BASE_DIR + "/", entity.name + ".git");
		try {
			org.eclipse.jgit.lib.Repository repo = builder.setGitDir(repoDir).readEnvironment().findGitDir().build();			
			com = JGitUtils.getCommit(repo, commit);
			
			if( com != null ) {
				
				path = JGitUtils.getFilesInPath(repo, pathStr, com);
				for(PathModel file : path){
					if(file.isTree()){
						//Logger.info("Tree: %s", "PATH: " + file.path + " isParentPath: " + file.isParentPath);
					}
					
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		render(entity, com, path, pathStr);
		
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
