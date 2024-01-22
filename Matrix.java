package Matrix;

import java.util.ArrayList;

public class Matrix extends ArrayList {
	public ArrayList ConceptList = new ArrayList();
	ArrayList TypeList = new ArrayList();

	//get relation name
	public String get(int i, int j) { 
		return (String) ( ( (ArrayList) (this.get(j))).get(i));
	}

	//set relation name
	public void set(int i, int j, String value) {
		ArrayList temp = (ArrayList) (this.get(j));
		temp.set(i, value);
		this.set(j, temp);
	}

	// add new relation and its type
	public void addRowCol(String concept, String concType) {
		ConceptList.add(concept);
	    TypeList.add(concType);

	    // 2.create new column
	    ArrayList m = new ArrayList();
	    int m1_size=0;

	    try
	    {
	    	ArrayList m1 = (ArrayList)this.get(0);
	    	m1_size=m1.size();
	     
	    } catch (Exception e5){
	    	m1_size=0;
	    }
	   
	    for (int i = 0; i < m1_size; i++) {
	    	m.add("0");
	    }
	      
	    // add column
	    this.add(m);
	}

	// return concept index
	public int getConceptNumber(String concName) {
		int i=0;
		for (i = 0; i < ConceptList.size(); i++) {
		//  System.out.println("i= "+i);
			if ( ( (String) (ConceptList.get(i))).equals(concName))  break;
		}
        return i;
	}

	// get concept index (many concepts are equal)
	public int getConceptNumberLast(String concName) {
	    int k = -1;
	    for (int i = 0; i < ConceptList.size(); i++) {
	    //	    System.out.println("i= "+i);
	    	if ( ( (String) (ConceptList.get(i))).equals(concName)) {
	    		k = i;
	        }
	    }
	    return k;
	}

	// return concept name
	public String getConceptName(int i) {
		String concept_name = ConceptList.get(i).toString();
	    return concept_name;
	}

	// return concept type
	public String getConceptType(int i) {
		String concept_type = TypeList.get(i).toString();
	    return concept_type;
	}

	// set concept type
	public void setConceptType(int i, String concType){
		TypeList.set(i,concType);
	}


	// return relation quantity of concept
	public int getLinkNumber(int i) {
		int link_number = 0;
	    String link_name = "";
	    for (int j = 0; j < this.size(); j++) {
	    	link_name = this.get(i, j);
	        if (!link_name.equals("0")) {
	        	link_number++;
	        }
	    }
	    return link_number;
	}

	public void print() {
		for (int i = 0; i < this.size(); i++) {
			System.out.print( (String) (ConceptList.get(i)) + " ");
			System.out.print( (String) (TypeList.get(i)));
			for (int j = 0; j < this.size(); j++) {
				System.out.print("\t" + (String) ( ( (ArrayList) (this.get(j))).get(i)));
			}
			System.out.println();
		}
	}

	// corrects names (delete indexes to concepts and _)
	public void refine() {
		String label="";
	    int i=0;
	    int j=0;
	    for (i = 0; i < this.size(); i++) {
	    	for (j = 0; j < this.size(); j++) {
	    		label = this.get(i, j);
	    		label=label.replace('_',' ');
	    		int ind=label.lastIndexOf(' ');
	    		// find out if is " "
	    		if (ind > -1) {
	    			// finds substring after last " "
	    			String sub = label.substring(ind + 1);
	    			// if not digits
	    			for (int k = 0; k < 10; k++) {
	    				if (sub.indexOf(Integer.toString(k)) > -1) {
	    					label = label.substring(0, ind);
	    					this.set(i, j, label);
	    				}
	    			}
	    		}	
	    		this.set(i,j,label);
	    	}
	    	String name=this.getConceptName(i).toString();
	    	name=name.replace('_',' ');
	    	ConceptList.set(i, name) ;
	    }
	}

	public void delete(int ind) {
		ConceptList.remove(ind);
	    TypeList.remove(ind);

	    //1.deletes rows one by one
	    for (int j = 0; j < this.size(); j++) {
	    	ArrayList temp = (ArrayList) (this.get(j));
	    	temp.remove(ind);
	    	this.set(j, temp);
	    }
	    this.remove(ind);
	}

	  // detects if concept is added to matrix
	  public int duplicate(String concept_name) {
		  int dupl = -1; //-1 not added, another - concept index
	      for (int ii = 0; ii < this.size(); ii++) {
	    	  if (this.getConceptName(ii).equals(concept_name)) {
	    		  dupl = ii;
	              break;
	          }
	          else
	        	  dupl = -1;
	      }
	      return dupl;
	  }
}	


