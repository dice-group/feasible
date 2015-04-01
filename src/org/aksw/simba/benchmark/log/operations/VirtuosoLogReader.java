package org.aksw.simba.benchmark.log.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.aksw.simba.largerdfbench.util.Selectivity;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
/**
 * Read Virtuoso Queries log
 * @author Saleem
 *
 */
public class VirtuosoLogReader {
	public static HashMap<String, HashMap<String, Set<Long>>> normalizedQueries  = new HashMap<String, HashMap<String, Set<Long>>> ();
	public static void main(String[] args) throws IOException, MalformedQueryException, RepositoryException, QueryEvaluationException {
		//String query = "select+distinct+%3Fn+++where+%7B+%3Fn++%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23narrower%3E+%3Chttp%3A%2F%2Fdbpedia.org%2Fresource%2FCategory%3AMen%2527s_social_titles%3E+.%7D";
		//query = (java.net.URLDecoder.decode(query, "UTF-8"));
		//		String query = "SELECT count(*) as ?Count  WHERE { ?s ?p ?o}";
		//		Query jenaQuery = QueryFactory.create(query, Syntax.syntaxARQ);
		//		jenaQuery.getProjectVars().clear();
		//		Set<String> vars = new HashSet<String>() ;
		//		vars.add("total");
		//		jenaQuery.addProjectVars(vars);
		//		System.out.println(jenaQuery.toString());
		//String queryLogsDir = "D:/Query Logs/RKBExplorer/";
		String queryLogDir = "D:/Query Logs/DBpedia3.5.1/";
		String endpoint = "http://localhost:8890/sparql";
		String graph = "http://aksw.org/feasible"; //can be null
		Selectivity.maxRunTime= -1; //query timeout in second. zero or negative means no timout limit
		HashMap<String, Set<String>> queries = getSesameLogQueries(queryLogDir);
		CleanQueryWriter.writeCleanQueriesWithStats(queries,endpoint,graph, "DBpediaCleanQueries.txt");


	}
	/**
	 * Get the set of all distinct queries from the log.
	 * @param queryLogDir Query Log Directory
	 * @return queries A Map of queries. Where keys of the Map are:select, ask, describe, construct
	 * @throws IOException
	 */
	public static HashMap<String, Set<String>> getSesameLogQueries(String queryLogDir) throws IOException  {
		HashMap<String, Set<String>> queries = new HashMap<String, Set<String>>();
		long totalLogQueries = 0 ;
		long parseErrorCount =0;
		Set<String> selectQueries = new HashSet<String> ();
		Set<String> constructQueries = new HashSet<String> ();
		Set<String> askQueries = new HashSet<String> ();
		Set<String> describeQueries = new HashSet<String> ();
		File dir = new File(queryLogDir);
		File[] listOfQueryLogs = dir.listFiles();
		System.out.println("Query Log Parsing in progress...");
		for (File queryLogFile : listOfQueryLogs)
		{
			System.out.println(queryLogFile.getName()+ ": in progress...");
			BufferedReader br = new BufferedReader(new FileReader(queryLogDir+queryLogFile.getName()));
			String line;
			while ((line = br.readLine()) != null)
			{	
				//System.out.println(line);
				if(line.contains("query="))
				{
					totalLogQueries++;
					String queryStr = getQuery(line); 
					//System.out.println(queryStr);
					try{
						Query query = QueryFactory.create(queryStr);
						query = removeNamedGraphs(query);
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
		System.out.println("Query log parsing completed\nTotal Number of queries (including duplicates): " + totalLogQueries );
		System.out.println("Number of queries with parse errors: " + parseErrorCount);
		System.out.println("Total distinct log queries: "+ (selectQueries.size()+constructQueries.size()+askQueries.size()+describeQueries.size()));
		System.out.println(" SELECT: "+selectQueries.size());
		System.out.println(" CNOSTRUCT: "+constructQueries.size());
		System.out.println(" ASK: "+askQueries.size() );
		System.out.println(" DESCRIBE: "+describeQueries.size() );
		return queries;
	}
	/**
	 * Remove Named Graphs from query
	 * @param query Jena parsed query
	 * @return Jena parsed query with no named graphs
	 */
	public static Query removeNamedGraphs(Query query) {
		query.getGraphURIs().clear();
		return query;
	}
	/**
	 * Parse the log line and get the required SAPARQL query 
	 * @param line Log Line
	 * @return query SPARQL quey
	 */
	private static String getQuery(String line) {
		String parts[] = line.split("query=");
		String query = parts[1];
		//		String [] parts  = query.split("&results=");
		//		query = parts[0];
		parts  = query.split("&format=");
		query = parts[0];
		parts  = query.split("&timeout=");
		query = parts[0];
		//		parts  = query.split("&maxrows=");
		//		query = parts[0];
		//		parts  = query.split("&_=");
		//		query = parts[0];
		//		parts  = query.split("&Accept=");
		//		query = parts[0];
		parts  = query.split("&graph=");
		query = parts[0];
		//		parts  = query.split("&output=");
		//		query = parts[0];
		//		parts  = query.split("&callback=");
		//		query = parts[0];
		parts  = query.split("&stylesheet");
		query = parts[0];
		parts  = query.split("&default-graph-uri=");
		query = parts[0];
		//	System.out.println(query);
		// query = queryPrts[0].substring(7,queryPrts[0].length());
		try{
			query  = java.net.URLDecoder.decode(query, "UTF-8");
		}
		catch (Exception e) {//System.err.println(query+ " "+ e.getMessage());
		}

		return query;
	}

}
