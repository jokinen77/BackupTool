/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template source, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.backuptool.handler;

import java.io.File;

/**
 *
 * @author Jake
 */
public class BackupFile {
    private File source;
    private File sourceRoot;
    private File target;

    public BackupFile(File source, File target, File sourceRoot) {
        this.source = source;
        this.sourceRoot = sourceRoot;
        this.target = target;
    }

    public File getSource() {
        return source;
    }
    
    public String getRelativeSourceRootPath() {
        if (sourceRoot.isDirectory()) {
            return "/" + sourceRoot.getName() + source.getAbsolutePath().toString().replace(sourceRoot.getAbsolutePath().toString(), "");
        }
        return "/" + source.getName();
    }
    
    public String getSourceAbsolutePath() {
        return this.source.getAbsolutePath().toString();
    }

    public File getTarget() {
        return target;
    }
}
