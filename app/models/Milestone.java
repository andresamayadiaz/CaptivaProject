package models;

import play.*;
import play.data.validation.*;
import play.db.jpa.*;
import javax.persistence.*;

import play.modules.paginate.ModelPaginator;
import play.modules.paginate.Paginator;
import play.mvc.Scope.Params;

import java.util.*;

@Entity
public class Milestone extends Model {
    
	@Required(message = "Name is required")
	@MaxSize(50)
	public String Name;
	
	@Required(message = "Description is required")
	@Lob
	public String Description;
	
	public Date DueDate;
	
	@Required(message = "Owner is required")
	@ManyToOne
	@JoinColumn (name="Owner")
	public User Owner;
	
	@Required(message = "Project is required")
	@ManyToOne
	@JoinColumn (name="Project")
	public Project Project;
	
	@OneToMany (mappedBy="Milestone")
	@OrderBy("DueDate DESC")
	public List<Task> Tasks;
	
	@OneToMany (mappedBy="Milestone")
	@OrderBy("DueDate DESC")
	public List<Issue> Issues;
	
	@OneToMany (mappedBy="Milestone")
	@OrderBy("created DESC")
	public List<Time> Times;
	
	@Required(message = "Status is required")
	public boolean isOpen = true;
	
	public Date created;
	
	public Date ClosedDate;
	
	public static int countByUser(java.lang.Long userId){
		int count = Milestone.find("Owner = ? AND isOpen = true", User.findById(userId)).fetch().size();
		return count;
	}
	
	public static Double totalPlannedTime(java.lang.Long id){
		
		Milestone entity = Milestone.findById(id);
		
		Double deltaTime = 0.0;
		for(Task task : entity.Tasks){
			deltaTime += task.estimated;
		}
		return deltaTime;
	}
	
	public static Double totalActualTime(java.lang.Long id){
		
		Milestone entity = Milestone.findById(id);
		
		Double deltaTime = 0.0;
		for(Time time : entity.Times){
			deltaTime += time.time;
		}
		return deltaTime;
	}
	
	public static Double getTotalEstimatedIssuesTime(java.lang.Long id) {
		Milestone entity = Milestone.findById(id);
		
		double deltaTime = 0.0;
		for (Issue issue : entity.Issues) {
			deltaTime += issue.estimated;
		}
		
		return deltaTime;
	}
	
	public static Double getTotalActualIssuesTime(java.lang.Long id) {
		Milestone entity = Milestone.findById(id);
		
		double deltaTime = 0.0;
		for (Issue issue : entity.Issues) {
			for(Time time : issue.Times) {
				deltaTime += time.time;
			}
		}
		
		return (double) Math.round((deltaTime/60)*100)/100; // round to two decimals;
	}
	
	public static Double getTotalEstimatedTasksTime(java.lang.Long id) {
		Milestone entity = Milestone.findById(id);
		
		double deltaTime = 0.0;
		for (Task issue : entity.Tasks) {
			deltaTime += issue.estimated;
		}
		
		return deltaTime;
	}
	
	public static Double getTotalActualTasksTime(java.lang.Long id) {
		Milestone entity = Milestone.findById(id);
		
		double deltaTime = 0.0;
		for (Task issue : entity.Tasks) {
			for(Time time : issue.Times) {
				deltaTime += time.time;
			}
		}
		
		return (double) Math.round((deltaTime/60)*100)/100; // round to two decimals;
	}
	
	@PrePersist
    protected void onCreate() {
		this.created = new Date(); 
    }
	
	public Paginator getTasks(Boolean isOpen) {
		//List<Task> tasks = Task.find("Milestone = ? and isOpen = ?", this, isOpen).fetch();
		Paginator tasks = new ModelPaginator(models.Task.class, "Milestone = ? and isOpen = ? ORDER BY dueDate ASC", this, isOpen).setPageSize(10);
		tasks.setParameterName("task" + isOpen.toString());
		
		return tasks;
	}
	
	public Paginator getIssues(Boolean isOpen) {
		//List<Issue> issues = Issue.find("Milestone = ? and isOpen = ?", this, isOpen).fetch();
		Paginator issues = new ModelPaginator(models.Issue.class, "Milestone = ? and isOpen = ? ORDER BY dueDate ASC", this, isOpen).setPageSize(10);
		return issues;
	}
	
	public void closeMilestone() {
    	this.isOpen = false;
    	this.ClosedDate = new Date();
    	
    	for(Task task : this.Tasks) {
    		task.closeTask();
    	}
    	
    	for(Issue issue : this.Issues) {
    		issue.closeIssue();
    	}
    	
    	this.save();
    }
	
	public String toString(){
		return Name;
	}
	
}
