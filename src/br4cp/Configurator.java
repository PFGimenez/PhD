package br4cp;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public interface Configurator {
		// FONCTIONS STANDARDS //
	
/*	VDD compil(ArrayList<String> file_names, boolean arg_plus, int arg_heuristique, int arg_heuristique_cons, int arg_affich_text);
	VDD charg(String arg_read, int arg_affich_text);
	void reinitialisation(VDD x);

	void transformation(VDD x, String forme_finale);
	int nb_nodes(VDD x);
	int nb_edges(VDD x);
	int nb_models(VDD x);*/
	
//	void assignAndPropagate(String var, String val);			//fake
//	void unassignAndRestore(String var);						//fake only

//	void initialize(); 											// consistance max min

//	int minCost();												// fake only
//	Map<String, String> minCostConfiguration();					// fake only
//	int maxCost();												// fake only
//	Map<String, String> maxCostConfiguration();					// fake only

//	int getSizeOfCurrentDomainOf(String var);					// fake only

//boolean isPresentInCurrentDomain(String var, String val);		// fake only

//Set<String> getCurrentDomainOf(String var);					// fake only

//Map<String, Integer> minCosts(String var); 					// fake only
//Map<String, Integer> maxCosts(String var); 					// fake only

//Set<String> getFreeVariables(); 

//boolean isConfigurationComplete(); 

//boolean isPossiblyConsistent(); 
	
	
		// FONCTIONS BR4CP //
	/**
	 * Read a configuration file. Both the xml format and the textual format
	 * will be provided. It is up to the solver to choose which format to use.
	 * Note that the prices are expected to be found in a file with a "_prices"
	 * postfix.
	 * 
	 * 
	 * @param problemName
	 *            the path to the problem, without the extension (.xml or .txt)
	 */
	void readProblem(String problemName);
	
	/**
	 * Such method should be used by the configurator to perform the tasks on the configuration problem
	 * before the first user choice. 
	 * This method can be used for instance to maintain GIC on the initial configuration problem.
	 * This method MUST BE called after {@link #readProblem(String)} and before any other method of the interface.
	 */
	void initialize();
	
	/**
	 * Assign a specific value to a variable.
	 * 
	 * @param var
	 * @param val
	 * @return true iff the assignment can be done
	 * @pre getCurrentDomainOf(var).contains(val)
	 */
	void assignAndPropagate(String var, String val);

	/**
	 * Unassign a specific variable
	 * 
	 * @param var
	 */
	void unassignAndRestore(String var);

	/**
	 * Get the minimal price of the configurations compatible with the current
	 * choices.
	 * 
	 * @return the cost of the configuration
	 */
	int minCost();

	/**
	 * Provide a full configuration of minimal cost.
	 * 
	 * @return a full assignment var->value of minimal cost (given by {@link #minCost()}
	 */
	Map<String, String> minCostConfiguration();

	/**
	 * Get the maximal price of the configurations compatible with the current
	 * choices.
	 * 
	 * @return the cost of the configuration
	 */
	int maxCost();
	
	/**
	 * Provide a full configuration of maximal cost.
	 * 
	 * @return a full assignment var->value of maximal cost (given by {@link #maxCost()}
	 */
	Map<String, String> maxCostConfiguration();

	/**
	 * @inv getSizeOfCurrentDomain(var) == getCurrentDomainOf(var).size()
	 */
	int getSizeOfCurrentDomainOf(String var);

	/**
	 * 
	 * @param var
	 * @param val
	 * @return
	 * @inv isCurrentInCurrentDomain(var,val)==
	 *      getCurrentDomainOf(var).contains(val)
	 */
	boolean isPresentInCurrentDomain(String var, String val);

	Set<String> getCurrentDomainOf(String var);

	/**
	 * Retrieve for each valid value of the variable the minimal cost of the
	 * configuration.
	 * 
	 * @param var
	 *            a variable id
	 * @return a map value->mincost
	 */
	Map<String, Integer> minCosts(String var); 

	/**
	 * Retrieve for each valid value of the variable the maximal cost of the
	 * configuration.
	 * 
	 * @param var
	 *            a variable id
	 * @return a map value->maxcost
	 */
	Map<String, Integer> maxCosts(String var); 

	/**
	 * Get all unassigned variables.
	 * 
	 * @return a set of non assigned variables.
	 */
	Set<String> getFreeVariables(); 

	/**
	 * Check that there is no more choice for the user.
	 * 
	 * @return true iff there is exactly one value left per variable.
	 */
	boolean isConfigurationComplete(); 

	/**
	 * Check there there is at least one value in each domain. Note that
	 * depending of the level of consistency used, the configuration may of may
	 * not be finally consistent.
	 * 
	 * @return true iff there is at least one value left per variable.
	 */
	boolean isPossiblyConsistent(); 

	Set<String> getAlternativeDomainOf(String var);
	
	
	
	
}
