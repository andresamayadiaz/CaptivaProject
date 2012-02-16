package controllers;

import java.util.List;
import models.Time;
import models.User;
import play.mvc.Controller;
import play.i18n.Messages;
import play.data.validation.Validation;
import play.data.validation.Valid;

public class Times extends Controller {
    public static void index() {
        List<Time> entities = models.Time.all().fetch();
        render(entities);
    }
    
    public static void create(Time entity) {
        render(entity);
    }

    public static void show(java.lang.Long id) {
        Time entity = Time.findById(id);
        render(entity);
    }

    public static void edit(java.lang.Long id) {
        Time entity = Time.findById(id);
        render(entity);
    }

    public static void delete(java.lang.Long id) {
        Time entity = Time.findById(id);
        entity.delete();
        index();
    }
    
    public static void save(Time entity) {
    	entity.createdBy = User.find("byUserName", Security.connected()).<User>first();
    	
    	validation.valid(entity);
        if (validation.hasErrors()) {
            flash.error(Messages.get("scaffold.validation"));
            render("@create", entity);
        }
        entity.save();
        flash.success(Messages.get("scaffold.created", "Time"));
        
        if (entity.Task != null) {
        	Tasks.show(entity.Task.id);
        } else {
        	Issues.show(entity.Issue.id);
        }
    }

    public static void update(@Valid Time entity) {
        if (validation.hasErrors()) {
            flash.error(Messages.get("scaffold.validation"));
            render("@edit", entity);
        }
        
              entity = entity.merge();
        
        entity.save();
        flash.success(Messages.get("scaffold.updated", "Time"));
        index();
    }
}
