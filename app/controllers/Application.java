package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;

import models.*;

@With(Deadbolt.class)
public class Application extends Controller {

    public static void index() {
        render();
    }
	
    @Restrictions({@Restrict("admin")})
	public static void home(){
		
	}

}