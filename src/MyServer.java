import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;


public class MyServer  {
    public static final int PORT = 8888;
    private Set<ClientHandler> clients;
    private AuthService authService;
    private BaseAuthService bas;

    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new BaseAuthService();
            authService.start();
            clients = new HashSet<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка в работе сервера");
        } finally {
            if (authService != null) {
                authService.stop();
            }
        }
    }
    public AuthService getAuthService() {
        return authService;
    }

    public synchronized boolean isOccupied (AuthService.Record record){
        for (ClientHandler ch:clients){
            if (ch.getRecord().equals(record)){
                return true;
            }
        }
        return false;
    }


    public synchronized void sendMsgToClient(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nickTo)) {
                o.sendMsg("от " + from.getName() + ": " + msg);
                from.sendMsg("клиенту " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Участника с ником " + nickTo + " нет в чат-комнате");
    }

    public synchronized void broadcastClientsList() {
        StringBuilder sb = new StringBuilder();
        for (ClientHandler o : clients) {
            System.out.println(o.getRecord().getName());
            sb.append(o.getRecord().getName()).append(" ");
        }
        broadcastMsg(sb.toString());
    }



    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
       broadcastClientsList();
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
        broadcastClientsList();
    }
    public Set<ClientHandler> getClients() {
        return clients;
    }
}


