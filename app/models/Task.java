package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import notifiers.Mails;

import java.text.SimpleDateFormat;
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
	public Date DueDate;
	
	@Required(message = "Owner is requiered")
	@ManyToOne
	@JoinColumn (name="Owner")
	public User Owner;
	
	@Required(message = "Milestone is required")
	@ManyToOne
	@JoinColumn (name="Milestone")
	public Milestone Milestone;
	
	@OneToMany (mappedBy="Task")
	public List<Comment> Comments;
	
	@OneToMany (mappedBy="Task")
	public List<Time> Times;
	
	@OneToMany(mappedBy="Task")
	public List<File> Files;
	
	@Required
	public boolean isOpen = true;
	
	public Date ClosedDate;
	
	@Transient
	public Double actual;
	
	public static int countByUser(java.lang.Long userId){
		
		int count = Task.find("Owner = ? AND isOpen = true", User.findById(userId)).fetch().size();
		return count;
		
	}
	
	public static List<Task> expireToday(){
		List<Task> tasks = Task.find("DueDate >= ? AND isOpen=true", new Date()).fetch();
				
		return tasks;
	}
	
	public static List<Task> expireThisWeek(){
		
        Calendar cal = Calendar.getInstance();;
        Date date = new Date();
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
		List<Task> tasks = Task.find("WEEK(DueDate) = ?  AND isOpen=true", week).fetch();
		
		return tasks;
	}
	
	@PrePersist 
    protected void onCreate() { 
            created = new Date(); 
    }
	
	@PostLoad
	protected void getActual(){
		Double act = 0.0;
		for(Time time : this.Times){
			act += time.time;
		}
		this.actual = (double) Math.round((act/60)*100)/100; // round to two decimals
	}
	
	public static Double getTotalHoursTime(java.lang.Long id) {
		Task entity = Task.findById(id);
		
		double deltaTime = 0.0;
		for (Time time : entity.Times) {
			deltaTime += time.time;
		}
		
		return (double) Math.round((deltaTime/60)*100)/100;
	}
	
	@PostPersist
    public void createdNotification(){
    	Mails mails = new Mails();
    	mails.taskCreated(this);
    }
    
    @PostUpdate
    public void updatedNotification(){
    	Mails mails = new Mails();
    	mails.taskUpdated(this);
    }
    
    public void closeTask() {
    	this.isOpen = false;
    	this.ClosedDate = new Date();
    	this.save();
    }
	
	public String toString(){
		return Name;
	}
}
