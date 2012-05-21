package salesmanager.Map;

import java.util.Collection;
import java.util.TreeMap;

/**
 *
 * @author głodoś
 */
public class Map{

    private TreeMap<String, City> allCities;
    public int numberOfCities;

    public Map(TreeMap<String, City> nodes) {
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

    public Collection<City> getAllCities() {
        return allCities.values();
    }

    public TreeMap<String, City> getAllCitiesInTM() {
        return allCities;
    }
}
