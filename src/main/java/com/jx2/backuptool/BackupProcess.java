package com.jx2.backuptool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jaakko
 */
@Slf4j
@Getter
public class BackupProcess extends BackupConfigs implements Runnable {

    private String source;
    private String target;
    private Set<File> filesToBackup;

    private int copiedFiles;
    private int copySuccess;
    private int copyFailed;

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        this.filesToBackup = new TreeSet();

        Path sourcePath = Paths.get(createRegex(source)).toAbsolutePath();
        Path targetPath = Paths.get(createRegex(target)).toAbsolutePath();
        
        log.info("{} -> {}", sourcePath.toFile(), targetPath.toFile());
        
        findAllSourceFiles(sourcePath.toFile());
        for (File sourceFile : filesToBackup) {
            Path relativeSource = sourcePath.getParent().relativize(sourceFile.toPath());
            File targetFile = targetPath.resolve(relativeSource).toFile();
            this.copyFileIfShould(sourceFile, targetFile);
        }
        
        double took =  ((double) System.currentTimeMillis() - startTime) / 1000;
        log.info("Files copied: {}, Success: {}/{}, Took: {}s, Source: {}", 
                copiedFiles, copySuccess, copySuccess + copyFailed, took,
                sourcePath.toFile().getAbsolutePath());
    }

    private void findAllSourceFiles(File file) {
        if (this.isExcluded(file) || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                findAllSourceFiles(subFile);
            }
        } else {
            filesToBackup.add(file);
        }
    }

    private void copyFileIfShould(File source, File target) {
        log.info("Copying: {}", source.getAbsolutePath());
        log.debug("Target: {}", target.getAbsolutePath());
        try {
            if (target.exists()) {
                if (source.lastModified() > target.lastModified()) {
                    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    this.copiedFiles++;
                }
            } else {
                target.mkdirs();
                Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                this.copiedFiles++;
            }
            this.copySuccess++;
        } catch (IOException e) {
            log.error("Cannot copy file!", e);
            this.copyFailed++;
        }
    }
    
    
    private boolean isExcluded(File path) {
        for (String exclude : this.getExcludes()) {
            String regex = createRegex(exclude);
            if (path.getName().matches(regex) || path.getAbsolutePath().toString().matches(regex)) {
                log.info("Excluding: " + path.getAbsolutePath());
                return true;
            }
        }
        return false;
    }
    
    public static String createRegex(String path) {
        String regex = path.trim();
        if (regex.startsWith("~")) {
            regex = regex.replaceFirst("~", System.getProperty("user.home"));
        }
        regex = regex.replaceAll("\\$date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        regex = regex.replaceAll("\\*", "(.*)");
        return regex;
    }

}
