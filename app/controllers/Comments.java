package controllers;

import java.util.List;
import models.Comment;
import models.User;
import play.mvc.Controller;
import play.mvc.With;
import play.i18n.Messages;
import play.data.validation.Validation;
import play.data.validation.Valid;

@With(Security.class)
@Check("any")
public class Comments extends BaseController {
    public static void index() {
        List<Comment> entities = models.Comment.all().fetch();
        render(entities);
    }

    public static void create(Comment entity) {
        render(entity);
    }

    public static void show(java.lang.Long id) {
        Comment entity = Comment.findById(id);
        render(entity);
    }

    public static void edit(java.lang.Long id) {
        Comment entity = Comment.findById(id);
        render(entity);
    }

    public static void delete(java.lang.Long id) {
        Comment entity = Comment.findById(id);
        entity.delete();
        index();
    }
    
    public static void save(Comment entity) {
    	entity.createdBy = User.find("byUserName", Security.connected()).<User>first();
    	
    	validation.valid(entity);
        if (validation.hasErrors()) {
            flash.error(Messages.get("scaffold.validation"));
            render("@create", entity);
        }
        entity.save();
        flash.success(Messages.get("scaffold.created", "Comment"));
        Tasks.show(entity.Task.id);
    }

    public static void update(@Valid Comment entity) {
        if (validation.hasErrors()) {
            flash.error(Messages.get("scaffold.validation"));
            render("@edit", entity);
        }
        
              entity = entity.merge();
        
        entity.save();
        flash.success(Messages.get("scaffold.updated", "Comment"));
        index();
    }
}
