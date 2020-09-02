import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class BaseAuthService implements AuthService {
    DBConfig data;
    Connection connection;

    public BaseAuthService() {
        data = new DBConfig("jdbc:mysql://localhost:3306/chat_users", "root", "root");
        connection = data.getConnection();
    }

    @Override
    public Record findRecord(String login, String password) {
        Connection connection = data.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT *FROM users WHERE login =? AND password = ?");
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Record(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("login"),
                        resultSet.getString("password")
                );
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
//        } finally {
//            try {
//                connection.close();
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        }

        }
        return null;
    }

    @Override
    public void addRecord(String name, String login, String password) {
        try {
            PreparedStatement preStatement = connection.prepareStatement("INSERT INTO users (name, login, password) VALUE (?, ?,?)");
            preStatement.setString(1, name);
            preStatement.setString(2, login);
            preStatement.setString(3, password);
            preStatement.execute();
            connection.commit();
        }catch (SQLException throwables){
            try {
                connection.rollback(); // если не ок, то роллбек и отменяем что наделали, откатываемся назад
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throwables.printStackTrace();
            throw new RuntimeException("Statement error");

        }
    }

    @Override
    public boolean isLoginFree(String login) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE login =?");
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return false;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
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

}





