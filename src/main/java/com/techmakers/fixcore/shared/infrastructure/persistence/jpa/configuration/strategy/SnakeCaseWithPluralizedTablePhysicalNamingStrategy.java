package com.techmakers.fixcore.shared.infrastructure.persistence.jpa.configuration.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class SnakeCaseWithPluralizedTablePhysicalNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return toSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return toSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return toSnakeCase(toPlural(identifier));
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return toSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return toSnakeCase(identifier);
    }

    private Identifier toSnakeCase(Identifier identifier) {
        if (identifier == null) return null;
        var snakeCase = identifier.getText()
                .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .toLowerCase();
        return Identifier.toIdentifier(snakeCase, identifier.isQuoted());
    }

    private Identifier toPlural(Identifier identifier) {
        if (identifier == null) return null;
        var text = identifier.getText();
        String plural;
        if (text.matches(".*[^aeiouAEIOU]y$")) plural = text.substring(0, text.length() - 1) + "ies";
        else if (text.matches(".*(s|x|z|ch|sh)$")) plural = text + "es";
        else plural = text + "s";
        return Identifier.toIdentifier(plural, identifier.isQuoted());
    }
}
