# Reinforcement-Learning-TicTacToe

# 🎮 TicTacToe: Markov Decision Processes & Reinforcement Learning

An academic project implementing **Value Iteration**, **Policy Iteration**, and **Q-Learning** agents to play 3x3 Tic-Tac-Toe using concepts from Markov Decision Processes (MDP) and Reinforcement Learning.

Developed in **Java**, this project explores how different learning and planning strategies behave in a controlled game environment.

---

## 📌 Project Goals

- Understand and implement MDP-based planning (Value Iteration, Policy Iteration)
- Apply Q-learning to learn policies through interaction
- Compare agent performance in a shared Tic-Tac-Toe environment
- Explore the structure and challenges of state transitions, rewards, and policies

---

## 🗂️ File Structure

| File | Description |
|------|-------------|
| `Game.java` | Runs the 3x3 Tic-Tac-Toe game |
| `TTTMDP.java` | Defines the MDP model |
| `TTTEnvironment.java` | Environment for RL agents |
| `ValueIterationAgent.java` | MDP-based agent using value iteration |
| `PolicyIterationAgent.java` | MDP-based agent using policy iteration |
| `QLearningAgent.java` | RL agent that learns via Q-learning |
| `Agent.java` | Abstract base agent class |
| `HumanAgent.java` | Allows human to play via command-line |
| `RandomAgent.java` | Plays randomly using `RandomPolicy` |
| `RandomPolicy.java` | A simple random policy |
| `Policy.java` | Abstract base for agent policies |
| `Move.java` | Defines a move in the game |
| `Outcome.java` | Represents a transition: (s, a, r, s') |
| `TransitionProb.java` | Wraps an `Outcome` and its probability |

---

## 🧪 How to Run

Make sure the project is compiled, then from the top-level project directory:

```bash
java -cp target/classes/ ticTacToe.Game

To view available agent options and customizations:

java -cp target/classes/ ticTacToe.Game -h
Example — random vs. random:


java -cp target/classes/ ticTacToe.Game -x random -o random
⚠️ Only vi, pi, and ql agents can play as X.
The O player is assumed to be part of the environment.

📊 Experimental Results
🧮 Policy Iteration Agent
Opponent	Total Games	Wins	Losses	Draws
Random Agent	50	43	0	7
Aggressive Agent	50	50	0	0
Defensive Agent	50	50	0	0

📈 Value Iteration Agent
Opponent	Total Games	Wins	Losses	Draws
Random Agent	50	50	0	0
Aggressive Agent	50	50	0	0
Defensive Agent	50	39	0	11

🤖 Q-Learning Agent
Opponent	Total Games	Wins	Losses	Draws
Random Agent	50	21	10*	19
Aggressive Agent	50	0	50	0
Defensive Agent	50	0	50	0

📝 *Losses inferred based on available data.
The Q-learning agent requires more training time to converge due to its model-free nature.

🧠 Concepts Covered
Markov Decision Processes
Value & Policy Iteration
Q-Learning (Model-free RL)
State transitions, reward shaping, agent-environment interactions
Abstract agent and policy design in OOP Java


📎 Related Keywords
Java, Tic-Tac-Toe, MDP, Q-Learning, Reinforcement Learning, Policy Iteration, Value Iteration, AI Agents, Game AI

