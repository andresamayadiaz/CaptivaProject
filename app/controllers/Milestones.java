package controllers;

import java.util.List;
import models.Milestone;
import models.Project;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import play.i18n.Messages;
import play.data.validation.Validation;
import play.data.validation.Valid;

@With(Security.class)
@Check("any")
public class Milestones extends BaseController {
	
    public static void index() {
    	
    	// check if filter apply
		if(params.get("statusFilter") != null) {			
			if(params.get("statusFilter").equals("true")){ // open
				List<Milestone> entities = Milestone.find("Owner = ? and isOpen = true ORDER BY Name", Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "true");
		        render(entities);
			} else if(params.get("statusFilter").equals("false")){ // closed
				List<Milestone> entities = Milestone.find("Owner = ? and isOpen = false ORDER BY Name", Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "false");
		        render(entities);
			} else if (params.get("statusFilter").equals("me")) { // me
				List<Milestone> entities = Milestone.find("Owner = ? and isOpen = ? ORDER BY Name", Security.getConnectedUser(), true).fetch();
				renderArgs.put("statusFilter", "me");
		        render(entities);
			} else { // all
				List<Milestone> entities = Milestone.find("Owner = ? ORDER BY Name", Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "all");
		        render(entities);
			}			
		}
    	
        List<Milestone> entities = Milestone.find("Owner = ? and isOpen = true ORDER BY Name", Security.getConnectedUser()).fetch();
        render(entities);
    }
    
    public static void create(Milestone entity) {
    	List<Project> projects = Project.find("isOpen", true).fetch();
    	List<User> owners = User.all().fetch();
        render(entity, projects, owners);
    }
    
    public static void show(java.lang.Long id) {
        Milestone entity = Milestone.findById(id);
        render(entity);
    }
    
    public static void edit(java.lang.Long id) {
        Milestone entity = Milestone.findById(id);
        List<Project> projects = Project.find("isOpen", true).fetch();
        List<User> owners = User.all().fetch();
        render(entity, projects, owners);
    }
    
    public static void delete(java.lang.Long id) {
        Milestone entity = Milestone.findById(id);
        entity.delete();
        index();
    }
    
    public static void save(@Valid Milestone entity) {
        if (validation.hasErrors()) {
            //flash.error(Messages.get("scaffold.validation"));
            render("@create", entity);
        }
        entity.save();
        flash.success(Messages.get("scaffold.created", "Milestone"));
        show(entity.id);
    }
    
    public static void update(@Valid Milestone entity) {
        if (validation.hasErrors()) {
            //flash.error(Messages.get("scaffold.validation"));
            render("@edit", entity);
        }
        
              entity = entity.merge();
        
        entity.save();
        flash.success(Messages.get("scaffold.updated", "Milestone"));
        show(entity.id);
    }
    
    @Check("any")
    public static void close(java.lang.Long id){
		Milestone entity = Milestone.findById(id);
		notFoundIfNull(entity);
		entity.isOpen = false;
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Milestone"));
		show(id);
    }
    
	@Check("any")
    public static void open(java.lang.Long id){
		Milestone entity = Milestone.findById(id);
		notFoundIfNull(entity);
		entity.isOpen = true;
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Milestone"));
		show(id);
    }
}
