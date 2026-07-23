package com.savbill.notification.savbilliwfnotification.util;

import freemarker.cache.FileTemplateLoader;

import java.io.File;
import java.io.IOException;

public class TemplateLoader {
    private static FileTemplateLoader templateLoader;

    private TemplateLoader(String path) throws IOException {
        // Initialize the template loader here
        String templateDirectory = System.getProperty("user.dir") + path;
        File templateDirectoryFile = new File(templateDirectory);
        templateLoader = new FileTemplateLoader(templateDirectoryFile);
    }

    public static FileTemplateLoader getTemplateLoader(String tempDirPath) throws IOException {
        if (templateLoader == null) {
            new TemplateLoader(tempDirPath); // Create instance if not already created
        }
        return templateLoader;
    }
}
