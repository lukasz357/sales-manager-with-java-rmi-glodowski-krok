/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Pomysł zaczerpnięty z http://www.eioba.pl/a71530/algorytmy_genetyczne
 */
package salesmanager.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author głodoś
 */
public class GeneticAlg {

    private ArrayList<Path> population;
    private int size;
    private int lengthOfPath;
    private Path best;
    private double costOfBest;
    private double costOfWorst;
    private int generation;
    private ArrayList<Double> marks;
    private Random rand;
    private double costBestEver;
    private Path bestEver;
    private double mutationProp;
    private double crossingProp;
    private double sum;
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

    public Path simulateNGenerations(int n) {
        if(lengthOfPath<6)
            generateManually(); //wyznaczamy trase znajdujac wszystkie permutacje
        else{ // w przeciwnym wypadku uzywamy algorytmu genetycznego
            generateRandomPopulation();
            while (generation < n) {
                simulateGeneration();
            }
        }
        return bestEver;
    }

    private void simulateGeneration() {
        generation++;
        sum = updateMarks();
        double[] chances = calculateChances(sum);
        population = chooseNewGeneration(chances);
        cross();
        mutate();
    }

    private double updateMarks() {
        updateBest();
        double temp = 0, sumOfAll = 0;
        marks = new ArrayList();
        for (Path s : population) {
            //temp = Math.pow(2, ((double) kosztNajgorszego * 1.2) - (double) s.kosztSciezki(k));
            temp = Math.pow(s.costOfPath(k, cities), -1) * 100;
            marks.add(temp);
            sumOfAll += temp;
        }

        return sumOfAll;
    }

    private double[] calculateChances(double sum) {
        double[] result = new double[size];
        double temp;
        for (int i = 0; i < size; i++) {
            temp = (double) marks.get(i);
            result[i] = (temp / sum) * 100;
        }
        return result;
    }

    private ArrayList chooseNewGeneration(double[] chances) {
        ArrayList result = new ArrayList(size);
        double medium = sum / size;
        int tmp = 0;

        for (int i = 0; i < size; i++) {
            do {
                tmp = rand.nextInt(size);
            } while (chances[tmp] < medium);

            result.add(population.get(tmp));
        }
        return result;
    }

    private void cross() {
        ArrayList<Integer> beingCrossed = new ArrayList<Integer>();
        int temp = 0, temp2 = 0;
        Path[] children;

        for (int i = 0; i < size; i++) {
            if (rand.nextDouble() < crossingProp) {
                beingCrossed.add(i);
            }
        }

        if (beingCrossed.size() % 2 == 1) {
            do {
                temp = rand.nextInt(size);
            } while (beingCrossed.contains(temp));
            beingCrossed.add(temp);
        }

        while (beingCrossed.size() > 0) {
            temp = rand.nextInt(beingCrossed.size());
            do {
                temp2 = rand.nextInt(beingCrossed.size());
            } while (temp == temp2);

            children = Path.cross((population.get(beingCrossed.get(temp))),
                    (population.get(beingCrossed.get(temp2))));

            population.set(beingCrossed.get(temp), children[0]);
            population.set(beingCrossed.get(temp2), children[1]);
            if (temp < temp2) {
                beingCrossed.remove(temp2);
                beingCrossed.remove(temp);
            } else {
                beingCrossed.remove(temp);
                beingCrossed.remove(temp2);
            }
        }
    }

    private void mutate() {
        for (int i = 0; i < size; i++) {
            if (rand.nextDouble() >= mutationProp) {
                continue;
            }
            population.get(i).mutate();
        }
    }

    private void updateBest() {
        best = population.get(1);
        costOfBest = best.costOfPath(k, cities);
        costOfWorst = costOfBest;
        double ks = 0;
        for (Path s : population) {
            if ((ks = s.costOfPath(k, cities)) < costOfBest) {
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

    private void generateRandomPopulation() {

        for (int i = 0; i < size; i++) {
            population.add((new Path(lengthOfPath)).random());
        }
    }

    private void generateManually(){
        Path p = new Path(lengthOfPath);
        bestEver = p;
        if(lengthOfPath<3)
            return;
        costOfBest = bestEver.costOfPath(k, cities);
        costOfWorst = costOfBest;
        int silnia = silnia(lengthOfPath-1);
        double ks;
        for(int i =0;i<silnia;i++){
            permutacja(p.path);
            if ((ks = p.costOfPath(k, cities)) < costOfBest) {
                costOfBest = ks;
                bestEver = p;
            } else if (ks > costOfWorst) {
                costOfWorst = ks;
            }
        }
    }

    private int silnia(int n){
        if(n==1) 
            return 1;
        else if(n<=0)
            return 0;
        else
            return(n*silnia(n-1));
    }

    /*
     * Algorytm autorstwa Sebastiana Pawlaka
     * http://sebastianpawlak.com/pl/Informatyka/Algorytmy/Permutacje/index.html
     */
    private void permutacja(int [] t) {
        int i, j = 0, l, max = t.length;

        if (max < 2)
            return;

        /* wyznaczanie pierwszego od prawej elementu
         * mniejszego niz jego sasiad z prawej strony
         */
        i = max - 1;
        while ((i > 1) && (t[i - 1] >= t[i]))
            i--;

        /* wyznaczanie elementu wiekszego od znalezionego */
        if (i > 1) {
            j = max;
            while ((j > 1) &&(t[j - 1] <= t[i - 1]))
                j--;
        }

        /* zamiana miejscami dwoch znalezionych wyzej elementow */
        if ((i > 1) && (j > 1)) {
            l = t[i - 1];
            t[i - 1] = t[j - 1];
            t[j - 1] = l;
        }

        /* odbicie lustrzane szeregu elementow od indeksu i+1 do konca tablicy */
        for (i++, j = max; i < j; i++, j--) {
            l = t[i - 1];
            t[i - 1] = t[j - 1];
            t[j - 1] = l;
        }

    }
}
