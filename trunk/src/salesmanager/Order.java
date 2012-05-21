/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package salesmanager;

import java.util.ArrayList;

import salesmanager.GenericMap.GenericCity;

/**
 *
 * @author głodoś | Łukasz
 */
class Order {
    private int id;
    public int weight;
    public GenericCity fromWho;
    public ArrayList products= new ArrayList<Product>();

    public Order(int id, GenericCity from){
        this.id = id;
        fromWho=from;
    }
    public void addProduct(String name, int amount, int size) {
        products.add(new Product(name, amount, size));
        weight+=amount*size;
    }

    public ArrayList<Product> getAllProducts() {
        return products;
    }

    public int getID() {
        return id;
    }
}
