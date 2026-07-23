package com.savbill.salescrmsbss.utils;

import java.io.IOException;

public class FileNotCreatedException extends IOException {
    
	public FileNotCreatedException() {
        super("File not created");
    }

    public FileNotCreatedException(String msg) {
        super(msg);
    }
}
