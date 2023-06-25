package com.jx2.backuptool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jaakko
 */
@Slf4j
@Getter
public class BackupConfigs {
    
    private List<String> excludes = new ArrayList<>();
    
    public void addExcludes(Collection<String> excludes) {
        this.excludes.addAll(excludes);
    }
}
