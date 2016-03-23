import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import Exceptions.ParserExceptions.ArgumentForEditingNotEnteredException;
import Exceptions.ParserExceptions.InvalidDateTimeFormatException;
import Exceptions.ParserExceptions.InvalidInputException;
import Exceptions.ParserExceptions.InvalidTaskDateException;
import Exceptions.ParserExceptions.InvalidTaskTimeException;
import Exceptions.ParserExceptions.NoInputException;
import Exceptions.ParserExceptions.TaskDateAlreadyPassedException;
import Exceptions.ParserExceptions.TaskTimeOutOfBoundException;

public class TaskWindow {

	private static JFrame frame;
	private JTextField taskEntryField;
	private static TaskManager taskManager;
	private int selectedIndex = 0;
	public final JList<String> taskList = new JList<String>();
	public JTextPane taskDetailView;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					taskManager = TaskManager.getInstance();
					TaskWindow window = new TaskWindow();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, "The application was unable to run at this time");
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TaskWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		taskEntryField = new JTextField();
		taskEntryField.setFont(new Font("Avenir Next", Font.PLAIN, 12));
		taskEntryField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					taskManager.executeCommand(taskEntryField.getText());
				} catch (NoInputException e1) {
					JOptionPane.showMessageDialog(frame, "No input was found.");
					e1.printStackTrace();
				} catch (InvalidInputException e1) {
					JOptionPane.showMessageDialog(frame, "The input entered was invalid.");
					e1.printStackTrace();
				} catch (InvalidTaskTimeException e1) {
					JOptionPane.showMessageDialog(frame, "The task time entered is invalid.");
					e1.printStackTrace();
				} catch (TaskTimeOutOfBoundException e1) {
					JOptionPane.showMessageDialog(frame, "The task time entered is out of bounds.");
					e1.printStackTrace();
				} catch (TaskDateAlreadyPassedException e1) {
					JOptionPane.showMessageDialog(frame, "The task date has already passed.");
					e1.printStackTrace();
				} catch (InvalidTaskDateException e1) {
					JOptionPane.showMessageDialog(frame, "The task date entered is invalid.");
					e1.printStackTrace();
				} catch (ArgumentForEditingNotEnteredException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InvalidDateTimeFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				refreshWindow();
			}
		});
		taskEntryField.setBounds(6, 244, 361, 28);
		frame.getContentPane().add(taskEntryField);
		taskEntryField.setColumns(10);

		JButton goButton = new JButton("Go");
		goButton.setForeground(UIManager.getColor("Button.select"));
		goButton.setBackground(UIManager.getColor("CheckBox.select"));
		goButton.setFont(new Font("Avenir Next", Font.BOLD, 13));
		goButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});
		goButton.setBounds(379, 245, 61, 29);
		frame.getContentPane().add(goButton);
		taskList.setBorder(new LineBorder(UIManager.getColor("Button.select"), 1, true));
		taskList.setBackground(UIManager.getColor("Button.background"));
		taskList.setFont(new Font("Avenir Next", Font.PLAIN, 12));
		taskList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				selectedIndex = taskList.getSelectedIndex();
				setTaskDetailView();
			}
		});
		taskList.setModel(new AbstractListModel<String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] values = taskManager.getTaskNames();
			public int getSize() {
				return values.length;
			}
			public String getElementAt(int index) {
				return values[index];
			}
		});
		
		taskList.setBounds(12, 12, 156, 222);
		frame.getContentPane().add(taskList);

		taskDetailView = new JTextPane();
		taskDetailView.setBackground(UIManager.getColor("Button.background"));
		taskDetailView.setFont(new Font("Avenir Next", Font.PLAIN, 13));
		setTaskDetailView();
		taskDetailView.setBounds(183, 12, 249, 222);
		taskDetailView.setBorder(new LineBorder(UIManager.getColor("Button.select"), 1, true));
		frame.getContentPane().add(taskDetailView);
	}

	private void setTaskDetailView() {
		if (selectedIndex < taskManager.getNumberOfTasks() && selectedIndex >= 0) {
			taskDetailView.setText(taskManager.getTask(selectedIndex).displayString());
		} else {
			taskDetailView.setText("");
		}
	}

	private void refreshWindow() {
		setTaskDetailView();
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
