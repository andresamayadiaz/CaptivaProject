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
	public String comment;
	
	@Required
	@ManyToOne
	@JoinColumn (name="CreatedBy")
	public User createdBy;
	
	public Date created;
	
	@ManyToOne
	@JoinColumn (name="Task")
	public Task Task;
	
	@ManyToOne
	@JoinColumn (name="Issue")
	public Issue Issue;
	
	@ManyToOne
	@JoinColumn (name="Milestone")
	public Milestone Milestone;
	
	@PrePersist 
    protected void onCreate() { 
		created = new Date(); 
    }
	
	public String toString(){
		return time.toString()+" ["+comment+"]";
	}
	
}
