package br.com.hotel.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/Hotel-system";
    private static final String USER = "postgres";
    private static final String PASSWORD = "duque";

    //Inicia a conexão com o banco de dados
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexão com o banco realizada com sucesso!");
            return conn;
        } catch (ClassNotFoundException e){
            throw new SQLException("Driver do PostgreSQL não encontrado", e);
        } catch (SQLException e) {
            System.err.println("❌ Erro ao conectar ao banco de dados: " + e.getMessage());
            throw e;
        }
    }

    //Encerra conexão com o banco de dados
    public static void closeConnection(Connection conn){
        if(conn != null){
            try{
                conn.close();
                System.out.println("Conexão fechada.");
            }catch (SQLException e){
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}
