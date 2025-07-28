package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardColumnEntity;
import br.com.dio.persistence.entity.BoardColumnKindEnum;
import br.com.dio.persistence.entity.BoardEntity;
import br.com.dio.persistence.entity.CardEntity;
import br.com.dio.service.BlockService;
import br.com.dio.service.BoardService;
import br.com.dio.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.*;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;
import static java.util.Arrays.stream;

@AllArgsConstructor
public class BoardMenu {

    private BoardEntity board;
    private final Scanner scanner;

    public void execute() throws SQLException {
        System.out.println("Bem-vindo ao board "+board.getName());
        separator();

        while (true) {
            System.out.println("1 - Criar um card");
            System.out.println("2 - Mover um card");
            System.out.println("3 - Bloquear um card");
            System.out.println("4 - Desbloquear um card");
            System.out.println("5 - Cancelar um card");
            System.out.println("6 - Visualizar colunas");
            System.out.println("7 - Visualizar coluna com cards");
            System.out.println("8 - Visualizar Card");
            System.out.println("9 - Voltar ao menu principal");
            System.out.println("10 - Sair");

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
            } while (option < 1 || option > 10);

            switch (option) {
                case 1:
                    this.createCard();
                    break;
                case 2:
                    this.moveCard();
                    break;
                case 3:
                    this.blockCard();
                    break;
                case 4:
                    this.unblockCard();
                    break;
                case 5:
                    this.cancelCard();
                    break;
                case 6:
                    this.showColumns();
                    break;
                case 7:
                    this.showColumnsWithCards();
                    break;
                case 8:
                    this.showCard();
                    break;
                case 9:
                    System.out.println("Voltando ao Menu Principal...");
                    this.separator();
                    return;
                case 10:
                    System.out.println("Fechando o Programa...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Opção inválida! Erro interno");
            }
        }
    }

    private void createCard() throws SQLException {
        var entity = new CardEntity();

        System.out.print("Digite o título do card: ");

        entity.setTitle(scanner.nextLine());

        System.out.print("Digite a descrição do card: ");

        entity.setDescription(scanner.nextLine());

        entity.setBoardColumn(board.getColumns().getFirst());

        try(var connection = getConnection()) {
            new CardService(connection).insert(entity);
        }

        this.updateBoard();
    }

    private void moveCard() throws SQLException {
        System.out.print("Digite o ID do card que deseja mover para a próxima coluna: ");

        Long id;

        while (true) {
            try {
                id = Long.parseLong(scanner.nextLine());
            } catch (InputMismatchException ignored) {
                System.out.print("Entrada inválida, digite novamente: ");
                continue;
            }

            break;
        }

        try(var connection = getConnection()) {
            var service = new CardService(connection);

            Optional<CardEntity> optional = service.findById(id);

            if (optional.isEmpty()) {
                System.out.println("Nenhum card encontrado com esse ID!");
                return;
            }

            CardEntity card = optional.get();
            BoardColumnEntity currentColumn = card.getBoardColumn();
            BoardEntity board = currentColumn.getBoard();

            BoardColumnKindEnum currentKind = currentColumn.getKind();

            if (currentKind == BoardColumnKindEnum.CANCEL || currentKind == BoardColumnKindEnum.FINAL) {
                System.out.println("O card está em uma coluna final ou cancelada e não pode ser movido.");
                return;
            }

            List<BoardColumnEntity> sortedColumns = board.getColumns()
                    .stream()
                    .sorted(Comparator.comparing(BoardColumnEntity::getOrder))
                    .toList();

            int index = -1;
            for (int i = 0; i < sortedColumns.size(); i++) {
                if (sortedColumns.get(i).getId().equals(currentColumn.getId())) {
                    index = i;
                    break;
                }
            }

            if (index == -1 || index == sortedColumns.size() - 1) {
                System.out.println("Não foi possível mover o card — já está na última coluna.");
                return;
            }

            BoardColumnEntity nextColumn = sortedColumns.get(index + 1);
            card.setBoardColumn(nextColumn);
            service.updateColumn(card);
        }

        this.updateBoard();
    }

    private void blockCard() throws SQLException {
        System.out.print("Informe o ID do card que será bloqueado: ");

        Long id;

        while (true) {
            try {
                id = Long.parseLong(scanner.nextLine());
            } catch (InputMismatchException ignored) {
                System.out.print("Entrada inválida, digite novamente: ");
                continue;
            }

            break;
        }

        System.out.print("Informe o motivo para o card ser bloqueado: ");
        String reason = scanner.nextLine();

        try(var connection = getConnection()) {
            var cardService = new CardService(connection);
            var blockService = new BlockService(connection);

            if(!cardService.isExists(id)) {
                System.out.println("Nenhum card encontrado com esse ID!");
                return;
            }

            if(blockService.isBlocked(id)) {
                System.out.println("Esse card já está bloqueado!");
                return;
            }

            cardService.blockCard(id, reason);

            System.out.println("Card bloqueado com sucesso!");
        }

        this.updateBoard();
    }

    private void unblockCard() throws SQLException {
        System.out.print("Informe o ID do card que será desbloqueado: ");

        Long id;

        while (true) {
            try {
                id = Long.parseLong(scanner.nextLine());
            } catch (InputMismatchException ignored) {
                System.out.print("Entrada inválida, digite novamente: ");
                continue;
            }

            break;
        }

        System.out.print("Informe o motivo para o card ser desbloqueado: ");
        String reason = scanner.nextLine();

        try(var connection = getConnection()) {
            var cardService = new CardService(connection);
            var blockService = new BlockService(connection);

            if(!cardService.isExists(id)) {
                System.out.println("Nenhum card encontrado com esse ID!");
                return;
            }

            if(!blockService.isBlocked(id)) {
                System.out.println("Esse card não está bloqueado!");
                return;
            }

            cardService.unblockCard(id, reason);

            System.out.println("Card desbloqueado com sucesso!");
        }

        this.updateBoard();
    }

    private void cancelCard() throws SQLException {
        System.out.print("Informe o ID do card que será cancelado: ");

        Long id;

        while (true) {
            try {
                id = Long.parseLong(scanner.nextLine());
            } catch (InputMismatchException ignored) {
                System.out.print("Entrada inválida, digite novamente: ");
                continue;
            }

            break;
        }

        try(var connection = getConnection()) {
            var cardService = new CardService(connection);

            var cardOptional = cardService.findById(id);

            if (cardOptional.isEmpty()) {
                System.out.println("Nenhum card encontrado com esse ID!");
                return;
            }

            var card = cardOptional.get();

            if(card.getBoardColumn().getKind() == BoardColumnKindEnum.CANCEL || card.getBoardColumn().getKind() == BoardColumnKindEnum.FINAL) {
                System.out.println("Esse card não pode ser cancelado pois ele já foi finalizado ou já foi cancelado");
                return;
            }

            cardService.cancelCard(card);
        }

        this.updateBoard();
    }

    private void showColumns() throws SQLException {
        board.getColumns().forEach(column -> {
            System.out.println(
                    "Nome: "   + column.getName()  +
                            " | ID: "  + column.getId()    +
                            " | Tipo: "+ column.getKind()  +
                            " (Ordem "+ column.getOrder() +")"
            );
        });

        this.separator();
    }

    private void showColumnsWithCards() throws SQLException {
        board.getColumns().forEach(column -> {
            System.out.println("Nome: "+column.getName()+ " ID: "+column.getId() + " Tipo: " + column.getKind() + " (Ordem " + column.getOrder() + ")");
            System.out.println("– Cartões:");

            column.getCards().forEach(card ->
                    System.out.println("   • [" + card.getId() + "] " + card.getTitle())
            );

            System.out.println();
        });
        this.separator();
    }

    private void showCard() throws SQLException {
        System.out.print("Informe o ID do card que deseja visualizar: ");

        Long id;
        while (true) {
            try {
                id = Long.parseLong(scanner.nextLine());
                break;
            } catch (NumberFormatException ignored) {
                System.out.print("Entrada inválida, digite novamente: ");
            }
        }

        try (var connection = getConnection()) {
            var service = new CardService(connection);
            Optional<CardEntity> optional = service.findById(id);

            if (optional.isEmpty()) {
                System.out.println("Nenhum card encontrado com esse ID!");
                return;
            }

            CardEntity card = optional.get();
            System.out.println("=== Detalhes do Card ===");
            System.out.println("ID:          " + card.getId());
            System.out.println("Título:      " + card.getTitle());
            System.out.println("Descrição:   " + card.getDescription());

            var col = card.getBoardColumn();
            System.out.println("Coluna:      " + col.getName() +
                    " (ID " + col.getId() +
                    ", " + col.getKind() +
                    ", ordem " + col.getOrder() + ")");

            var board = col.getBoard();
            System.out.println("Board:       " + board.getName() +
                    " (ID " + board.getId() + ")");
        }

        this.separator();
    }

    private void separator() {
        System.out.println("---------------------------");
    }

    private void updateBoard() throws SQLException {
        try (var connection = getConnection()) {
            var boardService = new BoardService(connection);

            var optional = boardService.findById(board.getId());

            if (optional.isEmpty()) {
                System.out.println("Nenhum board encontrado com esse ID!");
                return;
            }

            this.board = optional.get();
        }
    }
}
