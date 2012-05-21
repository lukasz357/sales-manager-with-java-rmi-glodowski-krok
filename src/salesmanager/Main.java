/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package salesmanager;

import salesmanager.GenericMap.GenericMap;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import salesmanager.Map.*;
import salesmanager.FileParsers.MapParser.FileFormatException;
import salesmanager.FileParsers.*;
import salesmanager.FileParsers.ArgsParser.BadArgsException;

/**
 *
 * @author Łukasz
 */
public class Main {

    public static void main(String[] args) throws BadArgsException, ClassNotFoundException, SQLException, IOException, FileFormatException {
        Class.forName("org.sqlite.JDBC");
        ArgsParser p = new ArgsParser();
        DataBase base = new DataBase();
        Connection conn = base.getConnection();
        switch(p.parseArgs(args)) {
            case CLEAR_BASE:
                base.clearBase();
                break;
            case MAKE_ORDERS:
                GenericMap<City> m = base.getMap();
                ArrayList<Driver> drivers = base.getDrivers();
                OrdersRealisation or = new OrdersRealisation(base.getOrders(m, drivers), m, drivers);
                Raport r = or.go();
                r.saveHTMLReport();
                or.wypisz();
                break;
            case DEL_PRODUCT:
                base.delProduct(args[1]);
                break;
            case DEL_DRIVER:
                base.delDriver(args[1]);
                break;
            case ADD_PR_FROM_FILE:
                ProductsParser pp = new ProductsParser(args[2]);
                ArrayList al = pp.readProducts();
                base.addProducts(al);
                break;
            case ADD_ORD_FROM_FILE:
                OrdersParser op = new OrdersParser("zamowienia.txt");
                ArrayList ords = op.readOrders();
                base.addOrders(ords);
                break;
            case ADD_PRODUCT:
                try{
                    base.addProduct(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                }catch(NumberFormatException ex) {
                    System.out.println("Nieprawidłowy argument - ilość sztuk");
                    System.exit(1);
                }
                break;
            case ADD_DRIVER:
                try{
                    base.addDriver(args[1], Integer.parseInt(args[2]));
                }catch(NumberFormatException ex) {
                    System.out.println("Nieprawidłowy argument - pojemność samochodu");
                    System.exit(1);
                }
                break;
            case ADD_MAP:
                MapParser mp = new MapParser(args[1]);
                ArrayList mapL = mp.readMap();
                base.addMap(mapL);
                break;
            default:
                System.out.println("Nieprawidłowe argumenty!!!");
                System.exit(1);
        }
        conn.close();
    }
    private static final int CLEAR_BASE = 1;
    private static final int MAKE_ORDERS = 2;
    private static final int DEL_PRODUCT = 3;
    private static final int DEL_DRIVER = 4;
    private static final int ADD_PR_FROM_FILE = 5;
    private static final int ADD_ORD_FROM_FILE = 6;
    private static final int ADD_PRODUCT = 7;
    private static final int ADD_DRIVER = 8;
    private static final int ADD_MAP = 9;
}
