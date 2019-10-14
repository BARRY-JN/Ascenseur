import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Instructions {

	Action act;

	private ArrayList<Command> instructions_list =new ArrayList<>();
	private ArrayList<Command> waiting_instructions_list =new ArrayList<>();
	private ArrayList<Command> remove_items=new ArrayList<>();

	Sens actual_direction = Sens.HAUT;
	private int actual_floor = 0;
	boolean actual_instruction_executed=true;
	boolean actual_instruction2_executed=true;
	Command actual_command = new Command(12,Sens.HAUT);
	int stop_to_floor=0;

	boolean highest_floor=false;
	boolean lowest_floor=false;
	boolean d_closed=true;
	boolean emergency=false;
	Timer timer;

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

	class CommandCompararatorUp implements Comparator<Command>{
		@Override
		public int compare(Command c1, Command c2)
		{

			return  c1.floor.compareTo(c2.floor);
		}
	}

	class CommandCompararatorDown implements Comparator<Command>{
		@Override
		public int compare(Command c1, Command c2)
		{

			return  c2.floor.compareTo(c1.floor);
		}
	}

	public void closed_doors(boolean bool) {
		d_closed=bool;
	}

	public ArrayList<Command> get_instructions(){
		return instructions_list;
	}

	public int get_floor(){
		return actual_floor;
	}

	public void upper_limits_reached(){
		highest_floor=true;
	}
	public void lowest_limits_reached(){
		lowest_floor=true;
	}
	public void limits_waived(){
		highest_floor=false;
		lowest_floor=false;
	}

	public void emergency_stop() {
		instructions_list.removeAll(instructions_list);
		emergency=true;
	}

	public void emergency_end() {
		emergency=false;
	}

	public void direction_reversal(){
		if(actual_direction==Sens.HAUT){
			actual_direction=Sens.BAS;
		}else{
			actual_direction=Sens.HAUT;
		}

	}

	public void update_floor_level(int f) {
		actual_floor=f;
		Action.output_text("        [INSTRUCTION] Etage actuel : "+actual_floor,true);
			if (highest_floor)
				actual_direction=Sens.BAS;
			if(lowest_floor)
				actual_direction=Sens.HAUT;

	}

	void add_external(int floor, Sens s){
		if(emergency)
			return;
		for(int i = 0; i< instructions_list.size(); i++){
			if(instructions_list.get(i).floor==floor)
				return;
		}
		instructions_list.add(new Command(floor,s));
	}

	public void add_internal(int floor) {
		if(floor>this.actual_floor){
			add_external(floor,Sens.HAUT);
		}else{
			add_external(floor,Sens.BAS);
		}
	}

	private void remove_items_not_immediatly(Command c){
		for(int i=0;i<remove_items.size();i++) {
			if(remove_items.get(i)==c)
				return;
		}
		remove_items.add(c);
	}

	private void await_order_opposite_direction(Command c){
		if (c.sens != actual_direction) {
			waiting_instructions_list.add(c);
			remove_items_not_immediatly(c);
		}
	}

	private void displacement_management(){
		int number_request_for_actual_direction=0;

		if(instructions_list.size()>0){
			if(instructions_list.size()==1){

				if(instructions_list.get(0).sens==Sens.HAUT){
					if(instructions_list.get(0).floor<actual_floor)
						direction_reversal();
				}else{
					if(instructions_list.get(0).floor>actual_floor)
						direction_reversal();
				}

			}else {
				for (int i = 0; i < instructions_list.size(); i++) {
					if(instructions_list.get(i).sens==actual_direction){
						number_request_for_actual_direction++;
					}else{
						await_order_opposite_direction(instructions_list.get(i));
					}

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
		if(number_request_for_actual_direction==0&&instructions_list.size()>1){
			if (actual_direction == Sens.HAUT) {
				Collections.sort(instructions_list, new CommandCompararatorDown());
			} else {
				Collections.sort(instructions_list, new CommandCompararatorUp());
			}
		}else {
			if (actual_direction == Sens.HAUT) {
				Collections.sort(instructions_list, new CommandCompararatorUp());
			} else {
				Collections.sort(instructions_list, new CommandCompararatorDown());
			}
		}

	}

	private void displacement_executor(){
		if(instructions_list.size()>0) {
			//Si la commande actuelle est différente de la commande qui a été placée comme prioritaire (premières valeurs du tableau)
			//alors la commande actuelle prend la valeur de la commande prioritaire

			if(!actual_instruction2_executed&&((actual_floor-stop_to_floor==1)||(actual_floor-stop_to_floor==-1))) {
				Action.output_text("        [INSTRUCTION] Appel à l'arrêt au prochain étage",true);
				act.next_floor();
				actual_instruction2_executed = true;
			}

			if (actual_floor == stop_to_floor && !actual_instruction_executed) {
				actual_instruction_executed = true;
				instructions_list.remove(0);
				Action.output_text("        [INSTRUCTION] Etage atteint, supression de cet étage dans la liste d'attente",true);
			}

			if (instructions_list.size()>0&&actual_command.floor!=instructions_list.get(0).floor) {
				actual_command = instructions_list.get(0);
				actual_instruction_executed=false;
				actual_instruction2_executed=false;
				stop_to_floor=actual_command.floor;
				Action.output_text("        [INSTRUCTION] Prochain étage à atteindre : "+stop_to_floor,true);
				if(actual_command.sens!=actual_direction) {
					if (actual_floor < stop_to_floor) {
						act.go_upstair();
					} else {
						act.go_downstair();
					}
				}else {
					if (actual_direction == Sens.HAUT)
						act.go_upstair();
					else
						act.go_downstair();
				}
			}

		}
	}

	Instructions(){
		timer = new Timer(100, e -> {
			if(d_closed)
				displacement_management();
			displacement_executor();
		});
		timer.start();

	}

}
