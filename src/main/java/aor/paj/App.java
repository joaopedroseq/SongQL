package aor.paj;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class App implements AutoCloseable {
    private final static String URL = "jdbc:postgresql://localhost:5432/postgres";
    private final static String USER = "postgres";
    private final static String PASSWORD = "postgres";
    private Connection conn;

    public App() throws SQLException {
        Scanner sc = new Scanner(System.in);
        this.conn = DriverManager.getConnection(App.URL, App.USER, App.PASSWORD);
        printLogo();
        menuPrincipal(sc, this);
    }


    /**
     * Consulta todas as músicas registadas na base de dados e exibe suas informações, incluindo
     * o título, data de lançamento, autor, álbum e faixa. Caso algum campo seja nulo, informações
     * padrão como "s/album" ou campos vazios são usadas.
     *
     * @throws SQLException Se ocorrer um erro de acesso a base de dados durante a execução da consulta.
     */
    public void consultarMusicas() throws SQLException {
        String sql = "SELECT identificador as id, titulo as titulo, data_criacao as lancamento, autor_nome as autor, coalesce(album_nome, 's/album') as album,\n" +
                "coalesce(faixa.num_faixa::text, '') as faixa\n" +
                "FROM musica\n" +
                "left outer join faixa on musica.identificador = faixa.musica_identificador";

        String formatoTitulo = "%-10s %-30s %-15s %-20s %-25s %-10s%n";
        String formatoDados = "%-10s %-30s %-15s %-20s %-25s %-10s%n";
        System.out.println("Lista de Músicas:");
        System.out.printf(formatoTitulo, "ID", "Título", "Lançamento", "Autor", "Album", "Faixa");
        System.out.println("-------------------------------------------------------------------------------" +
                "-----------------------------------------------------------");

        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            try (ResultSet rs1 = stm.executeQuery()) {
                while (rs1.next()) {
                    System.out.printf(formatoDados, rs1.getString("id"), rs1.getString("titulo"), rs1.getString("lancamento"),
                            rs1.getString("autor"), rs1.getString("album"), rs1.getString("faixa"));
                }
            }
        }
        System.out.println("-------------------------------------------------------------------------------" +
                "-----------------------------------------------------------");
    }

    /**
     * Consulta todas as músicas registadas na base de dados e exibe suas informações, incluindo
     * o título, data de lançamento, autor, álbum e faixa. Caso algum campo seja nulo, informações
     * padrão como "s/album" ou campos vazios são usadas.
     *
     * @throws SQLException Se ocorrer um erro de acesso a base de dados durante a execução da consulta.
     */
    public void consultarGeneros() throws SQLException {
        String sql = "SELECT * FROM genero";
        String formatoTitulo = "%-30s %n";
        String formatoDados = "%-30s %n";
        System.out.println("Lista de Géneros:");
        System.out.printf(formatoTitulo, "Nome do Género");
        System.out.println("------------------------------");

        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            try (ResultSet rs1 = stm.executeQuery()) {
                while (rs1.next()) {
                    System.out.printf(formatoDados, rs1.getString("nome"));
                }
            }
        }
        System.out.println("------------------------------");
    }

    /**
     * Adiciona uma nova música à base de dados, incluindo informações como título, data de lançamento,
     * autor, género, álbum e número da faixa. Realiza verificações e, caso necessário, cria registos
     * para autor, álbum e género antes de adicionar a música.
     *
     * @param titulo         O título da música a ser adicionada.
     * @param dataLancamento A data de lançamento da música no formato "yyyy-MM-dd".
     * @param autor          O nome do autor da música.
     * @param genero         O gênero musical associado à música.
     * @param album          O nome do álbum ao qual a música pertence. Se não houver álbum, deve ser vazio.
     * @param numFaixa       O número da faixa dentro do álbum. Use -1 se a música não tiver um álbum associado.
     * @throws SQLException Se ocorrer um erro de acesso a base de dados durante a operação.
     */
    public void adicionarMusica(String titulo, String dataLancamento, String autor, String genero, String album, long numFaixa) throws SQLException {
        if (titulo.trim().equals("") || dataLancamento.trim().equals("") || autor.trim().equals("") || genero.trim().equals("")) {
            System.out.println("Erro ao adicionar Musica. Parâmetros em falta");
        } else {
            java.sql.Date dataLancamentoSql = java.sql.Date.valueOf(dataLancamento);
            boolean existeAutor = verificarAutorExiste(autor);
            boolean existeGenero = verificarGeneroExiste(genero);
            boolean existeAlbum = true;
            boolean musicaComAlbum = true;
            boolean operacaoComSucesso = false;

            if (album.equals("") && numFaixa == -1) {
                musicaComAlbum = false;
            }
            if (musicaComAlbum) {
                if (verificarAlbumExiste(album)) {
                    existeAlbum = true;
                    if (verificarSeFaixaExiste(album, numFaixa)) {
                        System.out.println("Já existe a faixa " + numFaixa + " no album " + album);
                        return;
                    }
                } else {
                    existeAlbum = false;
                }
            }
            if (!existeAutor) {
                criarAutor(autor);
            }
            if (musicaComAlbum) {
                if (!existeAlbum) {
                    criarAlbum(album);
                }
            }
            if (!existeGenero) {
                criarGenero(genero);
            }

            //INSERT INTO musica (titulo, data_criacao, autor_nome) VALUES
            //('Let It Be', '1970-03-06', 'The Beatles'),
            String sql = "INSERT INTO musica (titulo, data_criacao, autor_nome) VALUES" +
                    "((?), (?), (?));" +
                    "INSERT INTO musica_genero (musica_identificador, genero_nome) VALUES" +
                    "((SELECT last_value FROM musica_identificador_seq), (?));";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setString(1, titulo);
                stm.setDate(2, dataLancamentoSql);
                stm.setString(3, autor);
                stm.setString(4, genero);
                int createdMusic = stm.executeUpdate();
                if (createdMusic == 0) {
                    System.out.println("Música não criada");
                } else {
                    System.out.println("Música criada");
                }
            }
            if (musicaComAlbum) {
                if (criarFaixa(numFaixa, album)) {
                    operacaoComSucesso = true;
                }
            }
            if (operacaoComSucesso) {
                System.out.println("Música adicionada com sucesso");
            }
        }
    }


    //FAIXAS

    /**
     * Verifica se uma faixa específica dentro de um álbum existe na base de dados.
     *
     * @param albumNome O nome do álbum no qual a faixa será verificada.
     * @param faixaNum  O número da faixa a ser verificada no álbum.
     * @return true se a faixa existir no álbum especificado, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso a base de dados ou a consulta falhar.
     */
    public boolean verificarSeFaixaExiste(String albumNome, long faixaNum) throws SQLException {
        String sql = "SELECT * FROM faixa WHERE album_nome LIKE (?) AND num_faixa=(?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, albumNome);
            stm.setLong(2, faixaNum);
            try (ResultSet rs = stm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public boolean verificarSeMusicaTemFaixa(long idenficadorMusica) throws SQLException {
        if (idenficadorMusica <= 0) {
            System.out.println("Parâmeteros vazios");
            return false;
        } else {
            String sqlSelectnumFaixaDaMusica = "SELECT num_faixa FROM musica " +
                    "inner join faixa on musica.identificador=faixa.musica_identificador " +
                    "where musica.identificador = (?)";
            try (PreparedStatement stm = conn.prepareStatement(sqlSelectnumFaixaDaMusica)) {
                stm.setLong(1, idenficadorMusica);
                try (ResultSet rs = stm.executeQuery()) {
                    if (!rs.isBeforeFirst()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
    }

    public boolean criarFaixa(long numFaixa, String albumNome) throws SQLException {
        String sql = "INSERT INTO faixa (num_faixa, album_nome, musica_identificador) VALUES" +
                "((?), (?), (SELECT last_value FROM musica_identificador_seq));";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setLong(1, numFaixa);
            stm.setString(2, albumNome);
            int createdAutor = stm.executeUpdate();
            if (createdAutor == 0) {
                return false;
            } else {
                return true;
            }
        }
    }

    //ALBUM

    /**
     * Verifica se existe um álbum com o nome fornecido no base de dados.
     *
     * @param nomeAlbum O nome do álbum cuja existência deve ser verificada.
     * @return true se o álbum existir, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso a base de dados ou a consulta falhar.
     */
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

    /**
     * Cria um álbum na base de dados com o nome especificado, caso ele ainda não exista.
     *
     * @param albumNome O nome do álbum a ser criado.
     * @throws SQLException Se ocorrer um erro de acesso a base de dados ou durante a execução da operação.
     */
    public void criarAlbum(String albumNome) throws SQLException {
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

    public void removerAlbumSeVazio(String albumNome) throws SQLException {
        if (verificarSeAlbumVazio(albumNome)) {
            String sql = "DELETE FROM album WHERE nome = (?)";
            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setString(1, albumNome);
                int deletedAlbum = stm.executeUpdate();
                if (deletedAlbum != 0) {
                    System.out.println("Album " + albumNome + " apagado");
                } else {
                    System.out.println("Album " + albumNome + " não apagado");
                }
            }
        } else {
            System.out.println("Album " + albumNome + " contém músicas");
        }
    }


    public boolean verificarSeAlbumVazio(String albumNome) throws SQLException {
        String sql = "SELECT * FROM album inner join faixa on album.nome=faixa.album_nome WHERE album.nome = (?)";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, albumNome);
            try (ResultSet rs = stm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }


    /**
     * Verifica se existe um autor com o nome fornecido na base de dados.
     *
     * @param nomeAutor O nome do autor cuja existência deve ser verificada.
     * @return true se o autor existir, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso ao base de dados ou a consulta falhar.
     */
    public boolean verificarAutorExiste(String nomeAutor) throws SQLException {     //A FUNCIONAR
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

    /**
     * Cria um novo autor na base de dados com o nome especificado, caso ele ainda não exista.
     *
     * @param autorNome O nome do autor a ser criado.
     * @throws SQLException Se ocorrer um erro de acesso a base de dados ou durante a execução da operação.
     */
    public void criarAutor(String autorNome) throws SQLException {
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

    /**
     * Verifica se um genero com o nome especificado existe na base de dados.
     *
     * @param generoNome O nome do gênero cuja existência deve ser verificada.
     * @return true se o gênero existir, false caso contrário.
     * @throws SQLException Se ocorrer um erro de acesso a base de dados ou a consulta falhar.
     */
    public boolean verificarGeneroExiste(String generoNome) throws SQLException { //A FUNCIONAR
        if (generoNome.trim().equals("")) {
            System.out.println("Parâmetros vazios");
            return false;
        } else {
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
    }

    /**
     * Cria um genero na base de dados com o nome especificado, caso ele ainda não exista.
     *
     * @param generoNome O nome do genero a ser criado.
     * @throws SQLException Se ocorrer um erro de acesso a base de dados durante a execução da operação.
     */
    public void criarGenero(String generoNome) throws SQLException {
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

    /**
     * Atualiza o título de uma música existente na base de dados com base no identificador fornecido.
     *
     * @param identificador O identificador único da música cujo título será atualizado.
     * @param novoTitulo    O novo título a ser atribuído à música.
     * @throws SQLException Se ocorrer um erro de acesso a base de dados ou durante a execução da operação.
     */
    public void atualizarTituloMusica(long identificador, String novoTitulo) throws SQLException {
        String sql = "UPDATE musica SET titulo = ? WHERE identificador = ?";
        try (PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, novoTitulo);
            stm.setLong(2, identificador);
            int linhasAtualizadas = stm.executeUpdate();
            if (linhasAtualizadas == 0) {
                System.out.println("Música não encontrada ou título não atualizado.");
            } else {
                System.out.println("Título atualizado com sucesso.");
            }
        }
    }

    /**
     * Remove uma música da base de dados, incluindo todos os registos associados
     * nas tabelas musica_genero e faixa, e por fim, o registo da tabela musica.
     *
     * @param identificador O identificador único da música que será removida da base de dados.
     * @throws SQLException Se ocorrer um erro de acesso a base de dados ou durante a execução das operações de remoção.
     */
    public void removerMusica(long identificador) throws SQLException {
        boolean seMusicaTemAlbum = verificarSeMusicaTemAlbum(identificador);
        String nomeAlbum;

        String sqlGenero = "DELETE FROM musica_genero WHERE musica_identificador = ?";
        try (PreparedStatement stm = conn.prepareStatement(sqlGenero)) {
            stm.setLong(1, identificador);
            stm.executeUpdate();
        }
        if (seMusicaTemAlbum) {
            nomeAlbum = obterAlbumDaMusica(identificador);
            String sqlFaixa = "DELETE FROM faixa WHERE musica_identificador = ?";
            try (PreparedStatement stm = conn.prepareStatement(sqlFaixa)) {
                stm.setLong(1, identificador);
                stm.executeUpdate();
            }
            removerAlbumSeVazio(nomeAlbum);
        }

        String sqlMusica = "DELETE FROM musica WHERE identificador = ?";
        try (PreparedStatement stm = conn.prepareStatement(sqlMusica)) {
            stm.setLong(1, identificador);
            int linhasAtualizadas = stm.executeUpdate();
            if (linhasAtualizadas == 0) {
                System.out.println("Música não encontrada ou não removida.");
            } else {
                System.out.println("Música removida com sucesso.");
            }
        }
    }

    public boolean verificarSeMusicaTemAlbum(long identificadorMusica) throws SQLException {
        //SELECT album_nome
        //FROM musica
        //inner join faixa on musica.identificador=faixa.musica_identificador
        //WHERE musica.identificador=13
        String sqlSelectIfAlbumDeMusica = "SELECT album_nome FROM musica " +
                "inner join faixa on musica.identificador=faixa.musica_identificador " +
                "WHERE musica.identificador=(?)";
        try (PreparedStatement stm = conn.prepareStatement(sqlSelectIfAlbumDeMusica)) {
            stm.setLong(1, identificadorMusica);
            try (ResultSet rs = stm.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    public String obterAlbumDaMusica(long identificadorMusica) throws SQLException {
        if (identificadorMusica <= 0) {
            System.out.println("Sem identificador");
            return null;
        } else {
            String sqlSelectAlbumDeMusica = "SELECT album_nome FROM musica " +
                    "inner join faixa on musica.identificador=faixa.musica_identificador " +
                    "WHERE musica.identificador=(?)";
            try (PreparedStatement stm = conn.prepareStatement(sqlSelectAlbumDeMusica)) {
                stm.setLong(1, identificadorMusica);
                try (ResultSet albuns = stm.executeQuery()) {
                    while (albuns.next()) {
                        return albuns.getString("album_nome");
                    }
                }
            }
        }
        return null;
    }


    /**
     * Gera uma playlist temporária com músicas de um género específico e exibe as informações
     * das músicas selecionadas. A seleção das músicas é feita de forma aleatória e limitada
     * ao número especificado pelo utilizador. Quando a sessão é fechada o albúm é removido.
     *
     * @param generoEscolhido O género das músicas que devem compor a playlist.
     * @param numeroMusicas   O número máximo de músicas a serem incluídas na playlist.
     * @throws SQLException Se ocorrer um erro de acesso à base de dados durante a execução da operação.
     */
    public void gerarPlaylist(String generoEscolhido, int numeroMusicas) throws SQLException {
        String sqlCreate = "CREATE TEMP TABLE temp_playlist AS " +
                "SELECT musica.identificador, musica.titulo, musica.data_criacao, musica.autor_nome, genero.nome AS genero_nome " +
                "FROM musica " +
                "INNER JOIN autor ON musica.autor_nome = autor.nome " +
                "INNER JOIN musica_genero ON musica.identificador = musica_genero.musica_identificador " +
                "INNER JOIN genero ON musica_genero.genero_nome = genero.nome " +
                "WHERE genero.nome = ? " +
                "ORDER BY RANDOM () " +
                "LIMIT ?;";

        String formatoTitulo = "%-10s %-30s %-15s %-20s %-20s%n";
        String formatoDados = "%-10s %-30s %-15s %-20s %-20s%n";
        System.out.println("Lista de Músicas na Playlist:");
        System.out.printf(formatoTitulo, "ID", "Título", "Lançamento", "Autor", "Género");
        System.out.println("-------------------------------------------------------------------------------" +
                "-----------------------------------------------------------");

        try (PreparedStatement stmCreate = conn.prepareStatement(sqlCreate)) {
            stmCreate.setString(1, generoEscolhido);
            stmCreate.setInt(2, numeroMusicas);
            stmCreate.executeUpdate();

            String selectSql = "SELECT * FROM temp_playlist";
            try (PreparedStatement selectStm = conn.prepareStatement(selectSql);
                 ResultSet rs = selectStm.executeQuery()) {
                while (rs.next()) {
                    System.out.printf(formatoDados, rs.getString("identificador"), rs.getString("titulo"), rs.getString("data_criacao"),
                            rs.getString("autor_nome"), rs.getString("genero_nome"));
                }
            }
        }

        // Remove a tabela temporária
        String sqlDrop = "DROP TABLE temp_playlist;";
        try (PreparedStatement stmDrop = conn.prepareStatement(sqlDrop)) {
            stmDrop.executeUpdate();
        }
        System.out.println("-------------------------------------------------------------------------------" +
                "-----------------------------------------------------------");
    }


    /**
     * Fecha a conexão com a base de dados associada a esta instância.
     * Caso a conexão não seja nula, ela será fechada para libertar recursos da base de dados.
     *
     * @throws SQLException Se ocorrer um erro ao tentar fechar a conexão.
     */
    @Override
    public void close() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }


    private void menuPrincipal(Scanner sc, App app) throws SQLException {
        String opcaoStr = "";
        int opcao = -1;
        boolean seValido = false;
        do {
            System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
            System.out.println("║                      O que pretende fazer?                        ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
            System.out.println("╔═══════════════════════════════════════════════════════════════════╗");
            System.out.println("║ 1. Consultar músicas                                              ║");
            System.out.println("║ 2. Adicionar uma música                                           ║");
            System.out.println("║ 3. Alterar o título de uma música                                 ║");
            System.out.println("║ 4. Remover uma música                                             ║");
            System.out.println("║ 5. Criar uma playlist                                             ║");
            System.out.println("║ 6. Sair                                                           ║");
            System.out.println("╚═══════════════════════════════════════════════════════════════════╝");
            opcaoStr = sc.nextLine();
            if (ValidacaoInput.validar(opcaoStr, 1, 6)) {
                opcao = Integer.parseInt(opcaoStr);
                seValido = true;
            } else {
                System.out.println("Escolha inválida. Escolha uma opção entre 1 e 5.");
            }
        } while (!seValido);

        switch (opcao) {
            case 1:
                try {
                    app.consultarMusicas();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                menuPrincipal(sc, app);
                break;
            case 2:
                menuAdicionarMusica(sc, app);
                menuPrincipal(sc, app);
                break;
            case 3:
                menuAlterarTituloMusica(sc, app);
                menuPrincipal(sc, app);
                break;
            case 4:
                long identificador = -1;
                seValido = false;
                do {
                    System.out.print("Digite o identificador da música que deseja remover: ");
                    String identificadorString = sc.nextLine();
                    if (ValidacaoInput.validar(identificadorString, 1)) {
                        identificador = Long.parseLong(identificadorString);
                        seValido = true;
                    }
                }
                while (!seValido);
                try {
                    app.removerMusica(identificador);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                menuPrincipal(sc, app);
                break;
            case 5:
                seValido = false;
                String generoPlaylist;
                do {
                    consultarGeneros();
                    System.out.print("Digite o género musical para a playlist: ");
                    String generoBruto = sc.nextLine();
                    generoPlaylist = ValidacaoInput.removerCarateresEspeciais(generoBruto);
                    if (generoPlaylist.trim().equals("")) {
                        seValido = false;
                    } else {
                        seValido = true;
                    }
                    if (seValido) {
                        if (!verificarGeneroExiste(generoPlaylist)) {
                            System.out.println("Género musical não existente. Escolha um da seguinte lista");
                            seValido = false;
                        }
                    }
                }
                while (!seValido);

                int numMusicas = -1;
                seValido = false;
                do {
                    System.out.println("Digite o número de músicas para a playlist: ");
                    String numMusicasString = sc.nextLine();
                    if (ValidacaoInput.validar(numMusicasString, 1)) {
                        numMusicas = Integer.parseInt(numMusicasString);
                        seValido = true;
                    }
                }
                while (!seValido);
                try {
                    app.gerarPlaylist(generoPlaylist, numMusicas);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                menuPrincipal(sc, app);
                break;
            case 6:
                System.out.println("A sair de SongQL");
                System.exit(0);
            default:
                System.out.println("Opção inválida.");
        }
    }


    public static void menuAdicionarMusica(Scanner sc, App app) throws SQLException {
        boolean seValido = false;
        String titulo;
        do {
            System.out.println("Digite o título da música:");
            String tituloBruto = sc.nextLine();
            titulo = ValidacaoInput.removerCarateresEspeciais(tituloBruto);
            if (titulo.trim().equals("")) {
                seValido = false;
            } else {
                seValido = true;
            }
        }
        while (!seValido);

        String dataLancamento = pedirData(sc);

        seValido = false;
        String autor;
        do {
            System.out.println("Digite o nome do autor:");
            String autorBruto = sc.nextLine();
            autor = ValidacaoInput.removerCarateresEspeciais(autorBruto);
            if (autor.trim().equals("")) {
                seValido = false;
            } else {
                seValido = true;
            }
        }
        while (!seValido);


        seValido = false;
        String genero;
        do {
            System.out.println("Digite o género musical:");
            String generoBruto = sc.nextLine();
            genero = ValidacaoInput.removerCarateresEspeciais(generoBruto);
            if (genero.trim().equals("")) {
                seValido = false;
            } else {
                seValido = true;
            }
        }
        while (!seValido);

        String album;
        System.out.println("Digite o nome do álbum (ou deixe em branco se não pertencer a um album):");
        String albumBruto = sc.nextLine();
        album = ValidacaoInput.removerCarateresEspeciais(albumBruto);

        int numFaixa = -1;
        if (!album.equals("")) {
            seValido = false;
            do {
                System.out.println("Digite o número da faixa:");
                String numFaixaString = sc.nextLine();
                if (ValidacaoInput.validar(numFaixaString, 1)) {
                    numFaixa = Integer.parseInt(numFaixaString);
                    seValido = true;
                }
            }
            while (!seValido);
        }
        try {
            app.adicionarMusica(titulo, dataLancamento, autor, genero, album, numFaixa);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void menuAlterarTituloMusica(Scanner sc, App app) throws SQLException {
        boolean seValido = false;
        long identificador = -1;
        do {
            System.out.print("Digite o identificador da música que deseja alterar: ");
            String identificadorString = sc.nextLine();
            if (ValidacaoInput.validar(identificadorString, 1)) {
                identificador = Long.parseLong(identificadorString);
                seValido = true;
            }
        }
        while (!seValido);

        String novoTitulo;
        do {
            System.out.println("Digite o novo título da música:");
            String novoTituloBruto = sc.nextLine();
            novoTitulo = ValidacaoInput.removerCarateresEspeciais(novoTituloBruto);
            if (novoTitulo.trim().equals("")) {
                seValido = false;
            } else {
                seValido = true;
            }
        }
        while (!seValido);

        try {
            app.atualizarTituloMusica(identificador, novoTitulo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String pedirData(Scanner sc) {
        boolean eValido;
        String dataString;
        do {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            System.out.println("Escreva a data que pretende no formato: yyyy-MM-dd:");
            dataString = sc.nextLine();
            try {
                eValido = true;
                Date dataEscolhida = dateFormat.parse(dataString);
                dataString = dateFormat.format(dataEscolhida);
            } catch (Exception e) {
                System.out.println("Formato de data e de hora inválido. Escreva a data no formato: yyyy-MM-dd");
                eValido = false;
            }
        }
        while (!eValido);
        return dataString;
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

    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        new App();



        /*try (App app = new App()) {
            app.consultarMusicas();
            //adicionarMusica(String titulo, String dataLancamento, String autor, String genero, String album, int numFaixa) throws SQLException {
            //app.gerarPlaylist("Rock", 20);
           //app.removerMusica(16);
        } catch (SQLException e) {
            e.printStackTrace();*/
    }
}

