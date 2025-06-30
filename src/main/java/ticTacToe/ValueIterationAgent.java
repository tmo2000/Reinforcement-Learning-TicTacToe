package ticTacToe;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Value Iteration Agent, only very partially implemented. The methods to implement are: 
 * (1) {@link ValueIterationAgent#iterate}
 * (2) {@link ValueIterationAgent#extractPolicy}
 * 
 * You may also want/need to edit {@link ValueIterationAgent#train} - feel free to do this, but you probably won't need to.
 * @author ae187
 *
 */
public class ValueIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states
	 */
	Map<Game, Double> valueFunction=new HashMap<Game, Double>();
	
	/**
	 * the discount factor
	 */
	double discount=0.9;
	
	/**
	 * the MDP model
	 */
	TTTMDP mdp=new TTTMDP();
	
	/**
	 * the number of iterations to perform - feel free to change this/try out different numbers of iterations
	 */
	int k=50;
	
	
	/**
	 * This constructor trains the agent offline first and sets its policy
	 */
	public ValueIterationAgent()
	{
		super();
		mdp=new TTTMDP();
		this.discount=0.9;
		initValues();
		train();
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public ValueIterationAgent(Policy p) {
		super(p);
		
	}

	public ValueIterationAgent(double discountFactor) {
		
		this.discount=discountFactor;
		mdp=new TTTMDP();
		initValues();
		train();
	}
	
	/**
	 * Initializes the {@link ValueIterationAgent#valueFunction} map, and sets the initial value of all states to 0 
	 * (V0 from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.valueFunction.put(g, 0.0);
		
		 
		
	}
	
	
	
	public ValueIterationAgent(double discountFactor, double winReward, double loseReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		mdp=new TTTMDP(winReward, loseReward, livingReward, drawReward);
	}
	
	/**
	 
	
	/*
	 * Performs {@link #k} value iteration steps. After running this method, the {@link ValueIterationAgent#valueFunction} map should contain
	 * the (current) values of each reachable state. You should use the {@link TTTMDP} provided to do this.
	 * 
	 *
	 */
	
	
	public void iterate()
	{	
		for (int i = 0; i < k; i++) {
			// Create a temporary map to store updated values
		    Map<Game, Double> updatedValueFunction = new HashMap<>();
			
			//iterate over all game states
			for(Game state : valueFunction.keySet()) {
				// Check if the state is terminal
	            if (mdp.isTerminal(state)) {
	                updatedValueFunction.put(state, 0.0);
	                continue;
	            }
	            
	            double maxUtility = Double.NEGATIVE_INFINITY;
	            
	            for (Move move : state.getPossibleMoves()) {
		            double expectedUtility = 0.0;
		            // Get transition probabilities for each move
		            List<TransitionProb> transitions = mdp.generateTransitions(state, move);
                
	                // Compute expected utility based on transitions
	                for (TransitionProb transition : transitions) {
	                	Game nextState = transition.outcome.sPrime;
	                	double probability = transition.prob;
	                	double reward = transition.outcome.localReward;
                    
	                	// Expected utility calculation uing bellman equation
	                	expectedUtility += probability * (reward + discount * valueFunction.getOrDefault(nextState, 0.0));

	                }
	                
	                // Find the max expected utility for this state
	                maxUtility = Math.max(maxUtility, expectedUtility);
	            }
	            // Store the updated max utility for this state
	            updatedValueFunction.put(state, maxUtility);
			}
			 // Update main value function after each iteration
	        valueFunction.putAll(updatedValueFunction);
		 }
		}
	
	/**This method should be run AFTER the train method to extract a policy according to {@link ValueIterationAgent#valueFunction}
	 * You will need to do a single step of expectimax from each game (state) key in {@link ValueIterationAgent#valueFunction} 
	 * to extract a policy.
	 * 
	 * @return the policy according to {@link ValueIterationAgent#valueFunction}
	 */
	public Policy extractPolicy()
	{
		Policy policy = new Policy();
		
		for (Game state : valueFunction.keySet()) {
			if (mdp.isTerminal(state))
				 // No action for terminal states
		          continue;
			
			Move bestMove = null;
	        double maxUtility = Double.NEGATIVE_INFINITY;
	        
	        // Evaluate possible moves
	        for (Move move : state.getPossibleMoves()) {
	            double expectedUtility = 0.0;
	            
	            // Get transition probabilities for each move
	            List<TransitionProb> transitions = mdp.generateTransitions(state, move);
	            // Calculate expected utility
	            for (TransitionProb transition : transitions) {
	                Game nextState = transition.outcome.sPrime;
	                double probability = transition.prob;
	                double reward = transition.outcome.localReward;
	                
	             // Expected utility calculation
                	expectedUtility += probability * (reward + discount * valueFunction.getOrDefault(nextState, 0.0));     
	            }
	            
	            if (expectedUtility > maxUtility) {
	            	maxUtility = expectedUtility;
	            	
	            	bestMove = move;
	            }
	            
	        }

	        if (bestMove != null) {
	        	 policy.policy.put(state, bestMove);
	        }
		}
		
		return policy;
	}
	
	/**
	 * This method solves the mdp using your implementation of {@link ValueIterationAgent#extractPolicy} and
	 * {@link ValueIterationAgent#iterate}. 
	 */
	public void train()
	{
		/**
		 * First run value iteration
		 */
		this.iterate();
		/**
		 * now extract policy from the values in {@link ValueIterationAgent#valueFunction} and set the agent's policy 
		 *  
		 */
		
		super.policy=extractPolicy();
		
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the iterate() & extractPolicy() methods");
			//System.exit(1);
		}
		
		
		
	}

	public static void main(String[] args) throws IllegalMoveException {
		    if (args.length < 4) {
		        System.out.println("Usage: java target/classes/ticTacToe.Game -x vi -o <opponent> -s <starting_player>");
		        return;
		    }

		    // Parse command-line arguments
		    String opponentType = args[6]; // Opponent type: random, aggressive, or defensive
		    char startingPlayer = args[8].charAt(0); // Starting player: 'x' or 'o'

		  
		    // Create ValueIterationAgent
		    ValueIterationAgent agent = new ValueIterationAgent();

		    // Initialize opponent agent
		    Agent opponent;
		    switch (opponentType.toLowerCase()) {
		        case "random":
		            opponent = new RandomAgent();
		            break;
		        case "aggressive":
		            opponent = new AggressiveAgent();
		            break;
		        case "defensive":
		            opponent = new DefensiveAgent();
		            break;
		        default:
		            System.out.println("Invalid opponent type. Use 'random', 'aggressive', or 'defensive'.");
		            return;
		    }

		    // To record the results
		    int wins = 0, losses = 0, draws = 0;

		    // Run 50 games
		    for (int i = 0; i < 50; i++) {
		        // Decide who starts
		        Game game = (startingPlayer == 'x') ? new Game(agent, opponent, agent) : new Game(agent, opponent, opponent);

		        game.playOut(); // Simulate the game

		        // Record results
		        if (game.state == Game.X_WON) {
		            wins++;
		        } else if (game.state == Game.O_WON) {
		            losses++;
		        } else if (game.state == Game.DRAW) {
		            draws++;
		        }
		    }

		    // Output results
		    System.out.println("Results after 50 games against " + opponentType + " agent:");
		    System.out.println("Wins: " + wins);
		    System.out.println("Losses: " + losses);
		    System.out.println("Draws: " + draws);
		}
	}