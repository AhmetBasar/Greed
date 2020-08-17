/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Baþar
 * 
 * This file is part of Greed.
 * 
 * Greed is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Greed is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Greed.  If not, see <https://www.gnu.org/licenses/>.
 **********************************************/
package chess.gui;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import chess.database.StorageConstants;
import chess.game.GamePlay;

public class BaseGui {

	private JFrame mainOuterFrame;
	private JFrame editModeFrame;
	private JDialog promotionFrame;
	private JFrame debugFrame;
	private JFrame testingFrame;
	private ChessBoardPanel chessBoardPanel;
	private ControlPanel controlPanel;
	private EditModePanel editModePanel;
	private PromotionPanel promotionPanel;
	private DebugPanel debugPanel;
	private TestingPanel testingPanel;
	private GamePlay gamePlay;
	
	private JMenuBar menuBar;
	private JMenu windowMenu;
	private JMenuItem preferencesItem;
	private JDialog preferencesFrame;
	private PreferencesPanel preferencesPanel;
	
	private GlassPane glassPane = new GlassPane();

	public BaseGui(GamePlay gamePlay) {
		this.gamePlay = gamePlay;
		init();
	}

	private void init() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		// MainFrame
		mainOuterFrame = new JFrame();
		mainOuterFrame.setLocationRelativeTo(null);
		mainOuterFrame.setLayout(null);
		mainOuterFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainOuterFrame.setResizable(false);
//		mainOuterFrame.setAlwaysOnTop(true);
		mainOuterFrame.setGlassPane(glassPane);
		mainOuterFrame.setSize(730, 490);
		int xLoc = 700;
		int yLoc = 300;
		if(gamePlay.getPreferences().containsKey(StorageConstants.Keys.XLOCATION)){
			xLoc = Integer.parseInt(gamePlay.getPreferences().get(StorageConstants.Keys.XLOCATION));
			yLoc = Integer.parseInt(gamePlay.getPreferences().get(StorageConstants.Keys.YLOCATION));
		}
		mainOuterFrame.setLocation(xLoc, yLoc);
		mainOuterFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try{
					gamePlay.getDbManager().save(StorageConstants.Keys.XLOCATION, String.valueOf(mainOuterFrame.getX()));
					gamePlay.getDbManager().save(StorageConstants.Keys.YLOCATION, String.valueOf(mainOuterFrame.getY()));
				} catch(Exception e) {
					e.printStackTrace();					
				}
		    }
		});
		
		// Menu Bar
		menuBar = new JMenuBar();
		windowMenu = new JMenu("Window");
		preferencesItem = new JMenuItem("Preferences");
		windowMenu.add(preferencesItem);
		menuBar.add(windowMenu);
		mainOuterFrame.setJMenuBar(menuBar);
		
		// Preferences Frame
		preferencesFrame = new JDialog();
		preferencesFrame.setLocationRelativeTo(null);
		preferencesFrame.setLayout(null);
		preferencesFrame.setResizable(false);
		preferencesFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		preferencesFrame.setAlwaysOnTop(true);
		preferencesFrame.setSize(230, 300);
		preferencesFrame.setModal(true);
		
		// Preferences Panel
		preferencesPanel = new PreferencesPanel(this);
		preferencesPanel.setSize(400, 300);
		preferencesFrame.add(preferencesPanel);

		preferencesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				preferencesFrame.setLocation((int) MouseInfo.getPointerInfo().getLocation().getX(),
						(int) MouseInfo.getPointerInfo().getLocation().getY());
				preferencesFrame.setVisible(true);
			}
		});
		
		// ChessBoard Panel
		chessBoardPanel = new ChessBoardPanel(this);
		chessBoardPanel.buildCells();
		chessBoardPanel.setSize(410, 410);
		chessBoardPanel.setLocation(10, 10);
		chessBoardPanel.setBackground(Color.gray);
		chessBoardPanel.startNewGame();
		mainOuterFrame.add(chessBoardPanel);
		
		// debugFrame
		debugFrame = new JFrame();
		debugFrame.setLocationRelativeTo(null);
		debugFrame.setLayout(null);
		debugFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		debugFrame.setResizable(false);
		debugFrame.setAlwaysOnTop(true);
		debugFrame.setSize(440, 440);
		debugFrame.setLocation(700,300);
		debugFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try{
					getGamePlay().getDbManager().save(StorageConstants.Keys.DEPTH, String.valueOf(debugPanel.getSearchDepth()));
					getGamePlay().getPreferences().put(StorageConstants.Keys.DEPTH, String.valueOf(debugPanel.getSearchDepth()));
				} catch(Exception ex) {
					ex.printStackTrace();
				}
		    }
		});
		
		// DebugPanel
		debugPanel = new DebugPanel(this);
		debugPanel.setLayout(null);
		debugPanel.setSize(700, 800);
		debugFrame.add(debugPanel);
		
		// testingFrame
		testingFrame = new JFrame();
		testingFrame.setLocationRelativeTo(null);
		testingFrame.setLayout(null);
		testingFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		testingFrame.setResizable(false);
		testingFrame.setAlwaysOnTop(true);
		testingFrame.setSize(440, 440);
		testingFrame.setLocation(700,300);
		testingFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try {
					
					
					getGamePlay().getDbManager().save(StorageConstants.Keys.TESTING_ENGINE_DEPTH, String.valueOf(testingPanel.getJtEngineDepth()));
					getGamePlay().getPreferences().put(StorageConstants.Keys.TESTING_ENGINE_DEPTH, String.valueOf(testingPanel.getJtEngineDepth()));
					
					getGamePlay().getDbManager().save(StorageConstants.Keys.TESTING_ENGINE_PACKAGE, String.valueOf(testingPanel.getJtEnginePackage()));
					getGamePlay().getPreferences().put(StorageConstants.Keys.TESTING_ENGINE_PACKAGE, String.valueOf(testingPanel.getJtEnginePackage()));
					
					getGamePlay().getDbManager().save(StorageConstants.Keys.TESTING_ENGINE_CLASS_1, String.valueOf(testingPanel.getEngineClass1()));
					getGamePlay().getPreferences().put(StorageConstants.Keys.TESTING_ENGINE_CLASS_1, String.valueOf(testingPanel.getEngineClass1()));
					getGamePlay().getDbManager().save(StorageConstants.Keys.TESTING_ENGINE_CLASS_2, String.valueOf(testingPanel.getEngineClass2()));
					getGamePlay().getPreferences().put(StorageConstants.Keys.TESTING_ENGINE_CLASS_2, String.valueOf(testingPanel.getEngineClass2()));
				} catch(Exception ex) {
					ex.printStackTrace();
				}
		    }
		});
		
		// testingPanel
		testingPanel = new TestingPanel(this);
		testingPanel.setLayout(null);
		testingPanel.setSize(700, 800);
		testingFrame.add(testingPanel);

		// Control Panel
		controlPanel = new ControlPanel(this);
		controlPanel.setSize(280, 500);
		controlPanel.setLocation(430, 10);
		mainOuterFrame.add(controlPanel);
		mainOuterFrame.setVisible(true);

		// Edit Mode Frame
		editModeFrame = new JFrame();
		editModeFrame.setLocationRelativeTo(null);
		editModeFrame.setLayout(null);
		editModeFrame.setResizable(false);
		editModeFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		editModeFrame.setAlwaysOnTop(true);
		editModeFrame.setSize(365, 140);

		// Edit Mode Panel
		editModePanel = new EditModePanel(this);
		editModePanel.setSize(400, 300);
		editModeFrame.add(editModePanel);
		
		// Promotion Frame
		promotionFrame = new JDialog();
		promotionFrame.setLocationRelativeTo(null);
		promotionFrame.setLayout(null);
		promotionFrame.setResizable(false);
		promotionFrame.setAlwaysOnTop(true);
		promotionFrame.setSize(215, 89);
		promotionFrame.setModal(true);
		promotionFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		promotionFrame.setLocation((int) MouseInfo.getPointerInfo().getLocation().getX(),
				(int) MouseInfo.getPointerInfo().getLocation().getY());

		// Edit Mode Panel
		promotionPanel = new PromotionPanel(this);
		promotionPanel.setSize(400, 300);
		promotionFrame.add(promotionPanel);
		
		ActionListener spaceListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getControlPanel().isEditMode()) {
					getEditModePanel().reEditCell();
				}
			}
		};
		getEditModeFrame().getRootPane().registerKeyboardAction(spaceListener, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		getEditModeFrame().getRootPane().registerKeyboardAction(new EscapeListener(getEditModeFrame()), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		ActionListener undoListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getGamePlay().undoMove();
			}
		};
		mainOuterFrame.getRootPane().registerKeyboardAction(undoListener, KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public BoardCell getSelectedCell() {
		return chessBoardPanel.getSelectedCell();
	}

	public byte[][] getBoard() {
		return chessBoardPanel.getBoard();
	}

	public void setBoard(byte[][] board) {
		chessBoardPanel.setBoard(board);
	}

	public ControlPanel getControlPanel() {
		return controlPanel;
	}

	public JFrame getEditModeFrame() {
		return editModeFrame;
	}

	public EditModePanel getEditModePanel() {
		return editModePanel;
	}

	public GamePlay getGamePlay() {
		return gamePlay;
	}

	public ChessBoardPanel getChessBoardPanel() {
		return chessBoardPanel;
	}

	public JFrame getMainOuterFrame() {
		return mainOuterFrame;
	}

	public void triggerTimerFinishEvent() {
		getGamePlay().triggerThreadFinishEvent();
	}
	
	public JDialog getPromotionFrame() {
		return promotionFrame;
	}
	
	public void runPopupPromotionFrame(int side){
		getPromotionFrame().setLocation((int) MouseInfo.getPointerInfo().getLocation().getX(),
				(int) MouseInfo.getPointerInfo().getLocation().getY());
		promotionPanel.setItems(side);
		promotionFrame.setVisible(true);
	}
	
	public PromotionPanel getPromotionPanel() {
		return promotionPanel;
	}
	
	public JFrame getDebugFrame() {
		return debugFrame;
	}
	
	public JFrame getTestingFrame() {
		return testingFrame;
	}

	public DebugPanel getDebugPanel() {
		return debugPanel;
	}
	
	public JDialog getPreferencesFrame() {
		return preferencesFrame;
	}
	
	public String getClassNameFromDb(String databaseKey){
		String className = "";
		if(getGamePlay().getPreferences().containsKey(databaseKey)){
			className = getGamePlay().getPreferences().get(databaseKey);
		}
		return className;
	}

	public GlassPane getGlassPane() {
		return glassPane;
	}

}
