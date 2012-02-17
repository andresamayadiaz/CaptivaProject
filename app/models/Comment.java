package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import notifiers.Mails;

import controllers.Secure.Security;

import java.util.*;

@Entity
public class Comment extends Model {
    
	@Required
	public String comment;
	
	@Required
	@ManyToOne
	@JoinColumn (name="CreatedBy")
	public User createdBy;
	
	@ManyToOne
	@JoinColumn (name="Task")
	public Task Task;
	
	@ManyToOne
	@JoinColumn (name="Issue")
	public Issue Issue;
	
	public Date created;
	
	@PrePersist 
    protected void onCreate() { 
		created = new Date();
    }
	
	@PostPersist
	public void newComment(){
		Mails mails = new Mails();
		if(Task != null){
			mails.taskComment(this);
		}
		if(Issue != null){
			mails.issueComment(this);
		}
	}
	
}
