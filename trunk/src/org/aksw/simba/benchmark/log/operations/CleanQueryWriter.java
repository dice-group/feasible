package org.aksw.simba.benchmark.log.operations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import org.aksw.simba.largerdfbench.util.QueryStatistics;
import org.aksw.simba.largerdfbench.util.Selectivity;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

public class CleanQueryWriter {
	public static long count =0;
	public static long zeroCount =0;
	public static long errorCount = 0;
	public static BufferedWriter	bw;
	public static long datasetSize = 0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static void writeCleanQueriesWithStats ( HashMap<String, Set<String>> queries, String endpoint, String graph, String outputQueryFile) throws QueryEvaluationException, RepositoryException, MalformedQueryException, IOException
	{		
		datasetSize = Selectivity.getEndpointTotalTriples(endpoint, graph);
		bw= new BufferedWriter(new FileWriter(outputQueryFile));	
		Set<String> selectQueries = queries.get("select");
		Set<String> constructQueries = queries.get("construct");
		Set<String> askQueries = queries.get("ask");
		Set<String> describeQueries = queries.get("describe");
		System.out.println("Total distinct log queries: "+ (selectQueries.size()+constructQueries.size()+askQueries.size()+describeQueries.size()));
		System.out.println("SELECT: "+selectQueries.size());
		System.out.println("CNOSTRUCT: "+constructQueries.size());
		System.out.println("ASK: "+askQueries.size() );
		System.out.println("DESCRIBE: "+describeQueries.size() );
		writeSelectQueries(selectQueries,endpoint,graph);
		writeConstructQueries(constructQueries,endpoint,graph);
		writeAskQueries(askQueries,endpoint,graph);
		writeDescribeQueries(describeQueries,endpoint,graph);
		bw.write("#--end---");
		bw.close();
		System.out.println("Query with stats successfully writen to "+outputQueryFile+"\nTotal queries with error: " + errorCount);
		System.out.println("Total queries with zero results: "+zeroCount+"\nNote: Zero result queries are not considered");
	}
	public static void writeDescribeQueries(Set<String> describeQueries,
			String endpoint, String graph) throws RepositoryException, MalformedQueryException {
		long resultSize = -1;
		for(String query:describeQueries)
		{  
			resultSize = Selectivity.getQueryResultSize(query, endpoint,"graph");
			//System.out.println(resultSize);
			if (resultSize>0)
			{
				
				try {
					String queryStats = "#-------------------------------------------------------\nQuery No: "+count+"\n";  //new query identifier
					queryStats = queryStats + "Query Type: DESCRIBE \n";
					queryStats = queryStats + "Results Size: "+resultSize  +"\n";
					queryStats = queryStats+QueryStatistics.getQueryStats(query,endpoint,graph,datasetSize);
					queryStats = queryStats+"Query String: "+java.net.URLEncoder.encode(query, "UTF-8")+"\n";
					//System.out.println(queryStats);
					bw.write(queryStats);
					System.out.println(count+ ": written...");
					count++;
				} catch (Exception e) {	errorCount++; }  
			}
			else if (resultSize==0)		
				zeroCount++;
			else
				errorCount++;
		}
	}

	public static void writeAskQueries(Set<String> askQueries,
			String endpoint, String graph) throws MalformedQueryException, RepositoryException, QueryEvaluationException, IOException {
		for(String query:askQueries)
		{  	
			try {
				System.out.println(count+ ": writing...");
				count++;
				String queryStats = "#-------------------------------------------------------\nQuery No: "+count+"\n";  //new query identifier
				queryStats = queryStats + "Query Type: ASK \n";
				queryStats = queryStats + "Results Size: 1 \n";
				queryStats = queryStats+QueryStatistics.getQueryStats(query,endpoint,graph,datasetSize);
				queryStats = queryStats+"Query String: "+java.net.URLEncoder.encode(query, "UTF-8")+"\n";
				//System.out.println(queryStats);
				bw.write(queryStats);
			} catch (Exception e) {//	System.err.println("Syntax Error in query: " + query); 
				//System.err.println(e.getMessage());
				errorCount++; }  
		}	

	}
	public static void writeConstructQueries(Set<String> constructQueries,
			String endpoint, String graph) throws RepositoryException, MalformedQueryException {
		long resultSize = -1;
		for(String query:constructQueries)
		{  
			resultSize = Selectivity.getQueryResultSize(query, endpoint,"graph");
			//System.out.println(resultSize);
			if (resultSize>0)
			{
				
				try {
					String queryStats = "#-------------------------------------------------------\nQuery No: "+count+"\n";  //new query identifier
					queryStats = queryStats + "Query Type: CONSTRUCT \n";
					queryStats = queryStats + "Results Size: "+resultSize  +"\n";
					queryStats = queryStats+QueryStatistics.getQueryStats(query,endpoint,graph,datasetSize);
					queryStats = queryStats+"Query String: "+java.net.URLEncoder.encode(query, "UTF-8")+"\n";
					//System.out.println(queryStats);
					bw.write(queryStats);
					System.out.println(count+ ": written...");
					count++;
				} catch (Exception e) {	errorCount++; }  
			}
			else if (resultSize==0)		
				zeroCount++;
			else
				errorCount++;
		}

	}
	public static void writeSelectQueries(Set<String> selectQueries, String endpoint, String graph) throws RepositoryException, MalformedQueryException, QueryEvaluationException, IOException {
		long resultSize = -1;
		for(String query:selectQueries)
		{  
			resultSize = Selectivity.getQueryResultSize(query, endpoint,"tuple");
			//System.out.println(resultSize);
			if (resultSize>0)
			{
				
				try {
					String queryStats = "#-------------------------------------------------------\nQuery No: "+count+"\n";  //new query identifier
					queryStats = queryStats + "Query Type: SELECT \n";
					queryStats = queryStats + "Results Size: "+resultSize  +"\n";
					queryStats = queryStats+QueryStatistics.getQueryStats(query,endpoint,graph,datasetSize);
					queryStats = queryStats+"Query String: "+java.net.URLEncoder.encode(query, "UTF-8")+"\n";
					//System.out.println(queryStats);
					bw.write(queryStats);
					System.out.println(count+ ": written...");
					count++;
				} catch (Exception e) {	errorCount++; }  
			}
			else if (resultSize==0)		
				zeroCount++;
			else
				errorCount++;
		}

	}
}
