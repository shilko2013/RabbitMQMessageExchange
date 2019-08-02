package com.hireright.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader extends Properties {

    public PropertiesReader(String pathToResources) {
        try (FileReader reader = new FileReader(pathToResources)) {
            load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
