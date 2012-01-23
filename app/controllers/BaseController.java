package controllers;

import models.User;
import play.Logger;
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
		if(Security.isConnected()) {
            User user = User.find("byUserName", Security.connected()).first();
            renderArgs.put("username", user.userName);
        }
    }

}
