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
		
		// check if filter apply
		if(params.get("statusFilter") != null){
			
			if(params.get("statusFilter").equals("true")){ // open
				List<Project> entities = Project.find("isOpen", true).fetch();
				renderArgs.put("statusFilter", "true");
		        render(entities);
			}else if(params.get("statusFilter").equals("false")){ // closed
				List<Project> entities = Project.find("isOpen", false).fetch();
				renderArgs.put("statusFilter", "false");
		        render(entities);
			}else { // all
				List<Project> entities = Project.all().fetch();
				renderArgs.put("statusFilter", "all");
		        render(entities);
			}

		}
		
        List<Project> entities = Project.find("isOpen", true).fetch();
        render(entities);
    }
    
	@Check("any")
    public static void create(Project entity) {
    	List<User> owners = User.find("isAdmin", true).fetch();
        render(entity, owners);
    }
    
	@Check("any")
    public static void show(java.lang.Long id) {
        Project entity = Project.findById(id);
        //Logger.info("Milestones Count: %s", entity.Milestones.size());
        render(entity);
    }
    
	@Check("any")
    public static void edit(java.lang.Long id) {
        Project entity = Project.findById(id);
        List<User> owners = User.find("isAdmin", true).fetch();
        render(entity, owners);
    }
    
	@Check("admin")
    public static void delete(java.lang.Long id) {
        Project entity = Project.findById(id);
        entity.delete();
        index();
    }
    
	@Check("any")
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

	@Check("any")
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
    
	@Check("any")
    public static void close(java.lang.Long id){
		Project entity = Project.findById(id);
		notFoundIfNull(entity);
		entity.isOpen = false;
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Project"));
		index();
    }
    
	@Check("any")
    public static void open(java.lang.Long id){
		Project entity = Project.findById(id);
		notFoundIfNull(entity);
		entity.isOpen = true;
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Project"));
		index();
    }
    
}
