import javax.swing.*;
import java.awt.*;

public class Action {

	private int y = 250;
	private int floor_size=50;
	private boolean go_up = false;
	private boolean go_down = false;
	private boolean stop_next_floor = false;
	private boolean emergency_stop = false;
	private Instructions ins;
	private static JTextArea textArea = new JTextArea(6,20);

	Action(Instructions ins){
		this.ins=ins;
	}

	Instructions get_Instructions(){
		return ins;
	}

	void print_externals_instructions(){
		for(Instructions.command i:ins.external_instructions){
			output_text("( "+ i.floor +" - "+i.sens.toString()+") ",false);
		}
		output_text("",true);
	}

	JTextArea get_text_area(){
		return textArea;
	}

	static void output_text(String text, boolean escape){
		if(escape)
			textArea.append("\n"+text);
		else
			textArea.append(text);
	}

	void go_upstair(){
		go_up=true;
		go_down=false;
		emergency_stop=false;
		stop_next_floor=false;
		output_text("[MOTEUR] L'ascenseur va en haut !",true);
	}

	void go_downstair(){
		go_up=false;
		go_down=true;
		emergency_stop=false;
		stop_next_floor=false;
		output_text("[MOTEUR] L'ascenseur va en bas !",true);

	}

	void next_floor(){
		emergency_stop=false;
		stop_next_floor=true;
		output_text("[MOTEUR] L'ascenseur s'arrétera au prochain étage !",true);
	}
	void stop_all(){
		go_up=false;
		go_down=false;
		emergency_stop=true;
		stop_next_floor=false;
		output_text("[MOTEUR] Arrêt d'urgence !",true);
	}
	private void stop(){
		go_up=false;
		go_down=false;
		emergency_stop=false;
		stop_next_floor=false;
	}

	boolean is_moving(){
		return go_up||go_down;
	}

	Point moveElevator() {
		if(!emergency_stop) {
			if (go_up) {
				//On ne monte plus si on arrive au dernier étage
				if(y>=0)
					y -= 2;
				else
					go_up=false;
			}
			if (go_down) {
				//On ne descend plus si on arrive au RDC
				if(y<floor_size*5)
					y += 2;
				else
					go_down=false;
			}
		}else{
			go_up=false;
			go_down=false;
		}
		return new Point(0,y);
	}

	void detected_floor() {
		//si un étage a été détecté, on vérifie qu'il n'y ait pas eu une demande d'arret au prochain étage
		if(stop_next_floor&&!emergency_stop){
			stop();
		}
		ins.update_floor_level();
	}

	public void direction_reversal(){

	}
}