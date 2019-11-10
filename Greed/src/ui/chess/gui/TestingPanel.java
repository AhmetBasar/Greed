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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import chess.database.StorageConstants;
import chess.engine.EngineConstants;
import chess.engine.ISearchableV2;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.Transformer;
import chess.engine.TranspositionTable;
import chess.game.GamePlay;

public class TestingPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private JTextField jtEnginePackage;
	private JTextField jtEngineClass1;
	private JTextField jtEngineClass2;
	private JTextField jtEngineDepth;
	private JButton jbStartTest;
	private JButton jbStopTest;
	private JButton jbRestartBoard;
	
	private JButton jbChangeEngine1;
	private JButton jbChangeEngine2;
	
	private BaseGui base;
	private GamePlay gamePlay;
	private volatile int i = 0;
	
	Class<?>[] engineParameters = new Class[] {int.class, int.class, int.class, long[].class, byte[].class, byte[][].class, int.class, long.class, long.class, int.class};
	Class<?>[] engineParametersV2 = new Class[] {SearchParameters.class};
	
	private volatile boolean suspended = true;
	
	private void waitIfSuspended() {
		synchronized (this) {
			while (suspended) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized void suspend() {
		suspended = true;
	}

	public synchronized void resume() {
		suspended = false;
		notify();
	}
	
	public TestingPanel(BaseGui base){
		this.base = base;
		gamePlay = base.getGamePlay();
		initComponents();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (;;) {
						i++;
						
						waitIfSuspended();
						
						System.out.println("Move - " + i);
						if (i == 8) {
							System.out.println("");
						}
						try {
							long t1 = System.currentTimeMillis();
							SearchResult searchResult1 = search(jtEngineClass1);
							long t2 = System.currentTimeMillis();
							SearchResult searchResult2 = search(jtEngineClass2);
							long t3 = System.currentTimeMillis();
							System.out.println("Engine-1 decided in " + (t2 - t1));
							System.out.println("Engine-2 decided in " + (t3 - t2));
							if (!searchResult1.equals(searchResult2)) {
								System.out.println("Not Equals");
								System.out.println("searchResult1 : " + searchResult1);
								System.out.println("searchResult2 : " + searchResult2);
								suspend();
							} else {
								SwingUtilities.invokeAndWait(new Runnable() {
									@Override
									public void run() {
										gamePlay.doMove(searchResult1.getBestMove());
									}
								});
							}
							
							waitIfSuspended();
						} catch (Exception e) {
							e.printStackTrace();
							suspend();
						}
						waitIfSuspended();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	private void initComponents() {
		
		JLabel lblEngineClass1 = new JLabel("Engine 1");
		lblEngineClass1.setSize(50, 25);
		lblEngineClass1.setLocation(10, 10);
		add(lblEngineClass1);
		
		jtEnginePackage = new JTextField();
		jtEnginePackage.setSize(250, 25);
		jtEnginePackage.setText(base.getClassNameFromDb(StorageConstants.Keys.TESTING_ENGINE_PACKAGE));
		jtEnginePackage.setLocation(75, 222);
		add(jtEnginePackage);
		
		jtEngineClass1 = new JTextField();
		jtEngineClass1.setSize(250, 25);
		jtEngineClass1.setText(base.getClassNameFromDb(StorageConstants.Keys.TESTING_ENGINE_CLASS_1));
		jtEngineClass1.setLocation(75, 10);
		add(jtEngineClass1);
		
		JLabel lblEngineClass2 = new JLabel("Engine 2");
		lblEngineClass2.setSize(50, 25);
		lblEngineClass2.setLocation(10, 50);
		add(lblEngineClass2);
		
		jtEngineClass2 = new JTextField();
		jtEngineClass2.setSize(250, 25);
		jtEngineClass2.setText(base.getClassNameFromDb(StorageConstants.Keys.TESTING_ENGINE_CLASS_2));
		jtEngineClass2.setLocation(75, 50);
		add(jtEngineClass2);
		
		jbStartTest = new JButton("Resume");
		jbStartTest.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbStartTest.setSize(99, 25);
		jbStartTest.setLocation(10, 100);
		add(jbStartTest);
		jbStartTest.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					gamePlay.setZobristKey(TranspositionTable.getZobristKey(Transformer.getBitboardStyl(base.getBoard()), gamePlay.getEpTarget(), gamePlay.getCastlingRights(), gamePlay.getSide()));
					gamePlay.setPawnZobristKey(TranspositionTable.getPawnZobristKey(Transformer.getBitboardStyl(base.getBoard())));
					resume();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}));
		
		jbStopTest = new JButton("Suspend");
		jbStopTest.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbStopTest.setSize(99, 25);
		jbStopTest.setLocation(110, 100);
		add(jbStopTest);
		jbStopTest.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					suspend();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}));
		
		jbRestartBoard = new JButton("Restart");
		jbRestartBoard.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbRestartBoard.setSize(99, 25);
		jbRestartBoard.setLocation(220, 100);
		add(jbRestartBoard);
		jbRestartBoard.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.getGamePlay().restartGame();
				i = 0;
			}
		}));
		
		jbChangeEngine1 = new JButton();
		jbChangeEngine1.setSize(90, 25);
		jbChangeEngine1.setText("Hyper");
		jbChangeEngine1.setLocation(75, 350);
		add(jbChangeEngine1);
		jbChangeEngine1.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtEngineClass1.setText("SearchEngineHyperModern");
				jtEngineClass2.setText("SearchEngineNegaMaxAlphaBetaTranspositionTableCheckSafeDrawSafeOnlyQueenPromotionsPreMove");
				jtEnginePackage.setText("chess.engineTranspositionTable");
			}
		}));
		
		jbChangeEngine2 = new JButton();
		jbChangeEngine2.setSize(90, 25);
		jbChangeEngine2.setText("Traditional");
		jbChangeEngine2.setLocation(175, 350);
		add(jbChangeEngine2);
		jbChangeEngine2.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtEngineClass1.setText("SearchEngineNegaMaxAlphaBetaBoard");
				jtEngineClass2.setText("SearchEngineNegaMaxAlphaBeta");
				jtEnginePackage.setText("chess.engine");
			}
		}));
		
		
		JLabel lblEngineDepth = new JLabel("Depth ");
		lblEngineDepth.setSize(50, 25);
		lblEngineDepth.setLocation(15, 150);
		add(lblEngineDepth);
		
		jtEngineDepth = new JTextField();
		jtEngineDepth.setSize(90, 25);
		jtEngineDepth.setText(base.getClassNameFromDb(StorageConstants.Keys.TESTING_ENGINE_DEPTH));
		jtEngineDepth.setLocation(75, 150);
		add(jtEngineDepth);
		
	}
	
	private SearchResult search(JTextField jtEngineClass) throws Exception {
		Class<?> cls = Class.forName(jtEnginePackage.getText() + "." + jtEngineClass.getText());
//		Object obj = cls.newInstance();
		
		Method m1= cls.getDeclaredMethod("getInstance",new Class[] {});
		Object obj = m1.invoke(null);
		
		//
		Method m3 = cls.getDeclaredMethod("resetTT", new Class[] {});
		m3.invoke(obj);
		//
		
		Method m2 = cls.getDeclaredMethod("setBoardStateHistory", new Class[] {Map.class});
		m2.invoke(obj, gamePlay.getBoardStateHistory());
		
		SearchResult searchResult = null;
		boolean isV2 = (obj instanceof ISearchableV2);
		if (isV2) {
			Method method = cls.getDeclaredMethod("search", engineParametersV2);
			
			SearchParameters params = new SearchParameters();
			params.setDepth(Integer.parseInt(jtEngineDepth.getText()));
			params.setEpT(gamePlay.getEpTarget());
			params.setEpS(gamePlay.getEpSquare());
			params.setBitboard(Transformer.getBitboardStyl(base.getBoard()));
			params.setPieces(Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getBoard())));
			params.setCastlingRights(gamePlay.getCastlingRights());
			params.setSide(gamePlay.getSide());
			params.setUiZobristKey(gamePlay.getZobristKey());
			params.setTimeLimit(1L);
			params.setFiftyMoveCounter(gamePlay.getFiftyMoveCounter());
			params.setEngineMode(EngineConstants.EngineMode.FIXED_DEPTH);
			params.setBookName(null);
			
			searchResult = (SearchResult) method.invoke(obj, params);
		} else {
			Method method = cls.getDeclaredMethod("search", engineParameters);
			
//		long ilk = System.currentTimeMillis();
			searchResult = (SearchResult) method.invoke(obj, Integer.parseInt(jtEngineDepth.getText()), 
					gamePlay.getEpTarget(), gamePlay.getEpSquare(), Transformer.getBitboardStyl(base.getBoard()),
					Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getBoard())),
					gamePlay.getCastlingRights(), gamePlay.getSide(), gamePlay.getZobristKey(), 1L, gamePlay.getFiftyMoveCounter());
//		System.out.println("time consumed =  "+ (System.currentTimeMillis() - ilk));
			
		}
		
		
		return searchResult;
	}
	
	public String getJtEngineDepth() {
		return jtEngineDepth.getText();
	}

	public String getJtEnginePackage() {
		return jtEnginePackage.getText();
	}

	public String getEngineClass1() {
		return jtEngineClass1.getText();
	}
	
	
	
	public String getEngineClass2() {
		return jtEngineClass2.getText();
	}
	
	public synchronized void setEnableAll(boolean enable){
		enableComponents(base.getDebugPanel(), enable);
	}
	
	private void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container)component, enable);
            }
        }
    }

}