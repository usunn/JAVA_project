package com.project.order.storage;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class FileManager {
    public static <T> List<T> loadAll(String filename, Function<String,T> parser) throws IOException {
        List<T> list = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(parser.apply(line));
            }
        }
        return list;
    }
    
    public static void saveAll(String filename, List<? extends Persistable> list) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Persistable p : list) {
                bw.write(p.toRecord()); bw.newLine();
            }
        }
    }

    
    public static void append(String filename, Persistable p) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(p.toRecord());
            bw.newLine();
        }
    }
}
