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

        String sql = "SELECT musica.identificador as id, titulo as titulo, data_criacao as lancamento, autor_nome as autor, coalesce(album_nome, 's/album') as album,\n" +
                "coalesce(numero_ordem.numero::text, '') as faixa\n" +
                "FROM musica\n" +
                "left outer join numero_ordem on musica.identificador = numero_ordem.musica_identificador";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            try (ResultSet rs1 = stm.executeQuery()) {
                while (rs1.next()) {
                    System.out.println("ID: " + rs1.getString("id") + " Título: " + rs1.getString("titulo") + " Data de Lançamento:"
                            + rs1.getString("lancamento") + " Autor:"
                            + rs1.getString("autor") + " Album: " + rs1.getString("album") +
                            " Faixa: " + rs1.getString("faixa"));
                }
            }
        }
    }

    public void adicionarMusica(String titulo, String dataLancamento, String autor, String genero, String album, int numFaixa) throws SQLException {
        boolean existeAutor = verificarAutorExiste(autor);
        boolean existeGenero = verificarGeneroExiste(genero);
        boolean temAlbum = true;

        if (album.equals("") && numFaixa == -1) {
            temAlbum = false;
        }

        if (!existeAutor) {
            criarAutor(autor);
        }
        if (!existeGenero) {
            criarGenero(genero);
        }

        String sql = "INSERT INTO musica (identificador, titulo, data_criacao, autor_nome) VALUES\n" +
                "(nextval('identificadores_musica'), (?), (?), (?);";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, titulo);
            stm.setString(2, dataLancamento);
            stm.setString(3, autor);
            int createdAutor = stm.executeUpdate();
            if (createdAutor == 0) {
                System.out.println("Música não criada");
            } else {
                System.out.println("Música criada");
            }
        }

        if(temAlbum) {
            if(!verificarAlbumExiste(album)){
                criarAlbum(album);
            }
        }
    }

    //Faixa
    public boolean verificarSeFaixaExiste(String albumNome, int faixaNum) throws SQLException {
        //Verificar se a faixa já está tomada
        //select *
        //from numero_ordem
        //where album_nome like 'Purple Rain' AND numero=6
        return true;

    }

    //Album
    public boolean verificarAlbumExiste(String nomeAlbum) throws SQLException {
        String sql = "SELECT * FROM album WHERE nome LIKE (?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, nomeAlbum);
            try (ResultSet rs = stm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public void criarAlbum(String albumNome) throws SQLException {
        if (!verificarAutorExiste(albumNome)) {
            String sql = "INSERT INTO album (nome) VALUES(?);";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setString(1, albumNome);
                int createdAutor = stm.executeUpdate();
                if (createdAutor == 0) {
                    System.out.println("Album não criado");
                } else {
                    System.out.println("Album criado");
                }
            }
        }
    }


    //Autor
    public boolean verificarAutorExiste(String nomeAutor) throws SQLException {
        String sql = "SELECT * FROM autor WHERE nome LIKE (?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, nomeAutor);
            try (ResultSet rs = stm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public void criarAutor(String autorNome) throws SQLException {
        if (!verificarAutorExiste(autorNome)) {
            String sql = "INSERT INTO autor (nome) VALUES(?);";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setString(1, autorNome);
                int createdAutor = stm.executeUpdate();
                if (createdAutor == 0) {
                    System.out.println("Autor não criado");
                } else {
                    System.out.println("Autor criado");
                }
            }
        }
    }

    //Genero
    public boolean verificarGeneroExiste(String generoNome) throws SQLException {
        String sql = "SELECT * FROM genero WHERE nome LIKE (?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, generoNome);
            try (ResultSet rs = stm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public void criarGenero(String generoNome) throws SQLException {
        if (!verificarGeneroExiste(generoNome)) {
            String sql = "INSERT INTO genero(nome) VALUES(?);";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setString(1, generoNome);
                int createdAutor = stm.executeUpdate();
                if (createdAutor == 0) {
                    System.out.println("Género não criado");
                } else {
                    System.out.println("Género criado");
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

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        //menuPrincipal(sc);
        //printLogo();


        try (App app = new App()) {
            //app.verificarAutorExiste(new String("John Lennon"));
            app.verificarGeneroExiste(new String("Heavy Metal"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void menuPrincipal(Scanner sc) {
        String opcaoStr = "";
        int opcao = -1;
        boolean seValido = false;
        do {
            System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
            System.out.println("║                      O que pretende fazar?                        ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
            System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
            System.out.println("║ 1. Consultar músicas                                              ║");
            System.out.println("║ 2. Adicionar uma música                                           ║");
            System.out.println("║ 3. Alterar o título de uma música                                 ║");
            System.out.println("║ 4. Remover uma música                                             ║");
            System.out.println("║ 5. Criar uma playlist                                             ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
            opcaoStr = sc.nextLine();
            if(ValidacaoInput.validar(opcaoStr, 1, 5)) {
                opcao = Integer.parseInt(opcaoStr);
                seValido = true;
            }
            else{
                System.out.println("Escolha inválida. Escolha uma opção entre 1 e 5.");
            }
        }
        while(!seValido);
        switch (opcao) {
            case 1:
        }
    }

    private static void printLogo() {
        System.out.println("╔═════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║   d888888o.       ,o888888o.     b.             8      ,o888888o.        ,o888888o.      8 8888         ║");
        System.out.println("║ .`8888:' `88.  . 8888     `88.   888o.          8     8888     `88.   . 8888     `88.    8 8888         ║");
        System.out.println("║ 8.`8888.   Y8 ,8 8888       `8b  Y88888o.       8  ,8 8888       `8. ,8 8888       `8b   8 8888         ║");
        System.out.println("║ `8.`8888.     88 8888        `8b .`Y888888o.    8  88 8888           88 8888        `8b  8 8888         ║");
        System.out.println("║  `8.`8888.    88 8888         88 8o. `Y888888o. 8  88 8888           88 8888         88  8 8888         ║");
        System.out.println("║   `8.`8888.   88 8888         88 8`Y8o. `Y88888o8  88 8888           88 8888     `8. 88  8 8888         ║");
        System.out.println("║    `8.`8888.  88 8888        ,8P 8   `Y8o. `Y8888  88 8888   8888888 88 8888      `8,8P  8 8888         ║");
        System.out.println("║8b   `8.`8888. `8 8888       ,8P  8      `Y8o. `Y8  `8 8888       .8' `8 8888       ;8P   8 8888         ║");
        System.out.println("║`8b.  ;8.`8888  ` 8888     ,88'   8         `Y8o.`     8888     ,88'   ` 8888     ,88'8.  8 8888         ║");
        System.out.println("║ `Y8888P ,88P'     `8888888P'     8            `Yo      `8888888P'        `8888888P'  `8. 8 888888888888 ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
    }


}