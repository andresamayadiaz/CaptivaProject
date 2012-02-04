package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Comment extends Model {
    
	@Required
	public String comment;
	
	@Required
	public User createdBy;
	
	public Date created;
	
	@PrePersist 
    protected void onCreate() { 
            created = new Date(); 
    }
	
}
