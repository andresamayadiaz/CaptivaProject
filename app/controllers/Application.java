package controllers;

import play.*;
import play.mvc.*;
import play.test.Fixtures;

import java.util.*;
import java.util.regex.Pattern;

import models.*;

@With(Security.class)
public class Application extends BaseController {
	
	//public final static Pattern usernamePattern = Pattern.compile("^[A-Za-z0-9_@.]+$");
	
    public static void index() {
        render();
    }
	
    @Check("any")
	public static void home(){
		render();
	}

}