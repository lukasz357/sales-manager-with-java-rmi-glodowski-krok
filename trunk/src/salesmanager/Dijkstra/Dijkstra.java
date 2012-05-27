package salesmanager.Dijkstra;

import salesmanager.Map.City;
import salesmanager.Map.CityMap;
import salesmanager.Map.Route;

/**
 *
 * @author głodoś
 */

public class Dijkstra {

    public DijkstraPriorityQueue priorityQ = new DijkstraPriorityQueue();
    public static final double INFINITY = 999999;
    public City startNode;
    private CityMap g;

    public Dijkstra(CityMap g) {
        this.g = g;    
    }

    public void search(String startNode)throws NodeDoesNotExist {  
        prepareData(startNode);
        while (priorityQ.hasMore()) {
            City n = priorityQ.remove();
            for (Route e : n.getOutGoingRoutes()) {
                City adjNode = e.getCity();
                double newPossiblePathCost = calculateCost(e) + n.getDistance();
                if (newPossiblePathCost < adjNode.getDistance()) {
                    adjNode.setDistance(newPossiblePathCost);
                    adjNode.setLength(adjNode.getLength() + e.getLength());
                    priorityQ.updateCityDistance(adjNode);
                }
            }
        }
    }
    private void prepareData(String startNode) throws NodeDoesNotExist{
        if(g.getCity(startNode) == null)
            throw new NodeDoesNotExist(startNode);
        this.startNode = g.getCity(startNode);
        this.startNode.setDistance(0);
        priorityQ.add(g.getAllCitiesCollection());
    }

    public double calculateCost(Route e){
        return e.getCost() + e.getLength();
    }

    public void resetData(){
        for(City n : g.getAllCitiesCollection()){
            n.setDistance(INFINITY);
        }

    }

    public double[] getCosts(){
        double[] costs = new double[g.numberOfCities+1];
        for(City c : g.getAllCitiesCollection()){
            costs[c.getID()]=c.getDistance();
        }
        return costs;
    }

    public double[] getLengths(){
        double[] lengths = new double[g.numberOfCities+1];
        for(City c : g.getAllCitiesCollection()){
            lengths[c.getID()]=c.getLength();
        }
        return lengths;
    }

   public class NodeDoesNotExist extends Exception {
        public NodeDoesNotExist(String nodeName) {
            super("Wierzchołek o nazwie " + nodeName + " nie istnieje.");
        }
    }
}
