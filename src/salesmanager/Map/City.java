package salesmanager.Map;

import java.util.ArrayList;

import salesmanager.Dijkstra.Dijkstra;
import salesmanager.GenericMap.GenericCity;

public class City implements Comparable<City>, GenericCity<City, Route> {

    private ArrayList<Route> outGoingRoutes = new ArrayList<Route>();
    private String value;
    private Double distanceFromStart = Dijkstra.INFINITY;
    private double length = 0;
    public int ID;
    private boolean isStarting = false;

    public City(String value) {
        this.value = value;
    }

    public void AddOutgoingRoute(City nodeNext, int length, int cost) {
        outGoingRoutes.add(new Route(nodeNext, length, cost));
    }

    public ArrayList<Route> getOutGoingRoutes() {
        return outGoingRoutes;
    }

    public String getVal() {
        return value;
    }

    public void setVal(String val) {
        this.value = val;
    }

    @Override
    public int compareTo(City n) {
        return distanceFromStart.compareTo(n.getDistance());
    }
    @Override
    public String toString(){
        return value + "--->";
    }

    public double getDistance() {
        return distanceFromStart;
    }

    public void setDistance(double distance) {
        distanceFromStart = distance;
    }

    public void setStarting(boolean statement){
        isStarting = statement;
    }
    public boolean isStarting(){
        return isStarting;
    }
    public int getID(){
        return ID;
    }
    public void setID(int ID){
        this.ID = ID;
    }

    public double getLength(){
        return length;
    }
    public void setLength(Double l){
        length = l;
    }
}
