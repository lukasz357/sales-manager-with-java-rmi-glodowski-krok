/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package salesmanager.FileParsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author Łukasz
 */
public class ProductsParser {

    private FileReader fstream;
    private BufferedReader in;
    private ArrayList<String> products;

    public ProductsParser(String path) {
        try {
            fstream = new FileReader(path);
            in = new BufferedReader(fstream);
            products = new ArrayList<String>(50);
        } catch (FileNotFoundException ex) {
            System.out.println("Nie ma takiego pliku: " + path);
            System.exit(1);
        }
    }

    public ArrayList<String> readProducts() throws IOException {
        String line, name, tryAmnt, trySize;
        while ((line = in.readLine()) != null) {
            Scanner sc = new Scanner(line);
            name = sc.next();
            products.add(name);
            try {
                tryAmnt = sc.next();
                Integer.parseInt(tryAmnt);
                products.add(tryAmnt);
            } catch (NumberFormatException ex) {
                System.err.println("Nieprawidłowa wartość dla liczby sztuk produktu: " + name);
                System.exit(1);
            } catch (NoSuchElementException ex) {
                System.out.println("Nie podano liczby sztuk w opisie produktu: " + name);
                System.exit(1);
            }
            try {
                trySize = sc.next();
                Integer.parseInt(trySize);
                products.add(trySize);
            } catch (NumberFormatException ex) {
                System.err.println("Nieprawidłowa wartość dla rozmiaru produktu: " + name);
                System.exit(1);
            } catch (NoSuchElementException ex) {
                System.out.println("Nie podano rozmiaru produktu " + name +"w jego opisie.");
                System.exit(1);
            }
        }
        return products;
    }
}
