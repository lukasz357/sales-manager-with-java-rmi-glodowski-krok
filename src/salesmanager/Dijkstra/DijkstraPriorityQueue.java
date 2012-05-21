package salesmanager.Dijkstra;

import java.util.Collection;
import java.util.PriorityQueue;
import salesmanager.GenericMap.GenericCity;
import salesmanager.Map.City;
import salesmanager.Map.Route;

/**
 *
 * @author głodoś
 */

public class DijkstraPriorityQueue {

    private PriorityQueue<GenericCity> pQueue = new PriorityQueue<GenericCity>();

    public DijkstraPriorityQueue() {
    }

    public void add(City n) {
        pQueue.add(n);
    }

    public void add(Collection<City> nodeCollection) {
        pQueue.addAll(nodeCollection);
    }

    public Boolean hasMore() {
        return !pQueue.isEmpty();
    }

    public GenericCity<City, Route> remove() {
        return pQueue.remove();
    }

    public void updateCityDistance(GenericCity n) {
        pQueue.remove(n);
        pQueue.add(n);
    }
}
