import javax.swing.*;
import java.awt.*;
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

	public ArrayList<command> external_instructions =new ArrayList<>();
	ArrayList<command> inner_instructions =new ArrayList<>();

	Sens actual_direction = Sens.HAUT;
	private int actual_floor = 0;

	public int get_floor(){
		return actual_floor;
	}

	public void direction_reversal(){
		if(actual_direction==Sens.HAUT){
			actual_direction=Sens.BAS;
		}else{
			actual_direction=Sens.HAUT;
		}

	}

	public void update_floor_level() {
		Action.output_text("        [INSTRUCTION] Etage actuel calculé : "+actual_floor,true);
		if(actual_direction==Sens.HAUT) {
			if (actual_floor < 5)
				actual_floor += 1;
			else
				direction_reversal();
		}else{
			if(actual_floor > 0)
				actual_floor -= 1;
			else
				direction_reversal();
		}

	}

	void add_external(int floor, Sens sens){
		for(int i=0;i<external_instructions.size();i++){
			if(external_instructions.get(i).floor==floor)
				return;
		}
		external_instructions.add(new command(floor,sens));
	}

	public void add_internal(int floor) {
		if(floor>this.actual_floor){
			add_external(floor,Sens.HAUT);
		}else{
			add_external(floor,Sens.BAS);
		}
	}


	Instructions(){
		/*
		Timer timer = new Timer(50, e -> {

		});
		timer.start();
		 */
	}

}
