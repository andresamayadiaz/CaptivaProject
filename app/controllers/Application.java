package controllers;

import play.*;
import play.mvc.*;
import play.test.Fixtures;

import java.util.*;

import models.*;

@With(Security.class)
public class Application extends BaseController {
	
    public static void index() {
        render();
    }
	
    @Check("any")
	public static void home(){
		render();
	}

}