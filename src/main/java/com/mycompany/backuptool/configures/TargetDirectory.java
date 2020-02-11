/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.backuptool.configures;

import java.io.File;
import java.time.LocalDate;

/**
 *
 * @author Jake
 */
public class TargetDirectory extends BackupDirectory {
    
    public TargetDirectory(String dir) {
        dir = fillVariables(dir);
        this.file = new File(dir);
    }
    
    private String fillVariables(String dir) {
        dir = dir.replaceAll("\\$\\{date\\}", LocalDate.now().toString());
        return dir;
    }
}
