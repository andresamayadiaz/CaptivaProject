package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import notifiers.Mails;

import java.util.*;

@Entity
public class Issue extends Model {
	
	@Required
	public String Name;
	
	@Required
	public String Description;
	
	@Required
	public Double estimated;
	
	public Date created;
	
	@Required
	public Date DueDate;
	
	@Required(message = "Owner is requiered")
	@ManyToOne
	@JoinColumn (name="Owner")
	public User Owner;
	
	@Required(message = "Milestone is required")
	@ManyToOne
	@JoinColumn (name="Milestone")
	public Milestone Milestone;
	
	@OneToMany (mappedBy="Issue")
	@OrderBy("created DESC")
	public List<Comment> Comments;
	
	@OneToMany (mappedBy="Issue")
	@OrderBy("created DESC")
	public List<Time> Times;
	
	@Required
	public boolean isOpen = true;
	
	public Date ClosedDate;
	
	@Transient
	public Double actual;
	
	public static int countByUser(java.lang.Long userId){
		
		int count = Issue.find("Owner = ? AND isOpen = true", User.findById(userId)).fetch().size();
		return count;
		
	}
	
	@PrePersist 
    protected void onCreate() { 
        created = new Date(); 
    }
	
	@PostLoad
	protected void getActual() {
		Double act = 0.0;
		for(Time time : this.Times){
			act += time.time;
		}
		this.actual = (double) Math.round((act/60)*100)/100; // round to two decimals
	}
	
	public static Double getTotalHoursTime(java.lang.Long id, Calendar startDate, Calendar dueDate) {
		Issue entity = Issue.findById(id);
		List<Time> times = Time.find("Issue = ? AND (created >= ? AND created <= ?) ORDER BY created DESC", entity, startDate.getTime(), dueDate.getTime()).fetch();
		
		double deltaTime = 0.0;
		for (Time time : times) {
			deltaTime += time.time;
		}
		
		return (double) Math.round((deltaTime/60)*100)/100;
	}
	
	@PostPersist
    public void createdNotification(){
    	Mails mails = new Mails();
    	mails.issueCreated(this);
    }
    
    @PostUpdate
    public void updatedNotification(){
    	Mails mails = new Mails();
    	mails.issueUpdated(this);
    }
    
    public void closeIssue() {
    	this.isOpen = false;
    	this.ClosedDate = new Date();
    	this.save();
    }
	
	public String toString(){
		return Name;
	}
}
