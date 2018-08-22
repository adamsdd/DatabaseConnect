/**
 * @author Dawid Adamczyk #adamsdd
 */

package pl.adamsdd;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

public class View extends JFrame
{
	private static final long serialVersionUID = 1L;
	private final int WIDTH = 600;
	private final int HEIGHT = 600;
	private Container cp;
	private JTable table;
	private JTextArea queryArea;
	private List<String> historyList;
	
	public View()
	{
		historyList = new ArrayList<String>();
		cp = getContentPane();
		table = new JTable();
		JScrollPane tableSP = new JScrollPane(table);
		JPanel southPanel = new JPanel();
		queryArea = new JTextArea(5, 30);
		JScrollPane sp = new JScrollPane(queryArea);
		JButton executeButt = new JButton("Execute");
		
		southPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
		southPanel.add(sp);
		southPanel.add(executeButt);
		
		cp.add(tableSP, BorderLayout.CENTER);
		cp.add(southPanel, BorderLayout.SOUTH);
		
		System.out.println("Start db conn...");
		table.setModel(new DatabaseConnect());
		
		JMenuBar mb = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem history = new JMenuItem("History");
		
		history.addActionListener((e) ->{
			JPanel historyPanel = new JPanel();
			historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
			JDialog historyDialog = null;
			
			for(int i = 0; i < historyList.size(); i++)
			{
				JTextArea historyArea = new JTextArea();
				historyArea.setEditable(false);
				historyArea.setText(historyList.get(i));
				historyPanel.add(historyArea);
				historyPanel.add(new JSeparator());
			}
			
			if(historyDialog == null)
			{
				historyDialog = new JDialog(this, false);
				historyDialog.setLocationRelativeTo(this);
				historyDialog.setTitle("History");
				historyDialog.setDefaultCloseOperation(HIDE_ON_CLOSE);
				historyDialog.setSize(400, 400);
				historyDialog.add(new JScrollPane(historyPanel));
			}

			historyDialog.setVisible(true);
		});
		
		history.setMnemonic('h');
		history.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
		
		Action executeButtAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				historyList.add(queryArea.getText());
				try {
					((DatabaseConnect) table.getModel()).executeQuery(queryArea.getText());
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null,
												  e1.getMessage(),
												  "SQL - ERROR",
												  JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		
		executeButt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "doQuery");
		executeButt.getActionMap().put("doQuery", executeButtAction);
		executeButt.addActionListener(executeButtAction);
		
		this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closing application");
                ((DatabaseConnect)table.getModel()).close();
                System.exit(1);
            }
        });
		
		file.add(history);
		mb.add(file);
		setJMenuBar(mb);
		setSize(WIDTH, HEIGHT);
		setTitle("DatabaseConnection");
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(() -> new View());
	}

}
