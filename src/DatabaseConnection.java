import java.sql.Connection;
import java.sql.DriverManager;

class DatabaseConnection{
    public static void main(String[] args) {
        String url = "jdbc:sqlserver://aulajava.mssql.somee.com;databaseName=aulajava;encrypt=true;trustServerCertificate=true";
        String user = "matheus007_SQLLogin_1";
        String password = "sbqfmszryw";
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Conexão bem-sucedida!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
