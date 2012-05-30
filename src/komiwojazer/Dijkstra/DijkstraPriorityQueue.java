package komiwojazer.Dijkstra;

import java.util.Collection;
import java.util.PriorityQueue;

import komiwojazer.Map.City;


/**
 *
 * @author głodoś
 */

public class DijkstraPriorityQueue {

    private PriorityQueue<City> pQueue = new PriorityQueue<City>();

    public DijkstraPriorityQueue() {
    }

    public void add(City n) {
        pQueue.add(n);
    }

    public void add(Collection<City> nodeCollection) {
        pQueue.addAll(nodeCollection);
    }

    public boolean hasMore() {
        return !pQueue.isEmpty();
    }

    public City remove() {
        return pQueue.remove();
    }

    public void updateCityDistance(City n) {
        pQueue.remove(n);
        pQueue.add(n);
    }
}
