/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package salesmanager.GenericMap;

import java.util.ArrayList;

/**
 *
 * @author głodoś
 */
public interface GenericCity<C, R extends GenericRoute> {
    public void AddOutgoingRoute(C cityNext, int length, int cost);

    public ArrayList<R> getOutGoingRoutes();
;
    public String getVal();

    public void setVal(String val);

    public double getDistance();
    public double getLength();
    public void setLength(Double l);
    public void setDistance(double distance) ;

    public void setStarting(boolean statement);
    public boolean isStarting();
    public int getID();
    public void setID(int ID);
}
