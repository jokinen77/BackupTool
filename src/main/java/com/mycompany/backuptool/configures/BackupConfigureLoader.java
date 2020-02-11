/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.backuptool.configures;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Jake
 */
public class BackupConfigureLoader {
    private static final Logger logger = LoggerFactory.getLogger(BackupConfigureLoader.class);
    
    private static final String targetVariableName = "target";
    private static final String sourceVariableName = "source";
    
    public static BackupConfigure loadConfigures(String file) throws IOException {
        List<TargetDirectory> targets = new ArrayList<>();
        List<SourceDirectory> sources = new ArrayList<>();
        List<String> excluded = new ArrayList<>();
        HashMap<String, String> extraConfs = new HashMap<>();
        Files.lines(Paths.get(file), StandardCharsets.UTF_8).forEach(line -> {
            if (!line.matches(".+[=].+") || line.startsWith("#")) {
                return;
            }
            String variable = line.split("=", 2)[0].trim();
            String value = handlePath(line.split("=", 2)[1].trim());
            if (variable.equals(targetVariableName)) {
                targets.add(new TargetDirectory(value));
            } else if (variable.equals(sourceVariableName)) {
                if (value.matches(".+\\-\\>.+")) {
                    String sourcePath = handlePath(value.split("->", 2)[0].trim());
                    String target = handlePath(value.split("->", 2)[1].trim());
                    SourceDirectory source = new SourceDirectory(sourcePath, target);
                    sources.add(source);
                } else {
                    SourceDirectory source = new SourceDirectory(value);
                    sources.add(source);
                }
            } else if (variable.equals("exclude")) {
                excluded.add(value);
            } else {
                extraConfs.put(variable, value);
            }
        });
        BackupConfigure configures = new BackupConfigure(targets, sources, excluded, extraConfs);
        return configures;
    }
    
    private static String handlePath(String path) {
        String newPath = path;
        if (newPath.startsWith("~")) {
            newPath = newPath.replaceFirst("~", System.getProperty("user.home"));
        }
        return newPath;
    }
}
