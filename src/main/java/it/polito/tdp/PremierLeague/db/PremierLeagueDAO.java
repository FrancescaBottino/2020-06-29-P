package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.Adiacenza;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;

public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void listAllMatches(Map <Integer, Match> idMap){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				if(!idMap.containsKey(res.getInt("m.MatchID"))) { 
				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				idMap.put(match.getMatchID(), match);
				}
				

			}
			conn.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	
	public List<Match> getAllVertici(Integer mese, Map <Integer, Match> idMap){
		
		String sql="SELECT MatchID "
				+ "FROM Matches "
				+ "WHERE Month(Date) = ? ";
		
		List<Match> result= new ArrayList<Match>();
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			ResultSet res = st.executeQuery();
		
			
			while (res.next()) {
				
				if(idMap.containsKey(res.getInt("MatchID"))) {
					
					result.add(idMap.get(res.getInt("MatchID")));
				}
			}
			
		
			
		conn.close();
		return result;
		
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
		
		
		
	}
	
	
	public List<Adiacenza> getAdiacenze(Integer mese, Integer minuti, Map<Integer, Match> idMap){
		
		
		String sql="SELECT a1.MatchID as mID1, a2.MatchID as mID2, count(*) as peso "
				+ "FROM Actions a1, Actions a2, Matches m1, Matches m2 "
				+ "WHERE m1.MatchID=a1.MatchID AND "
				+ "m2.MatchID = a2.MatchID AND "
				+ "Month(m1.Date) = ? AND Month(m2.Date) = ? AND "
				+ "a1.MatchID > a2.MatchID AND "
				+ "a1.PlayerID = a2.PlayerID AND "
				+ "a1.TimePlayed >= ? "
				+ "GROUP BY a1.MatchID, a2.MatchID ";
		
		List<Adiacenza> result= new ArrayList<Adiacenza>();
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, mese);
			st.setInt(2, mese);
			st.setInt(3, minuti);
			ResultSet res = st.executeQuery();
		
			
			while (res.next()) {
				
				
				Match m1 = idMap.get(res.getInt("mID1"));
				Match m2 = idMap.get(res.getInt("mID2"));
				
				Adiacenza a = new Adiacenza(m1, m2, res.getInt("peso"));
				result.add(a);
				
				
			}
			
			conn.close();
			return result;
			
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
}
