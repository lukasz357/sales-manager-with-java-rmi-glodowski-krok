package komiwojazer.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import komiwojazer.GeneticAlgorithm.Costs;
import komiwojazer.GeneticAlgorithm.GeneticAlg;
import komiwojazer.GeneticAlgorithm.Path;

public class Client {
	private final int clientID;
	
	private ServerInterface server;
	private int generationsCount;
	private int populationCount;
	
	private Costs costArray;
	private int[] nodes;
	
	private float mutationProp, crossingProp;
	public Client(ServerInterface  s) throws RemoteException{
		this.server = s;
		generationsCount = s.getGenerationsCount();
		populationCount = s.getPopulationCount();
		costArray = s.getCostArray();
		nodes = s.getNodeNumbers();
		mutationProp = s.getMutationProbability();
		crossingProp = s.getCrossProbability();
		clientID = new Random().nextInt();
	}
	
	public void start(){
		List<Path> population;
		try {
			
			population = server.register(clientID);	
			//p("Klient "+clientID+" laczy sie z serwerem");
			while(true){
				//p("Klient "+clientID+" rozpoczyna obliczenia");
				GeneticAlg ga = new GeneticAlg(populationCount, nodes, mutationProp, crossingProp, costArray);
				ga.setInitialPopulation(population);
				ga.simulateNGenerations(generationsCount);
				List<Path> result = ga.getPopulation();
				//Collections.sort(result);
				//p("Klient "+clientID+" zwraca wynik");
				population = server.newGeneration(clientID, result);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		System.setSecurityManager(new RMISecurityManager());
		
		String name = "ParallelGeneticServer";
		try {
			Registry registry = LocateRegistry.getRegistry(1099);
			ServerInterface server = (ServerInterface) registry.lookup(name);
			new Client( server).start();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void p(String s){
		System.out.println(s);
	}
}
