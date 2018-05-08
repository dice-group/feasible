package org.aksw.simba.benchmark;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.aksw.simba.benchmark.Fathom.Stats;
import org.openrdf.query.MalformedQueryException;
/**
 * This is just a dummy class we used for reading various files 
 * @author Saleem
 *
 */
@SuppressWarnings("unused")
public class Reader {

	public static void main(String[] args) throws IOException, MalformedQueryException {
		String dirStr = "D:/LSQ/fed_logs/networks/";
		File dir = new File(dirStr);
		File[] listOfQueryLogs = dir.listFiles();
		int count = 0,epp=0;
	 	//BufferedWriter bw= new BufferedWriter(new FileWriter("D:/LSQ/fixed1-cu.sgd.bio2rdf.org.csv"));
		for (File queryLogFile : listOfQueryLogs)
		{
			//System.out.println(queryLogFile.getName()+ ": in progress...");
			
			File catalinaOut = new File(System.getProperty("catalina.base"), queryLogFile.getName());
			
			BufferedReader br = new BufferedReader(new FileReader(dirStr+catalinaOut));
			String line;
			List<String> rankedServices = new  ArrayList<String> ();
			long faID = 1 , faIDnew = 0 ;
			br.readLine();
			System.out.print("faID\twords\timages\treferences\tFlesch-Kincaid reading ease score\tFlesch-Kincaid grade level score\tFog index");
			while ((line = br.readLine()) != null   )
			{

				String prts [] = line.split("\t");
				System.out.print("\n"+prts[0]);
				String wcount [] = prts[1].split(" ");
				int words = wcount.length-1;
				System.out.print("\t"+(wcount.length-1));
				prts[1] = prts[1].toLowerCase();
				wcount  = prts[1].split("image:");
				int imgCount = wcount.length-1;
				//int imgCount = 0 ;
			if(wcount.length<2)
				{
				prts[1] = prts[1].toLowerCase();	
				wcount  = prts[1].split("image =");
					 imgCount = (wcount.length-1);
				}
				if(wcount.length<2)
				{
					prts[1] = prts[1].toLowerCase();
					wcount  = prts[1].split("file:");
					 imgCount = (wcount.length-1);
				}
				if(wcount.length<2)
				{
					prts[1] = prts[1].toLowerCase();
					wcount  = prts[1].split("image_file");
					 imgCount = (wcount.length-1);
				}
				if(wcount.length<2)
				{
					prts[1] = prts[1].toLowerCase();
					wcount  = prts[1].split("image=");
					 imgCount = (wcount.length-1);
				}
				if(wcount.length<2)
				{
					prts[1] = prts[1].toLowerCase();
					wcount  = prts[1].split("image_skyline");
					 imgCount = (wcount.length-1);
				}
				
				if(wcount.length<2)
				{
					prts[1] = prts[1].toLowerCase();
					wcount  = prts[1].split(".jpg");
					 imgCount = (wcount.length-1);
					 wcount  = prts[1].split(".png");
					 imgCount = imgCount+(wcount.length-1);
					 wcount  = prts[1].split(".jpeg");
					 imgCount = imgCount+(wcount.length-1);
					 wcount  = prts[1].split(".svg");
					 imgCount = imgCount+(wcount.length-1);
					 wcount  = prts[1].split(".gif");
					 imgCount = imgCount+(wcount.length-1);
									}
				System.out.print("\t"+imgCount);
				prts[1] = prts[1].toLowerCase();
				wcount = prts[1].split("url=");
				System.out.print("\t"+ (wcount.length-1));
//				int sentences = prts[1].split("[!?.:]+").length;
//				int sylabals = (prts[1].length() - prts[1].replaceAll("a|e|i|o|u", "").length());
//				double prtA = (1.015 * (words/sentences));
//				double prtB = (84.6 *(sylabals/words)) ;
//                double readability = 206.836 - prtA - prtB;
//			//	System.out.print("\t"+ sentences);
//			//	System.out.print("\t"+ sylabals);
//			//	System.out.print("\t"+ readability);
				Stats s = Fathom.analyze(prts[1]);
				  System.out.print("\t"+Readability.calcFlesch(s));
				  System.out.print("\t"+Readability.calcKincaid(s));
				  System.out.print("\t"+Readability.calcFog(s));
			//	if(imgCount >1 )
			//				System.out.println(prts[1]);
				count++;
			}
			br.close();
		}
		//System.out.println(count+" and "+ epp);
		//bw.close();
	}
}