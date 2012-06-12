package komiwojazer.rmi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import komiwojazer.MapUtil;
import komiwojazer.Utils;
import komiwojazer.GeneticAlgorithm.Costs;
import komiwojazer.GeneticAlgorithm.Path;
import komiwojazer.Map.City;
import komiwojazer.Map.CityMap;

public class Server extends UnicastRemoteObject implements ServerInterface {

	private static final long serialVersionUID = -2065432534190043743L;

	private float crossProb, mutateProb;
	private int generationsCount, populationCount, tourCount;
	
	private int counter = 0;
	private Costs costArray;
	private int[] nodeNumbers;

	private CityMap map;
	private MapUtil mu;

	private List<Integer> clientRegistry;

	private Path actualBest, bestEver, actualWorst, worstEver;
	private List<Path> currentGeneration;


	private Object lock;
	private Object notifier;

	private Stack<List<Path>> clientResults;

	public Server() throws RemoteException {
		super();
	}

	public Server(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
		super(port, csf, ssf);
	}

	public Server(int port) throws RemoteException {
		super(port);
	}

	private void init() {
		clientResults = new Stack<List<Path>>();
		lock = new Object();
		notifier = new Object();
		clientRegistry = new ArrayList<Integer>();
		mu = new MapUtil(map);
		costArray = mu.getCostArray();
		nodeNumbers = mu.getNodeNumbers();
		currentGeneration = new ArrayList<Path>();
		for (int i = 0; i <populationCount; i++) {
			Path p = new Path(map.numberOfCities).random();
			p.updateCost(mu.getCostArray(), nodeNumbers);
			currentGeneration.add(p);
		}
		new Session().start();
	}

	@Override
	public float getMutationProbability() throws RemoteException {
		return mutateProb;
	}

	public void setMutationProbability(float prob) {
		this.mutateProb = prob;
	}

	@Override
	public float getCrossProbability() throws RemoteException {
		return crossProb;
	}

	public void setCrossProbability(float prob) {
		crossProb = prob;
	}

	@Override
	public int getPopulationCount() throws RemoteException {
		return populationCount;
	}

	public void setPopulationCount(int count) {
		populationCount = count;
	}

	@Override
	public int getGenerationsCount() throws RemoteException {
		return generationsCount;
	}

	public void setGenerationsCount(int count) {
		generationsCount = count;
	}

	@Override
	public Costs getCostArray() throws RemoteException {
		return costArray;
	}

	public void setCostArray(Costs c) {
		costArray = c;
	}

	@Override
	public int[] getNodeNumbers() throws RemoteException {
		return nodeNumbers;
	}

	public void setNodeNumbers(int[] num) {
		nodeNumbers = num;
	}

	public void setMap(CityMap m) {
		map = m;
		init();
	}
	
	public int getTourCount() throws RemoteException{
		return tourCount;
	}

	public void setTourCount(int tourCount) {
		this.tourCount = tourCount;
	}

	@Override
	public List<Path> register(int clientID) throws RemoteException {
		try {
			if(clientRegistry.size() == 0){
				//jesli jest pierwszy to obudz watek
				synchronized (notifier) {
					notifier.notify();
				}
			}
			p("Klient "+clientID+" chce sie polaczyc");
			// czekaj
			synchronized (lock) {
				lock.wait();
			}
			// dostal pozwolenie to teraz zapisuje sie
			// i w zamian dostaje pokolenie
			clientRegistry.add(clientID);
			p("Klient "+clientID+" podlaczony");
			return currentGeneration;
		} catch (InterruptedException e) {
			return null;
		}
	}

	
	/**
	 * 
	 *  klient zglasza wynik swojej pracy a w zamian otrzymuje
	 *	populacje do kolejnych obliczen
	 *
	 */
	
	@Override
	public List<Path> newGeneration(int clientID, List<Path> currentGeneration) throws RemoteException {

		clientResults.add(currentGeneration);
		
		// poinformuj watek zarzadzajacy
		synchronized (notifier) {
			notifier.notify();
		}
		p("Klient "+clientID+" zglosil wynik");
		// czekaj
		try {
			synchronized (lock) {
				lock.wait();
			}
			if(counter == tourCount) { //jak wszystkie tury przejdzie
				return null;
			}
			else {
				p("Klient "+clientID+" wraca do obliczen");
				return currentGeneration;
			}
				
		} catch (InterruptedException e) {
			return null;
		}
	}
	

	/**
	 * 
	 * Watek zarządzający klientami
	 *
	 */
	class Session extends Thread {
		public void run() {
			try {
				int handledCount = 0;
				while (true) {
					if(clientResults.isEmpty())
						synchronized (notifier) {
							notifier.wait();
						}
					
					if(clientRegistry.size()>0){
						//zbierz aktualne wyniki od klientów
						currentGeneration.clear();
						int gatherCount = populationCount / clientRegistry.size();
						while(!clientResults.isEmpty()){
							List<Path> result = clientResults.pop();
							currentGeneration.addAll(result.subList(0, gatherCount));
							handledCount++;
						}
					}
					if(handledCount == clientRegistry.size()){
						//jesli od wszystkich zebrane
						//uzupelnij liste jesli sa braki
						assertSize(currentGeneration);
						Collections.sort(currentGeneration);
						actualBest = currentGeneration.get(0);
						if(bestEver==null || actualBest.cost<bestEver.cost)
							bestEver = actualBest;
						actualWorst = currentGeneration.get(currentGeneration.size()-1);
						if(worstEver==null || actualWorst.cost>worstEver.cost)
							worstEver = actualWorst;
						printBestPath();
						handledCount = 0;
						counter++;
						//obudz czekajacych klientow
						synchronized (lock) {
							lock.notifyAll();
						}
					}
					
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void assertSize(List<Path> list){
		while(list.size()<populationCount){
			Path p = new Path(map.numberOfCities).random();
			p.updateCost(costArray, nodeNumbers);
			list.add(p);
		}
	}
	
	private void printBestPath(){
		System.out.println("Najlepszy koszt w sesji: "+actualBest.cost);
		System.out.println("Najlepszy do tej pory: "+bestEver.cost);
		System.out.println("Najgorszy koszt w sesji: "+actualWorst.cost);
		System.out.println("Najgorszy do tej pory: "+worstEver.cost);
	}
	

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		try {
			Server s = new Server(Utils.defaultPort);
			s.setCrossProbability(Utils.crossPossibility);
			s.setMutationProbability(Utils.mutationPossibility);
			s.setGenerationsCount(Utils.generationsCount); // Całkowita liczba pokoleń
			s.setTourCount(Utils.tourCount);           // liczba tur - na ile dzielona jest liczba pokoleń
			s.setPopulationCount(Utils.populationCount);
			CityMap m = readMap(Utils.defaultGraphPath);
			s.setMap(m);
            Registry registry = LocateRegistry.createRegistry(Utils.registryPort);
            registry.rebind("ParallelGeneticServer", s);
			System.out.println("Server started on port 9999");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static CityMap readMap(String path) throws IOException {
		TreeMap<String, City> nodes = new TreeMap<String, City>();
		String tmp;
		String[] line;
		int fileLine = 1;

		BufferedReader in = new BufferedReader(new FileReader(new File(path)));
		// sprawdzam czy jest nagłówek Nodes:
		if (!(tmp = in.readLine()).equals("Nodes:")) {
			throw new FileFormatException(1, "Brak naglowka \"Nodes:\"");
		}

		// wczytuję wierzchołki grafu
		while ((tmp = in.readLine()) != null && !tmp.equals("Edges:")) {
			nodes.put(tmp, new City(tmp));
			fileLine++;
		}

		// sprawdzam czy jest nagłówek Edges:
		if (tmp == null) {
			throw new FileFormatException(fileLine, "Brak naglowka \"Edges:\"");
		} else {
			// wczytuję połaczenia między wierzchołkami
			while ((tmp = in.readLine()) != null) {
				++fileLine;
				line = tmp.split(" ");
				if (line.length != 4)
					throw new FileFormatException(fileLine, "Bledna ilosc danych. Wymagane 4.");
				nodes.get(line[0]).AddOutgoingRoute(nodes.get(line[1]), Integer.parseInt(line[2]), Integer.parseInt(line[3]));
				nodes.get(line[1]).AddOutgoingRoute(nodes.get(line[0]), Integer.parseInt(line[2]), Integer.parseInt(line[3]));
			}
		}
		in.close();

		return new CityMap(nodes);
	}

	static class FileFormatException extends IOException {
		private static final long serialVersionUID = -3306760553607322125L;

		public FileFormatException(int line, String message) {
			super("Blad struktury pliku w linii " + line + ": " + message);
		}
	}
	
	public static void p(String s){
		System.out.println(s);
	}

}
