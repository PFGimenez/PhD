package br4cp;

import java.util.ArrayList;


class NodeDDlast extends NodeDD {	
	
	
	public NodeDDlast(){
		super(null);
		//add=new ArrayList<Structure>();
	}
	
	public NodeDDlast(int id){
		super(null, id);
		//add=new ArrayList<Structure>();
	}
	
	public boolean isLeaf(){
		return true;
	}	
	
	public int count(){
		
		if(fathers.get(0).s.printstr().compareTo("S")!=0){	//pas add
			return 1;
		}else{												//add
			int cpt=fathers.size();
			
			for(int i=0; i<fathers.size(); i++){
				for(int j=0; j<i; j++){
					if(fathers.get(i).s.equals(fathers.get(j).s)){
						cpt--;
						break;
					}
				}
			}
			return cpt;
		}
	}
	
	public String toDot(){
	
		if(fathers.size()>0){
		if(fathers.get(0).s.printstr().compareTo("S")!=0){	//pas add
	   		String s;
			
			//name label form
	   		s="n"+this.id + " [label=";
	    	if(this.isLeaf())
	    		s+="0, shape=box";
	    	else
	    		s+=this.variable.name;//+"_"+this.id;//+"_"+this.kidsdiffbottom();
	    	
	   		s+="];\n";
	    		if(!this.isLeaf()){
	   			for(int i=(this.kids.size()-1); i>=0; i--)
	   				s+=this.kids.get(i).toDot();
	   			//for(int i=(this.fathers.size()-1); i>=0; i--)
	   			//	s+=this.fathers.get(i).toDot2();
	
	   		}
	   		//for(int i=(this.fathers.size()-1); i>=0; i--)
	    		//	s+=this.fathers.get(i).toDot(binary, true);
	   		
	   		return s;
		}else{				//cas add

			int redirect;
			Arc a;
	   		String s="";
			for(int i=0; i<fathers.size(); i++){
				redirect=-1;
				for(int j=0; j<i; j++){
					if(fathers.get(i).s.equals(fathers.get(j).s)){
						redirect=j;
						break;
					}
				}
				if(redirect==-1){	//n'existe pas, on le cree
					//////////todot node/////////
					//name label form
			   		s+="n"+this.id +"_"+((S)fathers.get(i).s).last.toDot()+ " [label="+fathers.get(i).s.toDot()+", shape=box];\n";
					/////////////////////////////
					redirect=i;
				}
				//////////todot arc ////////
						a=this.fathers.get(i);
						if(a.pere!=null){			//si c'est pas le premier arc
							s+="n" + a.pere.id + " -> n" + a.fils.id+"_"+redirect + " [pos="+a.pos;
							if(a.pos==0)
								s+= ", style=dotted";
							if(a.pos==(a.pere.variable.domain-1))
								s+= ", style=dashed";
							s+="];\n";
						}else{					// si c'est le premier arc
							s+="nada -> n" + a.fils.id+"_"+redirect + ";\n";
							s+="nada [label=\" \",shape=plaintext];\n";
						}
					
				}
			return s;
		}
		}else
			return "";
				/////////////////////////////
				
				

	}
		
}
