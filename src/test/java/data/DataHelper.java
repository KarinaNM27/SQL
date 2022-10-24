package data;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;

public class DataHelper {

    private static final String vasyaParolEncrypted = "$2a$10$UPfoSG94Lm3aCh./w1TcK.PL8VCcu8LFy8wWilE3QigM/k77I1L7a";
    private static final String petyaParolEncrypted = "$2a$10$Rfw1kejtiFZaFI7tTTdl5O9Bf2Cd6zv9So2sjqhcPTk44Y8BU1/yC";

//        private static final String vasyaPassEncrypted = "eeeee";
//    private static final String petyaPassEncrypted = "11111";
    private static final String vasyaLogin = "vasya";
    private static final String vasyaPass = "qwerty123";

    private DataHelper() {
    }

    @SneakyThrows
    private static String requestCode(User user) {
        Thread.sleep(500);
        var runner = new QueryRunner();
        var sqlRequestSortByTime = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3305/app-db", "user", "pass"
                );

        ) {
            return runner.query(conn, sqlRequestSortByTime, user.getId(), new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static User getAuthInfo() {
        return new User(getDataBaseId(vasyaLogin), vasyaLogin, vasyaPass);
    }

    public static String getVerificationCodeFor(User authInfo) {
        return requestCode(authInfo);
    }

    public static String getRandomPass() {
        return new Faker().internet().password();
    }

    @SneakyThrows
    private static String getDataBaseId(String login) {
        var runner = new QueryRunner();
        var sqlRequestTakeUserId = "SELECT id FROM users WHERE login = ?";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3305/app-db", "user", "pass"
                );
        ) {
            return runner.query(conn, sqlRequestTakeUserId, login, new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static void clearSUTData() {
        var runner = new QueryRunner();
        var sqlDeleteAllAuthCodes = "DELETE FROM auth_codes;";
        var sqlDeleteAllCards = "DELETE FROM cards;";
        var sqlDeleteAllUsers = "DELETE FROM users;";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3305/app-db", "user", "pass"
                );
        ) {
            runner.update(conn, sqlDeleteAllAuthCodes);
            runner.update(conn, sqlDeleteAllCards);
            runner.update(conn, sqlDeleteAllUsers);
        }
    }

    @SneakyThrows
    public static void resetSUTData() {
        var runner = new QueryRunner();
        var sqlInsertUsers = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";

        String vasyaId = "1";
        String vasyaLogin = "vasya";
        String vasyaPass = vasyaParolEncrypted;
        String petyaId = "2";
        String petyaLogin = "petya";
        String petyaPass = petyaParolEncrypted;

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3305/app-db", "user", "pass"
                );
        ) {
            runner.update(conn, sqlInsertUsers, vasyaId, vasyaLogin, vasyaPass);
            runner.update(conn, sqlInsertUsers, petyaId, petyaLogin, petyaPass);
        }
    }
}
