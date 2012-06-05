/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package komiwojazer;

import java.util.ArrayList;
import java.util.List;

import komiwojazer.Dijkstra.Dijkstra;
import komiwojazer.Dijkstra.Dijkstra.NodeDoesNotExist;
import komiwojazer.GeneticAlgorithm.Costs;
import komiwojazer.GeneticAlgorithm.GeneticAlg;
import komiwojazer.GeneticAlgorithm.Path;
import komiwojazer.Map.City;
import komiwojazer.Map.CityMap;


/**
 *
 * @author głodoś
 *
 * 
 */
public class MapUtil {


    //private ArrayList<City> route;
    //private Path geneticAlgResult;
    private Costs k;
    private CityMap map;
    private int[] cityTab; //tablica identyfikatorów miast
    
    private double totalLen;

    public MapUtil(CityMap m) {
        map = m;
        map.getCity("A").setStarting(true);
        giveIdEachCity(map);
        try {
            createCostArray(map);
            cityTab = cityNumbers(map.getAllCities());
        } catch (NodeDoesNotExist e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

//    public ArrayList<City> go(int generations, int count) {
//        //associateCities();
//        calculateRoute(generations, count);
//        tranformPathsToListsOfCities();
//        totalLen = policzDlTrasy(route, k);
//        return route;
//    }
    
    public int[] getNodeNumbers(){
    	return cityTab;
    }
    public Costs getCostArray(){
    	return k;
    }

//    public void wypisz() {
//        p("Ilość kierowców: " + drivers.size());
//        p("przydzielone: ");
//        for (int i = 0; i < associatedCities.length; i++) {
//            p("Trasa " + i + ": ");
//            for(int j = 0; j < associatedCities[i].size(); j++)
//                System.out.print(associatedCities[i].get(j).getVal() + (j < associatedCities[i].size() - 1 ? "-->" : ""));
//            p("");
//        }
//        p("Wyznaczone: ");
//        for (int i = 0; i < routes.length; i++) {
//            p("Trasa " + i + ": ");
//            for(int j = 0; j < routes[i].size(); j++)
//                System.out.print(routes[i].get(j).getVal() + (j < routes[i].size() - 1 ? "-->" : ""));
//            p("");
//        }
//        p("");
//
//    }

    public double getTotalLength(){
    	return totalLen;
    }

    //nadaje unikalny ID każdemu miastu ( dla startowego zawsze 1)
    private void giveIdEachCity(CityMap m) {
        int i = 2;
        for (City c : m.getAllCities()) {
            if (c.isStarting()) {
                c.setID(1);
            } else {
                c.setID(i++);
            }
        }
    }

    //tworzy tablice kosztow dla miast (do alg. gen.)
    private void createCostArray(CityMap map) throws NodeDoesNotExist {
        Dijkstra dAlg = new Dijkstra(map);
        k = new Costs(map.numberOfCities + 1);
        dAlg.search(map.getCityByID(1).getVal());
        k.setCost(1,dAlg.getCosts());
        k.setLen(1, dAlg.getLengths());
        dAlg.resetData();
        for (City c : map.getAllCities()) {
            dAlg.search(c.getVal());
            k.setCost(c.getID(), dAlg.getCosts());
            k.setLen(c.getID(), dAlg.getLengths());
            dAlg.resetData();
        }

    }


//    private void calculateRoute(int gen, int count) {
//        geneticAlgResult = findShortestPath(map.getAllCities(), gen, count);
//    }
//
//    private Path findShortestPath(List<City> cities, int gen, int count) {
//        //algorytm genetyczny
//        //argument cities jest przekształcany na tablice int (numery miast)
//        GeneticAlg genAlg = new GeneticAlg(count, cityNumbers(cities), 0.03, 0.8, k);
//        return genAlg.simulateNGenerations(gen);
//    }

    // tworzy tablice numerów miast do alg. genetycznego
    private int[] cityNumbers(List<City> cities) {
        cityTab = new int[cities.size() + 1];
        int i = 1;
        for (City c : cities) {
            cityTab[i++] = c.getID();
        }
        return cityTab;
    }
    
    private double policzDlTrasy(ArrayList<City> trasa, Costs k){
        double dl = 0;
        for(int i = 0; i<trasa.size()-1;i++)
            dl+=k.getCost(trasa.get(i).getID(), trasa.get(i+1).getID());
        dl+=k.getCost(trasa.get(trasa.size()-1).getID(),trasa.get(0).getID());
        return dl;
    }

    //przekształca wyniki algorytmu genetycznego na tablice obiektow City
    public ArrayList<City> tranformPathsToListsOfCities(Path result) {
            ArrayList<City> route = new ArrayList<City>();
            for (int j = 0; j < map.numberOfCities; j++) {
                route.add(map.getCityByID(cityTab[result.getElem(j)]));
            }
            return route;
    }

}
