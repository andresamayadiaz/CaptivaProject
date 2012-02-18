package controllers;

import models.File;
import models.Task;
import models.User;
import play.Logger;
import play.data.Upload;
import play.db.jpa.Blob;
import play.i18n.Messages;
import play.mvc.*;

@With(Security.class)
@Check("any")
public class Files extends Controller {

    public static void index() {
        render();
    }
    
    public static void save(Upload entity, java.lang.Long taskId) {
    	File newFile = new File();
    	newFile.name = entity.getFileName();
    	
    	Blob blob = new Blob();
    	blob.set(entity.asStream(), entity.getContentType());
    	newFile.file = blob;
    	
    	newFile.contentType = entity.getContentType();
    	newFile.Owner = Security.getConnectedUser();
    	newFile.Task = Task.findById(taskId);
    	
    	validation.valid(newFile);
        if (validation.hasErrors()) {
            flash.error(Messages.get("scaffold.validation"));
            render("@create", newFile);
        }
        
        newFile.save();
        flash.success(Messages.get("scaffold.created", "File"));
        Tasks.show(newFile.Task.id);
    }
    
    public static void get(Long id) {
    	File entity = File.findById(id);
    	response.contentType = entity.contentType;
    	renderBinary(entity.file.get());
    }
}
