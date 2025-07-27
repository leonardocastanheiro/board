package br.com.dio.persistence.entity;

public enum BoardColumnKindEnum {

    INITIAL,FINAL,CANCEL,PENDING;

    public static BoardColumnKindEnum fromString(String value) {
        try {
            return BoardColumnKindEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Valor inválido para BoardColumnKindEnum: " + value);
        }
    }
}
