package Matrix;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.Filter;
import com.hp.hpl.jena.util.iterator.*;
import com.hp.hpl.jena.ontology.impl.OntResourceImpl;

import java.io.PrintStream;
import java.util.*;

public class SetMatrix {

	private final static String ROOTCLASS =        "RootClass";
	private final static String SUBCLASS =         "SubClass";
	private final static String INSTANCE =         "Instance";
	private final static String PROPERTY =         "Property";
	private final static String VALUE =            "Value";
	private final static String TYPE =             "Type";
	private final static String IS_A =             "is a";
	private final static String IS_INSTANCE_OF =   "is instance of";
	private final static String HAS_PROPERTY =     "has property";
	private final static String HAS_VALUE =        "has value";
	private final static String HAS_TYPE =         "has type";
	private final static String HAS_SYNONYM =      "has synonym";
	private final static String IS_NOT_A =         "is not a";

	public int Language;

	public SetMatrix() {
	}

	public SetMatrix(int l) {
		Language = l;
	}

	public void setLanguage(int l) {
		Language = l;
	}
	  
	// find root class
	public Matrix getRootClass(OntModel mod, Matrix matrix) {
		Iterator root_iter = mod.listHierarchyRootClasses().filterDrop(new Filter() {
	      public boolean accept(Object o) {
	        return ( (Resource) o).isAnon();
	      }
	    });
	    while (root_iter.hasNext()) {
	      OntClass rootcls = (OntClass) root_iter.next();
	      String rootcls_name = rootcls.getLocalName();
	      if (matrix.duplicate(rootcls_name) == -1) {
	    	  	matrix.addRowCol(rootcls_name, ROOTCLASS);
	      }
	      getSubClass(rootcls, matrix);

	      //!!Do not find classes, created with restriction

	      //4., 9., 10. ontol., ja Rules - NullPointerException
	    }

	    return matrix;
	  }

	// find subclass
	public void getSubClass(OntClass rootcls, Matrix matrix) {
	    int ii = 0;
	    int i  = 0;
	    int j = 0;
	    boolean duplic = false;
	    String rootcls_name = rootcls.getLocalName();

	    for (Iterator cls_iter = rootcls.listSubClasses(true); cls_iter.hasNext(); ) {
	      OntClass subcls = (OntClass) cls_iter.next();
	      String subcls_name = subcls.getLocalName();
		  matrix.addRowCol(subcls_name, SUBCLASS);
	        
	        for (int col = 0; col < matrix.ConceptList.size(); col++) {
	          ArrayList temp = (ArrayList) (matrix.get(col));
	          if(matrix.ConceptList.get(col).equals(rootcls_name)){
	        	  temp.add("RF");
	          } else 
	          if(matrix.ConceptList.get(col).equals(subcls_name)){
	        	  temp.add("CAND");
	          }else {
	        	  temp.add("0");  
	          }
	          matrix.set(col, temp);
	        }
	        getSubClass(subcls, matrix);
	      }
	    }
	//  }

	public void getSynonClass1(OntModel mod, Matrix matrix, Matrix matrix1, Matrix matrix2) {

	for(int i=0;i<matrix.ConceptList.size();i++){ //kopiruem koncepti v matricu ekvivalentnostej
			   matrix2.addRowCol(matrix.ConceptList.get(i).toString(),matrix.TypeList.get(i).toString());
	}  
	for(Iterator allc = mod.listClasses();allc.hasNext();){
		OntClass cls = (OntClass)allc.next();  
		for (ExtendedIterator eql_iter = cls.listEquivalentClasses(); eql_iter.hasNext(); ) {
			OntClass eqlcls = (OntClass) eql_iter.next();
		    String eqlcls_name = eqlcls.getLocalName();

		    if (! (eqlcls_name == null)) {
		    	if (matrix.duplicate(eqlcls_name) == -1) {
		    		matrix.addRowCol(eqlcls_name, "equivalent");
		    		matrix1.addRowCol(eqlcls_name, "equivalent");
		    		matrix2.addRowCol(eqlcls_name, "equivalent");
		        }
		    }
		}
	} 
		    
	for(Iterator allc = mod.listClasses();allc.hasNext();){ //all classes
		OntClass cls = (OntClass)allc.next();  
		    
	    String cls_name = cls.getLocalName();   
	        
	    System.out.println("Class name: "+cls_name);
	            
		for(ExtendedIterator con = cls.listEquivalentClasses();con.hasNext();){
			OntClass eqvCls = (OntClass) con.next();
	        String eqv_name = eqvCls.getLocalName();  
	                       
	            
	        System.out.println("Ekvivalent class: "+eqv_name);
		    if(con!=null){ //esli estj ekvivalentnie klassi
		    for(int stolbik=0;stolbik<matrix1.ConceptList.size();stolbik++){ // po stolbcam matrici
		    	ArrayList column = (ArrayList) (matrix1.get(stolbik));
		        System.out.println(matrix1.ConceptList.get(stolbik).toString());
		        if(eqv_name!=null){
		        	if((eqv_name.equals(matrix1.ConceptList.get(stolbik).toString()))){
		        		if(column.size()>0){	            			
		        			for(int stolbec=0;stolbec<column.size();stolbec++){   // kazdij element stolbca
		        				System.out.println(cls_name+": "+column.get(stolbec));
		            			if((column.get(stolbec).equals("RF"))){
		            					
		            				for (int col = 0; col < matrix1.ConceptList.size(); col++) {
		            					ArrayList temp = (ArrayList) (matrix1.get(col));
		            					ArrayList temp1 = (ArrayList) (matrix2.get(col));
		            						
		            					if(matrix1.ConceptList.get(col).equals(eqv_name)){
		            						temp1.add("0");
		            						System.out.println("YES");
		            					}else if(matrix1.ConceptList.get(col).equals(cls_name)){
		            						temp1.add("RF");
		            					} else{
		            						temp1.add(temp.get(stolbec));
		            					}	            								            						
		            				}
		            			} // if:
		            		} //for: elementi stolbca	
		            	}// if
		            }// if: klass raven stolbcu v matrix1          		    			
		        }
		    } //for stolbec
		   } // if: esli ekvivalentnie klassi            
		} // for - po ekvivalentnim klassam dannogo klassa
	        
	}  //all classes
		  
	for(int i=0;i<matrix2.size();i++){        // copy matrix
		ArrayList d = (ArrayList)(matrix.get(i));
		ArrayList d1 = (ArrayList)(matrix2.get(i));
		for(int j=0;j<d1.size();j++){
			d.add(d1.get(j).toString());
		}  
	    matrix.set(i,d);
		System.out.println(matrix2.get(i));
	}
}

	  
	public void getSameAsClass1(OntModel mod, Matrix matrix, Matrix matrix1, Matrix matrix2) {
		for(int i=0;i<matrix.ConceptList.size();i++){ //kopiruem koncepti v matricu ekvivalentnostej
			matrix2.addRowCol(matrix.ConceptList.get(i).toString(),matrix.TypeList.get(i).toString());
		}  
		for(Iterator allc = mod.listClasses();allc.hasNext();){
			OntClass cls = (OntClass)allc.next();  
			for (ExtendedIterator eql_iter = cls.listSameAs(); eql_iter.hasNext(); ) {
				OntResourceImpl eqlcls = (OntResourceImpl) eql_iter.next();
			    String eqlcls_name = eqlcls.getLocalName();
			    int i = matrix.getConceptNumber(cls.getLocalName());
			    String concType = matrix.getConceptType(i);

			    if (! (eqlcls_name == null)) {
			    	if (matrix.duplicate(eqlcls_name) == -1) {
			    		matrix.addRowCol(eqlcls_name, "SameAs");
			    		matrix1.addRowCol(eqlcls_name, "SameAs");
			    		matrix2.addRowCol(eqlcls_name, "SameAs");
			        }
			    }
			}
		} 
			    
		for(Iterator allc = mod.listClasses();allc.hasNext();){ //all classes
			OntClass cls = (OntClass)allc.next();  
			    
		    String cls_name = cls.getLocalName();   
		        
		    System.out.println("Class name: "+cls_name);
		            
			for(ExtendedIterator con = cls.listSameAs();con.hasNext();){
				OntResourceImpl eqvCls = (OntResourceImpl) con.next();
		        String eqv_name = eqvCls.getLocalName();  
		                       
		            
		        System.out.println("Ekvivalent class: "+eqv_name);
			    if(con!=null){ //esli estj ekvivalentnie klassi
			    	for(int stolbik=0;stolbik<matrix1.ConceptList.size();stolbik++){ // po stolbcam matrici
			    		ArrayList column = (ArrayList) (matrix1.get(stolbik));
			    		//System.out.println(matrix1.ConceptList.get(stolbik));	
			            if((eqv_name.equals(matrix1.ConceptList.get(stolbik).toString()))){
			            	if(column.size()>0){	            			
			            		for(int stolbec=0;stolbec<column.size();stolbec++){   // kazdij element stolbca
			            			System.out.println(cls_name+": "+column.get(stolbec));
			            			if((column.get(stolbec).equals("RF"))){
			            					
			            				for (int col = 0; col < matrix1.ConceptList.size(); col++) {
			            					ArrayList temp = (ArrayList) (matrix1.get(col));
			            					ArrayList temp1 = (ArrayList) (matrix2.get(col));
			            						
			            					if(matrix1.ConceptList.get(col).equals(eqv_name)){
			            						temp1.add("0");
			            						System.out.println("YES");
			            					}else if(matrix1.ConceptList.get(col).equals(cls_name)){
			            						temp1.add("RF");
			            					} else{
			            						temp1.add(temp.get(stolbec));
			            					}	            								            						
			            				}
			            			} 
			            		} 	
			            	}
			            }
			    			
			    			
			    	}
			    } 
			    	
			} 
		}  
			  
		for(int i=0;i<matrix2.size();i++){        // copy matrix
			//ArrayList temp = (ArrayList)(matrix1.get(i));
			ArrayList d = (ArrayList)(matrix.get(i));
			ArrayList d1 = (ArrayList)(matrix2.get(i));
			for(int j=0;j<d1.size();j++){
				d.add(d1.get(j).toString());
			}  
		    matrix.set(i,d);
			System.out.println(matrix2.get(i));
		}
	}
	  
	  
	// iterator return all classes
	public Iterator getAllClasses(OntModel mod) {
		Iterator i = mod.listClasses()
				.filterDrop(new Filter() {
	        	public boolean accept(Object o) {
	        			return ( (Resource) o).isAnon();
	        	}
	    });
	    return i;
	}

	// find object properties
	public void getObjectProperty1(OntModel mod, Matrix matrix, Matrix matrix1) {
		String rng_name = "";
	    String dmn_name = "";
	    
	    for(int i=0;i<matrix.ConceptList.size();i++){
	    	matrix1.addRowCol(matrix.ConceptList.get(i).toString(),matrix.TypeList.get(i).toString());
		}  


	    for (Iterator prop_iter = mod.listObjectProperties(); prop_iter.hasNext(); ) {
	    	ObjectProperty p = (ObjectProperty) prop_iter.next();
	        if (matrix.duplicate(p.getLocalName()) == -1) {
	        	matrix.addRowCol(p.getLocalName(),	"Link");
	        	matrix1.addRowCol(p.getLocalName(),	"Link");
			            			  			  
	            for (ExtendedIterator dmn_iter = p.listDomain(); dmn_iter.hasNext(); ) {
	            	OntClass dmnCls = (OntClass) dmn_iter.next();
	            	dmn_name = dmnCls.getLocalName();       
	            	for(ExtendedIterator ranges_iter = p.listRange(); ranges_iter.hasNext();){
	            		OntClass rngCls = (OntClass) ranges_iter.next();
	            		rng_name = rngCls.getLocalName();
	    				for (int col = 0; col < matrix.ConceptList.size(); col++) {
	    					ArrayList temp = (ArrayList) (matrix.get(col));
	    				    ArrayList temp1 = (ArrayList) (matrix1.get(col));
	    			    	//    int col_size = temp.size()-1;
	    				    if(matrix.getConceptName(col).equals(p.getLocalName())){
	    				    	temp.add("RF");
	    				     	 temp1.add("RF");
	    				    }  else
	    				    if(matrix.getConceptName(col).equals(dmn_name)){
	    				    	temp.add("CAND");
	    				     	temp1.add("CAND");
	    				    } else 
	    				    if(matrix.getConceptName(col).equals(rng_name)){
	   				     		temp.add("RF");
	   				     		temp1.add("RF");
	    				    } else {

	    				    	temp.add("0");
	    				    	temp1.add("0");
	    				    }
	    				    matrix.set(col, temp);
	    				    matrix1.set(col, temp1);
	    				}                 	
	    			}  
	            }
	        }  
	    }
	}
	  	  
	public boolean hasHiddenClasses(String name) {
		boolean has = false;
		ArrayList HIDDENCLASSES = new ArrayList();
	    HIDDENCLASSES.add("Alt");
	    HIDDENCLASSES.add("AnnotationProperty");
	    HIDDENCLASSES.add("Bag");
	    HIDDENCLASSES.add("Class");
	    HIDDENCLASSES.add("Container");
	    HIDDENCLASSES.add("ContainerMembershipProperty");
	    HIDDENCLASSES.add("DataRange");
	    HIDDENCLASSES.add("Datatype");
	    HIDDENCLASSES.add("DatatypeProperty");
	    HIDDENCLASSES.add("FunctionalProperty");
	    HIDDENCLASSES.add("InverseFunctionalProperty");
	    HIDDENCLASSES.add("List");
	    HIDDENCLASSES.add("Literal");
	    HIDDENCLASSES.add("Nothing");
	    HIDDENCLASSES.add("ObjectProperty");
	    HIDDENCLASSES.add("Ontology");
	    HIDDENCLASSES.add("OntologyProperty");
	    HIDDENCLASSES.add("Property");
	    HIDDENCLASSES.add("Resource");
	    HIDDENCLASSES.add("Restriction");
	    HIDDENCLASSES.add("Seq");
	    HIDDENCLASSES.add("Statement");
	    HIDDENCLASSES.add("SymmetricProperty");
	    HIDDENCLASSES.add("Thing");
	    HIDDENCLASSES.add("TransitiveProperty");
	    HIDDENCLASSES.add("XMLLiteral");
	    if (HIDDENCLASSES.contains(name))
	    	has = true;

	    return has;
	}
	  
	public void getDatatypeProperty1(OntModel mod, Matrix matrix, Matrix matrix1) {
		//			 String value = "";
		OntClass dmnCls;
		OntClass rngCls;
		String dmn_name = "";
		String rng_name = "";
		String value_name = "";

		for(int i=0;i<matrix.ConceptList.size();i++){
			matrix1.addRowCol(matrix.ConceptList.get(i).toString(),matrix.TypeList.get(i).toString());
		}  
			    
		for (Iterator prop_iter = mod.listDatatypeProperties(); prop_iter.hasNext(); ) {
			DatatypeProperty p = (DatatypeProperty) prop_iter.next();
			if (matrix1.duplicate(p.getLocalName()) == -1) {
				matrix.addRowCol(p.getLocalName(),	PROPERTY);
				matrix1.addRowCol(p.getLocalName(),	PROPERTY);
			}
		}  
					       				  			     
		for (Iterator prop_iter = mod.listDatatypeProperties(); prop_iter.hasNext(); ) {
			DatatypeProperty p = (DatatypeProperty) prop_iter.next();
					  
			for (ExtendedIterator dmn_iter = p.listDomain(); dmn_iter.hasNext(); ) {
				dmnCls = (OntClass) dmn_iter.next();
			    dmn_name = dmnCls.getLocalName();                     
			    for (int stolbik = 0; stolbik < matrix1.ConceptList.size(); stolbik++) {                 // po stolbikam
			    	ArrayList column = (ArrayList) (matrix1.get(stolbik));
			        if((dmn_name.equals(matrix1.ConceptList.get(stolbik).toString()))){
			        	if(column.size()>0){
			            			
			            for(int stolbec=0;stolbec<column.size();stolbec++){   // kazdij element stolbca
			            	if((column.get(stolbec).equals("RF"))){
			            		for (int col = 0; col < matrix1.ConceptList.size(); col++) {
			            			ArrayList temp = (ArrayList) (matrix1.get(col));
			            			int col_size = temp.size()-1;
			            			if(matrix1.getConceptName(col).equals(p.getLocalName())){
			            				temp.set(stolbec,"CAND");
			            			}  else
			            			if(matrix1.getConceptName(col).equals(dmn_name)){
			            				temp.set(stolbec,"RF");
			            			} else {
			            			}
			            			matrix1.set(col, temp);
			            		}
			            		break;
			            					
			            	} else if(stolbec==column.size()-1){
			            		for (int col = 0; col < matrix1.ConceptList.size(); col++) {
			            			ArrayList temp = (ArrayList) (matrix1.get(col));
			            			if(matrix1.getConceptName(col).equals(p.getLocalName())){
			            				temp.add("CAND");
			            			}  else
			            			if(matrix1.getConceptName(col).equals(dmn_name)){
			            				temp.add("RF");
			            			} else {
			            				temp.add("0");
			            			}
			            			matrix1.set(col, temp); 
			            						
			            		}  
			            		break;	
			            	}// if column.get(stolbec) 
			            } // kazdij element stolbca
			        }  else	{
			        	//System.out.println("3-oe pravilo: "+ "p: "+p.getLocalName());
	            		for (int col = 0; col < matrix1.ConceptList.size(); col++) {
	            			ArrayList temp = (ArrayList) (matrix1.get(col));
	            			//    int col_size = temp.size()-1;
	            			if(matrix1.getConceptName(col).equals(p.getLocalName())){
	            				temp.add("CAND");
	            			}  else
	            			if(matrix1.getConceptName(col).equals(dmn_name)){
	            				temp.add("RF");
	            			} else {
	            			temp.add("0");
	            		}
	            		matrix1.set(col, temp);
	            	} 
	            						
			        break;
			    }
			} //if	
			//-----------------------------------------                   	
		}	// stolbik for	
			            
	}

			    }
	          	
			    for(int i=0;i<matrix1.size();i++){        // kopiruem matricu
			    	ArrayList d = (ArrayList)(matrix.get(i));
			    	ArrayList d1 = (ArrayList)(matrix1.get(i));
			    	for(int j=0;j<d1.size();j++){
			    	  d.add(d1.get(j).toString());
			    	}  
		    	     matrix.set(i,d);
			    }
		 }  
	  
	// find object properties
	public void getComplementClass(OntModel mod, Matrix matrix, Matrix matrix1) {
		String rng_name = "";
	    String dmn_name = "";
	      
	    for(int i=0;i<matrix.ConceptList.size();i++){
	    	matrix1.addRowCol(matrix.ConceptList.get(i).toString(),matrix.TypeList.get(i).toString());
	  	}  


	    for (Iterator prop_iter = mod.listComplementClasses(); prop_iter.hasNext(); ) {
	    	OntClass p = (OntClass) prop_iter.next();
	    	  
	    	System.out.println("Atrasta �pa��ba - " + p.getLocalName());
	    	System.out.println(p.asComplementClass().getOperand().getLocalName());
	    	if (matrix.duplicate(p.getLocalName()) == -1) {
	  		  	matrix.addRowCol(p.getLocalName(),	"ComplementClass");
	  		  	matrix1.addRowCol(p.getLocalName(),	"ComplementClass");
	        }
	    }             
	  		 	  		    
	    for (ExtendedIterator dmn_iter =mod.listComplementClasses(); dmn_iter.hasNext(); ) {
	    	OntClass dmnCls = (OntClass)dmn_iter.next();
	        dmn_name = dmnCls.asComplementClass().getOperand().getLocalName(); 

	      	for (int col = 0; col < matrix.ConceptList.size(); col++) {
	      		ArrayList temp = (ArrayList) (matrix.get(col));
	      		ArrayList temp1 = (ArrayList) (matrix1.get(col));
 				   
	      	    if(matrix.getConceptName(col)!=null){
	      	    	if(matrix.getConceptName(col).equals(dmn_name)){
	      	    		temp.add("RNOT");
	      				temp1.add("RNOT");
	      			}  else
	      			if(matrix.getConceptName(col).equals(dmnCls.getLocalName())){
	      				temp.add("CNOT");
	      				temp1.add("CNOT");    				     
	      			} else {

	      				temp.add("0");
	      				temp1.add("0");
	      			}
	      				matrix.set(col, temp);
	      				matrix1.set(col, temp1);
	      	        } 
	      		}//for col           	                   
	                
	        }
	    }  	

}
