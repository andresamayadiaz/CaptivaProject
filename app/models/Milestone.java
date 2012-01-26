package models;

import play.*;
import play.data.validation.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Milestone extends Model {
    
	@Required(message = "Name is required")
	public String name;
	
}
