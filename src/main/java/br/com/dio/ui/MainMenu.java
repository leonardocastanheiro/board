package br.com.dio.ui;

import br.com.dio.persistence.config.ConnectionConfig;
import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.service.BoardService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException {
        System.out.println("Bem-vindo ao gerenciador de boards.");

        this.separator();

        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");

            this.separator();

            System.out.print("Selecione a opção desejada: ");

            int option = 0;

            do {
                try {
                    String text = scanner.nextLine();

                    option = Integer.parseInt(text);

                } catch (InputMismatchException ignored) {
                    option = 0;
                }
            } while (option < 1 || option > 4);

            switch (option) {
                case 1:
                    this.createBoard();
                    break;
                case 2:
                    this.selectBoard();
                    break;
                case 3:
                    this.deleteBoard();
                    break;
                case 4:
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida! Erro interno");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();

        System.out.print("Digite o nome do board: ");
        entity.setName(scanner.nextLine());

        int numberOfExtraColumns = 0;

        System.out.print("Digite a quantidade de colunas além das 3 padrões. Caso não queira nenhuma, digite '0': ");

        while (true) {
            try {
                numberOfExtraColumns = Integer.parseInt(scanner.nextLine());
            }
            catch (InputMismatchException ignored) {
                System.out.print("Entrada inválida, digite novamente: ");
                continue;
            }

            if(numberOfExtraColumns < 0) {
                System.out.print("O número digitado é menor que 0, tente novamente: ");
                continue;
            }

            break;
        }

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.print("Digite o nome da coluna inicial do board: ");
        columns.add(createBoardColumn(scanner.nextLine(),BoardColumnKindEnum.INITIAL,0));

        int i = 1;

        for(; i <= numberOfExtraColumns; i++) {
            System.out.print("Digite o nome da coluna pendente do board: ");
            columns.add(createBoardColumn(scanner.nextLine(),BoardColumnKindEnum.PENDING,i));
        }

        System.out.print("Digite o nome da coluna final do board: ");
        columns.add(createBoardColumn(scanner.nextLine(),BoardColumnKindEnum.FINAL,i++));

        System.out.print("Digite o nome da coluna cancelada do board: ");
        columns.add(createBoardColumn(scanner.nextLine(),BoardColumnKindEnum.CANCEL,i));

        entity.setColumns(columns);

        try(var connection = getConnection()) {
            var service = new BoardService(connection);

            service.insert(entity);
        }

        this.separator();
    }

    private BoardColumnEntity createBoardColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var entity = new BoardColumnEntity();

        entity.setName(name);
        entity.setKind(kind);
        entity.setOrder(order);

        return entity;
    }

    private void selectBoard() throws SQLException {
        System.out.println("Digite o ID do board que deseja selecionar: ");

        Long id;

        while(true) {
            try {
                id = Long.parseLong(scanner.nextLine());
            } catch (InputMismatchException ignored) {
                System.out.print("Entrada inválida, digite novamente: ");
                continue;
            }

            break;
        }

        Optional<BoardEntity> entity;

        try(var connection = getConnection()) {
            var service = new BoardService(connection);

            entity = service.findById(id);
        }

        if(entity.isEmpty()) {
            System.out.println("Nenhum board encontrado com esse ID...");
            this.separator();
            return;
        }

        BoardMenu boardMenu = new BoardMenu(entity.get());

        this.separator();
        boardMenu.execute();
    }

    private void deleteBoard() throws SQLException {
        System.out.print("Informe o ID do board que sera excluído: ");

        Long id;

        while (true) {
            try {
                id = Long.parseLong(scanner.nextLine());
            } catch (InputMismatchException ignored) {
                System.out.print("Entrada inválida, tente novamente: ");
                continue;

            }
            break;
        }

        try(Connection connection = getConnection()) {
            BoardService service = new BoardService(connection);

            var result = service.delete(id);

            if(result){
                System.out.println("Board deletado com sucesso!");
                return;
            }

            System.out.println("Erro ao deletar! Board não existe...");

        }

        separator();
    }

    private void separator() {
        System.out.println("---------------------------");
    }
}
