package models;

import play.*;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Time extends Model {
    
	@Required
	public Double time;
	
	@Required
	@MinSize(20)
	public String comment;
	
	@Required
	public User createdBy;
	
	public Date created;
	
	@Required(message = "Task is required")
	@ManyToOne
	@JoinColumn (name="Task")
	public Task Task;
	
	@PrePersist 
    protected void onCreate() { 
            created = new Date(); 
    }
	
	public String toString(){
		return time.toString()+" ["+comment+"]";
	}
	
}
