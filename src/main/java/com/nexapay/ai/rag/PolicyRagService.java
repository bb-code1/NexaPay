package com.nexapay.ai.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PolicyRagService {

    private static final Logger log = LoggerFactory.getLogger(PolicyRagService.class);
    private final VectorStore vectorStore;

    public PolicyRagService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public int ingestPolicyKnowledgeBase() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:docs/**/*.md");
            List<Document> documents = new ArrayList<>();

            for (Resource resource : resources) {
                String content = resource.getContentAsString(StandardCharsets.UTF_8);
                String filename = resource.getFilename();
                String category = extractCategory(resource.getURI().toString());

                // Markdown-aware rule chunking
                String[] sections = content.split("(?=## )");
                for (int i = 0; i < sections.length; i++) {
                    String sectionContent = sections[i].trim();
                    if (!sectionContent.isEmpty()) {
                        Document doc = new Document(
                                sectionContent,
                                Map.of(
                                        "source", filename != null ? filename : "unknown",
                                        "category", category,
                                        "chunk_index", i
                                )
                        );
                        documents.add(doc);
                    }
                }
            }

            if (!documents.isEmpty()) {
                vectorStore.accept(documents);
                log.info("Successfully ingested {} policy chunks into pgvector store", documents.size());
            }
            return documents.size();
        } catch (Exception e) {
            log.warn("Vector store ingestion fallback (pgvector/model offline): {}", e.getMessage());
            return 0;
        }
    }

    public List<Document> retrieveRelevantPolicies(String query, String category) {
        try {
            SearchRequest request = SearchRequest.query(query)
                    .withTopK(3)
                    .withSimilarityThreshold(0.65);

            if (category != null && !category.isBlank()) {
                request = request.withFilterExpression(new Filter.Expression(
                        Filter.ExpressionType.EQ,
                        new Filter.Key("category"),
                        new Filter.Value(category.toLowerCase())
                ));
            }

            return vectorStore.similaritySearch(request);
        } catch (Exception e) {
            log.warn("Similarity search fallback: {}", e.getMessage());
            return List.of();
        }
    }

    private String extractCategory(String uri) {
        if (uri.contains("/card/")) return "card";
        if (uri.contains("/payments/")) return "payments";
        if (uri.contains("/fraud/")) return "fraud";
        return "general";
    }
}
