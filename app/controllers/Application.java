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
	
	@Check("any")
    public static void index() {
		User entity = Security.getConnectedUser();
		List<Task> tasks = Task.find("Owner = ? AND isOpen = true ORDER BY dueDate ASC", entity).fetch();
		List<Issue> issues = Issue.find("Owner = ? AND isOpen = true ORDER BY dueDate ASC", entity).fetch();
        render(entity, tasks, issues);
    }
	
    @Check("any")
	public static void home() {
		render();
	}
}