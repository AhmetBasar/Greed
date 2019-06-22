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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Method;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import chess.database.StorageConstants;
import chess.engine.EngineConstants;
import chess.engine.ISearchableV2;
import chess.engine.SearchParameters;
import chess.engine.SearchResult;
import chess.engine.Transformer;
import chess.game.GamePlay;

public class ControlPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JCheckBox jcbEditMode;
	private JButton jbChangePerspective;
	private JButton jbChangeTurn;
	private JButton jbClearBoard;
	private JButton jbDebugMode;
	private JButton jbTesting;
	private JButton jbSuggestMove1;
	private JButton jbSuggestMove2;
	private JButton jbSuggestMove3;
	private JTextField jtEngineClass1;
	private JTextField jtEngineClass2;
	private JTextField jtEngineClass3;
	
	private JCheckBox jcbWhiteEngine;
	private JCheckBox jcbBlackEngine;
	
	private BaseGui base;
	private GamePlay gamePlay;
	private DebugPanel debugPanel;
	
	public boolean isWhiteEngineEnabled(){
		return jcbWhiteEngine.isSelected();
	}
	
	public boolean isBlackEngineEnabled(){
		return jcbBlackEngine.isSelected();
	}

	public ControlPanel(BaseGui base) {
		this.base = base;
		this.gamePlay = base.getGamePlay();
		this.debugPanel = base.getDebugPanel();
		initComponents();
	}

	public boolean isEditMode() {
		return jcbEditMode.isSelected();
	}

	private void initComponents() {
		jcbEditMode = new javax.swing.JCheckBox();
		setBorder(BorderFactory.createTitledBorder("Control Panel"));
		setLayout(null);
		jcbEditMode.setText("Edit Mode");
		add(jcbEditMode);
		jcbEditMode.setBounds(10, 20, 81, 23);
		
		jcbWhiteEngine = new javax.swing.JCheckBox();
		setLayout(null);
		jcbWhiteEngine.setText("White Engine");
		add(jcbWhiteEngine);
		jcbWhiteEngine.setBounds(110, 20, 81, 23);
		
		jcbBlackEngine = new javax.swing.JCheckBox();
		setLayout(null);
		jcbBlackEngine.setText("Black Engine");
		add(jcbBlackEngine);
		jcbBlackEngine.setBounds(210, 20, 81, 23);
		
		jbChangePerspective = new JButton("Change Perspective");
		jbChangePerspective.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbChangePerspective.setSize(125, 25);
		jbChangePerspective.setLocation(10, 50);
		add(jbChangePerspective);
		jbChangePerspective.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changePerspectiveMouseClicked(e);
			}
		}));
		
		jbChangeTurn = new JButton(gamePlay.getSide() == GuiConstants.WHITES_TURN ? "White's turn" : "Black's turn");
		jbChangeTurn.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbChangeTurn.setSize(85, 25);
		jbChangeTurn.setLocation(150, 50);
		add(jbChangeTurn);
		jbChangeTurn.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gamePlay.reverseTurn();
				jbChangeTurn.setText(gamePlay.getSide() == GuiConstants.WHITES_TURN ? "White's turn" : "Black's turn");
			}
		}));
		
		jbClearBoard = new JButton("Clear Board");
		jbClearBoard.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbClearBoard.setSize(85 , 25);
		jbClearBoard.setLocation(10, 80);
		add(jbClearBoard);
		jbClearBoard.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.getChessBoardPanel().clearBoard();
				for(int i = 0 ; i < 64 ; i++){
					base.getChessBoardPanel().getCell(i).setDebugItemChangeCount(0);
					base.getChessBoardPanel().getCell(i).repaint();
				}
			}
		}));
		
		jbDebugMode = new JButton("Debug");
		jbDebugMode.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbDebugMode.setSize(100, 25);
		jbDebugMode.setLocation(110, 80);
		add(jbDebugMode);
		jbDebugMode.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.getDebugFrame().setVisible(true);
			}
		}));
		
		jbTesting = new JButton("Testing Framework");
		jbTesting.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbTesting.setSize(150, 25);
		jbTesting.setLocation(110, 110);
		add(jbTesting);
		jbTesting.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.getTestingFrame().setVisible(true);
			}
		}));
		
		jbSuggestMove1 = new JButton("");
		jbSuggestMove1.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSuggestMove1.setSize(250, 25);
		jbSuggestMove1.setLocation(10, 180);
		add(jbSuggestMove1);
		jtEngineClass1 = new JTextField();
		jtEngineClass1.setSize(250, 25);
		jtEngineClass1.setText(getClassNameFromDb(StorageConstants.Keys.ENGINE_CLASS_1));
		jtEngineClass1.setLocation(10, 150);
		add(jtEngineClass1);
		jtEngineClass1.addFocusListener(new ClassNameFocusListener(base, jtEngineClass1, jbSuggestMove1, StorageConstants.Keys.ENGINE_CLASS_1));
		jbSuggestMove1.setText(getFormattedClassName(jtEngineClass1.getText()));
		decideButtonActivity(jbSuggestMove1, jtEngineClass1.getText());
		jbSuggestMove1.addActionListener(new SuggestMoveActionListener(base, gamePlay, debugPanel, jtEngineClass1));

		
		
		
		
		
		jbSuggestMove2 = new JButton("");
		jbSuggestMove2.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSuggestMove2.setSize(250, 25);
		jbSuggestMove2.setLocation(10, 260);
		add(jbSuggestMove2);
		jtEngineClass2 = new JTextField();
		jtEngineClass2.setSize(250, 25);
		jtEngineClass2.setText(getClassNameFromDb(StorageConstants.Keys.ENGINE_CLASS_2));
		jtEngineClass2.setLocation(10, 230);
		add(jtEngineClass2);
		jtEngineClass2.addFocusListener(new ClassNameFocusListener(base, jtEngineClass2, jbSuggestMove2, StorageConstants.Keys.ENGINE_CLASS_2));
		jbSuggestMove2.setText(getFormattedClassName(jtEngineClass2.getText()));
		decideButtonActivity(jbSuggestMove2, jtEngineClass2.getText());
		jbSuggestMove2.addActionListener(new SuggestMoveActionListener(base, gamePlay, debugPanel, jtEngineClass2));
		
		
		
		
		jbSuggestMove3 = new JButton("");
		jbSuggestMove3.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSuggestMove3.setSize(250, 25);
		jbSuggestMove3.setLocation(10, 340);
		add(jbSuggestMove3);
		jtEngineClass3 = new JTextField();
		jtEngineClass3.setSize(250, 25);
		jtEngineClass3.setText(getClassNameFromDb(StorageConstants.Keys.ENGINE_CLASS_3));
		jtEngineClass3.setLocation(10, 310);
		add(jtEngineClass3);
		jtEngineClass3.addFocusListener(new ClassNameFocusListener(base, jtEngineClass3, jbSuggestMove3, StorageConstants.Keys.ENGINE_CLASS_3));
		jbSuggestMove3.setText(getFormattedClassName(jtEngineClass3.getText()));
		decideButtonActivity(jbSuggestMove3, jtEngineClass3.getText());
		jbSuggestMove3.addActionListener(new SuggestMoveActionListener(base, gamePlay, debugPanel, jtEngineClass3));
		
		
	}
	
	public void refreshTurnView(){
		jbChangeTurn.setText(gamePlay.getSide() == GuiConstants.WHITES_TURN ? "White's turn" : "Black's turn");
		base.getDebugPanel().refreshTurnView();
	}

	private void changePerspectiveMouseClicked(ActionEvent e) {
		base.getChessBoardPanel().changePerspective();
	}
	
	static String getFormattedClassName(String fullClassName){
		if(fullClassName.substring(fullClassName.lastIndexOf(".") + 1).length() > 12){
			return fullClassName.substring(fullClassName.lastIndexOf(".") + 1).substring(12);
		} else {
			return fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
		}
	}
	
	static void decideButtonActivity(JButton button, String className){
		try{
			Class.forName(className);
			button.setEnabled(true);
		} catch(Exception ex){
			button.setEnabled(false);	
		}
	}
	
	private String getClassNameFromDb(String databaseKey){
		String className = "";
		if(base.getGamePlay().getPreferences().containsKey(databaseKey)){
			className = base.getGamePlay().getPreferences().get(databaseKey);
		}
		return className;
	}
	
}

class ClassNameFocusListener implements FocusListener {
	
	private JTextField jtClassName;
	private JButton suggestButton;
	private BaseGui base;
	private String databaseKey;
	
	public ClassNameFocusListener(BaseGui base, JTextField jtClassName, JButton suggestButton, String databaseKey){
		this.base = base;
		this.jtClassName = jtClassName;
		this.suggestButton = suggestButton;
		this.databaseKey = databaseKey;
	}

	@Override
	public void focusGained(FocusEvent arg0) {
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		try{
			base.getGamePlay().getDbManager().save(databaseKey, jtClassName.getText());
			suggestButton.setText(ControlPanel.getFormattedClassName(jtClassName.getText()));
		} catch(Exception e) {
			e.printStackTrace();
		}
		ControlPanel.decideButtonActivity(suggestButton, jtClassName.getText());
	}
}

class SuggestMoveActionListener implements ActionListener {
	
	private BaseGui base;
	private GamePlay gamePlay;
	private DebugPanel debugPanel;
	private JTextField jtClassName;
	Class<?>[] engineParameters = new Class[] {int.class, int.class, int.class, long[].class, byte[].class, byte[][].class, int.class, long.class, long.class, int.class};
	Class<?>[] engineParametersV2 = new Class[] {SearchParameters.class};
	
	public SuggestMoveActionListener(BaseGui base, GamePlay gamePlay, DebugPanel debugPanel, JTextField jtClassName){
		this.base = base;
		this.gamePlay = gamePlay;
		this.debugPanel = debugPanel;
		this.jtClassName = jtClassName;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
//		if (true) {
//			System.out.println("sa");
//			int[] moveArr = new int[gamePlay.getMoveHistory().size()];
//			for (int i = 0; i < gamePlay.getMoveHistory().size(); i++) {
//				GamePlayMove m = gamePlay.getMoveHistory().get(i);
//				moveArr[i] = m.getMove();
//			}
//			
//			System.out.println(Arrays.toString(moveArr));
//			
//			return;
//		}
		
		gamePlay.recalculateZobristKey();
		
		if(PieceEffects.existsActiveTimer()){
			return;
		}
		try{
			Class<?> cls = Class.forName(jtClassName.getText());
//				Object obj = cls.newInstance();
			
			Method m1= cls.getDeclaredMethod("getInstance",new Class[] {});
			Object obj = m1.invoke(null);
			
			//
			Method m2 = cls.getDeclaredMethod("setBoardStateHistory", new Class[] {Map.class});
			m2.invoke(obj, gamePlay.getBoardStateHistory());
			//
			
			//
//				Method m3 = cls.getDeclaredMethod("resetTT", new Class[] {});
//				m3.invoke(obj);
			//
			
			long ilk = System.currentTimeMillis();
			
			SearchResult move = null;
			boolean isV2 = (obj instanceof ISearchableV2);
			if (isV2) {
				Method method = cls.getDeclaredMethod("search", engineParametersV2);
				
				SearchParameters params = new SearchParameters();
				params.setDepth(debugPanel.getSearchDepth());
				params.setEpT(gamePlay.getEpTarget());
				params.setEpS(gamePlay.getEpSquare());
				params.setBitboard(Transformer.getBitboardStyl(base.getBoard()));
				params.setPieces(Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getBoard())));
				params.setCastlingRights(gamePlay.getCastlingRights());
				params.setSide(gamePlay.getSide());
				params.setUiZobristKey(GamePlay.getZobristKey());
				params.setTimeLimit(1L);
				params.setFiftyMoveCounter(gamePlay.getFiftyMoveCounter());
				params.setEngineMode(EngineConstants.EngineMode.FIXED_DEPTH);
				
				move = (SearchResult) method.invoke(obj, params);
			} else {
				
				Method method = cls.getDeclaredMethod("search", engineParameters);
				move = (SearchResult) method.invoke(obj, debugPanel.getSearchDepth(), 
						gamePlay.getEpTarget(), gamePlay.getEpSquare(), Transformer.getBitboardStyl(base.getBoard()),
						Transformer.getByteArrayStyl(Transformer.getBitboardStyl(base.getBoard())),
						gamePlay.getCastlingRights(), gamePlay.getSide(), GamePlay.getZobristKey(), 1, gamePlay.getFiftyMoveCounter());
			}
			
			System.out.println("time consumed =  "+ (System.currentTimeMillis() - ilk));
			gamePlay.doMove(move.getBestMove());
				
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
