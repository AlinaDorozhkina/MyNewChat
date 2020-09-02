import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Client extends JFrame {
    private static String name;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean authorized;
    private static Set<String> usersOnline;

    public Client() {

        openConnection();

    }

    public void openConnection() {
        try {
            socket = new Socket("localhost", 8888);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            setAuthorized(false);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.startsWith("/authok")) {
                                setAuthorized(true);
                                break;
                            }
                        }
                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.startsWith("/name")) {
                                name = strFromServer.split("\\s")[1];
                                System.out.println("name " + name);
                                break;
                            }
                        }
                        /*while (true) {
                            String strFromServer = in.readUTF();

                            if (strFromServer.startsWith("/online")) {
                                String[] logs = strFromServer.split("\\s");
                                usersOnline = new HashSet<>();
                                for (int i = 1; i < logs.length; i++) {
                                    usersOnline.add(logs[i]);
                                }
                                System.out.println("online from client" + usersOnline);
                                break;
                            }
                        }

                         */


                        ChatGui chat = new ChatGui(name, in, out);



                        while (true) {
                            String strFromServer = in.readUTF();

                            if (strFromServer.equals("/end")) {
                                break;
                            }

                            /*if (strFromServer.startsWith("Client is connected")){
                                String[] logs = strFromServer.split("\\s");
                                chat.getOnline().append(logs[3]);
                                chat.getOnline().append("\n");
                            }

                             */
                            if (strFromServer.startsWith("/online")){
                                String[] logs = strFromServer.split("\\s");
                                for (int i=1; i<logs.length;i++){
                                    chat.getOnline().append(logs[i]);
                                    chat.getOnline().append("\n");
                                }
                            }
                             if (strFromServer.startsWith("Client is connected")){
                                String[] logs = strFromServer.split("\\s");
                                chat.getOnline().append(logs[3]);
                                chat.getOnline().append("\n");
                            }

                           /*if (strFromServer.startsWith("unsibscribe")){
                                String [] unsibscribed = strFromServer.split("\\s");
                                String log = unsibscribed[1];
                                System.out.println("log"+log);
                                String [] users=chat.getOnline().getText().split("\n");
                                System.out.println("юзеры"+ Arrays.toString(users));
                                for (int i=0; i<users.length;i++){
                                    if (users[i].equals(log)) {
                                        System.out.println("log is delited");
                                    }else{
                                        chat.getOnline().setText(users[i]);
                                    }
                                }
                            }

                            */
                            chat.getTextArea().append(strFromServer);
                            chat.getTextArea().append("\n");
                            //FileWorker.write("C:/Users/DorozhkinaAlina/IdeaProjects/MyNewChat/src/a.txt", strFromServer);



                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }


}
