package com.nexapay.config;

import org.mockito.Mockito;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestConfiguration
@Profile("test")
public class TestVectorStoreConfig {

    @Bean
    @Primary
    public EmbeddingModel mockEmbeddingModel() {
        return Mockito.mock(EmbeddingModel.class);
    }

    @Bean
    @Primary
    public VectorStore testVectorStore() {
        VectorStore store = Mockito.mock(VectorStore.class);
        when(store.similaritySearch(any(org.springframework.ai.vectorstore.SearchRequest.class)))
                .thenReturn(List.of(
                        new org.springframework.ai.document.Document("Policy clause reference: Insufficient funds or card limits must be checked against available credit line.", java.util.Map.of("source", "card-limit-policy.md", "category", "card")),
                        new org.springframework.ai.document.Document("Policy clause reference: Settlement reconciliation mismatches must be escalated to operations.", java.util.Map.of("source", "capture-and-settlement.md", "category", "payments")),
                        new org.springframework.ai.document.Document("Policy clause reference: Expired cards must be declined with ISO-8583 response code 54.", java.util.Map.of("source", "card-lifecycle-policy.md", "category", "card")),
                        new org.springframework.ai.document.Document("Policy clause reference: High velocity transaction volume triggers automated anomaly investigation.", java.util.Map.of("source", "velocity-rules.md", "category", "fraud"))
                ));
        return store;
    }
}
