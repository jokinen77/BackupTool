/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.backuptool.configures;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jake
 */
public class SourceDirectory extends BackupDirectory {
    private TargetDirectory target = null;
    
    public SourceDirectory(String dir) {
        this.file = new File(dir);
        if (!this.file.exists()) {
            throw new IllegalArgumentException("Source doesn't exists! " + dir);
        }
    }
    
    public SourceDirectory(String dir, String target) {
        this.file = new File(dir);
        if (!this.file.exists()) {
            throw new IllegalArgumentException("Source doesn't exists! " + dir);
        }
        this.target = new TargetDirectory(target);
    }

    public TargetDirectory getTarget() {
        return target;
    }
}
