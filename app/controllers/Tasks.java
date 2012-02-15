package controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    	// check if filter apply
		if(params.get("statusFilter") != null) {			
			if(params.get("statusFilter").equals("true")){ // open
				List<Task> entities = Task.find("Owner = ? and isOpen = true ORDER BY Name", Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "true");
		        render(entities);
			} else if(params.get("statusFilter").equals("false")){ // closed
				List<Task> entities = Task.find("Owner = ? and isOpen = false ORDER BY Name", Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "false");
		        render(entities);
			} else { // all
				List<Task> entities = Task.find("Owner = ? ORDER BY Name", Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "all");
		        render(entities);
			}			
		}
    	
        List<Task> entities = Task.find("Owner = ? and isOpen = true ORDER BY Name", Security.getConnectedUser()).fetch();
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
        show(entity.id);
    }
    
    public static void update(@Valid Task entity) {
    	
    	/*if(entity.isOpen) {
    		entity.isOpen = true;
    	} else {
    		entity.isOpen = false;
    	}*/
    	
    	Logger.info("isOpen: " + entity.isOpen);
    	
        if (validation.hasErrors()) {
            flash.error(Messages.get("scaffold.validation"));
            render("@edit", entity);
        }
        
        entity = entity.merge();
        entity.save();
        flash.success(Messages.get("scaffold.updated", "Task"));
        show(entity.id);
    }
    
    public static void close(java.lang.Long id) {
		Task entity = Task.findById(id);
		notFoundIfNull(entity);
		entity.isOpen = false;
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Task"));
		Milestones.show(entity.Milestone.id);
    }
    
    public static void open(java.lang.Long id) {
		Task entity = Task.findById(id);
		notFoundIfNull(entity);
		entity.isOpen = true;
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Task"));
		Milestones.show(entity.Milestone.id);
    }
    
    public static void graphData(java.lang.Long id) {
    	Task entity = Task.findById(id);
		notFoundIfNull(entity);
		
		Double deltaDays = Math.ceil( (double)(entity.DueDate.getTime() - entity.created.getTime() ) / (24 * 60 * 60 * 1000) );
		Double deltaTime = (entity.estimated*60) / deltaDays;
		Double deltaTime2 = deltaTime * deltaDays;
		Date actual = new Date(entity.created.getTime());
		List<String[]> chart = new ArrayList<String[]>();
		//Logger.info("deltaDays: " + deltaDays + " deltaTime: " + deltaTime + " DueDate: "+entity.DueDate.getTime() + " created: "+entity.created.getTime());
		
		for(int i = 1; i <= deltaDays; i++){
			deltaTime2 -= deltaTime;
			//chart.put(actual.toString(), deltaTime2.toString());
			chart.add(new String[]{actual.toString(), deltaTime2.toString()});
			actual.setTime(actual.getTime()+1*24*60*60*1000); // add 1 day to actual date
		}
		
		renderJSON(chart);
    }
}
