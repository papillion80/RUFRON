package Rufron;


import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import java.util.ArrayList;
//import O.ExtFileFilter;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import Matrix.Matrix;
import Matrix.SetMatrix;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.rdf.model.* ;
import com.hp.hpl.jena.shared.*;


import java.lang.NullPointerException;


public class Rufron {

	private static final long serialVersionUID = 1L;

	
	File curDir = null;
	

	DefaultListModel listModel = new DefaultListModel();  //  @jve:decl-index=0:visual-constraint="1145,3"
	
	DefaultTableModel tableModel = new DefaultTableModel();  //  @jve:decl-index=0:visual-constraint="1139,44"


	public void generateRulesFromOntology(String ont) {

		//curDir = f.getCurrentDirectory();	
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		m.read("file:" + ont);
							
		listModel.clear();
					
		tableModel.getDataVector().removeAllElements();
							

		Matrix jtMatrix = new Matrix();  
		Matrix cpropMatrix = new Matrix(); 
		Matrix ekvclass = new Matrix(); 
		Matrix sameasclass = new Matrix(); 
		Matrix links = new Matrix(); 
		Matrix negations = new Matrix(); 

		jtMatrix = new SetMatrix().getRootClass(m, jtMatrix);  
	

		new SetMatrix().getDatatypeProperty1(m, jtMatrix, cpropMatrix);   

		new SetMatrix().getSynonClass1(m, jtMatrix, cpropMatrix, ekvclass);

		new SetMatrix().getSameAsClass1(m, jtMatrix, cpropMatrix, sameasclass);

		new SetMatrix().getObjectProperty1(m, jtMatrix, links);

		new SetMatrix().getComplementClass(m, jtMatrix, negations);


		String concepts = "";
		tableModel.addColumn("Nr.");
												
		System.out.println("Conceptu skaits: "+jtMatrix.ConceptList.size());
		for(int i=0;i<jtMatrix.size();i++){
			concepts=concepts+jtMatrix.getConceptName(i)+ "--";		
			tableModel.addColumn(jtMatrix.getConceptName(i));
		}
		listModel.addElement(concepts);

		for(int i=0;i<jtMatrix.size()-1;i++){
			ArrayList a = (ArrayList)jtMatrix.get(i);	
			listModel.addElement(a);							  									  					  
		}

		ArrayList r = (ArrayList)jtMatrix.get(0);
		int row_count = r.size();
		int col_count = jtMatrix.ConceptList.size();
		int row = 0;
		int col = 0;

		while(row<row_count){
			col = 0;
			Vector<String> vector = new Vector<String>();
			vector.add(Integer.toString(row));
			while(col<jtMatrix.ConceptList.size()){
				ArrayList comp = (ArrayList)jtMatrix.get(col);

				vector.add((String)comp.get(row));
	  			col++;  
			}
			tableModel.addRow(vector);
  			row++;
		}	

	}


}

