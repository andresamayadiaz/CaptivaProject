package controllers;

import java.util.List;

import com.google.gson.Gson;

import models.Project;
import models.User;
import play.Logger;
import play.mvc.*;
import play.i18n.Messages;
import play.data.validation.Validation;
import play.data.validation.Valid;

@With(Security.class)
public class Projects extends BaseController {
	
	@Check("any")
    public static void index() {
        List<Project> entities = Project.find("isOpen = ?", true).fetch();
        render(entities);
    }
    
    public static void create(Project entity) {
    	List<User> owners = User.find("isAdmin", true).fetch();
        render(entity, owners);
    }
    
    public static void show(java.lang.Long id) {
        Project entity = Project.findById(id);
        render(entity);
    }
    
    public static void edit(java.lang.Long id) {
        Project entity = Project.findById(id);
        List<User> owners = User.find("isAdmin", true).fetch();
        render(entity, owners);
    }
    
    public static void delete(java.lang.Long id) {
        Project entity = Project.findById(id);
        entity.delete();
        index();
    }
    
    public static void save(@Valid Project entity) {
        if (validation.hasErrors()) {
            //flash.error(Messages.get("scaffold.validation"));
            List<User> owners = User.find("isAdmin", true).fetch();
            render("@create", entity, owners);
        }
        entity.save();
        flash.success(Messages.get("scaffold.created", "Project"));
        index();
    }

    public static void update(@Valid Project entity) {
        if (validation.hasErrors()) {
            //flash.error(Messages.get("scaffold.validation"));
            render("@edit", entity);
        }
        
              entity = entity.merge();
        
        entity.save();
        flash.success(Messages.get("scaffold.updated", "Project"));
        index();
    }
}
