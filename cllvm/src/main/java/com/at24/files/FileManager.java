package com.at24.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileManager {
    public static String getFileContents(String path) throws IOException {
        path = System.getProperty("user.dir") + path;
        System.out.println(Paths.get(path));
        String content;
        Scanner scanner = new Scanner(Paths.get(path), StandardCharsets.UTF_8.name());
        try {
            content = scanner.useDelimiter("\\A").next();
        } catch (Exception e) {
            content = "";
        }
        scanner.close();

        return content;
    }

    public static void writeToFile(String fileName, String content, boolean override) throws IOException {

        File f = new File(System.getProperty("user.dir") + fileName);
        f.createNewFile();
        // if(!f.exists() && !f.isDirectory()) { 
            
        // }
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
        writer.write(content);
        
        writer.close();
    }


}
