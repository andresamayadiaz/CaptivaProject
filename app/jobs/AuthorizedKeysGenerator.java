package jobs;

import models.SSHKey;
import models.User;
import play.Logger;
import play.Play;
import play.jobs.Job;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AuthorizedKeysGenerator extends Job {
    private final static File sshFolder = new File(Play.configuration.getProperty("git.userhome"), ".ssh");
    private static final String HEADER = "# Captiva Project";
    
    private boolean checkAuthorizedKeys(){
        File authFile = new File(sshFolder, "authorized_keys");
        Logger.debug("Verifing authorized_keys File: %s", authFile.getAbsolutePath());
        
        if(!authFile.exists()){
            return true;
        }
        BufferedReader reader = null;
        boolean result = false;
        try {
            reader = new BufferedReader(new FileReader(authFile));
            result = reader.readLine().startsWith(HEADER);
        } catch (Exception e) {
            Logger.error(e,"error while checkAuthorizedKeys");
        } finally {
            if(reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }
    
    @Override
    synchronized public void doJob() throws Exception {
        if(!checkAuthorizedKeys()){
            Logger.warn("cannot update authorized_keys, it will be overwritten");
            return;
        }
        
        File tempFile = File.createTempFile("gittemp", null);
        BufferedWriter bufferedWriter = null;
        boolean success = false;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(tempFile));
            bufferedWriter.append(HEADER+"\n");
            List<User> users = User.all().fetch();
            
            for (User user : users ) {
                String command = "command=\"$HOME/gitaccess " + user.id + "\",no-port-forwarding,no-X11-forwarding,no-agent-forwarding,no-pty";
                for (SSHKey entry : user.sshkeys) {
                    bufferedWriter.append(command);
                    bufferedWriter.append(' ');
                    bufferedWriter.append(entry.sshkey); // verify value
                    bufferedWriter.append(' ');
                    bufferedWriter.append(entry.id.toString()+"-"+entry.name.replace(' ', '_')); // use id, name or both ??? aad Feb 2012
                    bufferedWriter.append('\n');
                }
            }
            success=true;
        }
        finally {
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
        }
        if(success)
            tempFile.renameTo(new File(sshFolder, "authorized_keys"));
    }
}
