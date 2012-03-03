package controllers;

import java.text.SimpleDateFormat;
import java.util.*;

import notifiers.Mails;

import com.google.gson.Gson;

import models.Comment;
import models.Milestone;
import models.Task;
import models.Time;
import models.User;
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
    	User user = null;
    	
    	if (params.get("userFilter") != null) {
    		if (!params.get("userFilter").equals("none")) {
    			user = User.findById(Long.parseLong(params.get("userFilter")));
    		}
    	}
    	
		if(params.get("statusFilter") != null) {			
			if(params.get("statusFilter").equals("true")) { // open
				List<Task> entities = Task.find("(?1 is null or Owner = ?1) and isOpen = true ORDER BY DueDate DESC", user != null ? user : Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "true");
				renderArgs.put("userFilter", user != null ? user.id : "none");
		        render(entities);
			} else if(params.get("statusFilter").equals("false")){ // closed
				List<Task> entities = Task.find("(?1 is null or Owner = ?1) and isOpen = false ORDER BY DueDate DESC", user != null ? user : Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "false");
				renderArgs.put("userFilter", user != null ? user.id : "none");
		        render(entities);
			} else { // all
				List<Task> entities = Task.find("(?1 is null or Owner = ?1) ORDER BY DueDate DESC", user != null ? user : Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "all");
				renderArgs.put("userFilter", user != null ? user.id : "none");
		        render(entities);
			}			
		}
    	
        List<Task> entities = Task.find("(?1 is null or Owner = ?1) and isOpen = true ORDER BY DueDate DESC", user != null ? user : Security.getConnectedUser()).fetch();
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
    	
        if (validation.hasErrors()) {
            flash.error(Messages.get("scaffold.validation"));
            render("@edit", entity);
        }
        
        if(entity.isOpen == true){
        	entity.ClosedDate = null;
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
		entity.ClosedDate = new Date();
		
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Task"));
		Milestones.show(entity.Milestone.id);
    }
    
    public static void open(java.lang.Long id) {
		Task entity = Task.findById(id);
		notFoundIfNull(entity);
		
		entity.isOpen = true;
		entity.ClosedDate = null;
		
		entity.save();
		flash.success(Messages.get("scaffold.updated", "Task"));
		Milestones.show(entity.Milestone.id);
    }
    
    public static void reminder(java.lang.Long id){
    	Task task = Task.findById(id);
    	notFoundIfNull(task);
    	
    	Mails mails = new Mails();
    	mails.taskReminder(task);
    	flash.success("Reminder Sent!");
    	show(id);
    }
    
    public static void getGraph(java.lang.Long id) {
    	Task entity = Task.findById(id);
    	
    	// Days
    	Time lastTime = Time.find("Task = ? ORDER BY created DESC", entity).first();
    	Double deltaDays = 0.0;
    	Double lastTimeValue = lastTime != null ? lastTime.created.getTime() : 0.0;
    	
    	if (entity.DueDate.getTime() > lastTimeValue) {
    		deltaDays = Math.ceil((double)(entity.DueDate.getTime() - entity.created.getTime()) / (24 * 60 * 60 * 1000));
    	} else {
    		deltaDays = Math.ceil((double)(lastTimeValue - entity.created.getTime()) / (24 * 60 * 60 * 1000));
    	}
    	
    	//Logger.info("Delta days: %s", deltaDays);
    	
    	Calendar date = Calendar.getInstance();
    	date.setTime(entity.created);
    	//date.set(Calendar.DATE, 23);
    	date.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), 0, 0, 0);
    	
    	// worked hours
    	List<Double> actualTimeList = new ArrayList<Double>();
    	Double actualTime = 0.0;
    	
    	// estimated hours
    	List<Double> estimatedTimeList = new ArrayList<Double>();
    	Double estimatedTime = entity.estimated;
    	
    	// json
    	List<List<Double>> taskGraph = new ArrayList<List<Double>>(); 
    	
    	for(int i = 0; i <= deltaDays; i++) {
    		for(Time time : entity.Times) {
    			Calendar created = Calendar.getInstance();
    			created.setTime(time.created);
    			created.set(created.get(Calendar.YEAR), created.get(Calendar.MONTH), created.get(Calendar.DATE), 0, 0, 0);
    			
				if(created.compareTo(date) == 0) {
					actualTime += time.time / 60;
				}
    		}
    		
    		actualTimeList.add(actualTime);
    		actualTime = 0.0;
    		date.add(Calendar.DATE, 1);
    	}
    	
    	int flag = 0;
    	double actualTimeAcumulated = 0.0;
    	for(Double time : actualTimeList) {
    		estimatedTime -= time;
    		actualTimeAcumulated += time;
			estimatedTimeList.add(estimatedTime);
			actualTimeList.set(flag, actualTimeAcumulated);
			flag++;
		}
    	
    	taskGraph.add(actualTimeList);
    	taskGraph.add(estimatedTimeList);
    	
    	//Logger.info("JSON taskGraph: %s", new Gson().toJson(taskGraph));
    	
    	renderJSON(taskGraph);
    }
    
    public static void getTicks(java.lang.Long id) {
    	Task entity = Task.findById(id);
    	
    	List<String> ticks = new ArrayList<String>();
    	
    	Time lastTime = Time.find("Task = ? ORDER BY created DESC", entity).first();
    	Double deltaDays = 0.0;
    	Double lastTimeValue = lastTime != null ? lastTime.created.getTime() : 0.0;
    	
    	if (entity.DueDate.getTime() > lastTimeValue) {
    		deltaDays = Math.ceil((double)(entity.DueDate.getTime() - entity.created.getTime() ) / (24 * 60 * 60 * 1000));
    	} else {
    		deltaDays = Math.ceil((double)(lastTimeValue - entity.created.getTime() ) / (24 * 60 * 60 * 1000));
    	}
    	
    	Date actual = new Date(entity.created.getTime());
    	
    	for(int i = 0; i <= deltaDays; i++) {
    		ticks.add(new SimpleDateFormat("yyyy-MM-dd").format(actual));
			actual.setTime(actual.getTime() + 1 * 24 * 60 * 60 * 1000);
		}
    	
    	//Logger.info("Delta Days getTicks(): %s", deltaDays);
    	//Logger.info("JSON ticks: %s", new Gson().toJson(ticks));
    	
    	renderJSON(ticks);
    }
}
