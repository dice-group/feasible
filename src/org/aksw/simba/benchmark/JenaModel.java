package org.aksw.simba.benchmark;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Just a dummy class to load rdf into jena model
 * @author Saleem
 *
 */
public class JenaModel {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel() ;
		//model.read("D:/LGDLSQDump/LSQ-LGD.ttl") ;
		model.read("C:/a.ttl") ;
			}

}
