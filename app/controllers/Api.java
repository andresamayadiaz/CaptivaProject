package controllers;

import java.util.List;

import models.Key;
import models.Repository;
import models.StatusJson;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by IntelliJ IDEA.
 * User: mush
 * Date: 8/15/11
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
@With({BaseController.class})
public class Api extends Controller {
	
    public static void listRepositories(String apikey) {
        final User user = User.find("byApikey", apikey).first();

        final List<Repository> repositories = Repository.find("byOwner", user.userName).fetch();
        renderJSON(new StatusJson(200, "LIST KEYS", repositories));
    }
    
    public static void addRepository(String name, String apikey) {
        try {
            final User user = User.find("byApikey", apikey).first();
            Repository.create(name, user.userName);

            renderJSON(new StatusJson(200, "CREATED"));
        } catch (Repository.RepositoryException e) {
            renderJSON(new StatusJson(500, e.getMessage()));
        }
    }
    
    public static void showRepository(String name, String apikey) {
        final User user = User.find("byApikey", apikey).first();
        //final Repository repository = Repository.find().filter("name =", name).filter("owner =", user.userName).first();
        final Repository repository = Repository.find("name = ? AND owner = ?", name, user.userName).first();
        renderJSON(new StatusJson(200, "REPOSITORY", repository));
    }

    public static void listSshKeys(String apikey) {
        final User user = User.find("byApikey", apikey).first();
        renderJSON(new StatusJson(200, "LIST KEYS", user.sshkeys));
    }
    
    public static void addSshKey(String name, String key, String apikey) {
        final User user = User.find("byApikey", apikey).first();
        try {
            user.addKey(name, key);
            user.save();
            Key.authorizedKeysGenerator.now();
            renderJSON(new StatusJson(200, "ADDED"));
        } catch (Key.SshKeyException e) {
            renderJSON(new StatusJson(500, e.getMessage()));
        }
    }
    
    public static void showSshKey(String name, String apikey) {
        final User user = User.find("byApikey", apikey).first();
        if(user == null){
        	renderJSON(new StatusJson(500, "apiKey not Found"));
        }
        final Key key = Key.find("byName", name).first();
        if(key == null){
        	renderJSON(new StatusJson(500, "key not found"));
        }
        renderJSON(new StatusJson(200, "KEY", key));
    }
    
    public static void deleteSshKey(String uuid, String apikey) {
        final User user = User.find("byApikey", apikey).first();
        user.sshkeys.remove(uuid);
        user.save();
        Key.authorizedKeysGenerator.now();
        renderJSON(new StatusJson(200, "DELETED"));
    }
}
