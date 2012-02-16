package controllers;

import models.Repository;
import models.User;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.With;
import controllers.Security;

/**
 * Created by IntelliJ IDEA. User: mush Date: 7/30/11 Time: 2:44 PM To change
 */
@With(Security.class)
@Check("any")
public class Repositories extends BaseController {

	@Check("admin")
	public static void add(String name) {
		try {
			Repository.create(name, Security.connected());
		} catch (Repository.RepositoryException e) {
			error(500, e.getMessage());
		}
        Application.index();
    }

    public static void access(String name) {
        try {
        	User user = Security.getConnectedUser();
            final Repository repository = Repository.find("byName", name).first();
            notFoundIfNull(repository);
            
            String domain = Http.Request.current().domain;
            render(user, repository, domain);
        } catch (IndexOutOfBoundsException e) {
            error(501, "user doesn't exist");
        }

		Application.index();
	}
    
	public static void accessDelete(String name, String type, String username) {
		final Repository repository = Repository.find("byName", name).first();
		notFoundIfNull(repository);
		
		if (!repository.owner.equals(Security.connected())) {
			error(500, "you are not the owner");
		}
		
		if ("read".equals(type)) {
			repository.readUsers.remove(username);
		} else if ("write".equals(type)) {
			repository.writeUsers.remove(username);
		} else
			error(500, "must be read or write");

		repository.save();
		access(name);
	}
    
	public static void accessAdd(String name) {
		String username = params.get("username");
		String type = params.get("type");
		
		/*
		if (!Application.usernamePattern.matcher(username).matches()) {
			error(500, "username must be [A-Za-z0-9_]");
			return;
		}
		*/
		
		if (User.find("byUserName", username).fetch().size() != 1) {
			error(500, "cannot find user");
		}
		
		final Repository repository = Repository.find("byName", name).first();
		if (!repository.owner.equals(Security.connected())) {
			error(500, "you are not the owner");
		}
		
		if ("read".equals(type)) {
			repository.readUsers.add( (User) User.find("byUserName", username).first());
		} else if ("write".equals(type)) {
			repository.writeUsers.add( (User) User.find("byUserName", username).first());
		} else
			error(500, "must be read or write");
		
		repository.save();
		access(name);
	}
}
