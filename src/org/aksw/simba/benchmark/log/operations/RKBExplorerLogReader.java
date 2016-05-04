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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
/**
 * RKBExplorer Log reader
 * @author Saleem
 *
 */
public class RKBExplorerLogReader {

	public static void main(String[] args) throws IOException, QueryEvaluationException, RepositoryException, MalformedQueryException {
		String logDir = "D:/Query Logs/RKBExplorer/";
		String endpoint = "http://localhost:8890/sparql";
		String graph = null;
		HashMap<String, Set<String>> queries = getBritishMuseumLogQueries(logDir);
		CleanQueryWriter.writeCleanQueriesWithStats(queries,endpoint,graph, "BMCleanQueries.txt");
	}
	/**
	 * Get british museum log queries (the majority of all) 
	 * @param logDir Log directory containing log finels
	 * @return queries A map of queries
	 * @throws IOException
	 */
	public static HashMap<String, Set<String>> getBritishMuseumLogQueries(String logDir) throws IOException
	{
		Set<String> selectQueries = new HashSet<String> ();
		Set<String> constructQueries = new HashSet<String> ();
		Set<String> askQueries = new HashSet<String> ();
		Set<String> describeQueries = new HashSet<String> ();
		long totalLogQueries = 0 ;
		long parseErrorCount =0;
		HashMap<String, Set<String>> queries = new HashMap<String, Set<String>>();
		File dir = new File(logDir);
		File[] listOfQueryLogs = dir.listFiles();
		System.out.println("Query Log Parsing in progress...");
		for (File queryLog : listOfQueryLogs)
		{
			BufferedReader br = new BufferedReader(new FileReader(logDir+queryLog.getName()));
			String line;
			while ((line = br.readLine()) != null)
			{
				String prts [] = line.split("\t");
				if(prts[1].equals("bm.rkbexplorer.com"))
				{
					String queryStr = prts[3];
					totalLogQueries++;
					try{
						Query query = QueryFactory.create(queryStr);
						if(query.isDescribeType())
						{
							if (!describeQueries.contains(query.toString()))
								describeQueries.add(query.toString());
						}
						else if (query.isSelectType())
						{
							if (!selectQueries.contains(query.toString()))
								selectQueries.add(query.toString());
						}
						else if (query.isAskType()){
							if (!askQueries.contains(query.toString()))
								askQueries.add(query.toString());
						}
						else if (query.isConstructType()){
							if (!constructQueries.contains(query.toString()))
								constructQueries.add(query.toString());
						}
					}
					catch (Exception ex){parseErrorCount++;}
				}
			}
			br.close();
		}
		queries.put("select", selectQueries);
		queries.put("construct", constructQueries);
		queries.put("ask", askQueries);
		queries.put("describe", describeQueries);
		System.out.println("Query log parsing completed");
		System.out.println("Query log parsing completed\nTotal Number of queries (including duplicates): " + totalLogQueries );
		System.out.println("Number of queries with parse errors:" + parseErrorCount);
		System.out.println("Total distinct log queries: "+ (selectQueries.size()+constructQueries.size()+askQueries.size()+describeQueries.size()));
		System.out.println(" SELECT: "+selectQueries.size());
		System.out.println(" CNOSTRUCT: "+constructQueries.size());
		System.out.println(" ASK: "+askQueries.size() );
		System.out.println(" DESCRIBE: "+describeQueries.size() );

		return queries;
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
