package komiwojazer.FileParsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Łukasz
 */
public class MapParser {

    private FileReader fstream;
    private BufferedReader in;
    private ArrayList<String> map;

    public MapParser(String path) {
        try {
            fstream = new FileReader(path);
            in = new BufferedReader(fstream);
            map = new ArrayList<String>(50);
        } catch (FileNotFoundException ex) {
            System.out.println("Nie ma takiego pliku: " + path);
            System.exit(1);
        }
    }

    public ArrayList<String> readMap() throws IOException, FileFormatException {
        if (in.readLine().compareTo("Nodes:") != 0) {
            System.out.println("Brak słowa \"Nodes:\" lub jest ono niepoprawnie wpisane.");
            System.exit(1);
        }
        String s;
        try {
            String name;
            while ((name = in.readLine()).compareTo("Edges:") != 0) {
                Scanner sc = new Scanner(name);
                name = sc.next();
                if (name.length() > 0) {
                    map.add(name);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("W pliku brakuje słowa \"Edges:\" lub jest ono niepoprawnie wpisane.");
            System.exit(1);
        }
        map.add("Edges:");
        while ((s = in.readLine()) != null) {
            String[] t = new String[4];
            t = parseLine(s);
            double tryD = 0, tryC = 0;
            try {
                tryD = Double.parseDouble(t[2]);
            } catch (NumberFormatException e) {
                System.out.println("Błąd w pliku mapy. Nieprawidłowa wartość na 3 pozycji dla krawędzi: " + t[0] + " - " + t[1]);
                System.exit(1);
            }
            try {
                tryC = Double.parseDouble(t[3]);
            } catch (NumberFormatException e) {
                System.out.println("Błąd w pliku mapy. Nieprawidłowa wartość na 4 pozycji dla krawędzi: " + t[0] + " - " + t[1]);
                System.exit(1);
            }
            map.addAll(Arrays.asList(t));
        }
        return map;
    }

    private String[] parseLine(String s) {
        String[] t = new String[4];
        Scanner sc = new Scanner(s);
        try {
            t[0] = sc.next();
            t[1] = sc.next();
            t[2] = sc.next();
            t[3] = sc.next();
        } catch (NoSuchElementException e) {
            System.out.println("Błąd w pliku mapy. Brak pewnej wartości w opisie krawędzi");
            System.exit(1);
        }
        return t;
    }

    public static class FileFormatException extends Exception {

        public FileFormatException() {
        }

        public FileFormatException(String gripe) {
            super(gripe);
        }
    }
}
