/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package salesmanager.GeneticAlgorithm;

/**
 *
 * @author głodoś
 */
public class Costs {
    public double [][] costs;
    public double [][] lengths;

    public Costs(int size){
        costs = new double[size][size];
        lengths = new double[size][size];
    }

    public void print(){
        for(int i=0;i<costs.length;i++){
            for(int j=0;j<costs[i].length;j++){
                System.out.print(costs[i][j] + " ");
            }
            System.out.println();
        }
    }


}
