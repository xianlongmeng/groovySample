package com.ly.resource.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class ResourceTest {
    public Collection<File> findResources(String path) throws URISyntaxException {
        if (path==null)
            return new ArrayList<>();
        URL url = this.getClass().getResource(path);
        if (url==null)
            return new ArrayList<>();
        File file = new File(url.toURI());
        return FileUtils.listFiles(file, new String[]{"groovy"}, true);
    }
}
