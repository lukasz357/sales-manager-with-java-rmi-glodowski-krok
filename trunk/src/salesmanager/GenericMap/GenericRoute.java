/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package salesmanager.GenericMap;

import salesmanager.GenericMap.GenericCity;

/**
 *
 * @author głodoś
 */
public interface GenericRoute {
public GenericCity getCity();

    public double getCost();

    public double getLength();
}
