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
	public User Owner;
	
	@Required(message = "Project is required")
	@ManyToOne
	@JoinColumn (name="Project")
	public Project Project;
	
	@OneToMany (mappedBy="Milestone")
	@OrderBy("DueDate DESC")
	public List<Task> Tasks;
	
	@Required(message = "Status is required")
	public boolean isOpen = true;
	
	public Date created;
	
	@PrePersist
    protected void onCreate() {
		this.created = new Date(); 
    }
	
	public String toString(){
		return Name;
	}
	
}
