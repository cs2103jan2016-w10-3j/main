import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import java.awt.SystemColor;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.AbstractListModel;
import java.awt.Cursor;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ApplicationWindow {

	private JFrame frame;
	private JTextField searchField;
	private JTextField textFieldTaskManage;
	private static TaskManager taskManager;
	public int selectedListIndex = 0;
	public int selectedButtonIndex = 0;
	public static JList<String> taskList = new JList<String>();
	private ApplicationWindow window = this;
	private JTextArea txtAreaTaskDetails;
	private JTextPane txtAreaDescription;
	private JButton homeButton;
	private JButton historyButton;
	private JButton helpButton;
	private JButton settingsButton;
	private JTextPane txtLabelStatus;
	private boolean firstFocusManageText = true;
	private boolean firstFocusSearchText = true;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					taskManager = TaskManager.getInstance();
					ApplicationWindow window = new ApplicationWindow();
					window.frame.setVisible(true);
					window.frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ApplicationWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 828, 580);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(51, 204, 153));
		panel_1.setBounds(0, 0, 828, 64);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JLabel lblSnaptask = new JLabel("snaptask");
		lblSnaptask.setFont(new Font("Open Sans", Font.PLAIN, 48));
		lblSnaptask.setForeground(new Color(255, 255, 255));
		lblSnaptask.setBounds(16, 0, 216, 58);
		panel_1.add(lblSnaptask);

		JPanel searchLine = new JPanel();
		searchLine.setBackground(new Color(255, 165, 0));
		searchLine.setBorder(null);
		searchLine.setBounds(556, 44, 251, 6);
		searchLine.setEnabled(false);
		panel_1.add(searchLine);

		searchField = new JTextField();
		searchField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (firstFocusSearchText) {
					searchField.setText("");
					firstFocusSearchText = false;
				}
			}
		});
		searchField.setFont(new Font("Open Sans", Font.PLAIN, 15));
		searchField.setForeground(new Color(169, 169, 169));
		searchField.setToolTipText("Search for tasks!");
		searchField.setText("Search for tasks!");
		searchField.setBounds(552, 15, 258, 36);
		panel_1.add(searchField);
		searchField.setColumns(10);

		homeButton = new JButton("\n");
		homeButton.setOpaque(true);
		homeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedButtonIndex = 0;
				refreshButtons();
			}
		});
		homeButton.setIcon(new ImageIcon("/Users/admin/Desktop/ListIcon.png"));
		homeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		homeButton.setBackground(new Color(0, 204, 153));
		homeButton.setBounds(237, 0, 66, 64);
		panel_1.add(homeButton);

		historyButton = new JButton("\n");
		historyButton.setOpaque(true);
		historyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedButtonIndex = 1;
				refreshButtons();
			}
		});
		historyButton.setIcon(new ImageIcon("/Users/admin/Desktop/HistoryIcon.png"));
		historyButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		historyButton.setBackground(new Color(0, 204, 153));
		historyButton.setBounds(312, 0, 66, 64);
		panel_1.add(historyButton);

		settingsButton = new JButton("\n");
		settingsButton.setOpaque(true);
		settingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedButtonIndex = 2;
				refreshButtons();
			}
		});
		settingsButton.setIcon(new ImageIcon("/Users/admin/Desktop/SettingsIcon.png"));
		settingsButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		settingsButton.setBackground(new Color(0, 204, 153));
		settingsButton.setBounds(391, 0, 66, 64);
		panel_1.add(settingsButton);

		helpButton = new JButton("\n");
		helpButton.setOpaque(true);
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedButtonIndex = 3;
				refreshButtons();
			}
		});
		helpButton.setIcon(new ImageIcon("/Users/admin/Desktop/InfoImage.png"));
		helpButton.setBorder(new EmptyBorder(0, 0, 0, 0));
		helpButton.setBackground(new Color(0, 204, 153));
		helpButton.setBounds(470, 0, 66, 64);
		panel_1.add(helpButton);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(SystemColor.textHighlight));
		panel.setBackground(new Color(255, 255, 255));
		panel.setBounds(557, 80, 251, 422);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JPanel panel_2 = new JPanel();
		panel_2.setEnabled(false);
		panel_2.setBorder(null);
		panel_2.setBackground(new Color(255, 165, 0));
		panel_2.setBounds(0, 20, 252, 29);
		panel.add(panel_2);
		panel_2.setLayout(null);

		txtLabelStatus = new JTextPane();
		txtLabelStatus.setDisabledTextColor(SystemColor.text);
		txtLabelStatus.setSelectionColor(SystemColor.activeCaption);
		txtLabelStatus.setEnabled(false);
		txtLabelStatus.setBounds(72, 0, 105, 28);
		txtLabelStatus.setFont(new Font("Open Sans", Font.PLAIN, 20));
		txtLabelStatus.setForeground(new Color(255, 255, 255));
		txtLabelStatus.setBackground(new Color(255, 165, 0));
		panel_2.add(txtLabelStatus);

		txtAreaDescription = new JTextPane();
		txtAreaDescription.setFont(new Font("Open Sans", Font.PLAIN, 14));
		txtAreaDescription.setForeground(new Color(60, 179, 113));
		txtAreaDescription.setBounds(10, 232, 208, 159);
		txtAreaDescription.setEnabled(false);
		panel.add(txtAreaDescription);

		txtAreaTaskDetails = new JTextArea();
		txtAreaTaskDetails.setFont(new Font("Open Sans", Font.PLAIN, 17));
		txtAreaTaskDetails.setForeground(SystemColor.scrollbar);
		txtAreaTaskDetails.setBounds(10, 61, 233, 231);
		txtAreaTaskDetails.setEnabled(false);
		panel.add(txtAreaTaskDetails);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(SystemColor.textHighlight));
		panel_3.setBackground(new Color(255, 255, 255));
		panel_3.setBounds(20, 80, 516, 422);
		frame.getContentPane().add(panel_3);
		panel_3.setLayout(null);

		taskList = new JList<String>();
		taskList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedListIndex = taskList.getSelectedIndex();
				setTaskDetailView();
			}
		});
		taskList.setBackground(UIManager.getColor("TabbedPane.selectedTabTitlePressedColor"));
		taskList.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		taskList.setToolTipText("");
		taskList.setSelectionForeground(new Color(255, 255, 255));
		taskList.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		taskList.setSelectionBackground(new Color(255, 165, 0));
		taskList.setForeground(new Color(255, 165, 0));
		taskList.setFont(new Font("Open Sans", Font.PLAIN, 17));
		taskList.setModel(new AbstractListModel<String>() {
			private static final long serialVersionUID = 1L;
			String[] values = taskManager.getTaskNames();
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		taskList.setFixedCellHeight(25);
		//list.setCellRenderer(new CustomCellRenderer());
		taskList.setBounds(11, 44, 496, 368);
		panel_3.add(taskList);

		JComboBox comboBox = new JComboBox();
		comboBox.setBackground(new Color(255, 255, 255));
		comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		comboBox.setForeground(new Color(60, 179, 113));
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Filter by...", "Incomplete Tasks", "Complete Tasks", "Short Tasks (<1 hour)", "Medium Tasks (1 - 3 hours)", "Long Tasks (3+ hours)", "Tasks Ending Soon"}));
		comboBox.setFont(new Font("Open Sans", Font.PLAIN, 15));
		comboBox.setBounds(6, 6, 258, 36);
		panel_3.add(comboBox);

		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setOpaque(true);
		comboBox_1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Sort By...", "Name", "Date", "Starting Time", "Ending Time", "Duration"}));
		comboBox_1.setForeground(new Color(60, 179, 113));
		comboBox_1.setFont(new Font("Open Sans", Font.PLAIN, 15));
		comboBox_1.setBackground(Color.WHITE);
		comboBox_1.setBounds(261, 6, 249, 36);
		panel_3.add(comboBox_1);


		JPanel taskManageLine = new JPanel();
		taskManageLine.setEnabled(false);
		taskManageLine.setBorder(null);
		taskManageLine.setBackground(new Color(255, 165, 0));
		taskManageLine.setBounds(21, 540, 786, 6);
		taskManageLine.setEnabled(false);
		frame.getContentPane().add(taskManageLine);

		textFieldTaskManage = new JTextField();
		textFieldTaskManage.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (firstFocusManageText) {
					textFieldTaskManage.setText("");
					firstFocusManageText = false;
				}
			}
		});
		textFieldTaskManage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					taskManager.executeCommand(textFieldTaskManage.getText(), window);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(frame, e1.getMessage());
				}
				refreshWindow();
				textFieldTaskManage.setText("");
			}
		});
		textFieldTaskManage.setDisabledTextColor(new Color(128, 128, 128));
		textFieldTaskManage.setForeground(new Color(169, 169, 169));
		textFieldTaskManage.setFont(new Font("Open Sans", Font.PLAIN, 15));
		textFieldTaskManage.setToolTipText("Manage your tasks here!");
		textFieldTaskManage.setColumns(10);
		textFieldTaskManage.setBounds(18, 510, 792, 36);
		textFieldTaskManage.setText("Manage your tasks here!");
		frame.getContentPane().add(textFieldTaskManage);

		refreshButtons();
	}
	
	public void setTaskDetailView() {
		if (selectedListIndex < taskManager.getNumberOfTasks() && selectedListIndex >= 0) {
			txtAreaTaskDetails.setText(taskManager.getTask(selectedListIndex).displayString());
			txtLabelStatus.setText(taskManager.getTask(selectedListIndex).getStatusString());
		} 
	}
	
	private void refreshButtons() {
		homeButton.setBackground(new Color(0, 204, 153));
		historyButton.setBackground(new Color(0, 204, 153));
		settingsButton.setBackground(new Color(0, 204, 153));
		helpButton.setBackground(new Color(0, 204, 153));
		switch (selectedButtonIndex) {
		case 0:
			homeButton.setBackground(new Color(68, 220, 168));
			System.out.println("yes");
			break;
		case 1:
			historyButton.setBackground(new Color(68, 220, 168));
			break;
		case 2:
			settingsButton.setBackground(new Color(68, 220, 168));
			break;
		default:
			helpButton.setBackground(new Color(68, 220, 168));
			break;
		}
	}
	
	public void invalidIndex() {
		JOptionPane.showMessageDialog(frame, "The index entered was invalid.");
	}

	public void refreshWindow() {
		setTaskDetailView();
		refreshButtons();
		taskList.setModel(new AbstractListModel() {
			String[] values = taskManager.getTaskNames();
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
	}
	
}
