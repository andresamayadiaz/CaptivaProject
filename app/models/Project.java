package models;

import play.*;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

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
	
	@PrePersist 
    protected void onCreate() { 
            created = new Date(); 
    }	
	
    @PreUpdate 
    protected void onUpdate() { 
            updated = new Date(); 
    }
    
    @PostLoad
    protected void getTotalTasks() {
    	int totalTask = 0;
		for(Milestone milestone : this.Milestones){
			if (milestone.Tasks != null) {
				totalTask += milestone.Tasks.size();
			}
		}
		
		this.totalTasks = totalTask;
    }
}
