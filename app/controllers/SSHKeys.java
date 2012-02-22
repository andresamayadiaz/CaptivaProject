package controllers;

import models.SSHKey;
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
public class SSHKeys extends BaseController {
	
    public static void delete(java.lang.Long id) {
    	SSHKey entity = SSHKey.findById(id);
    	notFoundIfNull(entity);
    	entity.delete();
    	SSHKey.authorizedKeysGenerator.now();
        Users.profile();
    }
    
    public static void add(@Required String keyName, @Required String key) {
    	//checkAuthenticity();
        User user = Security.getConnectedUser();
        try {
        	user.addKey(keyName, key);
            //user.save();
        	SSHKey.authorizedKeysGenerator.now();
            Users.profile();
        } catch (SSHKey.SshKeyException e) {
            error(500, e.getMessage());
        }
    }
}
