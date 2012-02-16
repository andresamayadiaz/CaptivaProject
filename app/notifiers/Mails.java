package notifiers;

import models.*;
import play.Play;
import play.mvc.Mailer;

public class Mails extends Mailer {
	
	public static String mailFrom = Play.configuration.getProperty("mail.from");
	public static String mailCC = Play.configuration.getProperty("mail.addcc");
	
	public static void welcome(User user) {
		
		setSubject("Welcome %s", user.fullName);
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
		
		setSubject("Project Created: %s", project.Description.substring(0, 20));
		addRecipient(project.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(project);
	}
	
	public static void projectUpdated(Project project){
		
		setSubject("Project Updated: %s", project.Description.substring(0, 20));
		addRecipient(project.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(project);
	}
	
	// TASKS
	public static void taskCreated(Task task){
		String mailFrom = Play.configuration.getProperty("mail.from");
		
		setSubject("Task Created: %s", task.Description.substring(0, 20));
		addRecipient(task.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(task);
	}
	
	public static void taskUpdated(Task task){
		
		setSubject("Task Updated: %s", task.Description.substring(0, 20));
		addRecipient(task.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(task);
	}
	
	public static void taskReminder(Task task){
		
		setSubject("Task Reminder: %s", task.Description.substring(0, 20));
		addRecipient(task.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(task);
	}
	
	public static void taskExpire(Task task){
		
		setSubject("Task Expiration: %s", task.Description.substring(0, 20));
		addRecipient(task.Owner.userName);
		addCc(mailCC);
		setFrom(mailFrom);
		
		send(task);
	}
	
	// TODO: implementar
	public static void lostPassword(User user) {

	}
	
}
