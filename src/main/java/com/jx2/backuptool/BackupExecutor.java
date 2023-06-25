package com.jx2.backuptool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jaakko
 */
@Slf4j
@Getter
public class BackupExecutor extends BackupConfigs {
    
    private int threads = 3;
    private List<BackupProcess> backupItems;

    public void start() {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long startTime = System.currentTimeMillis();
        log.info("Starting backup with {} threads...", threads);
        
        for (BackupProcess runner : this.backupItems) {
            runner.addExcludes(this.getExcludes());
            executor.execute(runner);
        }
        executor.shutdown();
        
        try {
            executor.awaitTermination(365, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            log.error("Executor waiting problem!", e);
        }
        
        log.info("Backup finished! Total time: {}s", ((double) System.currentTimeMillis() - startTime) / 1000);
    }

    public static BackupExecutor readYamlConfigs(String file) throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        return mapper.readValue(Paths.get(file).toFile(), BackupExecutor.class);
    }

}
