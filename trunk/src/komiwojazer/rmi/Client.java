package komiwojazer.rmi;

import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Random;

import komiwojazer.Utils;
import komiwojazer.GeneticAlgorithm.Costs;
import komiwojazer.GeneticAlgorithm.GeneticAlg;
import komiwojazer.GeneticAlgorithm.Path;

public class Client {
	private final int clientID;
	
	private ServerInterface server;
	private int tourGenerationCount;
	private int populationCount;
	
	private Costs costArray;
	private int[] nodes;
	
	private float mutationProp, crossingProp;
	public Client(ServerInterface  s) throws RemoteException{
		this.server = s;
		tourGenerationCount = s.getGenerationsCount()/s.getTourCount();
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
			while(population != null){
				GeneticAlg ga = new GeneticAlg(populationCount, nodes, mutationProp, crossingProp, costArray);
				ga.setInitialPopulation(population);
				ga.simulateNGenerations(tourGenerationCount);
				List<Path> result = ga.getPopulation();
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
			Registry registry = LocateRegistry.getRegistry(Utils.registryPort);
			ServerInterface server = (ServerInterface) registry.lookup(name);
			new Client(server).start();
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
