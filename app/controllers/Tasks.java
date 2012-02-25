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
		if(params.get("statusFilter") != null) {			
			if(params.get("statusFilter").equals("true")){ // open
				List<Task> entities = Task.find("Owner = ? and isOpen = true ORDER BY DueDate DESC", Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "true");
		        render(entities);
			} else if(params.get("statusFilter").equals("false")){ // closed
				List<Task> entities = Task.find("Owner = ? and isOpen = false ORDER BY DueDate DESC", Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "false");
		        render(entities);
			} else { // all
				List<Task> entities = Task.find("Owner = ? ORDER BY DueDate DESC", Security.getConnectedUser()).fetch();
				renderArgs.put("statusFilter", "all");
		        render(entities);
			}			
		}
    	
        List<Task> entities = Task.find("Owner = ? and isOpen = true ORDER BY DueDate DESC", Security.getConnectedUser()).fetch();
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
    
    public static void getTasksTimeGraph(java.lang.Long id) {
    	Task entity = Task.findById(id);
    	Double deltaDays = Math.ceil((double)(entity.DueDate.getTime() - entity.created.getTime() ) / (24 * 60 * 60 * 1000) );
    	
    	// estimated
    	Double estimatedTime = entity.estimated;
    	
    	Date date = new Date(entity.created.getTime());
    	
    	LinkedHashMap<String, Double> actualTimeDaysMap = new LinkedHashMap<String, Double>();
    	List<Double> actualTimeDays = new ArrayList<Double>();
    	List<Double> estimatedTimeDays = new ArrayList<Double>();
    	Double actualTime = 0.0;
    	
    	for(int i = 0; i <= deltaDays; i++) {
    		for(Time time : entity.Times) {
    			String created = new SimpleDateFormat("yyyy-MM-dd").format(time.created);
    			String actual = new SimpleDateFormat("yyyy-MM-dd").format(date);
				if(created.equals(actual)) {
					actualTime += time.time / 60;
					
					if (actualTimeDaysMap.containsKey(created)) {
						actualTime = actualTimeDaysMap.put(created, actualTime);
						actualTime += time.time / 60;
						actualTimeDaysMap.put(created, actualTime);
					} else {
						actualTimeDaysMap.put(created, actualTime);
					}
					
					actualTime = 0.0;
				}
			}
    		
    		date.setTime(date.getTime() + 1 * 24 * 60 * 60 * 1000); // add 1 day to actual date
    		actualTimeDaysMap.put(new SimpleDateFormat("yyyy-MM-dd").format(date), 0.0);
		}
    	
    	for(Double time : actualTimeDaysMap.values()) {
    		Double tmp = estimatedTime - time;
			estimatedTimeDays.add(tmp);
			
		}
    	
    	for(int i = actualTimeDaysMap.size(); i < deltaDays; i++) {
    		estimatedTimeDays.add(estimatedTime);
    	}
    	
    	for(Double time : actualTimeDaysMap.values()) {
			actualTimeDays.add(time);
		}
    	
    	Logger.info("Size estimated: %s", estimatedTimeDays.size());
    	Logger.info("Size actual: %s", actualTimeDays.size());
    	
    	Logger.info("JSON estimatedTimeDays: %s", new Gson().toJson(estimatedTimeDays));
    	Logger.info("JSON actualTimeDays: %s", new Gson().toJson(actualTimeDays));    	
    	
    	List<List<Double>> result = new ArrayList<List<Double>>();
    	result.add(estimatedTimeDays);
    	result.add(actualTimeDays);
    	
    	Logger.info("JSON: %s", new Gson().toJson(result));
    	
    	renderJSON(result);
    }
    
    public static void getGraph(java.lang.Long id) {
    	Task entity = Task.findById(id);
    	
    	Logger.info("------------ %s ------------", new Date());
    	
    	// Days
    	Double deltaDays = Math.ceil((double)(entity.DueDate.getTime() - entity.created.getTime() ) / (24 * 60 * 60 * 1000) );
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
    			
    			Logger.info("%s == %s", created.getTime(), date.getTime());
				if(created.compareTo(date) == 0) {
					actualTime += time.time / 60;
				}
    		}
    		
    		actualTimeList.add(actualTime);
    		actualTime = 0.0;
    		date.add(Calendar.DATE, 1);
    	}
    	
    	for(int i = 0; i <= deltaDays; i++) {
    		for(Double time : actualTimeList) {
    			//estimatedTime -= time;
    			break;
    		}
    		
    		//estimatedTime = 0.0;
    		//estimatedTimeList.add(estimatedTime);
    	}
    	
    	for(Double time : actualTimeList) {
    		estimatedTime -= time;
			estimatedTimeList.add(estimatedTime);
		}
    	
    	taskGraph.add(actualTimeList);
    	taskGraph.add(estimatedTimeList);
    	
    	Logger.info("JSON: %s", new Gson().toJson(actualTimeList));
    	
    	renderJSON(taskGraph);
    }
    
    public static void getTicks(java.lang.Long id) {
    	Task entity = Task.findById(id);
    	
    	List<String> ticks = new ArrayList<String>();
    	
    	Time lastTime = Time.find("Task = ? ORDER BY created DESC", entity).first();
    	Double deltaDays = Math.ceil( (double)(entity.DueDate.getTime() - entity.created.getTime() ) / (24 * 60 * 60 * 1000) );
    	Date actual = new Date(entity.created.getTime());
    	
    	for(int i = 0; i <= deltaDays; i++) {
    		ticks.add(new SimpleDateFormat("yyyy-MM-dd").format(actual));
			actual.setTime(actual.getTime() + 1 * 24 * 60 * 60 * 1000);
		}
    	
    	renderJSON(ticks);
    }
}
