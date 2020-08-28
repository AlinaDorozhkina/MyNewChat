import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.startsWith("/online")) {
                                String[] logs = strFromServer.split("\\s");
                                usersOnline = new HashSet<>();
                                for (int i = 1; i < logs.length; i++) {
                                    usersOnline.add(logs[i]);
                                }
                                System.out.println("online " + usersOnline);
                                break;
                            }
                        }

                        ChatGui chat = new ChatGui(usersOnline, name, in, out);

                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.equals("/end")) {
                                break;
                            }
                            chat.getTextArea().append(strFromServer);
                            chat.getTextArea().append("\n");

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
