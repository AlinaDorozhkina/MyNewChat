import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private AuthService.Record record;
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public void authentication() throws IOException {
        AuthGui authGui = new AuthGui(myServer);
        while (authGui.isActive()) ;
        record = authGui.getPossibleRecord();
        myServer.subscribe(this);

        myServer.broadcastMsg(String.format("Client is connected %s", record.getName()));
        out.writeUTF("/authok");
        out.writeUTF("/name " + record.getName());
        out.writeUTF("/online " + getOnline());

        System.out.println(record.getName());

        System.out.println("/authok");
    }

    private StringBuilder getOnline() {
        StringBuilder sb = new StringBuilder();
        for (ClientHandler o : myServer.getClients()) {
            System.out.println(o.getRecord().getName());
            sb.append(o.getRecord().getName()).append(" ");
        }
        System.out.println("пользователи онлайн " + sb);
        return sb;
    }


    public void readMessages() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/")) {
                if (str.equals("/end")) {
                    break;
                }
                if (str.startsWith("/w ")) {
                    String[] tokens = str.split("\\s");
                    String nick = tokens[1];
                    String msg = str.substring(4 + nick.length());
                    myServer.sendMsgToClient(this, nick, msg);
                }
                continue;
            }
            myServer.broadcastMsg(record.getName() + ": " + str);
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMsg("unsibscribe " + this.record.getName());
        try {
            System.out.println("in is closed");
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
            System.out.println("out is closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
            System.out.println("socket is closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public AuthService.Record getRecord() {
        return record;
    }
}
