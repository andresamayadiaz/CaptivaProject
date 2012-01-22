package controllers;

import play.*;
import play.mvc.*;
import play.test.Fixtures;

import java.util.*;

import models.*;

public class Application extends BaseController {
	
    public static void index() {
        render();
    }
	
	public static void home(){
		render();
	}

}