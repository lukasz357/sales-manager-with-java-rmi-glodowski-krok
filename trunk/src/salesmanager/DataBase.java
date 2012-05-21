package salesmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import salesmanager.GenericMap.GenericCity;
import salesmanager.GenericMap.GenericMap;
import salesmanager.Map.City;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author lukasz357
 */
public class DataBase {

    private Connection conn;
    private Statement stat;

    public DataBase() {
    	try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println("Problem z połączeniem do bazy danych");
		}
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:SalesManagerBase.db");
            stat = conn.createStatement();
            stat.executeUpdate("create table if not exists PRODUCTS (pr_name char(100), amount int, size int, PRIMARY KEY(pr_name));");
            stat.executeUpdate("create table if not exists CITIES (city_name char(100), PRIMARY KEY(city_name));");
            stat.executeUpdate("create table if not exists WAY_START (ws_id INTEGER PRIMARY KEY AUTOINCREMENT, city_name char(100) references CITIES(city_name));");
            stat.executeUpdate("create table if not exists WAY(ws_id INTEGER references WAY_START(ws_id), city_name char(100) references CITIES(city_name), way_len int, way_cost int );");
            stat.executeUpdate("create table if not exists ORDERS (order_id INTEGER PRIMARY KEY AUTOINCREMENT, city_name char(100) references CITIES(city_name));");
            stat.executeUpdate("create table if not exists ORDERS_DESC (order_id INTEGER references ORDERS(order_id), pr_name char(100) references PRODUCTS(pr_name), amount int);");
            stat.executeUpdate("create table if not exists DRIVERS (name char(100), capacity int, PRIMARY KEY(name));");
        } catch (SQLException ex) {
            Logger.getLogger(DataBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Driver> getDrivers() throws SQLException {
        ResultSet rs = stat.executeQuery("select * from DRIVERS;");
        ArrayList<Driver> drvs = new ArrayList<Driver>(10);
        while (rs.next()) {
            String name = rs.getString(1);
            int capacity = rs.getInt(2);
            drvs.add(new Driver(name, capacity));
        }
        return drvs;
    }
    public ArrayList<Product> getProducts() throws SQLException {
        ArrayList<Product> products = new ArrayList(5);
        ResultSet rs = stat.executeQuery("SELECT * FROM PRODUCTS;");
        while(rs.next()) {
            String name = rs.getString(1);
            int amount = rs.getInt(2);
            int weight = rs.getInt(3);
            products.add(new Product(name,amount,weight));
        }
        return products;
    }

    public Collection getOrders(GenericMap<City> m, ArrayList<Driver> drivers) throws SQLException {
        int capacityOfDrivers = totalCapacity(drivers);
        HashMap<Integer, Order> orders = new HashMap<Integer, Order>();
        HashMap<String, Integer> prds = new HashMap<String, Integer>(20);
        try {
            ResultSet prset = stat.executeQuery("SELECT pr_name, size FROM PRODUCTS");
            while (prset.next()) {
                prds.put(prset.getString(1), prset.getInt(2));
            }
            ResultSet rs = stat.executeQuery("select * from ORDERS;");
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                GenericCity city = m.getCity(name);
                orders.put(id, new Order(id, city));
            }
        } catch (SQLException ex) {
            System.out.println("W bazie nie ma zadnych zamowien.");
            System.exit(1);
        }
        try {
            ResultSet rsd = stat.executeQuery("select * from ORDERS_DESC");
            int tmpCapacity = 0;
            while (rsd.next()) {
                int id = rsd.getInt(1);
                String name = rsd.getString(2);
                int amount = rsd.getInt(3);
                int size = prds.get(name);
                Order o = orders.get(id);
                if (o != null) {
                    if((tmpCapacity + (amount*size) ) > capacityOfDrivers )
                        break;
                    o.addProduct(name, amount, size);
                    tmpCapacity += (amount*size);
                }
            }
            ArrayList<Integer> ids = new ArrayList<Integer>(orders.size());
            for (Order o : orders.values()) {
                if (o.getAllProducts().isEmpty()) {
                    ids.add(o.getID());
                }
            }
            for (int i : ids) {
                orders.remove(i);
            }
        } catch (SQLException ex) {
            System.out.println("W bazie nie ma zadnych opisów zamówien.");
            System.exit(1);
        }
//        try {
//            PreparedStatement prepOrd = conn.prepareStatement("DELETE FROM ORDERS WHERE order_id = ?");
//            Iterator<Integer> ordIterator = orders.keySet().iterator();
//            while (ordIterator.hasNext()) {
//                prepOrd.setInt(1, ordIterator.next());
//                prepOrd.addBatch();
//            }
//            conn.setAutoCommit(false);
//            prepOrd.executeBatch();
//            conn.setAutoCommit(true);
//            PreparedStatement prepDesc = conn.prepareStatement("DELETE FROM ORDERS_DESC WHERE order_id = ?");
//            Iterator<Integer> descIterator = orders.keySet().iterator();
//            while (descIterator.hasNext()) {
//                prepDesc.setInt(1, descIterator.next());
//                prepDesc.addBatch();
//            }
//            conn.setAutoCommit(false);
//            prepDesc.executeBatch();
//            conn.setAutoCommit(true);
//        } catch (SQLException ex) {
//            System.out.println("Nie udało się usunąć z bazy zrealizowanych zamówień.");
//            System.exit(1);
//        }
        return orders.values();
    }

    public GenericMap getMap() throws SQLException {
        TreeMap<String, GenericCity> nodes = new TreeMap<String, GenericCity>();
        try {
            ResultSet rs = stat.executeQuery("select * from CITIES;");
            while (rs.next()) {
                String name = rs.getString("city_name");
                nodes.put(name, new City(name));
            }
            ArrayList<String> cn = new ArrayList<String>();
            ResultSet ws = stat.executeQuery("select * from WAY_START;");
            while (ws.next()) {
                cn.add(ws.getString(2));
            }
            ResultSet wd = stat.executeQuery("select * from WAY;");
            int i = 0;
            while (wd.next()) {
                String city_name = cn.get(i);
                String next_name = wd.getString(2);
                int wlen = wd.getInt(3);
                int wcost = wd.getInt(4);
                GenericCity tmp = nodes.get(city_name);
                GenericCity next = nodes.get(next_name);
                tmp.AddOutgoingRoute(next, wlen, wcost);
                next.AddOutgoingRoute(tmp, wlen, wcost);
                i++;
            }
        } catch (SQLException ex) {
            System.out.println("Problem z odczytem mapy z bazy");
            System.exit(1);
        }
        return new GenericMap(nodes);
    }
    public ArrayList<Integer> addOrders(ArrayList<String> orders) throws SQLException {
        if (orders == null || orders.size() < 3) {
            System.out.println("Nieprawidłowa lista zamówień");
            System.exit(1);
        }
        int i = 0;
        ArrayList<Integer> tmp = new ArrayList<Integer>(30);
        HashSet<String> cts = new HashSet<String>(30);
        HashMap<String, Integer> pds = new HashMap<String, Integer>(30);
        HashMap<String, Integer> pdsToUpdate = new HashMap<String, Integer>();
        ResultSet cities = stat.executeQuery("select city_name from CITIES;");
        while (cities.next()) {
            cts.add(cities.getString(1));
        }
        ResultSet ids = null;
        ResultSet products = stat.executeQuery("select pr_name, amount from PRODUCTS;");
        while (products.next()) {
            pds.put(products.getString(1), products.getInt(2));
        }
        PreparedStatement prep = conn.prepareStatement("insert into ORDERS (city_name) values (?);");
        while (i < orders.size()) {
            if (orders.get(i).charAt(0) == '#') {
                String city_name = orders.get(i).substring(1);
                if (!cts.contains(city_name)) {
                    System.out.println("Nie mozna złożyć zamówień do miasta: " + city_name + ".\nW bazie nie ma takiego miasta");
                    i++;
                    continue;
                }
                prep.setString(1, city_name);
                prep.addBatch();
                conn.setAutoCommit(false);
                prep.executeBatch();
                conn.setAutoCommit(true);
                ids = prep.getGeneratedKeys();
                while (ids.next()) {
                    int x = ids.getInt(1);
                    tmp.add(x);
                }
            }
            i++;
        }
        i = 0;
        int j = -1;
        PreparedStatement prep2 = conn.prepareStatement("insert into ORDERS_DESC values(?, ?, ?);");
        for (i = 0; i < orders.size() - 1; i++) {
            if (orders.get(i).charAt(0) == '#' && j < tmp.size()) {
                i++;
                j++;
            }
            String prName = orders.get(i);
            if (!pds.containsKey(prName)) {
                System.out.println("Nie można złożyć zamówienia na produkt " + prName + ".\nNie ma go w bazie");
                i++;
                continue;
            }
            prep2.setInt(1, tmp.get(j));
            prep2.setString(2, prName);
            int amount = Integer.parseInt(orders.get(i + 1));
            int amAv = pds.get(prName);
            if ((amAv - amount) < 0) {
                System.out.println("Nie mozna dokonać zamówienia na produkt: " + prName + ".\nZa mało dostępnych sztuk: " + amAv);
                i++;
                continue;
            }
            prep2.setInt(3, amount);
            if (pdsToUpdate.size() > 0) {
                if (!pdsToUpdate.containsKey(prName)) {
                    pdsToUpdate.put(prName, amount);
                } else {
                    int prevAm = pdsToUpdate.get(prName);
                    if ((amAv - prevAm - amount) < 0) {
                        System.out.println("Nie mozna dokonać zamówienia na produkt: " + prName + ".\nZa mało dostępnych sztuk: " + (amAv - prevAm));
                        i++;
                        continue;
                    }
                    pdsToUpdate.remove(prName);
                    pdsToUpdate.put(prName, prevAm + amount);
                }
            } else {
                pdsToUpdate.put(prName, amount);
            }
            prep2.addBatch();
            i++;
        }
        conn.setAutoCommit(false);
        prep2.executeBatch();
        conn.setAutoCommit(true);
        PreparedStatement prep3 = conn.prepareStatement("UPDATE PRODUCTS SET amount = amount -  ? WHERE pr_name = ?");
        for (java.util.Map.Entry<String, Integer> entry : pdsToUpdate.entrySet()) {
            prep3.setInt(1, entry.getValue());
            prep3.setString(2, entry.getKey());
            prep3.addBatch();
        }
        conn.setAutoCommit(false);
        prep3.executeBatch();
        conn.setAutoCommit(true);
        return tmp;
    }

    public void addProducts(ArrayList<String> products) throws SQLException {
        if (products == null || products.size() < 3) {
            System.out.println("Nieprawidłowa lista produktów");
            System.exit(1);
        }
        int i = 0;
        PreparedStatement prep = conn.prepareStatement("insert into PRODUCTS values (?, ?, ?);");
        while (i < products.size() - 2) {
            prep.setString(1, products.get(i));
            prep.setInt(2, Integer.parseInt(products.get(i + 1)));
            prep.setInt(3, Integer.parseInt(products.get(i + 2)));
            prep.addBatch();
            i += 3;
        }
        try {
            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            System.out.println("Nie udało się wczytać wszystkich produktów do bazy\nNiektóre znajdowały się już w bazie");
            System.exit(1);
        }
    }

    public void addMap(ArrayList<String> map) throws SQLException {
        int i = 0;
        if (map == null || map.size() < 3) {
            System.out.println("Nieprawidłowa mapa");
            System.exit(1);
        }
        String city_name;
        PreparedStatement prep = conn.prepareStatement("insert into CITIES values (?);");
        while ((city_name = map.get(i++)).compareTo("Edges:") != 0) {
            prep.setString(1, city_name);
            prep.addBatch();
        }
        try {
            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            System.out.println("Nie wczytano wszystkich miast do mapy.\nNiektóre miasta znajdowały się już w bazie.");
            System.exit(1);
        }
        int j = i;
        PreparedStatement prep2 = conn.prepareStatement("insert into WAY_START (city_name) values (?);");
        ResultSet ids = null;
        ArrayList<Integer> tmp = new ArrayList<Integer>(30);
        while ((i + 4) <= map.size()) {
            prep2.setString(1, map.get(i));
            prep2.addBatch();
            conn.setAutoCommit(false);
            prep2.executeBatch();
            conn.setAutoCommit(true);
            ids = prep2.getGeneratedKeys();
            while (ids.next()) {
                int x = ids.getInt(1);
                tmp.add(x);
            }
            i += 4;
        }
        ids.close();
        int k = 0;
        PreparedStatement prep4 = conn.prepareStatement("insert into WAY values(?, ?, ?, ?)");
        while ((j + 4) <= map.size()) {
            prep4.setInt(1, tmp.get(k++));
            prep4.setString(2, map.get(j + 1));
            prep4.setInt(3, Integer.parseInt(map.get(j + 2)));
            prep4.setInt(4, Integer.parseInt(map.get(j + 3)));
            prep4.addBatch();
            j += 4;
        }
        conn.setAutoCommit(false);
        prep4.executeBatch();
        conn.setAutoCommit(true);
    }

    public void addProduct(String prName, int amountAvailable, int size) {
        try {
            PreparedStatement prep = conn.prepareStatement("insert into PRODUCTS values (?, ?, ?);");
            prep.setString(1, prName);
            prep.setInt(2, amountAvailable);
            prep.setInt(3, size);
            prep.addBatch();
            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            System.out.println("Produkt \"" + prName + "\" znajduje się już w bazie.");
            System.exit(1);
        }
    }

    public void delProduct(String prName) {
        try {
            PreparedStatement prep = conn.prepareStatement("delete from PRODUCTS where pr_name = ?");
            prep.setString(1, prName);
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Produkt \"" + prName + "\" nie znajduje się w bazie.");
            System.exit(1);
        }
    }

    public void addDriver(String drName, int capacity) throws SQLException {
        try {
            PreparedStatement prep = conn.prepareStatement("insert into DRIVERS values (?, ?);");
            prep.setString(1, drName);
            prep.setInt(2, capacity);
            prep.addBatch();
            conn.setAutoCommit(false);
            prep.executeBatch();
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            System.out.println("Kierowca \"" + drName + "\" znajduje się już w bazie.");
            System.exit(1);
        }
    }

    public void delDriver(String drName) {
        try {
            PreparedStatement prep = conn.prepareStatement("delete from DRIVERS where name = ?");
            prep.setString(1, drName);
            prep.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Kierowca \"" + drName + "\" nie znajduje się w bazie.");
            System.exit(1);
        }
    }

    public void delOrders(ArrayList<Integer> ids) {
        try { 
            for(Integer i : ids) {
                PreparedStatement prep = conn.prepareStatement("delete from ORDERS where order_id = ?");
                prep.setInt(1, i);
                prep.executeUpdate();
            }
            
            for(Integer i : ids) {
                PreparedStatement prep2 = conn.prepareStatement("delete from ORDERS_DESC where order_id = ?");
                prep2.setInt(1, i);
                prep2.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println("Nie udane usunięcie zamówienia");
            System.exit(1);
        }
    }
    public void clearBase() throws SQLException {
        try {
            stat.executeUpdate("DROP TABLE PRODUCTS");
            stat.executeUpdate("DROP TABLE CITIES");
            stat.executeUpdate("DROP TABLE WAY_START");
            stat.executeUpdate("DROP TABLE WAY");
            stat.executeUpdate("DROP TABLE ORDERS");
            stat.executeUpdate("DROP TABLE ORDERS_DESC");
            stat.executeUpdate("DROP TABLE DRIVERS");
        } catch (SQLException ex) {
            System.out.println("Nieudane usunięcie danych z bazy.");
            System.exit(1);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public Statement getStatement() {
        return stat;
    }


    private int totalCapacity(ArrayList<Driver> d) {
        int total = 0;
        for (Driver dr : d) {
            total += dr.getCapacity();
        }
        return total;
    }
}
