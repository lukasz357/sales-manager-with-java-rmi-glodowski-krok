/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package salesmanager;

import java.util.ArrayList;

/**
 *
 * @author Łukasz
 */
public class Driver {
    private String name;
    private int capacity;
    //private int freeSpace;
    //private ArrayList<Product> stuff;

    public Driver(String name, int capacity){
        this.name = name;
        this.capacity = capacity;
        //this.capacity = this.freeSpace = capacity;
        //stuff = new ArrayList<Product>();
    }

//    public boolean canAddStuff(Product p){
//        if(freeSpace < p.sizeOfOne*p.count)
//            return false;
//        stuff.add(p);
//        freeSpace -= p.sizeOfOne*p.count;
//        return true;
//    }
//    public ArrayList<Product> getStuff() {
//        return stuff;
//    }
//    public void addStuff( Product p) {
//        stuff.add(p);
//    }
    public String getName() {
        return name;
    }
//    public int getFreeSpace() {
//        return freeSpace;
//    }
    public int getCapacity() {
        return capacity;
    }

}
