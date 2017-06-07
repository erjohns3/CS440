
/**
 * Do NOT edit implemented methods.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Class Decision Tree
 */
public class DecisionTree {
	/**
	 * Class Attribute
	 */
	public static class Attribute {
		/**
		 * Name of the attribute
		 */
		private final String name;

		/**
		 * Index of the column in the instances that corresponds to value of
		 * this attribute. Leftmost column has an index of zero.
		 */
		private final int columnIndex;

		/**
		 * All possible values that the attribute may take.
		 */
		private final String[] possibleAttrValues;

		/**
		 * Constructor.
		 *
		 * @param attributeInfo
		 *            String representation of all the fields of this class.
		 */
		public Attribute(final String attributeInfo) {
			final String[] fields = attributeInfo.split("\\s+");
			columnIndex = Integer.parseInt(fields[0]);
			name = fields[1];
			possibleAttrValues = Arrays.copyOfRange(fields, 2, fields.length);
		}

	}

	/**
	 * Class Instance
	 */
	private static class Instance {
		/**
		 * Label of an instance
		 */
		private final Label label;

		/**
		 * Features of an instance. These are values of all the attributes for
		 * this instance.
		 */
		private final String[] features;

		/**
		* Constructor.
		*
		* @param featuresAndLabel
		*            String representation of the fields of this class.
		*/
		public Instance(final String featuresAndLabel) {
			final String[] fields = featuresAndLabel.split("\\s+");
			features = Arrays.copyOfRange(fields, 0, fields.length - 1);
			label = Label.valueOf(fields[fields.length - 1]);
		}

		/**
		* Get value of an attribute in this instance. May use while creating
		* splits on an attribute-value pair.
		*
		* @param attr
		*            Attribute
		* @return Value
		*/
		public final String getValueForAttribute(final Attribute attr) {
			return features[attr.columnIndex];
		}

	}

	/**
	 * Possible Labels of an instance
	 */
	private static enum Label {
		YES, NO
	}

	/**
	 * @param args
	 *            Paths to input files.
	 */
	public static void main(final String[] args) {
		final List<Attribute> attributeInfo = readAttributes(args[0]);
		final List<Instance> trainingData = readInstances(args[1]);
		final DecisionTree tree = new DecisionTree(attributeInfo, trainingData);
		if(args.length > 3) {
			if(args[3].equalsIgnoreCase("prune")){
				// Only for bonus credit.
				System.out.println("Pruning not implemented"); // Comment this out if you do implement!
				/*
				 * Add code to call your pruning method(s) here as appropriate.
				 * For example: tree.prune(arguments...);
				 */
			}
		}
		tree.print();
		System.out.println("\n");
		final List<Instance> testData = readInstances(args[2]);
		for (final Instance testInstance : testData) {
			System.out.println(tree.classify(testInstance));
		}
		System.out.println("\n");
		System.out.println("Training error = " + tree.computeError(trainingData) + " , Test error = "
				+ tree.computeError(testData));
	}

	/**
	 * To parse the attribute info file.
	 *
	 * @param attrInfoPath
	 *            file path
	 * @return List of attributes (objects of Class Attribute)
	 */
	public static List<Attribute> readAttributes(final String attrInfoPath) {
		final List<Attribute> attributes = new ArrayList<>();
		BufferedReader br = null;

		try {
			String currentLine;

			br = new BufferedReader(new FileReader(attrInfoPath));

			while ((currentLine = br.readLine()) != null) {
				final Attribute attribute = new Attribute(currentLine);
				attributes.add(attribute);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return attributes;
	}

	/**
	 * To parse the training data (instances)
	 *
	 * @param trainDataPath
	 *            file path
	 * @return List of Instances.
	 */
	public static List<Instance> readInstances(final String trainDataPath) {
		final List<Instance> instances = new ArrayList<>();
		BufferedReader br = null;

		try {
			String currentLine;

			br = new BufferedReader(new FileReader(trainDataPath));
			br.readLine();

			while ((currentLine = br.readLine()) != null) {
				final Instance instance = new Instance(currentLine);
				instances.add(instance);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return instances;
	}

	/**
	 * The attribute which is the root of this tree.
	 */
	private final Attribute rootAttribute;

	/**
	 * True if this tree is a leaf.
	 */
	private final Boolean isLeaf;

	/**
	 * The label to be output if this tree is a leaf; Set to Null if the
	 * 'isleaf' flag is false.
	 */
	private final Label leafVal;

	/**
	 * List of the children trees sorted in the same order as the corresponding
	 * values of the root attribute.
	 */
	private final List<DecisionTree> children;

	/**
	 * Constructor. Builds the tree given the following parameters.
	 *
	 * @param attributeList
	 *            List of attributes
	 * @param instanceList
	 *            List of instances
	 */
	public DecisionTree(final List<Attribute> attributeList, final List<Instance> instanceList) {
		isLeaf = shouldThisBeLeaf(instanceList, attributeList);
		if (isLeaf) {
			leafVal = computeLeafLabel(instanceList);
			rootAttribute = null;
			children = null;
			return;
		}
		leafVal = null;
		rootAttribute = computeBestAttribute(attributeList, instanceList);
		final List<Attribute> remAttributeList = getRemainingAttributes(attributeList, rootAttribute);
		children = new ArrayList<>();
		for (final String possibleVal : rootAttribute.possibleAttrValues) {
			children.add(new DecisionTree(remAttributeList,
				generateSplitForAttrVal(instanceList, rootAttribute, possibleVal)));
		}
	}

	/**
	 * Classify an instance. May also be used when evaluating performance on test data.
	 *
	 * @param instance
	 *            Instance to be classified.
	 * @return Label output
	 */
	public Label classify(final Instance instance) {
		// TODO Implement this!
		return instance.label;
		//return null;
	}

	/**
	 * Computes the best attribute (least entropy)
	 *
	 * @param attributeList
	 *            List of attributes
	 * @param instanceList
	 *            List of instances
	 * @return The best attribute
	 */
	private Attribute computeBestAttribute(final List<Attribute> attributeList, final List<Instance> instanceList) {
		// TODO Implement this!
		double bestEntropy = 1;
		int bestIndex = 0;
		// iterate through all of the attributes
		for(int i=0; i<attributeList.size(); i++){
			List<String> valueList = new ArrayList<String>();
			int[] yesList = new int[instanceList.size()];
			int[] noList = new int[instanceList.size()];
			// iterate through all of the instances while finding all the possible values
			for(int j=0; j<instanceList.size(); j++){
				String str = instanceList.get(j).getValueForAttribute(attributeList.get(i));
				if(!valueList.contains(str)){
					valueList.add(str);
				}
				int index = valueList.indexOf(str);
				// count the number of yes's and no's in of each attribute
				if(instanceList.get(j).label.name().equals("YES")){
					yesList[index]++;
				}else{
					noList[index]++;
				}
			}
			//find the average entropy of each attribute
			double entropy = 0;
			for(int j=0; j<valueList.size(); j++){
				double sum = yesList[j] + noList[j];
				double yesTmp = yesList[j] / sum;
				double noTmp = noList[j] / sum;
				double tmp = ((-1)*yesTmp*Math.log(yesTmp)/Math.log(2)) - (noTmp*Math.log(noTmp)/Math.log(2));
				entropy += tmp*sum/instanceList.size();
			}
			// keep track of the one with the least amount of entropy
			if(entropy < bestEntropy){
				bestIndex = i;
				bestEntropy = entropy;
			}
		}
		//return attribute with least entropy
		return attributeList.get(bestIndex);
	}

	/**
	 * Evaluate performance of this tree.
	 *
	 * @param trainingData
	 * @return
	 */
	private double computeError(final List<Instance> trainingData) {
		// TODO Implement this!

		return 0.0;
	}

	/**
	 * computes the label to be output at a leaf (which minimizes error on
	 * training data). If the given split is empty, you can assign any label for
	 * this leaf.
	 *
	 * @param instanceList
	 *            List of instances
	 * @return computed label
	 */
	private Label computeLeafLabel(final List<Instance> instanceList) {
		// TODO Implement this!
		if(instanceList.size() == 0){
			return null;
		}
		return instanceList.get(0).label;
	}

	/**
	 * Split the data on an attribute-value pair.
	 *
	 * @param instanceList
	 *            List of instances
	 * @param splitAttribute
	 *            Attribute to split on
	 * @param splitVal
	 *            Value to split on
	 * @return List of instances that constitute the said split (i.e. have the
	 *         given value for the given attribute)
	 */
	private List<Instance> generateSplitForAttrVal(final List<Instance> instanceList, final Attribute splitAttribute, final String splitVal) {
		// TODO Implement this!
		List<Instance> splitList = new ArrayList<Instance>();
		//only return the instances that have the given value for the given attribute
		for(int i=0; i<instanceList.size(); i++){
			if(instanceList.get(i).getValueForAttribute(splitAttribute).equals(splitVal)){
				// if they match, add to the list
				splitList.add(instanceList.get(i));
			}
		}
		return splitList;
	}

	/**
	 * @param attributeList
	 *            List of candidate attributes at this subtree
	 * @param rootAttribute
	 *            Attribute chosen as the root
	 * @return List of remaining attributes
	 */
	private List<Attribute> getRemainingAttributes(final List<Attribute> attributeList, final Attribute rootAttribute) {
		// TODO implement this!
		List<Attribute> remainingList = new ArrayList<Attribute>();
		// copy over the whole list of attributes
		for(int i=0; i<attributeList.size(); i++){
			remainingList.add(attributeList.get(i));
		}
		// remove the root
		remainingList.remove(rootAttribute);
		return remainingList;
	}

	/**
	 * Print a representation of this tree.
	 */
	public void print() {
		print(0);
	}

	/**
	 * Print relative to a calling super-tree.
	 *
	 * @param rootDepth
	 *            Depth of the root of this tree in the super-tree.
	 */
	private void print(final int rootDepth) {
		if (!isLeaf) {
			final Iterator<DecisionTree> itr = children.iterator();
			for (final String possibleAttrVal : rootAttribute.possibleAttrValues) {
				printIndent(rootDepth);
				System.out.println(rootAttribute.name + " = " + possibleAttrVal + " :");
				itr.next().print(rootDepth + 1);
			}
		} else {
			printIndent(rootDepth);
			System.out.println(leafVal);
		}
	}

	/**
	 * For formatted printing.
	 *
	 * @param n
	 *            Indent
	 */
	private void printIndent(final int n) {
		for (int i = 0; i < n; i++)
			System.out.print("\t");
	}

	/**
	 * Determine if this is simply a leaf, as a function of the given
	 * parameters.
	 *
	 * @param instanceList
	 *            List of instances
	 * @param attributeList
	 *            List of attributes
	 * @return True iff this tree should be a leaf.
	 */
	private boolean shouldThisBeLeaf(final List<Instance> instanceList, final List<Attribute> attributeList) {
		// TODO Implement this!

		//if there is only 1 instance or no attributes, its a leaf
		if(attributeList.size() == 0 || instanceList.size() <= 1){
			return true;
		}
		String str = instanceList.get(0).label.name();

		// if any of the values differ it's not a leaf
		for(int i=1; i<instanceList.size(); i++){
			if(!instanceList.get(i).label.name().equals(str)){
				return false;
			}
		}
		return true;
	}
}
