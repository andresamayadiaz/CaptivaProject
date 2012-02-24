package controllers;

import play.*;
import play.modules.paginate.ModelPaginator;
import play.modules.paginate.Paginator;
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
		Paginator tasks = new ModelPaginator(models.Task.class, "Owner = ? AND isOpen = true ORDER BY DueDate ASC", entity).setPageSize(10);
		Paginator issues = new ModelPaginator(models.Issue.class, "Owner = ? AND isOpen = true", entity).setPageSize(10); // ORDER BY DueDate ASC
        render(entity, tasks, issues);
    }
	
    @Check("any")
	public static void home() {
		render();
	}
}