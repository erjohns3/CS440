
import java.util.Random;

public class QLearningAgent implements Agent{
	public QLearningAgent(){
		this.rand = new Random();
	}

	public void initialize(int numOfStates, int numOfActions){
		this.numOfStates = numOfStates;
		this.numOfActions = numOfActions;
		policy = new int[numOfStates];
		qValue = new double[numOfStates][numOfActions];
		return;
	}

	public int chooseAction(int state){
		if(state >= numOfStates){
			return -1;
		}
		if((double)rand.nextInt(100)/100.0 < exploration){
			return rand.nextInt(numOfActions);
		}else{
			return policy[state];
		}
	}

	public void updatePolicy(double reward, int action, int oldState, int newState){
		if(oldState >= numOfStates || newState >= numOfStates || action >= numOfActions){
			return;
		}
		qValue[oldState][action] = (1-learningRate)*qValue[oldState][action] + learningRate*(reward + discountFactor*qValue[newState][policy[newState]]);
		double max = 0;
		double current = 0;
		for(int i=0; i<numOfActions; i++){
			current = qValue[oldState][i];
			if(current >= max){
				max = current;
				policy[oldState] = i;
			}
		}
		return;
	}

	public Policy getPolicy(){

		return new Policy(policy);
	}

	private Random rand;
	private int numOfStates;
	private int numOfActions;

	private int[] policy;
	private double[][] qValue;

	private double discountFactor = 0.95;
	private double learningRate = 0.1;
	private double exploration = 0.1;
}
