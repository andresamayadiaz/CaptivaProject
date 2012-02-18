package jobs;

import java.util.List;

import com.jcraft.jsch.Logger;

import notifiers.Mails;

import play.jobs.*;
import models.Task;

@On("0 0 06 * * ?")
public class Notifications extends Job {
	
	Mails mails = new Mails();
	
	public void doJob(){
		
		// Get All Tasks That Expire Today
		for(Task task : Task.expireToday()){
			
			mails.taskExpire(task);
			
		}
		
		// Get All Tasks That Expire This Week
		for(Task task : Task.expireThisWeek()){
			
			mails.taskReminder(task);
			
		}
		
	}
	
}
