package models;

import play.*;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import notifiers.Mails;

import java.util.*;

@Entity
public class Project extends Model {
    
	@Required(message = "Name is requiered")
	@Column(unique=true)
	public String Name;
	
	@Required(message = "Description is requiered")
	public String Description;
	
	public Date created;
	
	public Date updated;
	
	@Required(message = "Owner is requiered")
	@ManyToOne
	@JoinColumn (name="Owner")
	public User Owner;
	
	@Required(message = "Due Date is requiered")
	public Date DueDate;
	
	@Required(message = "Status is requiered")
	public boolean isOpen = true;
	
	@OneToMany (mappedBy="Project")
	@OrderBy("DueDate DESC")
	public List<Milestone> Milestones;
	
	@Transient
	public int totalTasks;
	
	@Transient
	public int totalIssues;
	
	public Date ClosedDate;
	
	@PrePersist 
    protected void onCreate() { 
            created = new Date(); 
    }	
	
    @PreUpdate 
    protected void onUpdate() { 
            updated = new Date(); 
    }
    
    @PostLoad
    protected void calculatePostLoadData() {
    	
    	// total Tasks
    	int totalTask = 0;
		for(Milestone milestone : this.Milestones){
			if (milestone.Tasks != null) {
				totalTask += milestone.Tasks.size();
			}
		}
		
		this.totalTasks = totalTask;
		
		// total Issues
		int totalIssue = 0;
		for(Milestone milestone : this.Milestones){
			if (milestone.Issues != null) {
				totalIssue += milestone.Issues.size();
			}
		}
		
		this.totalIssues = totalIssue;
    }
    
    @PostPersist
    public void createdNotification(){
    	Mails mails = new Mails();
    	mails.projectCreated(this);
    }
    
    public static Double getTotalHoursTasksTime(java.lang.Long id) {
    	Project entity = Project.findById(id);
    	List<Milestone> milestones = Milestone.find("Project = ?", entity).fetch();
    	double deltaTime = 0.0;
    	
    	for (Milestone milestone : milestones) {
    		List<Task> tasks = Task.find("Milestone = ?", milestone).fetch();
    		for (Task task : tasks) {
    			List<Time> times = Time.find("Task = ?", task).fetch();
    			for (Time time : times) {
    				deltaTime += time.time;
    			}
    		}
    	}
		
		return (double) Math.round((deltaTime/60)*100)/100;
    }
    
    public static Double getTotalHoursIssuesTime(java.lang.Long id) {
    	Project entity = Project.findById(id);
    	List<Milestone> milestones = Milestone.find("Project = ?", entity).fetch();
    	double deltaTime = 0.0;
    	
    	for (Milestone milestone : milestones) {
    		List<Issue> issues = Issue.find("Milestone = ?", milestone).fetch();
    		for (Issue issue : issues) {
    			List<Time> times = Time.find("Issue = ?", issue).fetch();
    			for (Time time : times) {
    				deltaTime += time.time;
    			}
    		}
    	}
		
		return (double) Math.round((deltaTime/60)*100)/100;
    }
    
    public void closeProject() {
    	this.isOpen = false;
    	this.ClosedDate = new Date();
    	
    	for (Milestone milestone : this.Milestones) {
    		milestone.closeMilestone();
    	}
    	
    	this.save();
    }
    
    @PostUpdate
    public void updatedNotification(){
    	Mails mails = new Mails();
    	mails.projectUpdated(this);
    }
    
}
