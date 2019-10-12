import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static java.lang.Thread.sleep;

public class Instructions {

	Action act;

	public void set_Actionner(Action action) {
		act=action;
	}

	public enum Sens{HAUT,BAS};
	class Command  {
		public Integer floor;
		Sens sens;

		public Command(int floor,Sens sens){
			this.floor=floor;
			this.sens=sens;
		}
	}

	class CommandCompararator implements Comparator<Command>{
		@Override
		public int compare(Command c1, Command c2)
		{

			return  c1.floor.compareTo(c2.floor);
		}
	}

	private ArrayList<Command> instructions_list =new ArrayList<>();
	private ArrayList<Command> waiting_instructions_list =new ArrayList<>();

	ArrayList<Integer> remove_items=new ArrayList<>();

	Sens actual_direction = Sens.HAUT;
	private int actual_floor = 0;
	boolean actual_instruction_executed=true;
	int stop_to_floor=0;

	public ArrayList<Command> get_instructions(){
		return instructions_list;
	}

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
		for(int i = 0; i< instructions_list.size(); i++){
			if(instructions_list.get(i).floor==floor)
				return;
		}
		instructions_list.add(new Command(floor,sens));
	}

	public void add_internal(int floor) {
		if(floor>this.actual_floor){
			add_external(floor,Sens.HAUT);
		}else{
			add_external(floor,Sens.BAS);
		}
	}

	private void remove_items_not_immediatly(int index){
		remove_items.add(index);
	}

	private void await_order_opposite_direction(int index){
		if (instructions_list.get(index).sens != actual_direction) {
			waiting_instructions_list.add(instructions_list.get(index));
			remove_items_not_immediatly(index);
		}
	}

	private void displacement_management(){
		if(instructions_list.size()>0){
			if(instructions_list.size()==1){
				if(instructions_list.get(0).sens!=actual_direction){
					direction_reversal();
				}
			}else {
				for (int i = 0; i < instructions_list.size(); i++) {
					await_order_opposite_direction(i);
					if (instructions_list.get(i).floor == actual_floor)
						remove_items_not_immediatly(i);
				}
			}
		}else{
			if(waiting_instructions_list.size()>0) {
				instructions_list.addAll(waiting_instructions_list);
				waiting_instructions_list.removeAll(waiting_instructions_list);
			}
		}
		for(int k=0;k<remove_items.size();k++){
			instructions_list.remove(remove_items.get(k));
		}
		remove_items.removeAll(remove_items);
		Collections.sort(instructions_list,new CommandCompararator());
	}

	private void displacement_executor(){
		if(instructions_list.size()>0) {
			if (actual_instruction_executed) {
				Command c = instructions_list.get(0);
				actual_instruction_executed=false;
				stop_to_floor=c.floor;
				if(actual_direction==Sens.HAUT)
					act.go_upstair();
				else
					act.go_downstair();

			}else{
				if(actual_floor-stop_to_floor==1||actual_floor-stop_to_floor==-1) {
					act.next_floor();
					actual_instruction_executed=true;
					if(act.can_open_doors())
						instructions_list.remove(0);
				}
			}
		}
	}

	Instructions(){
		Timer timer = new Timer(100, e -> {
			displacement_management();
			displacement_executor();
		});
		timer.start();

	}

}
