package com.nexapay.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Profile("!test")
public class VectorStoreConfig {

    private static final Logger log = LoggerFactory.getLogger(VectorStoreConfig.class);

    @Bean
    @Primary
    @ConditionalOnMissingBean(VectorStore.class)
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate,
                                  EmbeddingModel embeddingModel,
                                  @Value("${spring.ai.vectorstore.pgvector.dimensions:1536}") int dimensions,
                                  @Value("${spring.ai.vectorstore.pgvector.distance-type:COSINE_DISTANCE}") PgVectorStore.PgDistanceType distanceType) {
        boolean hasPgVector = false;
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM pg_extension WHERE extname = 'vector'", Integer.class);
            hasPgVector = (count != null && count > 0);
        } catch (Exception e) {
            log.warn("Could not query pg_extension for vector support: {}", e.getMessage());
        }

        if (hasPgVector) {
            log.info("PostgreSQL pgvector extension detected. Configuring PgVectorStore.");
            return new PgVectorStore(jdbcTemplate, embeddingModel, dimensions, distanceType, false, PgVectorStore.PgIndexType.HNSW, false);
        } else {
            log.info("PostgreSQL pgvector extension NOT available. Automatically using SimpleVectorStore (In-Memory Vector Store) for local development.");
            return new SimpleVectorStore(embeddingModel);
        }
    }
}
