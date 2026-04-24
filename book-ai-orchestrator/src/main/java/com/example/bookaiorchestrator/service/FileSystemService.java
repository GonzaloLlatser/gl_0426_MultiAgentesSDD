package com.example.bookaiorchestrator.service;

import com.example.bookaiorchestrator.dto.FileChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class FileSystemService {

    private static final Logger log = LoggerFactory.getLogger(FileSystemService.class);

    public void applyChanges(List<FileChange> changes) {
        if (changes == null || changes.isEmpty()) {
            return;
        }

        for (FileChange change : changes) {
            applyChange(change);
        }
    }

    private void applyChange(FileChange change) {
        try {
            if ("CREATE".equals(change.action()) || "MODIFY".equals(change.action())) {
                writeFile(change);
            } else {
                log.warn("Unsupported file action: {}", change.action());
            }
        } catch (Exception e) {
            log.error("Could not apply file change. action={}, path={}", change.action(), change.path(), e);
        }
    }

    private void writeFile(FileChange change) throws Exception {
        Path path = Path.of(change.path());
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(path, change.content());
        log.info("Applied file change. action={}, path={}", change.action(), change.path());
    }
}
