package br.com.dio;

import br.com.dio.persistence.migration.MigrationStrategy;

import java.sql.DriverManager;
import java.sql.SQLException;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try(var connection = getConnection()) {
            new MigrationStrategy(connection).executeMigration();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}