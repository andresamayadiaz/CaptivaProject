package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

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
	public List<Comment> Comments;
	
	@OneToMany (mappedBy="Issue")
	public List<Time> Times;
	
	@Required
	public boolean isOpen = true;
	
	@Transient
	public Double actual;
	
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
	
	public String toString(){
		return Name;
	}
}
