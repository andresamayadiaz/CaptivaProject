package controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import models.Milestone;
import models.Project;
import models.Time;
import models.User;
import models.Task;
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
	
	public static void burnDown(java.lang.Long id){
		Milestone entity = Milestone.findById(id);
		notFoundIfNull(entity);
		
		Double deltaDays = Math.ceil( (double)(entity.DueDate.getTime() - entity.created.getTime() ) / (24 * 60 * 60 * 1000) ); // Duration in Days
		Double totalEstimated = 0.0;
		Double totalCompleted = 0.0;
		int totalTasks = entity.Tasks.size();
		int totalCompletedTasks = 0;
		
		// GET A TOTAL ESTIMATED TIME
		for( Task task : entity.Tasks ){
			totalEstimated += task.estimated;
			if(!task.isOpen){
				totalCompleted += task.estimated;
				totalCompletedTasks++;
			}
		}
		
		Double deltaTime = (totalEstimated) / deltaDays;
		Double deltaTime2 = deltaTime;
		Date actual = new Date(entity.created.getTime());
		List<String[]> ideal = new ArrayList<String[]>();
		List<String[]> real = new ArrayList<String[]>();
		
		for(int i = 1; i <= deltaDays; i++){
			
			// IDEAL Data
			ideal.add(new String[]{actual.toString(), deltaTime2.toString()});
			
			// REAL Data
			real.add(new String[]{actual.toString(), ""});
			
			// Add deltaTime
			deltaTime2 += deltaTime;
			
			// Add 1 Day
			actual.setTime(actual.getTime()+1*24*60*60*1000);
			
		}
		
		Gson gson = new Gson();
		Logger.info("Estimated: %s", gson.toJson(ideal));
		
		renderJSON(ideal);
		
	}
	
	public static void plannedData(java.lang.Long id){
		Milestone entity = Milestone.findById(id);
		notFoundIfNull(entity);
		
		Double deltaDays = Math.ceil( (double)(entity.DueDate.getTime() - entity.created.getTime() ) / (24 * 60 * 60 * 1000) );
		Double deltaTasks = entity.Tasks.size() / deltaDays;
		Double deltaTask = deltaTasks;
		Date actual = new Date(entity.created.getTime());
		List<String[]> planned = new ArrayList<String[]>();
		
		for(int i = 1; i <= deltaDays; i++){
			
			// PLANNED
			planned.add(new String[]{actual.toString(), deltaTask.toString()});
			
			// UPDATE DATA
			deltaTask += deltaTasks;
			actual.setTime(actual.getTime()+1*24*60*60*1000); // add 1 day to actual date
			
		}
		
		renderJSON(planned);
		
	}
	
	public static void realData(java.lang.Long id){
		Milestone entity = Milestone.findById(id);
		notFoundIfNull(entity);
		
		// TODO: better to use total tasks or total milestone estimated???
		
		Double deltaDays = Math.ceil( (double)(entity.DueDate.getTime() - entity.created.getTime() ) / (24 * 60 * 60 * 1000) );
		Double deltaTasks = entity.Tasks.size() / deltaDays;
		Double deltaTask = deltaTasks;
		Double tmp = 0.0;
		Date actual = new Date(entity.created.getTime());
		List<String[]> real = new ArrayList<String[]>();
		
		for(int i = 1; i <= deltaDays; i++){
			
			// REAL
			for(Task task : entity.Tasks){
				if(task.ClosedDate != null){
					if(task.ClosedDate.compareTo(actual)<=0){
						tmp += 1;
					}
				}
			}
			real.add(new String[]{actual.toString(), tmp.toString()});
			
			// UPDATE DATA
			tmp = 0.0;
			deltaTask += deltaTasks;
			actual.setTime(actual.getTime()+1*24*60*60*1000); // add 1 day to actual date
			
		}
		
		renderJSON(real);
		
	}
	
}
