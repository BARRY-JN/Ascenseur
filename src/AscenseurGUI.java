import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class AscenseurGUI {
	public class ElevatorVisualizationPanel extends JPanel {
		private int y = 250;
		private boolean moving=false;
		private int step=1;
		private int actual_floor = 5;
		private int floor_size=50;
		private Action action;

		public ElevatorVisualizationPanel(Action action) {
			this.action=action;
			Timer timer = new Timer(40, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(floor_detected())
						action.detected_floor();
					Point coord = action.moveElevator();
					y = coord.y;
					repaint();
				}
			});
			timer.start();
		}

		public boolean floor_detected(){
			if(action.is_moving()&&(y==0||y==50||y==100||y==150||y==200||y==250)) {
				action.output_text("[ASCENSEUR] Etage détecté");
				return true;
			}else{
				return false;
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(220, 300);
		}

		@Override
		protected void paintComponent(Graphics g) {

			super.paintComponent(g);
			Graphics2D elevator = (Graphics2D) g.create();
			for(int i=0;i<=5;i++) {
				elevator.setColor(Color.ORANGE);
				elevator.fillRect(0, i*floor_size, 200, floor_size);
				elevator.setColor(Color.YELLOW);
				elevator.fillRect(2, (i*floor_size)+2, 196, floor_size-4);
				elevator.setColor(Color.BLACK);
				elevator.drawString("[" + (5-i) + "]",180,(i*floor_size)+(floor_size/2));
			}

			elevator.setColor(Color.BLACK);
			elevator.fillRect(0,y,floor_size-10,floor_size);

			elevator.setColor(Color.LIGHT_GRAY);

			elevator.fillRect(0,y,(floor_size-10)-4,floor_size);
			elevator.fillRect(floor_size-10,y,(floor_size-10)-4,floor_size);
			elevator.dispose();
		}

	}


	public ArrayList<JButton> createFloorButtons(){
		ArrayList<JButton> ButtonsList = new ArrayList<>();
		JButton Floor_button;
		Image button_icon;

		for(int i=5;i>0;i--) {
			Floor_button = new JButton("Etage "+i);
			try {
				button_icon = ImageIO.read(getClass().getResource("etage"+i+"off.png"));
				Floor_button.setIcon(new ImageIcon(button_icon));
				button_icon = ImageIO.read(getClass().getResource("etage"+i+"on.png"));
				Floor_button.setPressedIcon (new ImageIcon(button_icon));
				Floor_button.setMaximumSize(new Dimension(194,60));
			} catch (Exception ex) {
				System.out.println(ex);
			}
			ButtonsList.add(Floor_button);
		}
		Floor_button = new JButton();
		//Floor_button.setMaximumSize(new Dimension(100,100));

		try {
			button_icon = ImageIO.read(getClass().getResource("boutonsAUoff.png"));
			Floor_button.setIcon(new ImageIcon(button_icon));
			button_icon = ImageIO.read(getClass().getResource("boutonsAUon.png"));
			Floor_button.setPressedIcon(new ImageIcon(button_icon));
		} catch (Exception ex) {
			System.out.println(ex);
		}
		ButtonsList.add(Floor_button);
		return ButtonsList;
	}

	public AscenseurGUI(Action action){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame f=new JFrame();//creating instance of JFrame

		GridBagLayout grid = new GridBagLayout();
		f.getContentPane().setLayout(grid);

		GridBagConstraints gbc = new GridBagConstraints();
		ElevatorVisualizationPanel EVP = new ElevatorVisualizationPanel(action);


		JPanel Panel_InternalCommand=new JPanel();
		Panel_InternalCommand.setLayout(new BoxLayout(Panel_InternalCommand, BoxLayout.Y_AXIS));
		JLabel Label_InternalCommand=new JLabel("Commandes internes");
		gbc.gridx = 0;
		gbc.gridy = 0;
		//gbc.anchor = GridBagConstraints.WEST;
		Panel_InternalCommand.add(Label_InternalCommand);
		for(JButton Floor_button:createFloorButtons()) {
			Panel_InternalCommand.add(Floor_button);
		}
		f.add(Panel_InternalCommand,gbc);

		JPanel Panel_Visualization=new JPanel();
		Panel_Visualization.setLayout(new BoxLayout(Panel_Visualization, BoxLayout.Y_AXIS));
		JLabel Label_Visualization=new JLabel("Visualisation");
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 100, 10, 10);
		//gbc.weighty = 1.0;
		//gbc.fill = GridBagConstraints.BOTH;
		Panel_Visualization.add(Label_Visualization);
		Panel_Visualization.add(EVP);
		f.add(Panel_Visualization,gbc);

		JPanel row,column;
		gbc.gridx = 2;
		gbc.gridy = 0;
		//gbc.weighty = 1.0;
		//gbc.fill = GridBagConstraints.FIRST_LINE_START;
		//gbc.gridheight = 5;
		column=new JPanel();
		column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
		column.add(new JLabel("Appel de la cabine"));
		for(int i=5;i>=0;i--){
			row = new JPanel();
			//Declaration et initialisation des boutons qui serviront aux appels dans les étages
			row.add(new JLabel(" Etage "+i+" "));
			if(i!=5) {
				JButton jb = new JButton("/\\");
				final int finalI = i;
				jb.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						action.get_Instructions().add_external(finalI, Instructions.Sens.HAUT);
					}
				});
				row.add(jb);
			}
			if(i!=0) {
				JButton jb2 = new JButton("\\/");
				final int finalI = i;
				jb2.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						action.get_Instructions().add_external(finalI, Instructions.Sens.BAS);
					}
				});
				row.add(jb2);
			}
			column.add(row);
		}
		f.add(column,gbc);

		JPanel Panel_OperativeControl=new JPanel();
		JPanel column1=new JPanel();
		JPanel column2=new JPanel();
		Panel_OperativeControl.setLayout(new BoxLayout(Panel_OperativeControl, BoxLayout.Y_AXIS));
		JLabel Label_OperativeControl=new JLabel("Contrôle du moteur");
		//gbc.ipady = 100;
		//gbc.ipadx = 200;
		gbc.gridx = 0;
		gbc.gridy = 1;
		Panel_OperativeControl.add(Label_OperativeControl);
		JButton Up=new JButton("Monter");
		Up.addActionListener(e -> action.go_upstair());
		JButton Down=new JButton("Descendre");
		Down.addActionListener(e -> action.go_downstair());
		column1.add(Up);
		column1.add(Down);
		Panel_OperativeControl.add(column1);
		JButton StopNextFloor=new JButton("Arrêter au prochain niveau");
		StopNextFloor.addActionListener(e -> action.next_floor());
		JButton Stop=new JButton("ARRET D'URGENCE");
		Stop.addActionListener(e -> action.stop_all());
		column2.add(StopNextFloor);
		column2.add(Stop);
		Panel_OperativeControl.add(column1);
		Panel_OperativeControl.add(column2);
		f.add(Panel_OperativeControl,gbc);

		JPanel Panel_Output=new JPanel();
		Panel_Output.setLayout(new BoxLayout(Panel_Output, BoxLayout.Y_AXIS));
		JLabel Label_Output=new JLabel("Output :");
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		action.get_text_area().setEditable(false);
		JScrollPane scrollableTextArea = new JScrollPane(action.get_text_area());
		scrollableTextArea.setPreferredSize(new Dimension(500,200));
		scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Panel_Output.add(Label_Output);
		Panel_Output.add(scrollableTextArea);
		f.add(Panel_Output,gbc);

		f.setTitle("Projet Ascenseur");
		f.setSize(1120,650);
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
		f.setVisible(true);//making the frame visible
	}
	public static void main(String[] args) {
		Instructions ins = new Instructions();
		Action action = new Action(ins);
		AscenseurGUI AscGUI = new AscenseurGUI(action);
		//action.get_text_area().append("Test");
	}
}


