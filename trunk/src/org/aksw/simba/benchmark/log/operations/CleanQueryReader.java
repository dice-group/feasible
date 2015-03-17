package org.aksw.simba.benchmark.log.operations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aksw.simba.benchmark.Config;
import org.aksw.simba.benchmark.clustring.QueryClustering;

public class CleanQueryReader {
  public static int length;
	public static void main(String[] args) throws IOException {
		String queryFileWithStats = "CleanQueries.txt";
		Map<String, Double[]> normalizedVectors = 	getNormalizedFeaturesVectors(queryFileWithStats);
		QueryClustering qc = new QueryClustering();
		System.err.println(qc.getPrototypicalQueries(normalizedVectors, 20));
	}
	@SuppressWarnings("resource")
	public static Map<String, Double[]> getNormalizedFeaturesVectors(String queryFileWithStats) throws IOException {
		Map<String, Double[]> vectors = new HashMap<String, Double[]>();
		 length = getVectorLength();
		Double [] maxIndexVal = new Double[length]; 
		for(int i=0;i<length;i++)
			maxIndexVal[i] = 0d;
		//System.out.println(length);
		BufferedReader br = new BufferedReader(new FileReader(new File(queryFileWithStats)));
		//	long count = 0;
		System.out.println("Feature vectors loading in progress...");
		String line= br.readLine();
		String queryNumber = "";
		while ((line = br.readLine()) != null && !line.equals("#--end---"))
		{
			//System.out.println(line);
			//String queryFeatures = br.readLine()+"\n";  // query number
			String queryNumberLine [] = line.split(":"); // query number
			queryNumber = queryNumberLine[1];
			//System.out.println(line);
			Double [] vector = new Double[length]; 
			int index = 0;
			br.readLine(); //query type
			//----- selected get features
			line = br.readLine();
			if (Config.resultSize==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				vector[index] =Double.parseDouble(prts[1].trim());
				if(maxIndexVal[index]<vector[index])
					maxIndexVal[index] = vector[index];
				// System.out.println(prts[1]);
				index++;
			}
			line = br.readLine();
			if (Config.BGPs==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				vector[index] =Double.parseDouble(prts[1].trim());
				if(maxIndexVal[index]<vector[index])
					maxIndexVal[index] = vector[index];
				//System.out.println(prts[1]);
				index++;
			}
			line = br.readLine();
			if (Config.triplePatternsCount==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				vector[index] =Double.parseDouble(prts[1].trim());
				if(maxIndexVal[index]<vector[index])
					maxIndexVal[index] = vector[index];
				//System.out.println(prts[1]);
				index++;
			}
			line = br.readLine();
			if (Config.joinVertices==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				vector[index] =Double.parseDouble(prts[1].trim());
				if(maxIndexVal[index]<vector[index])
					maxIndexVal[index] = vector[index];
				//System.out.println(prts[1]);
				index++;
			}
			line = br.readLine();
			if (Config.meanJoinVerticesDegree==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				vector[index] =Double.parseDouble(prts[1].trim());
				if(maxIndexVal[index]<vector[index])
					maxIndexVal[index] = vector[index];
				//System.out.println(prts[1]);
				index++;
			}
			line = br.readLine();
			if (Config.meanTriplePatternSelectivity==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				vector[index] =Double.parseDouble(prts[1].trim());
				if(maxIndexVal[index]<vector[index])
					maxIndexVal[index] = vector[index];
				//System.out.println(prts[1]);
				index++;
			}
			line = br.readLine();
			if (Config.UNION==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				if(prts[1].trim().equals("No"))
					vector[index] =0d;
				else
					vector[index] =1d;
				maxIndexVal[index] = 1d;
				index++;

			}
			line = br.readLine();
			if (Config.DISTINCT==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				if(prts[1].trim().equals("No"))
					vector[index] =0d;
				else
					vector[index] =1d;
				maxIndexVal[index] = 1d;
				index++;
			}
			line = br.readLine();
			if (Config.ORDERBY==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				if(prts[1].trim().equals("No"))
					vector[index] =0d;
				else
					vector[index] =1d;
				maxIndexVal[index] = 1d;
				index++;
			}
			line = br.readLine();
			if (Config.REGEX==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				if(prts[1].trim().equals("No"))
					vector[index] =0d;
				else
					vector[index] =1d;
				maxIndexVal[index] = 1d;
				index++;
			}
			line = br.readLine();
			if (Config.LIMIT==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				if(prts[1].trim().equals("No"))
					vector[index] =0d;
				else
					vector[index] =1d;
				maxIndexVal[index] = 1d;
				index++;
			}
			line = br.readLine();
			if (Config.OFFSET==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				if(prts[1].trim().equals("No"))
					vector[index] =0d;
				else
					vector[index] =1d;
				maxIndexVal[index] = 1d;
				index++;
			}
			line = br.readLine();
			if (Config.OPTIONAL==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				if(prts[1].trim().equals("No"))
					vector[index] =0d;
				else
					vector[index] =1d;
				maxIndexVal[index] = 1d;
				index++;
			}
			line = br.readLine();
			if (Config.FILTER==true)
			{
				//queryFeatures = queryFeatures + line+"\n";
				String prts [] = line.split(":");
				if(prts[1].trim().equals("No"))
					vector[index] =0d;
				else
					vector[index] =1d;
				maxIndexVal[index] = 1d;
				index++;
			}
			line = br.readLine();  //query string
			line = br.readLine();  //start of new query
			//queryFeatures = queryFeatures + line+"\n";
			//--------get query string
//			if(line.trim().equals("Query String:"))
//			{
//				String query = "";
//				line = br.readLine();
//				while(!line.equals("#-------------------------------------------------------")  )
//				{
//					query = query+line+"\n";
//					line = br.readLine() ;
//					if(line.equals(" #--end---"))
//						break;
//				}
				//vectors.put(query, vector);
				//if(vectors.keySet().contains(queryNumber))
				//	System.out.println("Duplicate flag on");
				//if(queryNumber == null)
					//System.out.println("query Number problem");
				vectors.put(queryNumber, vector);
				//if(length!=vector.length)
				//System.err.println("Problem with vector lenghty for query no: "+ queryNumber);
			//	System.out.println(queryNumber);
		//	}
			// System.out.println(count++);
		}
		//for(int i=0;i<length;i++)
		//	System.out.println(maxIndexVal[i]);
		System.out.println("Feature vectors loading completed. Vectors returned");
		return getNormalizedVectors(vectors,maxIndexVal);

	}
	private static Map<String, Double[]> getNormalizedVectors(Map<String, Double[]> vectors, Double[] maxIndexVal)
	{
		Map<String, Double[]> normalizedVectors = new HashMap<String, Double[]>();
		for(String query:vectors.keySet())
		{
			Double[] vector = vectors.get(query);
			for(int i=0;i<vector.length;i++)
			{
				vector[i] =vector[i]/ maxIndexVal[i];
			}
			normalizedVectors.put(query, vector);
		}
		vectors.clear();
		return normalizedVectors;
	}
	public static int getVectorLength() {
		int length = 0;
		if (Config.resultSize==true)
			length++;
		if (Config.BGPs==true)
			length++;
		if (Config.triplePatternsCount==true)
			length++;
		if (Config.joinVertices==true)
			length++;
		if (Config.meanJoinVerticesDegree==true)
			length++;
		if (Config.meanTriplePatternSelectivity==true)
			length++;
		if (Config.UNION==true)
			length++;
		if (Config.DISTINCT==true)
			length++;
		if (Config.ORDERBY==true)
			length++;
		if (Config.REGEX==true)
			length++;
		if (Config.LIMIT==true)
			length++;
		if (Config.OFFSET==true)
			length++;
		if (Config.OPTIONAL==true)
			length++;
		if (Config.FILTER==true)
			length++;
		return length;
	}
	@SuppressWarnings("resource")
	public static Set<String> getQueriesWithStats(String queryFileWithStats, Set<String> queriesIds) throws IOException {
		Set<String> queries = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(queryFileWithStats)));
		String line= br.readLine();
		String queryNumber = "";
		String queryFeatures ="";
		while ((line = br.readLine()) != null && !line.equals("#--end---")  && ! queriesIds.isEmpty())
		{
			String queryNumberLine [] = line.split(":"); // query number
			//System.out.println(line);
			queryNumber = queryNumberLine[1];
			br.readLine(); // for query type;
			if(queriesIds.contains(queryNumber))
			{
				line = br.readLine();
				if (Config.resultSize==true)
				queryFeatures = queryFeatures + line+"\n";
				line = br.readLine();
				if (Config.BGPs==true)
				queryFeatures = queryFeatures + line+"\n";
				line = br.readLine();
				if (Config.triplePatternsCount==true)
				queryFeatures = queryFeatures + line+"\n";
				line = br.readLine();
				if (Config.joinVertices==true)
				queryFeatures = queryFeatures + line+"\n";				
				line = br.readLine();
				if (Config.meanJoinVerticesDegree==true)
				queryFeatures = queryFeatures + line+"\n";					
				line = br.readLine();
				if (Config.meanTriplePatternSelectivity==true)
				queryFeatures = queryFeatures + line+"\n";				
				line = br.readLine();
				if (Config.UNION==true)
				queryFeatures = queryFeatures + line+"\n";					
				line = br.readLine();
				queryFeatures = queryFeatures + line+"\n";				
				line = br.readLine();
				if (Config.ORDERBY==true)
				queryFeatures = queryFeatures + line+"\n";				
				line = br.readLine();
				if (Config.REGEX==true)
				queryFeatures = queryFeatures + line+"\n";				
				line = br.readLine();
				if (Config.LIMIT==true)
				queryFeatures = queryFeatures + line+"\n";				
				line = br.readLine();
				if (Config.OFFSET==true)
				queryFeatures = queryFeatures + line+"\n";
				line = br.readLine();
				if (Config.OPTIONAL==true)
				queryFeatures = queryFeatures + line+"\n";		
				line = br.readLine();
				queryFeatures = queryFeatures + line+"\n";				
				line = java.net.URLDecoder.decode(br.readLine(), "UTF-8");  //query string
				queries.add(queryFeatures+line+"\n");
				queryFeatures="";
				line = br.readLine();  //start of new query
				queriesIds.remove(queryNumber);
			}
			else
				for(int i=0;i<16;i++)
					line = br.readLine();
		}
		return queries;
	}


}
