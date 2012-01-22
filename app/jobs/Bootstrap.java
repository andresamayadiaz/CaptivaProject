package jobs;

import play.*;
import play.jobs.*;
import play.test.*;
 
import models.*;

@OnApplicationStart
public class Bootstrap {

	public void doJob() {
        // Check if the database is empty
        if(User.count() <= 0) {
        	Logger.info("Loading initial-data.yml");
            Fixtures.loadModels("initial-data.yml");
            Logger.info("Users Count: "+User.count());
        }
    }
	
}
