package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Task extends Model {
    
	@Required
	public String Name;
	
	@Required
	public String Description;
	
	@Required
	public Double estimated;
	
	public Date created;
	
	@Required
	public User Owner;
	
	@Required(message = "Milestone is required")
	@ManyToOne
	@JoinColumn (name="Milestone")
	public Milestone Milestone;
	
	@Required
	public boolean isOpen = true;
	
	@PrePersist 
    protected void onCreate() { 
            created = new Date(); 
    }
	
	public String toString(){
		return Name;
	}
}
