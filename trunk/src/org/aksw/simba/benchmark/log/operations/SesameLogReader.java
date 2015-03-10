package org.aksw.simba.benchmark.log.operations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
/**
 * Read Sesame Queries log
 * @author Saleem
 *
 */
public class SesameLogReader {
	public static HashMap<String, HashMap<String, Set<Long>>> normalizedQueries  = new HashMap<String, HashMap<String, Set<Long>>> ();
	public static void main(String[] args) throws IOException, MalformedQueryException, RepositoryException, QueryEvaluationException {
		//String queryLogsDir = "D:/Query Logs/RKBExplorer/";
		String queryLogFile = "D:/Query Logs/SWDF/SWDF.log";
		String endpoint = "http://localhost:8890/sparql";
		String graph = "http://aksw.org/benchmark"; //can be null
		//String outputDir = "D:/Query Logs/RKBEndpointsLogs/";
		//writeRKBExploreEndpointsLogs(queryLogsDir,outputDir);
		HashMap<String, Set<String>> queries = getSesameLogQueries(queryLogFile);
		CleanQueryWriter.writeCleanQueriesWithStats(queries,endpoint,graph, "CleanQueries.txt");
		
	}

	@SuppressWarnings("resource")
	public static HashMap<String, Set<String>> getSesameLogQueries(String queryLogFile) throws IOException, MalformedQueryException, RepositoryException, QueryEvaluationException {
		HashMap<String, Set<String>> queries = new HashMap<String, Set<String>>();
		Set<String> selectQueries = new HashSet<String> ();
		Set<String> constructQueries = new HashSet<String> ();
		Set<String> askQueries = new HashSet<String> ();
		Set<String> describeQueries = new HashSet<String> ();
		BufferedReader br = new BufferedReader(new FileReader(new File(queryLogFile)));
		String line;
		System.out.println("Query Log Parsing in progress...");
		while ((line = br.readLine()) != null)
		{	
			//System.out.println(line);
			if(line.contains("sparql?query="))
			{
				String query = getQuery(line); 
				if(query.contains("DESCRIBE")){
					if (!describeQueries.contains(query))
						describeQueries.add(query);
				}
				else if (query.contains("SELECT"))
				{
					if (!selectQueries.contains(query))
						selectQueries.add(query);
				}
				else if (query.contains("ASK")){
					if (!askQueries.contains(query))
						askQueries.add(query);
				}
				else if (query.contains("CONSTRUCT")){
					if (!constructQueries.contains(query))
						constructQueries.add(query);
				}

			}

		}
		queries.put("select", selectQueries);
		queries.put("construct", constructQueries);
		queries.put("ask", askQueries);
		queries.put("describe", describeQueries);
		System.out.println("Query log parsing completed");
		return queries;
	}
	private static String getQuery(String line) {
		String prts[] = line.split("query=");
		String queryPrts [] =  prts[1].split("HTTP/1.");
		String query = queryPrts[0];
		String [] parts  = query.split("&results=");
		query = parts[0];
		parts  = query.split("&format=");
		query = parts[0];
		parts  = query.split("&timeout=");
		query = parts[0];
		parts  = query.split("&maxrows=");
		query = parts[0];
		parts  = query.split("&_=");
		query = parts[0];
		parts  = query.split("&Accept=");
		query = parts[0];
		parts  = query.split("&graph=");
		query = parts[0];
		parts  = query.split("&output=");
		query = parts[0];
		parts  = query.split("&callback=");
		query = parts[0];
		parts  = query.split("&stylesheet");
		query = parts[0];
		parts  = query.split("&format=");
		query = parts[0];
		parts  = query.split("&default-graph-uri=");
		query = parts[0];

		// query = queryPrts[0].substring(7,queryPrts[0].length());
		try{
			query  = java.net.URLDecoder.decode(query, "UTF-8");
		}
		catch (Exception e) {//System.err.println(query+ " "+ e.getMessage());
		}

		return query;
	}
	/**
	 * Write RKBEXploere query logs into files for each SPARQL endpoint, i.e., one per endpoint
	 * @param queryLogsDir Query log directory
	 * @param outputDir Output directory
	 * @throws IOException Execptions
	 */
	public static void writeRKBExploreEndpointsLogs(String queryLogsDir, String outputDir) throws IOException {
		ArrayList<String> endpoints = getRKBEndpoints(queryLogsDir);
		System.out.println("Total RKBExplorer endpoints: " + endpoints.size());
		for(String endpoint:endpoints)
		{
			System.out.print(endpoint +" total queries: ");
			writeEndpointLogs(endpoint,queryLogsDir,outputDir);
		}	
	}
	/**
	 * Write the endpoint log into a separate file
	 * @param queryLogsDir Query log directory
	 * @param outputDir Output directory
	 * @throws IOException Execptions
	 */
	@SuppressWarnings("resource")
	public static void writeEndpointLogs(String endpoint, String queryLogsDir, String outputDir) throws IOException {
		BufferedWriter	bw= new BufferedWriter(new FileWriter(new File(outputDir+endpoint+".txt")));
		File dir = new File(queryLogsDir);
		File[] listOfQueryLogs = dir.listFiles();
		long count = 0;
		for (File queryLog : listOfQueryLogs)
		{
			BufferedReader br = new BufferedReader(new FileReader(queryLogsDir+queryLog.getName()));
			String line;
			while ((line = br.readLine()) != null)
			{
				String prts [] = line.split("\t");
				if(prts[1].equals(endpoint))
				{
					bw.write(prts[3]);
					bw.newLine();
					count++;
				}
			}
		}
		System.out.println(count);
		bw.close();

	}
	/**
	 * The RKB Explorer list of SPARQL endpoints
	 * @param queryLogsDir Logs directory
	 * @return endpoint List of SPARQL endpoints
	 * @throws IOException Exceptions
	 */
	@SuppressWarnings("resource")
	public static ArrayList<String> getRKBEndpoints(String queryLogsDir) throws IOException {
		ArrayList<String> endpoints = new ArrayList<String> ();
		File dir = new File(queryLogsDir);
		File[] listOfQueryLogs = dir.listFiles();
		for (File queryLog : listOfQueryLogs)
		{
			BufferedReader br = new BufferedReader(new FileReader(queryLogsDir+queryLog.getName()));
			String line;
			while ((line = br.readLine()) != null)
			{
				String prts [] = line.split("\t");
				if(!endpoints.contains(prts[1]))
					endpoints.add(prts[1]);
			}
		}
		return endpoints;
	}
}
