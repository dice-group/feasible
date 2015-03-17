package org.aksw.simba.benchmark.startup;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.aksw.simba.benchmark.clustring.QueryClustering;
import org.aksw.simba.benchmark.log.operations.CleanQueryReader;

public class QuerySelecter {

	public static void main(String[] args) throws IOException {
	//	System.out.println(java.net.URLDecoder.decode("%0A++++prefix+qb%3A+%3Chttp%3A%2F%2Fpurl.org%2Flinked-data%2Fcube%23%3E+%0A++++ASK+WHERE+%7B%3Fx+%3Fp+%3Fo+.+filter+%28%3Fo%3Dqb%3AObservation+%7C%7C+%3Fo%3Dqb%3ADataset+%29%7D%0A%09"));
		String queryFileWithStats = "CleanQueries.txt";
		long curTime = System.currentTimeMillis();
		Set<String> queries = getBenchmarkQueries(queryFileWithStats,5);
		System.out.println("Total Queries: "+ queries.size());
		int count = 1;
		for(String query:queries)
		{
			String [] qryParts = query.split("Query String:");
			System.out.println(count+":----------------------\nQuery Features: \n\n"+qryParts[0]+ "\nQuery: \n"+qryParts[1].trim());
			count++;
		}
		System.out.println("Benchmark generation time (sec): "+(System.currentTimeMillis()-curTime)/1000);

	}

	public static Set<String> getBenchmarkQueries(String queryFileWithStats,int numberOfQueries) throws IOException {
		Map<String, Double[]> normalizedVectors = 	CleanQueryReader.getNormalizedFeaturesVectors(queryFileWithStats);
		QueryClustering qc = new QueryClustering();
		Set<String> queriesIds = qc.getPrototypicalQueries(normalizedVectors,numberOfQueries);
		Set<String> queries = CleanQueryReader.getQueriesWithStats(queryFileWithStats,queriesIds);
		return queries;
	}

}
