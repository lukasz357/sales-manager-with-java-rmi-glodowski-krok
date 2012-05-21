/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package salesmanager;

/**
 *
 * @author głodoś
 */
public class Product {
    public String name;
    public int count;
    public int sizeOfOne;

    public Product(String name, int count, int sizeOfOne){
        this.name = name;
        this.count = count;
        this.sizeOfOne = sizeOfOne;
    }
}
