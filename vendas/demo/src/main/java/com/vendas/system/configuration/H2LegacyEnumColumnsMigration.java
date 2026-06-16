package com.vendas.system.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;

/**
 * H2 creates native ENUM domains for {@code @Enumerated(STRING)}; changing enum literals breaks reads.
 * After Hibernate DDL, converts affected columns to VARCHAR and maps legacy status values.
 */
@Component
@Order
public class H2LegacyEnumColumnsMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(H2LegacyEnumColumnsMigration.class);

    private final DataSource dataSource;
    private final String jdbcUrl;

    public H2LegacyEnumColumnsMigration(DataSource dataSource,
            @Value("${spring.datasource.url:}") String jdbcUrl) {
        this.dataSource = dataSource;
        this.jdbcUrl = jdbcUrl != null ? jdbcUrl : "";
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!jdbcUrl.contains(":h2:")) {
            return;
        }

        var jdbc = new JdbcTemplate(dataSource);
        if (!tableExists(jdbc, "pedidos")) {
            return;
        }

        // Remove CHECK constraints obsoletas geradas por enums antigos (ex.: tipo_entrega só aceitava ENTREGA/RETIRADA).
        dropCheckConstraints(jdbc, "PEDIDOS");
        dropCheckConstraints(jdbc, "ITENS_PEDIDO");

        tryMigrate(jdbc, "ALTER TABLE pedidos ALTER COLUMN status VARCHAR(32) NOT NULL");
        tryMigrate(jdbc, "UPDATE pedidos SET status = 'EM_PRODUCAO' WHERE status IN ('EM_ANDAMENTO', 'EM_TRANSITO')");
        tryMigrate(jdbc, "UPDATE pedidos SET status = 'FINALIZADO' WHERE status IN ('CONCLUIDO', 'ENTREGUE_RETIRADO')");

        tryMigrate(jdbc, "ALTER TABLE pedidos ALTER COLUMN tipo_entrega VARCHAR(32)");

        // valor_total agora pode ser nulo no modo solicitação
        tryMigrate(jdbc, "ALTER TABLE pedidos ALTER COLUMN valor_total DROP NOT NULL");

        if (tableExists(jdbc, "itens_pedido")) {
            // Colunas legadas (produto_id, tamanho, cor) substituídas por item_padronizado_id, tamanho_id, cor_id.
            tryMigrate(jdbc, "ALTER TABLE itens_pedido ALTER COLUMN produto_id DROP NOT NULL");
            tryMigrate(jdbc, "ALTER TABLE itens_pedido ALTER COLUMN preco_unitario DROP NOT NULL");
            tryMigrate(jdbc, "ALTER TABLE itens_pedido ALTER COLUMN valor_total DROP NOT NULL");
            tryMigrate(jdbc, "ALTER TABLE itens_pedido ALTER COLUMN tamanho VARCHAR(32)");
            tryMigrate(jdbc, "ALTER TABLE itens_pedido ALTER COLUMN cor VARCHAR(32)");
        }
    }

    private static void dropCheckConstraints(JdbcTemplate jdbc, String tableName) {
        try {
            List<String> nomes = jdbc.queryForList(
                    "SELECT CONSTRAINT_NAME FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS "
                            + "WHERE CONSTRAINT_TYPE = 'CHECK' AND UPPER(TABLE_NAME) = UPPER(?)",
                    String.class,
                    tableName);
            for (String nome : nomes) {
                tryMigrate(jdbc, "ALTER TABLE " + tableName + " DROP CONSTRAINT IF EXISTS \"" + nome + "\"");
            }
        } catch (Exception ex) {
            log.debug("Não foi possível listar/remover check constraints de [{}]: {}", tableName, ex.getMessage());
        }
    }

    private static boolean tableExists(JdbcTemplate jdbc, String tableName) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = UPPER(?)",
                Integer.class,
                tableName);
        return n != null && n > 0;
    }

    private static void tryMigrate(JdbcTemplate jdbc, String sql) {
        try {
            jdbc.execute(sql);
        } catch (Exception ex) {
            log.debug("H2 migration skipped for [{}]: {}", sql, ex.getMessage());
        }
    }
}
