package models;

import play.*;
import play.data.validation.*;
import play.db.jpa.*;
import javax.persistence.*;
import java.util.*;

@Entity
public class Milestone extends Model {
    
	@Required(message = "Name is requiered")
	@MaxSize(50)
	public String Name;
	
	@Required(message = "Description is requiered")
	public String Description;
	
	public Date DueDate;
	
	@Required(message = "Owner is required")
	@ManyToOne
	public User Owner;
	
	@Required(message = "Status is required")
	public boolean isOpen = true;
	
	public String toStrign(){
		return Name;
	}
	
	@Required(message = "Name is required")
	public String name;
	
}
