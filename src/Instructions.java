import java.util.ArrayList;

public class Instructions {

	public enum Sens{HAUT,BAS};
	class command {
		public int floor;
		Sens sens;

		public command(int floor,Sens sens){
			this.floor=floor;
			this.sens=sens;
		}
	}
	ArrayList<command> external_instructions =new ArrayList<>();
	ArrayList<command> inner_instructions =new ArrayList<>();

	void add_external(int floor, Sens sens){
		external_instructions.add(new command(floor,sens));
	}

	private int actual_floor = 0;
}
