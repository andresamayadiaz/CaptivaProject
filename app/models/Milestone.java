package models;

import play.*;
import play.data.validation.*;
import play.db.jpa.*;
import javax.persistence.*;

import java.util.*;

@Entity
public class Milestone extends Model {
    
	@Required(message = "Name is required")
	@MaxSize(50)
	public String Name;
	
	@Required(message = "Description is required")
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
	
	@PrePersist
    protected void onCreate() {
		this.created = new Date(); 
    }
	
	public String toString(){
		return Name;
	}
	
}
