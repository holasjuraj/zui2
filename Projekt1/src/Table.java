import java.util.HashMap;

public class Table {
	private HashMap<String, Double> tab;
	
	public Table(){
		tab =  new HashMap<String, Double>();
	}
	
	public void set(String a, String b, Double value){
		tab.put(a+"#"+b, value);
	}
	
	public Double get(String a, String b){
		Double value = tab.get(a+"#"+b);
		return (value == null) ? 0.0 : value;
	}
	
	public void inc(String a, String b){
		set(a, b, (get(a, b) + 1.0));
	}
	
	public void div(String a, String b, Double x){
		set(a, b, (get(a, b) / x));
	}
}