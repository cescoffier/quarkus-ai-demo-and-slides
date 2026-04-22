package io.quarkus.presentation.ai;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class AuditLog {

    public record Entry(String timestamp, String toolName, String parameters, String result) {}

    private final List<Entry> entries = Collections.synchronizedList(new ArrayList<>());

    public void log(String toolName, String parameters, String result) {
        entries.add(new Entry(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
            toolName,
            parameters,
            result
        ));
    }

    public List<Entry> getEntries() {
        return List.copyOf(entries);
    }

    public void clear() {
        entries.clear();
    }
}
