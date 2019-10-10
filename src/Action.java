import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Action extends JPanel {

	private int x = 0;
	private int y = 0;
	private int step=1;
	private int actual_floor = 5;
	private int floor_size=50;
	private boolean go_up = false;
	private boolean go_down = false;
	private boolean stop_next_floor = false;
	private boolean emergency_stop = false;
	private JTextArea textArea = new JTextArea(6,20);

	public JTextArea get_text_area(){
		return textArea;
	}

	public void output_text(String text){
		textArea.append("\n"+text);
	}

	public void go_upstair(){
		go_up=true;
		go_down=false;
		emergency_stop=false;
		stop_next_floor=false;
		output_text("[MOTEUR] L'ascenseur va en haut !");
	}

	public void go_downstair(){
		go_up=false;
		go_down=true;
		emergency_stop=false;
		stop_next_floor=false;
		output_text("[MOTEUR] L'ascenseur va en bas !");

	}

	public void next_floor(){
		emergency_stop=false;
		stop_next_floor=true;
		output_text("[MOTEUR] L'ascenseur s'arrétera au prochain étage !");
	}
	public void stop_all(){
		go_up=false;
		go_down=false;
		emergency_stop=true;
		stop_next_floor=false;
		output_text("[MOTEUR] Arrêt d'urgence !");
	}
	public void stop(){
		go_up=false;
		go_down=false;
		emergency_stop=false;
		stop_next_floor=false;
	}

	protected Point moveElevator() {
		if(go_up&&!emergency_stop&&y>=0) {
			y -= 2;
		}
		if(go_down&&!emergency_stop&&y<floor_size*5) {
			y += 2;
		}
		if(stop_next_floor&&!emergency_stop){
			if(go_up) {
				if(y==0||y==50||y==100||y==150||y==200)
					stop();
			}
			if(go_down) {
				if(y==50||y==100||y==150||y==200||y==250)
					stop();
			}
		}
		return new Point(x,y);
	}
}