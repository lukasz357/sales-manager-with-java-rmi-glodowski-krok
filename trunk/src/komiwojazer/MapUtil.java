package komiwojazer;

import java.util.ArrayList;
import java.util.List;

import komiwojazer.Dijkstra.Dijkstra;
import komiwojazer.Dijkstra.Dijkstra.NodeDoesNotExist;
import komiwojazer.GeneticAlgorithm.Costs;
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
    
    public int[] getNodeNumbers(){
    	return cityTab;
    }
    public Costs getCostArray(){
    	return k;
    }

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
        for (City c : map.getAllCities()) {
            dAlg.search(c.getVal());
            k.setCost(c.getID(), dAlg.getCosts());
            dAlg.resetData();
        }
        k.print();

    }


    // tworzy tablice numerów miast do alg. genetycznego
    private int[] cityNumbers(List<City> cities) {
        cityTab = new int[cities.size() + 1];
        int i = 1;
        for (City c : cities) {
            cityTab[i++] = c.getID();
        }
        return cityTab;
    }
    
    @SuppressWarnings("unused")
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
