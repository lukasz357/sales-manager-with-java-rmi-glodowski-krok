/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package salesmanager.GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author głodoś
 */
public class Path {

    public int[] path;
    public static int length;
    public static Random rand;

    public Path(int numOfCities) {
        path = new int[numOfCities];
        path[0] = 1;
        for (int i = 1; i < numOfCities; i++) {
            path[i] = i+1;
        }
        length = numOfCities;
        rand = new Random();
    }

    public Path random() {
        int randomVal = 0;
        for (int i = 0; i < path.length; i++) {
            path[i] = 0;
        }
        path[0] = 1;
        for (int i = 1; i < path.length; i++) {
            do {
                randomVal = rand.nextInt(path.length);
            } while (path[randomVal] > 0);
            path[randomVal] = i + 1;
        }
        return this;
    }

    public static Path[] cross(Path s1, Path s2) {
        Path children[] = new Path[2];
        children[0] = s1.clonePath();
        children[1] = s2.clonePath();
        //zalozenie jest takie, ze ciecie moze przypasc przed lub po dowolnym miescie
        //ale ciecia nie moga sie pokrywac, zeby doszlo chocby do minimalnej wymiany

        int cut1 = rand.nextInt(Path.length - Path.length/2) + Path.length/4, cut2;
        do {
            cut2 = rand.nextInt(Path.length-Path.length/2) + Path.length/4;
        } while (cut1 == cut2);

        if (cut1 > cut2) {
            int temp = cut1;
            cut1 = cut2;
            cut2 = temp;
        }

        ArrayList<Integer> used1 = new ArrayList<Integer>();
        ArrayList<Integer> used2 = new ArrayList<Integer>();
        for (int i = cut1; i < cut2; i++) { // tu wymieniamy DNA osobnikow z obszaru ciecia
            children[0].path[i] = s2.getElem(i);
            children[1].path[i] = s1.getElem(i);
            used2.add(children[0].path[i]);
            used1.add(children[1].path[i]);
        }
        for (int i = 0; i < Path.length; i++) {
            if (i >= cut1 && i < cut2) { //pomin obszar ciecia
                continue;
            }

            while (used2.contains(children[0].getElem(i))) {
                children[0].path[i] = used1.get(used2.indexOf(children[0].path[i]));
            } //jesli element w sciezce sie powtarza to wymien go z powiazanym

            while (used1.contains(children[1].getElem(i))) {
                children[1].path[i] = used2.get(used1.indexOf(children[1].path[i]));
            } //jak wyzej ale dla drugiego dziecka
        }
        return children;
    }

    public void mutate() {
        int p1 = rand.nextInt(path.length-2) + 1, p2;
        do{
             p2 = rand.nextInt(path.length-2) + 1;
        }while(p2==p1);
        
        int temp = path[p1];
        path[p1] = path[p2];
        path[p2] = temp;
    }

    public Path clonePath() {
        Path result = new Path(this.path.length);
        System.arraycopy(this.path, 0, result.path, 0, path.length);
        return result;
    }

    public double costOfPath(Costs k, int [] cities){
        int sum=0;
        for(int i=0;i<path.length-1;i++){
            sum+=k.costs[cities[path[i]]][cities[path[i+1]]];
        }
        sum+=k.costs[cities[path[path.length-1]]][cities[path[0]]];
        return sum;
    }

    public int getElem(int pos) {
        return path[pos];
    }
}
