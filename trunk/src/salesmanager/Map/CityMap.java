package salesmanager.Map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author głodoś
 */
public class CityMap{

    private TreeMap<String, City> allCities;
    public int numberOfCities;

    public CityMap(TreeMap<String, City> nodes) {
        allCities = nodes;
        numberOfCities = allCities.size();
    }

    public City getCity(String key) {
        return allCities.get(key);
    }

    public City getCityByID(int ID) {
        for (City c : allCities.values()) {
            if (c.ID == ID) {
                return c;
            }
        }
        return null;
    }

    public List<City> getAllCities() {
    	ArrayList<City> l = new ArrayList<City>();
    	l.addAll(allCities.values());
        return l;
    }
    
    public Collection<City> getAllCitiesCollection(){
    	return allCities.values();
    }

    public TreeMap<String, City> getAllCitiesInTM() {
        return allCities;
    }
}
