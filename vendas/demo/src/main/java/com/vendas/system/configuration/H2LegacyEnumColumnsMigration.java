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

        tryMigrate(jdbc, "ALTER TABLE pedidos ALTER COLUMN status VARCHAR(32) NOT NULL");
        tryMigrate(jdbc, "UPDATE pedidos SET status = 'EM_PRODUCAO' WHERE status IN ('EM_ANDAMENTO', 'EM_TRANSITO')");
        tryMigrate(jdbc, "UPDATE pedidos SET status = 'FINALIZADO' WHERE status IN ('CONCLUIDO', 'ENTREGUE_RETIRADO')");

        tryMigrate(jdbc, "ALTER TABLE pedidos ALTER COLUMN tipo_entrega VARCHAR(32) NOT NULL");

        if (tableExists(jdbc, "itens_pedido")) {
            tryMigrate(jdbc, "ALTER TABLE itens_pedido ALTER COLUMN tamanho VARCHAR(32)");
            tryMigrate(jdbc, "ALTER TABLE itens_pedido ALTER COLUMN cor VARCHAR(32)");
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
