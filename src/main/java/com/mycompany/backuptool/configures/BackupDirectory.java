/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.backuptool.configures;

import java.io.File;

/**
 *
 * @author Jake
 */
public abstract class BackupDirectory {
    protected File file;
    
    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return this.file.getAbsolutePath();
    }
    
}
