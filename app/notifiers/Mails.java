package notifiers;

import models.*;
import play.Play;
import play.mvc.Mailer;

public class Mails extends Mailer {
	
	public static String mailFrom = Play.configuration.getProperty("mail.from");
	public static String mailCC = Play.configuration.getProperty("mail.addcc");
	
	public static void welcome(User user) {
		
		setSubject("Welcome: %s", user.fullName);
		addRecipient(user.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		// EmailAttachment attachment = new EmailAttachment();
		// attachment.setDescription("A pdf document");
		// attachment.setPath(Play.getFile("rules.pdf").getPath());
		// addAttachment(attachment);
		send(user);
		
	}
	
	// PROJECTS
	public static void projectCreated(Project project){
		
		setSubject("Project Created: %s", project.Name);
		addRecipient(project.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(project);
	}
	
	public static void projectUpdated(Project project){
		
		setSubject("Project Updated: %s", project.Name);
		addRecipient(project.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(project);
	}
	
	// TASKS
	public static void taskCreated(Task task){
		
		setSubject("Task Created: %s", task.Name);
		addRecipient(task.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(task);
	}
	
	public static void taskUpdated(Task task){
		
		setSubject("Task Updated: %s", task.Name);
		addRecipient(task.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(task);
	}
	
	public static void taskReminder(Task task){
		
		setSubject("Task Reminder: %s", task.Name);
		addRecipient(task.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(task);
	}
	
	public static void taskExpire(Task task){
		
		setSubject("Task Expiration: %s", task.Name);
		addRecipient(task.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(task);
	}
	
	public static void taskComment(Comment comment){
		
		setSubject("New Comment on Task: %s", comment.Task.Name);
		addRecipient(comment.Task.Owner.userName);
		addRecipient(comment.Task.Milestone.Project.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(comment);
	}
	
	// ISSUES
	public static void issueCreated(Issue issue){
		
		setSubject("Issue Created: %s", issue.Name);
		addRecipient(issue.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(issue);
	}
	
	public static void issueUpdated(Issue issue){
		
		setSubject("Issue Updated: %s", issue.Name);
		addRecipient(issue.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(issue);
	}
	
	public static void issueComment(Comment comment){
		
		setSubject("New Comment on Issue: %s", comment.Issue.Name);
		addRecipient(comment.Issue.Owner.userName);
		addRecipient(comment.Issue.Milestone.Project.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(comment);
	}
	
	// TODO: implementar
	public static void lostPassword(User user) {

	}
	
}
