/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package salesmanager;

import salesmanager.GenericMap.GenericMap;
import salesmanager.GenericMap.GenericCity;
import salesmanager.GeneticAlgorithm.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import salesmanager.Dijkstra.Dijkstra;
import salesmanager.Map.*;
import salesmanager.Dijkstra.Dijkstra.NodeDoesNotExist;

/**
 *
 * @author głodoś
 *
 * jeszcze nie testowane, metody są wypisane po kolei tak jak bedą uzywane
 */
public class OrdersRealisation {

    private HashMap<GenericCity, ArrayList<Product>> orders;
    private ArrayList<GenericCity>[] associatedCities;
    private ArrayList<GenericCity>[] routes;
    private Path[] geneticAlgResults;
    private Costs k;
    private GenericMap<City> map;
    private ArrayList<Driver> drivers;
    private int[][] cityTabs; //tablica identyfikatorów miast
    private int iterator;

    public OrdersRealisation(Collection<Order> orders, GenericMap<City> m, ArrayList<Driver> drivers) {
        map = m;
        this.orders = new HashMap<GenericCity, ArrayList<Product>>();
        map.getCity("A").setStarting(true);
        for (Order o : orders) {
            if (!this.orders.containsKey(o.fromWho)) { // jesli nie ma jeszcze zamówienia z tego miasta to dodaj
                this.orders.put(o.fromWho, o.products);
        } else { // jeśli już było zamówienie z tego miasta to tylko dodaj produkty
            ArrayList<Product> tmp = this.orders.get(o.fromWho);
            for (Product p : tmp) {
                if(tmp.contains(p)) //jeśli było zamówienie na ten produkt
                    tmp.get(tmp.indexOf(p)).count+=p.count; //zwiększ liczbę
                else
                    tmp.add(p); //dodaj nowy
            }
        }

        }
        this.drivers = drivers;
        giveIdEachCity(map);
        try {
            createCostArray(map);
        } catch (NodeDoesNotExist e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        geneticAlgResults = new Path[drivers.size()];
        routes = new ArrayList[drivers.size()];
    }

    public Raport go() {
        associateCities();
        calculateRoute();
        tranformPathsToListsOfCities();
        return new Raport(routes, drivers, orders,k,  "raport.html", "Raport");
    }

    public void wypisz() {
        p("Ilość kierowców: " + drivers.size());
        p("przydzielone: ");
        for (int i = 0; i < associatedCities.length; i++) {
            p("Trasa " + i + ": ");
            for(int j = 0; j < associatedCities[i].size(); j++)
                System.out.print(associatedCities[i].get(j).getVal() + (j < associatedCities[i].size() - 1 ? "-->" : ""));
            p("");
        }
        p("Wyznaczone: ");
        for (int i = 0; i < routes.length; i++) {
            p("Trasa " + i + ": ");
            for(int j = 0; j < routes[i].size(); j++)
                System.out.print(routes[i].get(j).getVal() + (j < routes[i].size() - 1 ? "-->" : ""));
            p("");
        }
        p("");

    }


    //nadaje unikalny ID każdemu miastu ( dla startowego zawsze 1)
    private void giveIdEachCity(GenericMap<City> m) {
        int i = 2;
        for (GenericCity c : m.getAllCities()) {
            if (c.isStarting()) {
                c.setID(1);
            } else {
                c.setID(i++);
            }
        }
    }

    //tworzy tablice kosztow dla miast (do alg. gen.)
    private void createCostArray(GenericMap map) throws NodeDoesNotExist {
        Dijkstra dAlg = new Dijkstra(map);
        k = new Costs(map.numberOfCities + 1);
        dAlg.search(map.getCityByID(1).getVal());
        k.costs[1] = dAlg.getCosts();
        k.lengths[1] = dAlg.getLengths();
        dAlg.resetData();
        for (GenericCity c : orders.keySet()) {
            dAlg.search(c.getVal());
            k.costs[c.getID()] = dAlg.getCosts();
            k.lengths[c.getID()] = dAlg.getLengths();
            dAlg.resetData();
        }

    }

    private void associateCities() {
        //dla kazdego kierowcy tablica miast do przejechania
        associatedCities = divideMap(drivers.size());
    }

    private ArrayList[] divideMap(int ileKawalkow) {
        //mapa jest dzielona na n kawalkow bez wglądu w odległości miedzy miastami
        int i = 0;
        double j = 1, l = 0.5;
        ArrayList[] tab = new ArrayList[ileKawalkow];
        for (int m = 0; m < ileKawalkow; m++) {
            tab[m] = new ArrayList<City>();
            tab[m].add(map.getCityByID(1));
        }
        double cut = (double)orders.size() / (double)ileKawalkow;
        for (GenericCity c : orders.keySet()) {
            if (j > cut + l) {
                l += cut;
                i++;
            }
            tab[i].add(c);
            j++;

        }
        return tab;
    }

    private void calculateRoute() {
        //wyznacza najkrotsze trasy dla przydzielonych miast
        cityTabs = new int[drivers.size()][];
        int i = 0;
        while (i < drivers.size()) {
            geneticAlgResults[i] = findShortestPath(associatedCities[i]);
            i++;
        }

    }

    private Path findShortestPath(ArrayList<GenericCity> cities) {
        //algorytm genetyczny
        //argument cities jest przekształcany na tablice int (numery miast)
        GeneticAlg genAlg = new GeneticAlg(100, cityNumbers(cities), 0.03, 0.8, k);
        return genAlg.simulateNGenerations(10000);
    }

    // tworzy tablice numerów miast do alg. genetycznego
    private int[] cityNumbers(ArrayList<GenericCity> cities) {
        cityTabs[iterator] = new int[cities.size() + 1];
        int i = 1;
        for (GenericCity c : cities) {
            cityTabs[iterator][i++] = c.getID();
        }
        return cityTabs[iterator++];
    }

//nie ma tu zadnego sprawdzania czy towar sie zmiesci. to bedzie sprawdzone
//    w algorytmie, ktory dzieli mape
//    private void loadStuff() {
//        przeksztalcSciezki();
//        for (int i = 0; i < geneticAlgResults.length; i++) {
//            for (GenericCity c : routes[i]) {
//                if (c.isStarting()) {
//                    continue;
//                }
//                ArrayList<Product> tmp = zamowienia.get(c);
//                for (Product p : tmp) {
//                    drivers.get(i).canAddStuff(p);
//                }
//            }
//        }
//    }
    //przekształca wyniki algorytmu genetycznego na tablice obiektow City
    private void tranformPathsToListsOfCities() {
        for (int i = 0; i < geneticAlgResults.length; i++) {
            Path tmp = geneticAlgResults[i];
            routes[i] = new ArrayList<GenericCity>();
            for (int j = 0; j < associatedCities[i].size(); j++) {
                routes[i].add(map.getCityByID(cityTabs[i][tmp.getElem(j)]));
            }
        }
    }

    private void p(String s) {
        System.out.println(s);
    }
}
