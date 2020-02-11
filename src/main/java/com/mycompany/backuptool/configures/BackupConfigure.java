/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.backuptool.configures;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jake
 */
public class BackupConfigure {
    private static final Logger logger = LoggerFactory.getLogger(BackupConfigure.class);
    private List<TargetDirectory> targets;
    private List<SourceDirectory> sources;
    private boolean forceReplaceExisting = false;
    private boolean copyOnlyToArrowTarget = true;
    private List<String> excludeDirs;
    
    public BackupConfigure(List<TargetDirectory> targets, List<SourceDirectory> sources, List<String> excludeDirs, HashMap<String, String> extraConfs) {
        if (!validateListNotNullOrEmpty(targets)) {
            throw new IllegalArgumentException("Targets is null or empty!");
        }
        if (!validateListNotNullOrEmpty(sources)) {
            throw new IllegalArgumentException("Sources is null or empty!");
        }
        this.targets = new ArrayList<>();
        targets.stream().forEach((target) -> {
            this.targets.add(target);
        });
        this.sources = new ArrayList<>();
        sources.stream().forEach((source) -> {
            this.sources.add(source);
        });
        this.excludeDirs = excludeDirs;
        this.forceReplaceExisting = handleExtraConf(extraConfs, "forceReplaceExisting");
        this.copyOnlyToArrowTarget = handleExtraConf(extraConfs, "copyOnlyToArrowTarget");
    }

    public List<String> getExcludeDirs() {
        return excludeDirs;
    }
    
    public List<TargetDirectory> getTargets() {
        return targets;
    }

    public List<SourceDirectory> getSources() {
        return sources;
    }

    public boolean isForceReplaceExisting() {
        return forceReplaceExisting;
    }
    
    private boolean validateListNotNullOrEmpty(List list) {
        return list != null && !list.isEmpty();
    }
    
    public void printToLog() {
        for (String line : this.toString().split("\n")) {
            logger.info(line);
        }
    }

    public boolean isCopyOnlyToArrowTarget() {
        return copyOnlyToArrowTarget;
    }

    public void setCopyOnlyToArrowTarget(boolean copyOnlyToArrowTarget) {
        this.copyOnlyToArrowTarget = copyOnlyToArrowTarget;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BackupConfigures:\n");
        sb.append("\tTargets:\n");
        this.targets.stream().forEach((target) -> {
            sb.append("\t\t" + target + "\n");
        });
        sb.append("\tSources:\n");
        this.sources.stream().forEach((source) -> {
            sb.append("\t\t" + source + "\n");
            if (source.getTarget() != null) {
                sb.append("\t\t\t-> " + source.getTarget().getFile().getAbsolutePath() + "\n");
            }
        });
        sb.append("\tExcluded:\n");
        this.excludeDirs.stream().forEach((source) -> {
            sb.append("\t\t" + source.replaceAll("\\(\\.\\*\\)", "\\*") + "\n");
        });
        sb.append("\tExtra configures:\n");
        sb.append("\t\tforceReplaceExisting = " + forceReplaceExisting + "\n");
        sb.append("\t\tcopyOnlyToArrowTarget = " + copyOnlyToArrowTarget + "\n");
        return sb.toString();
    }
    
    private boolean handleExtraConf(HashMap<String, String> extraConfs, String key) {
        return extraConfs != null && extraConfs.get(key) != null && extraConfs.get(key).equals("true");
    }
}
