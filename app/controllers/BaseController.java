package controllers;

import java.util.List;

import com.google.gson.Gson;

import models.User;
import play.Logger;
import play.libs.Codec;
import play.mvc.*;
import play.test.Fixtures;

@With(Secure.class)
public class BaseController extends Controller {

	@Before
    static void setConnectedUser() {
		// todo remove this on production
		if(User.count() <= 0) {
        	Logger.info("Loading initial-data.yml");
            Fixtures.loadModels("initial-data.yml");
            Logger.info("Users Count: "+User.count());
        }
		
		/*List<User> entities = User.all().fetch();
    	Gson gson = new Gson();
    	for(User entity : entities){
    		Logger.info(gson.toJson(entity));
    		//Logger.info("Password Hash: %s", Codec.hexSHA1(entity.password));
    	}*/
		
		if(Security.isConnected()) {
            User user = User.find("byUserName", Security.connected()).first();
            renderArgs.put("username", user.userName);
            renderArgs.put("userId", user.id);
        }
    }

}
