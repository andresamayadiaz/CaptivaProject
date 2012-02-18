package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class File extends Model {
    @Required
    public String name;
    
    @Required
    public Blob file;
    
    @Required
    public String contentType;
    
    @Required
    @ManyToOne
    @JoinColumn(name="Owner")
    public User Owner;
    
    @Required
    @ManyToOne
    @JoinColumn(name="Task")
    public Task Task;
    
    @Override
    public String toString() {
    	return "name: " + this.name + "filename: " + this.file.getFile().getPath();
    }
}
