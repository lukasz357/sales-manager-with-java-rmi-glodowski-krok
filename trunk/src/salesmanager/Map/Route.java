/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package salesmanager.Map;



/**
 *
 * @author głodoś
 */

public final class Route{

    private City cityNext;
    private double cost;
    private double length;

    public Route(City nodeNext, double length, double cost) {
        this.cityNext = nodeNext;
        this.cost = cost;
        this.length = length;
    }

    public City getCity() {
        return cityNext;
    }

    public double getCost() {
        return cost;
    }

    public double getLength(){
        return length;
    }

}
