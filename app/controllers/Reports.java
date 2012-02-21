package controllers;

import models.Issue;
import models.Milestone;
import models.Project;
import models.Task;

import org.joda.time.DateTime;

import play.Logger;
import play.mvc.*;
import static play.modules.pdf.PDF.*;

@With(Security.class)
public class Reports extends Controller {

	@Check("any")
    public static void project(java.lang.Long id) {
    	Project entity = Project.findById(id);
    	Options options = new Options();
    	options.filename = entity.Name + ".pdf";
    	renderPDF(entity, options);
    }
}
