package com.jx2.backuptool;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            var backupExecutor = BackupExecutor.readYamlConfigs("backup.yaml");
            backupExecutor.start();
        } else if (args.length == 1) {
            var backupExecutor = BackupExecutor.readYamlConfigs(args[0]);
            backupExecutor.start();
        } else {
            log.error("Usage: java BackupTool-<version>.jar [?config-file-path]");
        }
    }

}
