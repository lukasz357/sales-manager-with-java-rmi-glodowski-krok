/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package komiwojazer.GeneticAlgorithm;

/**
 *
 * @author głodoś
 */
public class Costs {
    private double [][] costs;
    private double [][] lengths;

    public Costs(int size){
        costs = new double[size][size];
        lengths = new double[size][size];
    }
    
    public double getCost(int from, int to){
    	return costs[from][to] + lengths[from][to];
    }
    
    public void setLen(int from, int to, double val){
    	lengths[from][to] = val;
    }
    public void setCost(int from, int to, double val){
    	costs[from][to] = val;
    }
    public void setLen(int from, double[] to){
    	lengths[from] = to;
    }
    public void setCost(int from, double[] to){
    	costs[from] = to;
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
