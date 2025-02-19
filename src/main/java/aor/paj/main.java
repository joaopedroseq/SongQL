import aor.paj.App;
import aor.paj.ValidacaoInput;

import java.sql.SQLException;
import java.util.Scanner;

public static void main(String[] args) throws SQLException {
    Scanner sc = new Scanner(System.in);
    //menuPrincipal(sc);
    //printLogo();


    try (App app = new App()) {
        System.out.println(app.verificarAutorExiste(new String("John Lennon")));
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