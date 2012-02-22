package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;

import models.Issue;
import models.Milestone;
import models.Project;
import models.Task;

import org.allcolor.yahp.converter.IHtmlToPdfTransformer;
import org.joda.time.DateTime;
import org.xhtmlrenderer.css.value.PageSize;

import play.Logger;
import play.data.binding.As;
import play.data.validation.Error;
import play.data.validation.Required;
import play.mvc.*;
import static play.modules.pdf.PDF.*;

@With(Security.class)
public class Reports extends BaseController {
	
	@Check("admin")
	public static void index() {
		render();
	}

	@Check("admin")
    public static void project(@Required java.lang.Long id) {
    	Project entity = Project.findById(id);
    	Options options = new Options();
    	options.pageSize = IHtmlToPdfTransformer.A4P;
    	options.filename = entity.Name + " - " + new SimpleDateFormat("yyyy-MM-dd").format(entity.created)  + ".pdf";
    	renderPDF(entity, options);
    }
	
	@Check("admin")
	public static void projectDetailedByDates(@Required java.lang.Long id, @As("yyyy-MM-dd") @Required Calendar startDate, @As("yyyy-MM-dd") @Required Calendar dueDate) {
		if (validation.hasErrors()) {
			for(Error error : validation.errors()) {
				Logger.info("@ " + error.getKey());
			}
			render("@index", id, startDate, dueDate);
		}
		
		Project entity = Project.findById(id);
		List<Task> tasksByRange = new ArrayList();
		List<Issue> issuesByRange = new ArrayList();
		
		dueDate.set(Calendar.HOUR, 23);
		dueDate.set(Calendar.MINUTE, 59);
		dueDate.set(Calendar.SECOND, 59);
		dueDate.set(Calendar.MILLISECOND, 59);
		
		for(Milestone milestone : entity.Milestones) {
			List<Task> result = Task.find("Milestone = ? AND (created >= ? AND created <= ?) ORDER BY isOpen DESC", milestone, startDate.getTime(), dueDate.getTime()).fetch();
			for (Task task : result) {
				if (task.Times.size() > 0) {
					tasksByRange.add(task);
				}
			}
		}
		
		for(Milestone milestone : entity.Milestones) {
			List<Issue> result = Issue.find("Milestone = ? AND (created >= ? AND created <= ?) ORDER BY isOpen DESC", milestone, startDate.getTime(), dueDate.getTime()).fetch();
			for (Issue issue : result) {
				if (issue.Times.size() > 0) {
					issuesByRange.add(issue);
				}
			}
		}
		
		Options options = new Options();
    	options.pageSize = IHtmlToPdfTransformer.A4P;
    	options.filename = entity.Name + " - " + new SimpleDateFormat("yyyy-MM-dd").format(entity.created)  + ".pdf";
		
		renderPDF(entity, tasksByRange, issuesByRange, startDate, dueDate, options);
	}
}
