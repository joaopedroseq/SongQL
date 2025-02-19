package aor.paj;

import java.sql.*;
import java.util.Scanner;

public class App implements AutoCloseable {
    private final static String URL = "jdbc:postgresql://localhost:5432/postgres";
    private final static String USER = "postgres";
    private final static String PASSWORD = "postgres";
    private Connection conn;

    public App() throws SQLException {
        this.conn = DriverManager.getConnection(App.URL, App.USER, App.PASSWORD);
    }

    private void queryEmployees() throws SQLException {

        String sql = "SELECT * FROM musica inner join autor on musica.autor_nome=autor.nome WHERE nome LIKE ('John Lennon')";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            //stm.setInt(1, 30);
            //stm.setInt(2, 20);
            try (ResultSet rs1 = stm.executeQuery()) {
                while (rs1.next()) {
                    System.out.println("ID: " + rs1.getString("identificador") + " Título: " + rs1.getString("titulo") + " Data:"
                            + rs1.getString("data_criacao") + " Autor:"
                            + rs1.getString("autor_nome"));
                }
            }
        }
    }

    public void consultarMusicas() throws SQLException {
        /*SELECT titulo as titulo, data_criacao as lancamento, autor_nome as autor, coalesce(album_nome, 's/album') as album,\n" +
                "coalesce(numero_ordem.numero::text, '') as faixa\n" +
                "FROM musica\n" +
                "left outer join numero_ordem on musica.identificador = numero_ordem.musica_identificador*/

        String sql = "SELECT titulo as titulo, data_criacao as lancamento, autor_nome as autor, coalesce(album_nome, 's/album') as album,\n" +
                "coalesce(numero_ordem.numero::text, '') as faixa\n" +
                "FROM musica\n" +
                "left outer join numero_ordem on musica.identificador = numero_ordem.musica_identificador";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            try (ResultSet rs1 = stm.executeQuery()) {
                while (rs1.next()) {
                    System.out.println(" Título: " + rs1.getString("titulo") + " Data de Lançamento:"
                            + rs1.getString("lancamento") + " Autor:"
                            + rs1.getString("autor") + " Album: " + rs1.getString("album") +
                            " Faixa: " + rs1.getString("faixa"));
                }
            }
        }
    }

    public void adicionarMusicaComAlbum(String titulo, String dataLancamento, String autor, String genero, String album, int faixa) throws SQLException {




        String sql = "INSERT INTO autor (nome) VALUES\n" +
                "('Xutos e Pontapés');\n" +
                "\n" +
                "INSERT INTO musica (identificador, titulo, data_criacao, autor_nome) \n" +
                "values (nextval('identificadores_musica'), 'A minha casinha', '1987-02-02', 'Xutos e Pontapés');\n" +
                "\n" +
                "\n" +
                "INSERT INTO musica_genero (musica_identificador, genero_nome) \n" +
                "VALUES ((SELECT identificador \n" +
                "FROM musica \n" +
                "WHERE titulo = 'A minha casinha'),'Rock')";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            //stm.setInt(1, 30);
            //stm.setInt(2, 20);
            try (ResultSet rs1 = stm.executeQuery()) {
                while (rs1.next()) {
                    System.out.println("ID: " + rs1.getString("identificador") + " Título: " + rs1.getString("titulo") + " Data:"
                            + rs1.getString("data_criacao") + " Autor:"
                            + rs1.getString("autor_nome"));
                }
            }
        }
    }

    public void adicionarMusicaSemAlbum(String titulo, String dataLancamento, String autor, String genero, String album, int faixa) throws SQLException {



        String sql = "INSERT INTO autor (nome) VALUES\n" +
                "('Xutos e Pontapés');\n" +
                "\n" +
                "INSERT INTO musica (identificador, titulo, data_criacao, autor_nome) \n" +
                "values (nextval('identificadores_musica'), 'A minha casinha', '1987-02-02', 'Xutos e Pontapés');\n" +
                "\n" +
                "\n" +
                "INSERT INTO musica_genero (musica_identificador, genero_nome) \n" +
                "VALUES ((SELECT identificador \n" +
                "FROM musica \n" +
                "WHERE titulo = 'A minha casinha'),'Rock')";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            //stm.setInt(1, 30);
            //stm.setInt(2, 20);
            try (ResultSet rs1 = stm.executeQuery()) {
                while (rs1.next()) {
                    System.out.println("ID: " + rs1.getString("identificador") + " Título: " + rs1.getString("titulo") + " Data:"
                            + rs1.getString("data_criacao") + " Autor:"
                            + rs1.getString("autor_nome"));
                }
            }
        }
    }

    //Autor
    public void criarAutor(){

    }

    public boolean verificarAutorExiste(String nomeAutor) throws SQLException {
        String sql = "SELECT * FROM autor WHERE nome LIKE ('"+nomeAutor+"')";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            try (ResultSet rs = stm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }


}