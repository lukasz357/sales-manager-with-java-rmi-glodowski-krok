/*
 * Pomysł zaczerpnięty z http://www.eioba.pl/a71530/algorytmy_genetyczne
 */
package komiwojazer.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


/**
 *
 * @author głodoś
 */
public class GeneticAlg {

    private List<Path> population;
    private int size;
    private int lengthOfPath;
    private Path best;
    private double costOfBest;
    private double costOfWorst;
    private int generation;
    private Random rand;
    private double costBestEver;
    private Path bestEver;
    private double mutationProp;
    private double crossingProp;
    private int[] cities;
    public Costs k;
    

    public GeneticAlg(int size, int[] cities, double mutationProp, double crossingProp, Costs k) {
        this.size = size;
        this.lengthOfPath = cities.length - 1;
        this.mutationProp = mutationProp;
        this.crossingProp = crossingProp;
        this.k = k;
        this.cities = cities;
        rand = new Random();
        costBestEver = 999999999;
        population = new ArrayList<Path>();
    }


    public void print() {
        System.out.println("Ostatnie pokolenie: ");
        printGeneration();
        System.out.println();
    }

    public double costOfBest() {
        return this.costOfBest;
    }

    public double costBestEver() {
        return this.costBestEver;
    }

    private void printGeneration() {
        for (int i = 0; i < size; i++) {
            System.out.print("<");
            for (int j = 0; j < Path.length; j++) {
                System.out.print(population.get(i).getElem(j) + ", ");
            }
            System.out.println(">");
        }

    }
    
    public void setInitialPopulation(List<Path> list){
    	this.population = list;
    }

    public Path simulateNGenerations(int n) {
        while (generation < n) {
            simulateGeneration();
        }
        updateMarks();
        return bestEver;
    }
    
    public List<Path> getPopulation(){
    	return population;
    }
   

    private void simulateGeneration() {
        generation++;
        updateMarks();
        cross();
        mutate();
    }

    private void updateMarks() {
    	 for (Path s : population) {
         	s.updateCost(k, cities);
         }
        updateBest();
        Collections.sort(population);
    }

    private void cross() {
    	int count = (int) (size * crossingProp);
    	if(count>size)
    		count = size;
        ArrayList<Integer> beingCrossed = new ArrayList<Integer>(count);
        for(int i = 0;i<count;i++)
        	beingCrossed.add(i);
        if(beingCrossed.size() % 2 == 1)
        	beingCrossed.add(count);
        int parent1 = 0, parent2 = 0;
        Path[] children;

        while (beingCrossed.size() > 0) {
        	parent1 = beingCrossed.remove(rand.nextInt(beingCrossed.size()));
        	parent2 = beingCrossed.remove(rand.nextInt(beingCrossed.size()));
            children = Path.cross((population.get(parent1)),
                    (population.get(parent2)));

            population.set(parent1, children[0]);
            population.set(parent2, children[1]);
        }
    }

    private void mutate() {
        for (int i = 0; i < size; i++) {
            if (rand.nextDouble() >= mutationProp) {
                continue;
            }
            population.get(i).mutate();
            population.get(i).updateCost(k, cities);
        }
    }

    private void updateBest() {
        best = population.get(1);
        costOfBest = best.cost;
        costOfWorst = costOfBest;
        double ks = 0;
        for (Path s : population) {
            if ((ks = s.cost) < costOfBest) {
                costOfBest = ks;
                best = s;
            } else if (ks > costOfWorst) {
                costOfWorst = ks;
            }
        }
        if (costBestEver > costOfBest) {
            bestEver = best;
            costBestEver = costOfBest;
        }
    }

    @SuppressWarnings("unused")
	private void generateRandomPopulation() {

        for (int i = 0; i < size; i++) {
        	Path p = new Path(lengthOfPath).random();
        	p.updateCost(k, cities);
            population.add(p);
        }
    }
    
    @SuppressWarnings("unused")
	private Comparator<Path> sorter = new Comparator<Path>() {
    	@Override
		public int compare(Path o1, Path o2) {
			return (int) (o2.mark - o1.mark);
		}
	};

}
