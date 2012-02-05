package controllers;

import java.util.List;

import com.google.gson.Gson;

import models.Milestone;
import models.Task;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import play.i18n.Messages;
import play.data.validation.Validation;
import play.data.validation.Valid;

@With(Security.class)
@Check("any")
public class Tasks extends BaseController {
    public static void index() {
        List<Task> entities = models.Task.all().fetch();
        render(entities);
    }

    public static void create(Task entity) {
        render(entity);
    }

    public static void show(java.lang.Long id) {
        Task entity = Task.findById(id);
        render(entity);
    }

    public static void edit(java.lang.Long id) {
        Task entity = Task.findById(id);
        render(entity);
    }

    public static void delete(java.lang.Long id) {
        Task entity = Task.findById(id);
        entity.delete();
        index();
    }
    
    public static void save(@Valid Task entity) {
        if (validation.hasErrors()) {
            flash.error(Messages.get("scaffold.validation"));
            render("@create", entity);
        }
        //Logger.info("Saving Task: %s", "Name: "+entity.Name+" / Desc: "+entity.Description+" / Owner: "+entity.Owner.id +" / Milestone: "+entity.Milestone.id);
        entity.save();
        flash.success(Messages.get("scaffold.created", "Task"));
        Milestones.show(entity.Milestone.id);
    }

    public static void update(@Valid Task entity) {
        if (validation.hasErrors()) {
            flash.error(Messages.get("scaffold.validation"));
            render("@edit", entity);
        }
        
              entity = entity.merge();
        
        entity.save();
        flash.success(Messages.get("scaffold.updated", "Task"));
        index();
    }
    
    public static void close(java.lang.Long id){
		Task entity = Task.findById(id);
		notFoundIfNull(entity);
		entity.isOpen = false;
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Task"));
		Milestones.show(entity.Milestone.id);
    }
    
    public static void open(java.lang.Long id){
		Task entity = Task.findById(id);
		notFoundIfNull(entity);
		entity.isOpen = true;
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Task"));
		Milestones.show(entity.Milestone.id);
    }
}
