package model;

import com.jcraft.jsch.*;
import org.controlsfx.dialog.Dialogs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class SSHConnect {

    private final String SFTPHOST;
    private final int    SFTPPORT = 22;
    private final String SFTPUSER;
    private final String SFTPPASS;
//    private final String SFTPWORKINGDIR = "/etc/pektoral/";
    private final String SFTPWORKINGDIR = "/home/vadym/";

    private Session     session     = null;
    private Channel     channel     = null;
    private ChannelSftp channelSftp = null;

    public SSHConnect(String host, String user, String password) {
        SFTPHOST = host;
        SFTPUSER = user;
        SFTPPASS = password;
    }

    public List<ConfValue> loadConfFileFromSSH(){
        List<ConfValue> result = null;
        try{
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER,SFTPHOST,SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            channelSftp.cd(SFTPWORKINGDIR);
            BufferedReader readFromFile = new BufferedReader(new InputStreamReader(channelSftp.get("pektoralTest.conf")));
            result = Parse.parseFile(readFromFile);
        }catch(JSchException ex){
            Dialogs.create()
                    .title("Lost connection")
                    .masthead("Something wrong with your connection...")
                    .message("Maybe correct you login or password.'")
                    .showWarning();
        }catch(Exception ex){
            ex.printStackTrace();
            Dialogs.create()
                    .title("Error")
                    .masthead("Something wrong with your connection...")
                    .message("No file or directory. Maybe something else...")
                    .showWarning();
        }
        channelSftp.disconnect();
        channelSftp.exit();
        session.disconnect();
        channel.disconnect();
        return result;
    }

    public void saveFileOnSSH(List<ConfValue> list) {
        try {
            long begin = System.currentTimeMillis();
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);

            OutputStream os = channelSftp.put("pektoralTest.conf", ChannelSftp.OVERWRITE);
            OutputStreamWriter safeFile = new OutputStreamWriter (os);
            for (ConfValue values : list){
                safeFile.write(values.getTitle() + " " + values.getValue() + "\n");
                Thread.sleep(100);
            }
            os.flush();
            safeFile.flush();
            safeFile.close();
            os.close();
            channelSftp.disconnect();
            session.disconnect();
            channel.disconnect();
            channelSftp.exit();

            long finish = System.currentTimeMillis();

            System.out.println("begin: " + begin + " - " + finish + " = " + (begin - finish));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}