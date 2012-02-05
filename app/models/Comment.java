package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import controllers.Secure.Security;

import java.util.*;

@Entity
public class Comment extends Model {
    
	@Required
	public String comment;
	
	@Required
	@ManyToOne
	public User createdBy;
	
	@Required(message = "Task is required")
	@ManyToOne
	@JoinColumn (name="Task")
	public Task Task;
	
	public Date created;
	
	@PrePersist 
    protected void onCreate() { 
            created = new Date();
    }
	
}
