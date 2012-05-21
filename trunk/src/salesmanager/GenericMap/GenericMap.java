/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package salesmanager.GenericMap;

import java.util.Collection;
import java.util.TreeMap;

/**
 *
 * @author głodoś
 */
public class GenericMap<T extends GenericCity> {
    private TreeMap<String, T> allCities;
    public int numberOfCities;

    public GenericMap(TreeMap<String, T> nodes) {
        allCities = nodes;
        numberOfCities = allCities.size();
    }

    public T getCity(String key) {
        return allCities.get(key);
    }

    public T getCityByID(int ID) {
        for (T c : allCities.values()) {
            if (c.getID() == ID) {
                return c;
            }
        }
        return null;
    }

    public Collection<T> getAllCities() {
        return allCities.values();
    }

}
