package ticTacToe;

import java.util.List;
import java.util.Random;

/**
 * A Q-Learning agent with a Q-Table, i.e. a table of Q-Values. This table is implemented in the {@link QTable} class.
 * 
 *  The methods to implement are: 
 * (1) {@link QLearningAgent#train}
 * (2) {@link QLearningAgent#extractPolicy}
 * 
 * Your agent acts in a {@link TTTEnvironment} which provides the method {@link TTTEnvironment#executeMove} which returns an {@link Outcome} object, in other words
 * an [s,a,r,s']: source state, action taken, reward received, and the target state after the opponent has played their move. You may want/need to edit
 * {@link TTTEnvironment} - but you probably won't need to. 
 * @author ae187
 */

/**
 * @author sunil
 *
 */
public class QLearningAgent extends Agent {

	/**
	 * The learning rate, between 0 and 1.
	 */
	double alpha = 0.1;

	/**
	 * The number of episodes to train for
	 */
	int numOfEpisodes = 10000;

	/**
	 * The discount factor (gamma)
	 */
	double discount = 0.9;

	/**
	 * The epsilon in the epsilon greedy policy used during training.
	 */
	double epsilon = 0.1;

	/**
	 * This is the Q-Table. To get an value for an (s,a) pair, i.e. a (game, move)
	 * pair.
	 * 
	 */

	QTable qTable = new QTable();

	/**
	 * This is the Reinforcement Learning environment that this agent will interact
	 * with when it is training. By default, the opponent is the random agent which
	 * should make your q learning agent learn the same policy as your value
	 * iteration and policy iteration agents.
	 */
	TTTEnvironment env = new TTTEnvironment();

	/**
	 * Construct a Q-Learning agent that learns from interactions with
	 * {@code opponent}.
	 * 
	 * @param opponent     the opponent agent that this Q-Learning agent will
	 *                     interact with to learn.
	 * @param learningRate This is the rate at which the agent learns. Alpha from
	 *                     your lectures.
	 * @param numEpisodes  The number of episodes (games) to train for
	 */
	public QLearningAgent(Agent opponent, double learningRate, int numEpisodes, double discount) {
		env = new TTTEnvironment(opponent);
		this.alpha = learningRate;
		this.numOfEpisodes = numEpisodes;
		this.discount = discount;
		initQTable();
		train();
	}

	/**
	 * Initialises all valid q-values -- Q(g,m) -- to 0.
	 * 
	 */

	protected void initQTable() {
		List<Game> allGames = Game.generateAllValidGames('X');// all valid games where it is X's turn, or it's terminal.
		for (Game g : allGames) {
			List<Move> moves = g.getPossibleMoves();
			for (Move m : moves) {
				this.qTable.addQValue(g, m, 0.0);
				// System.out.println("initing q value. Game:"+g);
				// System.out.println("Move:"+m);
			}

		}

	}

	/**
	 * Uses default parameters for the opponent (a RandomAgent) and the learning
	 * rate (0.2). Use other constructor to set these manually.
	 */
	public QLearningAgent() {
		this(new RandomAgent(), 0.1, 100000, 0.9);

	}

	/**
	 * Implement this method. It should play {@code this.numEpisodes} episodes of
	 * Tic-Tac-Toe with the TTTEnvironment, updating q-values according to the
	 * Q-Learning algorithm as required. The agent should play according to an
	 * epsilon-greedy policy where with the probability {@code epsilon} the agent
	 * explores, and with probability {@code 1-epsilon}, it exploits.
	 * 
	 * At the end of this method you should always call the {@code extractPolicy()}
	 * method to extract the policy from the learned q-values. This is currently
	 * done for you on the last line of the method.
	 */

	public void train() {

		// Loop through each training episode
		for (int i = 0; i < numOfEpisodes; i++) {
			// Continue to play the game until a terminal state is reached
			while (!this.env.isTerminal()) {

				// Get the current game state
				Game g = this.env.getCurrentGameState();

				// If the game state is already terminal, skip to the next iteration
				if (g.isTerminal()) {
					continue;
				}

				// Pick a move using an epsilon-greedy strategy
				Move m = epsilonMove(g);
				Outcome outcome = null;

				try {
					// Execute the chosen move and get the outcome
					outcome = this.env.executeMove(m);
				} catch (IllegalMoveException e) {
					// Handle an exception if the move is illegal
					e.printStackTrace();
				}

				// Retrieve the Q-value for the current state and move
				double qvalue = this.qTable.getQValue(outcome.s, outcome.move);
				double newqvalue;

				// Updated Q(g, m) = (1 - alpha) * old Q(g, m) + alpha * (reward + discount *
				// maxQvalue(g'))
				newqvalue = (1 - this.alpha) * qvalue
						+ this.alpha * (outcome.localReward + this.discount * maxQvalue(outcome.sPrime));

				// Store the updated Q-value in the Q-table
				this.qTable.addQValue(outcome.s, outcome.move, newqvalue);
			}

			// Reset the environment for the next episode
			this.env.reset();
		}

		// --------------------------------------------------------
		// you shouldn't need to delete the following lines of code.
		this.policy = extractPolicy();
		if (this.policy == null) {
			System.out.println("Unimplemented methods! First implement the train() & extractPolicy methods");
			// System.exit(1);
		}

	}

	
	// Helper method to get the Q-value of sPrime
	private Double maxQvalue(Game gPrime) {
		// If the game state is terminal, return a Q-value of 0
		if (gPrime.isTerminal()) {
			return 0.0;
		}

		double max = -Integer.MAX_VALUE; // Initialize max to a very low value
		double qvalue = 0;

		// Iterate through all possible moves and find the maximum Q-value
		for (Move m : gPrime.getPossibleMoves()) {
			qvalue = this.qTable.getQValue(gPrime, m);
			if (qvalue > max) {
				max = qvalue; // Update the maximum Q-value
			}
		}

		return max; // Return the maximum Q-value

	}
	
	// Helper method for epsilon greedy policy
		private Move epsilonMove(Game g) {
			// Get all possible moves for the current game state
			List<Move> moves = g.getPossibleMoves();
			Move m = null;
			Random r = new Random();

			// Variables for tracking the Q-values
			double qvalue = 0;
			double max = -Integer.MAX_VALUE;

			// Generate a random number to decide whether to explore or exploit
			double random = r.nextDouble();

			// Exploration: choose a random move with probability epsilon
			if (random < epsilon) {
				if (moves.size() != 0) {
					Random random1 = new Random();
					int num = random1.nextInt(moves.size()); // Pick a random move index
					m = moves.get(num); // Select the random move
				}
			} else {
				// Exploitation: choose the move with the highest Q-value
				for (Move m1 : moves) {
					qvalue = qTable.getQValue(g, m1);
					if (qvalue >= max) {
						max = qvalue; // Update the maximum Q-value
						m = m1; // Select the move with the highest Q-value
					}
				}
			}

			return m; // Return the chosen move
		}


	/**
	 * Implement this method. It should use the q-values in the {@code qTable} to
	 * extract a policy and return it.
	 *
	 * @return the policy currently inherent in the QTable
	 */
	public Policy extractPolicy() {

		// Create a new Policy object to store the optimal moves for each game state
		Policy policy = new Policy();

		// Iterate through all the game states present in the Q-table
		for (Game game : this.qTable.keySet()) {
			// If the game state is terminal, skip to the next iteration
			if (game.isTerminal()) {
				continue;
			}

			double max = -Integer.MAX_VALUE; // Initialize max to a very low value
			Move maxMove = null; // Initialize maxMove to null

			// Iterate through all possible moves from the current game state
			for (Move move : game.getPossibleMoves()) {
				// Get the Q-value for the current state and move
				double sum = qTable.getQValue(game, move);

				// Check if the current Q-value is greater than the maximum Q-value found so far
				if (sum > max) {
					max = sum; // Update the maximum Q-value
					maxMove = move; // Update the move corresponding to the maximum Q-value
				}
			}

			// Store the optimal move for the current game state in the policy
			policy.policy.put(game, maxMove);
		}

		// Return the constructed Policy object
		return policy;

	}

	public static void main(String args[]) throws IllegalMoveException {
		 if (args.length < 4) {
		        System.out.println("Usage: java target/classes/ticTacToe.Game -x ql -o <opponent> -s <starting_player>");
		        return;
		    }

		    // Parse command-line arguments
		    String opponentType = args[5]; // Opponent type: random, aggressive, or defensive
		    char startingPlayer = args[7].charAt(0); // Starting player: 'x' or 'o'

		    // Create PolicyIterationAgent
		    QLearningAgent agent = new QLearningAgent();

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
