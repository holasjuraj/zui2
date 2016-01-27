import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;


public class Classifier {
	public static final int IN_PARAM_NUM = 6;
	public static final String DATA_FILE = "car.data";

	public static void main(String[] args) {
		// Read input data
		DataSet data = readData(DATA_FILE);
		
		// Get labels and label probabilities
		TreeMap<String, Integer> labelCount = countLabel(data);
		ArrayList<String> labels = new ArrayList<String>(labelCount.size());
		for(Entry<String, Integer> entry : labelCount.entrySet()){
			labels.add(entry.getKey());
		}
		TreeMap<String, Double> labelProb = new TreeMap<String, Double>();
		for(String label : labels){
			labelProb.put(label, (double)labelCount.get(label) / data.size());
		}
		
		// Get condition tables for each parameter
		ArrayList<Table> condTables = new ArrayList<Table>(IN_PARAM_NUM);
		for(int i = 0; i < IN_PARAM_NUM; i++){
			condTables.add(countConditionTable(data, i, labelCount));
		}
		
		// Print condition tables
		String[] captions = {"BUY_PRICE", "MAINTENANCE_PRICE", "DOORS", "PERSONS", "LUGGAGE", "SAFETY"};
		for(int i = 0; i < captions.length; i++){
			printTable(data, condTables, i, captions[i]);
		}
		
		// Count confusion matrix, print results
		Table confusionMatrix = countConfusionMatrix(data, condTables, labelProb);
		int wrong = 0;
		System.out.println("\nCONFUSION MATRIX:");
		for(String label : labels){
			System.out.print("\t"+label);
		}
		for(String actual : labels){
			System.out.print("\n"+actual);
			for(String predicted : labels){
				int count = (int)Math.round(confusionMatrix.get(actual, predicted));
				System.out.print("\t"+count);
				if(!actual.equals(predicted)){ wrong += count; }
			}
		}
		System.out.println(String.format("\n\nCLASSIFICATION ERROR = %.3f%%" ,((double)wrong / data.size() * 100)));
	}
	
	private static DataSet readData(String file){
		DataSet data = new DataSet();
		
		Scanner in = null;
		try { in = new Scanner(new File(DATA_FILE)).useDelimiter("\\s*[,\\s]\\s*"); }
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return data;
		}
		
		while(in.hasNext()){
			ArrayList<String> row = new ArrayList<String>(IN_PARAM_NUM);
			for(int i = 0; i < IN_PARAM_NUM+1; i++){ row.add(in.next()); }
			data.add(row);
		}		
		in.close();
		return data;
	}

	private static TreeMap<String, Integer> countLabel(DataSet data){
		TreeMap<String, Integer> count = new TreeMap<String, Integer>();
		for(int i = 0; i < data.size(); i++){
			String label = data.get(i).get(IN_PARAM_NUM);
			Integer old = count.get(label);
			count.put(label, ( (old==null) ? 1 : (old+1) ));
		}
		return count;
	}
		
	private static Table countConditionTable(DataSet data, int paramIndex, Map<String, Integer> labelCount){
		Table table = new Table();
		TreeSet<String> conditions = new TreeSet<String>();
		for(int i = 0; i < data.size(); i++){
			String condition = data.get(i).get(paramIndex);
			String label = data.get(i).get(IN_PARAM_NUM);
			table.inc(condition, label);
			conditions.add(condition);
		}
		
		for(Entry<String, Integer> label : labelCount.entrySet()){
			for(String condition : conditions){
				table.div(condition, label.getKey(), (double)label.getValue());
			}
		}
		return table;
	}
	
	private static void printTable(DataSet data, List<Table> condTables, int index, String caption){
		TreeSet<String> conditions = new TreeSet<String>();
		for(int i = 0; i < data.size(); i++){
			conditions.add(data.get(i).get(index));
		}
		String[] labels = {"unacc", "acc", "good", "vgood"}; // for custom sorting
		System.out.println("P("+caption+" | A):");
		for(String label : labels){
			System.out.print("\t"+label);
		}
		for(String cond : conditions){
			System.out.print("\n"+cond);
			for(String label : labels){
				System.out.print(String.format("\t%.3f", condTables.get(index).get(cond, label)));
			}
		}
		System.out.println("\n");
	}
	
	private static String classify(List<String> params, List<Table> condTables, Map<String, Double> labelProb){
		String res = null;
		double maxProb = -1.0;
		for(Entry<String, Double> entry : labelProb.entrySet()){
			String label = entry.getKey();
			double prob = entry.getValue();
			for(int i = 0; i < IN_PARAM_NUM; i++){
				prob *= condTables.get(i).get(params.get(i), label);
			}
			if(prob > maxProb){
				maxProb = prob;
				res = label;
			}
		}
		return res;
	}
	
	private static Table countConfusionMatrix(DataSet data, List<Table> condTables, Map<String, Double> labelProb){
		Table matrix = new Table();
		for(int i = 0; i < data.size(); i++){
			String actual = data.get(i).get(IN_PARAM_NUM);
			String predicted = classify(data.get(i), condTables, labelProb);
			matrix.inc(actual, predicted);
		}
		return matrix;
	}
	
	
	private static class DataSet extends ArrayList<ArrayList<String>>{
		private static final long serialVersionUID = 811897358805150254L;
		public DataSet(){ super(); }
	}
}