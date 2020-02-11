/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.backuptool.handler;

import com.mycompany.backuptool.configures.BackupConfigure;
import com.mycompany.backuptool.configures.BackupDirectory;
import com.mycompany.backuptool.configures.SourceDirectory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jake
 */
public class BackupHandler {

    private static final Logger logger = LoggerFactory.getLogger(BackupHandler.class);
    private BackupConfigure configures;
    private List<BackupFile> sourceFiles;
    private int copyFailedCount = 0;
    private int copySuccessCount = 0;
    private int newFilesCount = 0;
    private List<String> errorMessages;

    public BackupHandler(BackupConfigure configures) {
        this.configures = configures;
        this.sourceFiles = new ArrayList<>();
        this.errorMessages = new ArrayList<>();
    }

    public void backupFiles() {
        logger.info("####################### Backup starting #######################");
        long startTime = System.currentTimeMillis();
        for (SourceDirectory dir : configures.getSources()) {
            findAllSourceFiles(dir.getFile(), dir.getFile(), dir.getTarget() != null ? dir.getTarget().getFile() : null, this.sourceFiles);
        }
        for (BackupFile source : this.sourceFiles) {
            if (source.getTarget() != null) {
                File target = getTarget(source.getTarget(), source);
                copyFilesFromSourceToDestination(source.getSource(), target);
                if (this.configures.isCopyOnlyToArrowTarget()) {
                    continue;
                }
            }
            for (BackupDirectory destination : this.configures.getTargets()) {
                File target = getTarget(destination.getFile(), source);
                copyFilesFromSourceToDestination(source.getSource(), target);
            }
        }
        logger.info("####################### Errors #######################");
        this.errorMessages.stream().forEach((message) -> {
            logger.error(message);
        });
        logger.info("####################### Backup finished! #######################");
        logger.info("Copy total count: " + countTotelCopies());
        logger.info("Copy success: " + this.copySuccessCount);
        logger.info("Copy failed: " + this.copyFailedCount);
        logger.info("New files copied: " + this.newFilesCount);
        logger.info("Backup took: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void copyFilesFromSourceToDestination(File source, File target) {
        long startTime = System.currentTimeMillis();
        logger.info("####################### Copying file #######################");
        logger.info("Source: " + source.getAbsolutePath());
        logger.info("Target: " + target.getAbsolutePath().toString());
        try {
            if (target.exists()) {
                if (source.lastModified() > target.lastModified() || this.configures.isForceReplaceExisting()) {
                    Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                target.mkdirs();
                Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                newFilesCount++;
            }
            this.copySuccessCount++;
        } catch (IOException ex) {
            logger.error("Cannot copy file!", ex);
            this.errorMessages.add(ex.toString());
            this.copyFailedCount++;
        }
        logger.info("Took: " + (System.currentTimeMillis() - startTime) + "ms");
        logger.info("Copy " +  (copySuccessCount + copyFailedCount) + "/" + countTotelCopies());
    }

    private void findAllSourceFiles(File root, File path, File target, List<BackupFile> files) {
        if (path.isDirectory()) {
            for (File file : path.listFiles()) {
                if (!isExcluded(file)) {
                    findAllSourceFiles(root, file, target, files);
                }
            }
        } else if (!isExcluded(path)) {
            files.add(new BackupFile(path, target, root));
        }
    }

    private File getTarget(File target, BackupFile file) {
        File targetFile = new File(target.getAbsolutePath().toString() + "" + file.getRelativeSourceRootPath());
        return targetFile;
    }

    private boolean isExcluded(File path) {
        for (String exclude : this.configures.getExcludeDirs()) {
            if (path.getName().matches(exclude) || path.getAbsolutePath().toString().matches(exclude)) {
                logger.info("Excluding file: " + path.getAbsolutePath());
                return true;
            }
        }
        return false;
    }
    
    private int countTotelCopies() {
        int count = 0;
        for (BackupFile sourceFile : sourceFiles) {
            if (this.configures.isCopyOnlyToArrowTarget()) {
                if (sourceFile.getTarget() != null) {
                    count++;
                } else {
                    count += this.configures.getTargets().size();
                }
            } else {
                count += this.configures.getTargets().size();
                if (sourceFile.getTarget() != null) {
                    count++;
                }
            }
        }
        return count;
    }
}
