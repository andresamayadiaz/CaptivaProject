package controllers;

import models.Key;
import models.User;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import controllers.Security;


/**
 * Created by IntelliJ IDEA. User: mush Date: 7/31/11 Time: 6:21 PM To change
 * Modified by andres.amaya.diaz@gmail.com Feb 2012
 */
@With(Security.class)
@Check("any")
public class Keys extends BaseController {
	
    public static void delete(java.lang.Long id) {
    	Key entity = Key.findById(id);
    	notFoundIfNull(entity);
    	entity.delete();
        Key.authorizedKeysGenerator.now();
        Users.profile();
    }
    
    public static void add(@Required String keyName, @Required String key) {
    	//checkAuthenticity();
        User user = Security.getConnectedUser();
        try {
        	user.addKey(keyName, key);
            //user.save();
            Key.authorizedKeysGenerator.now();
            Users.profile();
        } catch (Key.SshKeyException e) {
            error(500, e.getMessage());
        }
    }
}
