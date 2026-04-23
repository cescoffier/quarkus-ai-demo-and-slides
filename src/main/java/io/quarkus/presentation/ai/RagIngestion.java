package io.quarkus.presentation.ai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

@Singleton
@Startup
public class RagIngestion {

    @Inject
    EmbeddingStore<TextSegment> store;

    @Inject
    EmbeddingModel embeddingModel;

    @PostConstruct
    void ingest() {
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(300, 30))
                .build();

        ingestor.ingest(loadDocument("rag/acme-products.md"));
        ingestor.ingest(loadDocument("rag/acme-policies.md"));
        Log.info("RAG documents ingested");
    }

    private Document loadDocument(String classpathResource) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpathResource)) {
            if (is == null) {
                throw new IllegalStateException("Resource not found: " + classpathResource);
            }
            return Document.document(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + classpathResource, e);
        }
    }
}
