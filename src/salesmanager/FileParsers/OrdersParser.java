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
public class OrdersParser {

    private FileReader fstream;
    private BufferedReader in;
    private ArrayList<String> orders;

    public OrdersParser(String path) {
        try {
            fstream = new FileReader(path);
            in = new BufferedReader(fstream);
            orders = new ArrayList<String>(50);
        } catch (FileNotFoundException ex) {
            System.out.println("Nie ma takiego pliku: " + path);
            System.exit(1);
        }
    }

    public ArrayList<String> readOrders() throws IOException {
        String line, name, tryAmnt;
        while ((line = in.readLine()) != null) {
            if (line.charAt(0) == '#') {
                orders.add(line);
            } else {
                Scanner sc = new Scanner(line);
                name = sc.next();
                orders.add(name);
                try {
                    tryAmnt = sc.next();
                    Integer.parseInt(tryAmnt);
                    orders.add(tryAmnt);
                } catch (NumberFormatException ex) {
                    System.err.println("Nieprawidłowa wartość dla liczby sztuk produktu: " + name);
                    System.exit(1);
                } catch (NoSuchElementException ex) {
                    System.out.println("Nie podano liczby sztuk w opisie produktu: " + name);
                    System.exit(1);
                }
            }
        }
        return orders;
    }
}
