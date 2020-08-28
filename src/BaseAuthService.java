import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    DataBaseUsers data;


    public BaseAuthService() {
        data = new DataBaseUsers();
    }

    @Override
    public Record findRecord(String login, String password) {
        for (Record r : DataBaseUsers.users) {
            if (r.getLogin().equals(login) && r.getPassword().equals(password)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public void addRecord(String name, String login, String password) {
        data.addUser(name, login, password);
    }

    @Override
    public boolean isLoginFree(String login) {
        for (Record r : DataBaseUsers.users) {
            if (r.getLogin().equals(login)) {
                return false;
            }
        }

        return true;
    }


    @Override
    public void start() {
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
    }


    static class DataBaseUsers {
        private Connection connection;
        private static List<AuthService.Record> users;

        public DataBaseUsers() {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Driver not found");
            }

            try {
                DriverManager.registerDriver(new Driver());
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_users", "root", "root");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                throw new RuntimeException("Driver Registration error");
            }
            try {

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
                users = new ArrayList<>();
                while (resultSet.next()) {
                    users.add(new AuthService.Record(
                                    resultSet.getInt("id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("login"),
                                    resultSet.getString("password")
                            )
                    );
                }


            } catch (SQLException throwables) {
                throwables.printStackTrace();
                throw new RuntimeException("Statement error");
            }


        }

        public static List<Record> getUsers() {
            return users;
        }

        public void addUser(String name, String login, String password) {
            try {
                connection.setAutoCommit(false);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users (name, login, password) VALUE (?, ?,?)");
                statement.setString(1, name);
                statement.setString(2, login);
                statement.setString(3, password);

                statement.executeUpdate(); // запись execute для insert , executeQuery для select или delete
                connection.commit();// если все ок, то коммит и сохраняем в бд
                System.out.println("это из даты" + users.toString());
            } catch (SQLException throwables) {
                try {
                    connection.rollback(); // если не ок, то роллбек и отменяем что наделали, откатываемся назад
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("это исключенте");
                }
                throwables.printStackTrace();
                throw new RuntimeException("Statement error");
            }

        }
    }
}






