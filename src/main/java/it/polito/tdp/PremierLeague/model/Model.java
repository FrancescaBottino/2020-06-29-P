package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph <Match, DefaultWeightedEdge> grafo;
	private Map<Integer, Match> idMap;
	
	
	public Model() {
		
		dao = new PremierLeagueDAO();
		idMap= new TreeMap<Integer, Match>();
		this.dao.listAllMatches(idMap);
		
	}
	
	public void creaGrafo(String mese, Integer minuti) {
		
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		
		//vertici
		
		int meseN = this.getMese(mese);
		
		Graphs.addAllVertices(this.grafo, this.dao.getAllVertici(meseN, idMap));
		
		
		//archi
		
		/*
		 * Due match sono collegati con un arco se esiste almeno un giocatore,
		 * di qualunque squadra, che abbia giocato almeno MIN minuti in entrambi i match.
		 * Il peso dell’arco, in particolare, rappresenta il numero di giocatori distinti
		 * (sempre > 0) che rispettano tale vincolo.
		 */
		
		
		for(Adiacenza a: this.dao.getAdiacenze(meseN, minuti, idMap)) {
			
			if(a.getPeso()>0) {
				Graphs.addEdgeWithVertices(grafo, a.getM1(), a.getM2(), a.getPeso());
			}
			
		}
		
		

		
		
	}
	
	//punto d: Premendo il bottone “Connessione Max”, si stampi la coppia
	//(o le coppie) di match in cui hanno giocato, per almeno MIN minuti
	//in entrambe le partite, il maggior numero di giocatori.
	
	
	
	public String getConnessioneMax() {
		

		
		List<Adiacenza> best=new ArrayList<Adiacenza>();
		
		int pesoMigliore=0;
		
		
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			
			if(this.grafo.getEdgeWeight(e) > pesoMigliore) {
				
				pesoMigliore = (int) this.grafo.getEdgeWeight(e);
				best.clear();
				
				best.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), pesoMigliore));
				
			}
			else if (this.grafo.getEdgeWeight(e) == pesoMigliore) {
				
				best.add(new Adiacenza(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), pesoMigliore));
				
			}
		}
		
		
		String s="";
		for(Adiacenza aa: best) {
			
			s+= aa.getM1().toString()+ "- "+aa.getM2().toString()+" : "+aa.getPeso()+"\n";
			
		}
		
		return s;
		
			
	}
	
	

	public Integer getNVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public Integer getNArchi() {
		return this.grafo.edgeSet().size();
	}
	
	
	
	public Integer getMese(String mese) {
		
		
			if(mese.equals("Gennaio"))
				return 1;
			if(mese.equals("Febbraio"))
				return 2;
			if(mese.equals("Marzo"))
				return 3;
			if(mese.equals("Aprile"))
				return 4;
			if(mese.equals("Maggio"))
				return 5;
			if(mese.equals("Giugno"))
				return 6;
			if(mese.equals("Luglio"))
				return 7;
			if(mese.equals("Agosto"))
				return 8;
			if(mese.equals("Settembre"))
				return 9;
			if(mese.equals("Ottobre"))
				return 10;
			if(mese.equals("Novembre"))
				return 11;
			if(mese.equals("Dicembre"))
				return 12;
			
			return 0;
		}
	
	public Graph<Match, DefaultWeightedEdge> getGrafo() {
		return this.grafo;
	}
	
	public Set<Match> getVertici(){
		
		return this.grafo.vertexSet();
	}
		
}
