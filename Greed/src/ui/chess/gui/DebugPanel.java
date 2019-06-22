/**********************************************
 * Greed, a chess engine written in java.
 * Copyright (C) 2019 Ahmet Ba�ar
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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import chess.database.StorageConstants;
import chess.debug.DebugUtility;
import chess.debug.PerformanceTesting;
import chess.debug.PerformanceTestingSimple;
import chess.debug.PerformanceTestingSingleThreaded;
import chess.debug.PerformanceTestingSingleThreadedCopyMake;

public class DebugPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	
	// Thread Count
	private JTextField jtThredCount;
	// Search Depth
	private JTextField jtSearchDepth;
	// Castling Rights
	private JCheckBox jcbWhiteQueenSideCastlingRight;
	private JCheckBox jcbWhiteKingSideCastlingRight;
	private JCheckBox jcbBlackQueenSideCastlingRight;
	private JCheckBox jcbBlackKingSideCastlingRight;
	// Enpassant
	private JTextField jtEnpassantTarget;
	private JTextField jtEnpassantSquare;
	// FEN
	private JTextField jtFEN;
	
	// Results
	private JTextPane jtpResult;
	private byte[][] castlingRights = new byte[2][2];
	int threadCount;
	
	private JButton jbSearch;
	private JButton jbSearchSimple;
	private JButton jbSearchSimpleKinderGarten;
	private JButton jbSearchCopyMake;
	private JCheckBox jcbUseActualGameParameters;
	private JButton jbSearchBoard;
	private JButton jbSearchBoardThreaded;
	private JButton jbChangeTurn;
	private JButton jbSetFEN;
	private JButton jbClearFEN;
	private JButton jbGetFEN;
	private BaseGui base;
	public DebugPanel(BaseGui base){
		this.base = base;
		initComponents();
	}
	
	private void initComponents() {
		buildThreadInfo();
		buildCastlingRights();
		buildTestBoards();
		buildSearchDepthInfo();
		buildEnpassantInfo();
		buildFENInfo();
		
		jtpResult = new JTextPane();
		jtpResult.setSize(410,170);
		jtpResult.setLocation(10,220);
		add(jtpResult);
		
		jbSearch = new JButton("Search");
		jbSearch.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSearch.setSize(80, 25);
		jbSearch.setLocation(340, 70);
		add(jbSearch);
		jbSearch.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtpResult.setText("");
				castlingRights[0][0] = jcbWhiteQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[0][1] = jcbWhiteKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][0] = jcbBlackQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][1] = jcbBlackKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				threadCount = Integer.parseInt(jtThredCount.getText());
				
				if (jcbUseActualGameParameters.isSelected()) {
					PerformanceTesting.getAllVariations(base.getBoard(),
							base.getGamePlay().getSide(),
							Integer.parseInt(jtSearchDepth.getText()),
							base.getGamePlay().getCastlingRights(),
							base,
							threadCount,
							base.getGamePlay().getEpTarget(),
							base.getGamePlay().getEpSquare());
				} else {
					PerformanceTesting.getAllVariations(base.getBoard(), base.getGamePlay().getSide(), Integer.parseInt(jtSearchDepth.getText()), castlingRights, base, threadCount,
							Integer.parseInt(jtEnpassantTarget.getText()), Integer.parseInt(jtEnpassantSquare.getText()));
				}
				
				
				setEnableAll(false);
			}
		}));
		
		jbSearchSimple = new JButton("Search Simple");
		jbSearchSimple.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSearchSimple.setSize(80, 25);
		jbSearchSimple.setLocation(340, 100);
		add(jbSearchSimple);
		jbSearchSimple.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtpResult.setText("");
				castlingRights[0][0] = jcbWhiteQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[0][1] = jcbWhiteKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][0] = jcbBlackQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][1] = jcbBlackKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				threadCount = Integer.parseInt(jtThredCount.getText());
				
				if (jcbUseActualGameParameters.isSelected()) {
					PerformanceTestingSimple.getAllVariations(base.getBoard(),
							base.getGamePlay().getSide(),
							Integer.parseInt(jtSearchDepth.getText()),
							base.getGamePlay().getCastlingRights(),
							base,
							threadCount,
							base.getGamePlay().getEpTarget(),
							base.getGamePlay().getEpSquare());
				} else {
					PerformanceTestingSimple.getAllVariations(base.getBoard(), base.getGamePlay().getSide(), Integer.parseInt(jtSearchDepth.getText()), castlingRights, base, threadCount,
							Integer.parseInt(jtEnpassantTarget.getText()), Integer.parseInt(jtEnpassantSquare.getText()));
				}
				
				setEnableAll(false);
			}
		}));
		
		jbSearchSimpleKinderGarten = new JButton("KinderGarten");
		jbSearchSimpleKinderGarten.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSearchSimpleKinderGarten.setSize(120, 25);
		jbSearchSimpleKinderGarten.setLocation(300, 190);
		add(jbSearchSimpleKinderGarten);
		jbSearchSimpleKinderGarten.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtpResult.setText("");
				castlingRights[0][0] = jcbWhiteQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[0][1] = jcbWhiteKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][0] = jcbBlackQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][1] = jcbBlackKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				threadCount = Integer.parseInt(jtThredCount.getText());
				
//				if (jcbUseActualGameParameters.isSelected()) {
//					PerformanceTestingSimpleKinderGarten.getAllVariations(base.getBoard(),
//							base.getGamePlay().getSide(),
//							Integer.parseInt(jtSearchDepth.getText()),
//							base.getGamePlay().getCastlingRights(),
//							base,
//							threadCount,
//							base.getGamePlay().getEpTarget(),
//							base.getGamePlay().getEpSquare());
//				} else {
//					PerformanceTestingSimpleKinderGarten.getAllVariations(base.getBoard(), base.getGamePlay().getSide(), Integer.parseInt(jtSearchDepth.getText()), castlingRights, base, threadCount,
//							Integer.parseInt(jtEnpassantTarget.getText()), Integer.parseInt(jtEnpassantSquare.getText()));
//				}
				
				setEnableAll(false);
			}
		}));
		
		jbSearchCopyMake = new JButton("S.Cop");
		jbSearchCopyMake.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSearchCopyMake.setSize(50, 25);
		jbSearchCopyMake.setLocation(340, 160);
		add(jbSearchCopyMake);
		jbSearchCopyMake.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtpResult.setText("");
				castlingRights[0][0] = jcbWhiteQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[0][1] = jcbWhiteKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][0] = jcbBlackQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][1] = jcbBlackKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				threadCount = Integer.parseInt(jtThredCount.getText());
				
				if (jcbUseActualGameParameters.isSelected()) {
					PerformanceTestingSingleThreadedCopyMake.getAllVariations(base.getBoard(),
							base.getGamePlay().getSide(),
							Integer.parseInt(jtSearchDepth.getText()),
							base.getGamePlay().getCastlingRights(),
							base,
							threadCount,
							base.getGamePlay().getEpTarget(),
							base.getGamePlay().getEpSquare());
				} else {
					PerformanceTestingSingleThreadedCopyMake.getAllVariations(base.getBoard(), base.getGamePlay().getSide(), Integer.parseInt(jtSearchDepth.getText()), castlingRights, base, threadCount,
							Integer.parseInt(jtEnpassantTarget.getText()), Integer.parseInt(jtEnpassantSquare.getText()));
				}
//				setEnableAll(false);
			}
		}));
		
		jcbUseActualGameParameters = new JCheckBox();
		jcbUseActualGameParameters.setText("asdfasdfasdfasdfsad");
		jcbUseActualGameParameters.setSelected(false);
		jcbUseActualGameParameters.setSize(50, 25);
		jcbUseActualGameParameters.setLocation(390, 160);
		add(jcbUseActualGameParameters);
		
		jbSearchBoardThreaded = new JButton("S.Thre");
		jbSearchBoardThreaded.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSearchBoardThreaded.setSize(50, 25);
		jbSearchBoardThreaded.setLocation(290, 160);
		add(jbSearchBoardThreaded);
		jbSearchBoardThreaded.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtpResult.setText("");
				castlingRights[0][0] = jcbWhiteQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[0][1] = jcbWhiteKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][0] = jcbBlackQueenSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				castlingRights[1][1] = jcbBlackKingSideCastlingRight.isSelected() ? (byte)1 : (byte)0;
				threadCount = Integer.parseInt(jtThredCount.getText());
				
				
				setEnableAll(false);
				if (jcbUseActualGameParameters.isSelected()) {
					PerformanceTestingSingleThreaded.getAllVariations(base.getBoard(),
							base.getGamePlay().getSide(),
							Integer.parseInt(jtSearchDepth.getText()),
							base.getGamePlay().getCastlingRights(),
							base,
							threadCount,
							base.getGamePlay().getEpTarget(),
							base.getGamePlay().getEpSquare());
				} else {
					PerformanceTestingSingleThreaded.getAllVariations(base.getBoard(), base.getGamePlay().getSide(), Integer.parseInt(jtSearchDepth.getText()), castlingRights, base, threadCount,
							Integer.parseInt(jtEnpassantTarget.getText()), Integer.parseInt(jtEnpassantSquare.getText()));
				}
				
			}
		}));
		
		
		
		//
		
		
		
		jbChangeTurn = new JButton(base.getGamePlay().getSide() == GuiConstants.WHITES_TURN ? "White's turn" : "Black's turn");
		jbChangeTurn.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbChangeTurn.setSize(80, 25);
		jbChangeTurn.setLocation(250, 70);
		add(jbChangeTurn);
		jbChangeTurn.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.getGamePlay().reverseTurn();
				jbChangeTurn.setText(base.getGamePlay().getSide() == GuiConstants.WHITES_TURN ? "White's turn" : "Black's turn");
			}
		}));
		

	}
	
	public void refreshTurnView(){
		jbChangeTurn.setText(base.getGamePlay().getSide() == GuiConstants.WHITES_TURN ? "White's turn" : "Black's turn");
	}

	private void buildCastlingRights(){
		JLabel lblCastlingRights;
		JLabel lblWhiteQueenSideCastlingRight;
		JLabel lblWhiteKingSideCastlingRight;
		JLabel lblBlackQueenSideCastlingRight;
		JLabel lblBlackKingSideCastlingRight;
		
		lblCastlingRights = new JLabel("Castling Right :");
		lblCastlingRights.setSize(100, 25);
		lblCastlingRights.setLocation(10, 70);
		add(lblCastlingRights);
		
		lblWhiteQueenSideCastlingRight = new JLabel("Q ");
		lblWhiteQueenSideCastlingRight.setSize(25, 25);
		lblWhiteQueenSideCastlingRight.setLocation(105, 70);
		add(lblWhiteQueenSideCastlingRight);
		
		jcbWhiteQueenSideCastlingRight = new JCheckBox();
		jcbWhiteQueenSideCastlingRight.setSelected(true);
		jcbWhiteQueenSideCastlingRight.setSize(20, 20);
		jcbWhiteQueenSideCastlingRight.setLocation(115, 72);
		add(jcbWhiteQueenSideCastlingRight);
		
		lblWhiteKingSideCastlingRight = new JLabel("K ");
		lblWhiteKingSideCastlingRight.setSize(25, 25);
		lblWhiteKingSideCastlingRight.setLocation(140, 70);
		add(lblWhiteKingSideCastlingRight);
		
		jcbWhiteKingSideCastlingRight = new JCheckBox();
		jcbWhiteKingSideCastlingRight.setSelected(true);
		jcbWhiteKingSideCastlingRight.setSize(20, 20);
		jcbWhiteKingSideCastlingRight.setLocation(150, 72);
		add(jcbWhiteKingSideCastlingRight);
		
		lblBlackQueenSideCastlingRight = new JLabel("q ");
		lblBlackQueenSideCastlingRight.setSize(25, 25);
		lblBlackQueenSideCastlingRight.setLocation(175, 70);
		add(lblBlackQueenSideCastlingRight);
		
		jcbBlackQueenSideCastlingRight = new JCheckBox();
		jcbBlackQueenSideCastlingRight.setSelected(true);
		jcbBlackQueenSideCastlingRight.setSize(20, 20);
		jcbBlackQueenSideCastlingRight.setLocation(185, 72);
		add(jcbBlackQueenSideCastlingRight);
		
		lblBlackKingSideCastlingRight = new JLabel("k ");
		lblBlackKingSideCastlingRight.setSize(25, 25);
		lblBlackKingSideCastlingRight.setLocation(210, 70);
		add(lblBlackKingSideCastlingRight);
		
		jcbBlackKingSideCastlingRight = new JCheckBox();
		jcbBlackKingSideCastlingRight.setSelected(true);
		jcbBlackKingSideCastlingRight.setSize(20, 20);
		jcbBlackKingSideCastlingRight.setLocation(220, 72);
		add(jcbBlackKingSideCastlingRight);
	}
	
	private void buildTestBoards(){
		JButton jbGetBoard1;
		JButton jbGetBoard2;
		JButton jbGetBoard3;
		JButton jbGetBoard4;
		JButton jbGetBoard5;
		JButton jbGetBoard6;
		
		jbGetBoard1 = new JButton("board 1");
		jbGetBoard1.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbGetBoard1.setSize(70, 25);
		jbGetBoard1.setLocation(210, 10);
		add(jbGetBoard1);
		jbGetBoard1.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.setBoard(DebugUtility.getDefaultBoard());
			}
		}));
		
		jbGetBoard2 = new JButton("board 2");
		jbGetBoard2.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbGetBoard2.setSize(70, 25);
		jbGetBoard2.setLocation(280, 10);
		add(jbGetBoard2);
		jbGetBoard2.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.setBoard(DebugUtility.getBoard2());
			}
		}));
		
		jbGetBoard3 = new JButton("board 3");
		jbGetBoard3.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbGetBoard3.setSize(70, 25);
		jbGetBoard3.setLocation(350, 10);
		add(jbGetBoard3);
		jbGetBoard3.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.setBoard(DebugUtility.getBoard3());
			}
		}));
		
		jbGetBoard4 = new JButton("board 4");
		jbGetBoard4.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbGetBoard4.setSize(70, 25);
		jbGetBoard4.setLocation(210, 40);
		add(jbGetBoard4);
		jbGetBoard4.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.setBoard(DebugUtility.getBoard4());
			}
		}));
		
		jbGetBoard5 = new JButton("board 5");
		jbGetBoard5.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbGetBoard5.setSize(70, 25);
		jbGetBoard5.setLocation(280, 40);
		add(jbGetBoard5);
		jbGetBoard5.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.setBoard(DebugUtility.getBoard5());
			}
		}));
		
		jbGetBoard6 = new JButton("board 6");
		jbGetBoard6.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbGetBoard6.setSize(70, 25);
		jbGetBoard6.setLocation(350, 40);
		add(jbGetBoard6);
		jbGetBoard6.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				base.setBoard(DebugUtility.getBoard6());
			}
		}));
	}

	private void buildThreadInfo(){
		JLabel lblThreadCount;
		JButton jbIncrementThreadCount;
		JButton jbDecrementThreadCount;
		
		lblThreadCount = new JLabel("Thread Count: ");
		lblThreadCount.setSize(100, 25);
		lblThreadCount.setLocation(10, 10);
		add(lblThreadCount);
		
		jtThredCount = new JTextField();
		jtThredCount.setEditable(false);
		jtThredCount.setText("5");
		jtThredCount.setSize(35, 25);
		jtThredCount.setLocation(100, 10);
		add(jtThredCount);
		
		jbIncrementThreadCount = new JButton("+");
		jbIncrementThreadCount.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbIncrementThreadCount.setSize(25, 25);
		jbIncrementThreadCount.setLocation(140, 10);
		add(jbIncrementThreadCount);
		jbIncrementThreadCount.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtThredCount.setText("" + (1+Integer.parseInt(jtThredCount.getText())));
				if(Integer.parseInt(jtThredCount.getText()) > 20){
					jtThredCount.setText("20");
				}
			}
		}));
		
		jbDecrementThreadCount = new JButton("-");
		jbDecrementThreadCount.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbDecrementThreadCount.setSize(25, 25);
		jbDecrementThreadCount.setLocation(170, 10);
		add(jbDecrementThreadCount);
		jbDecrementThreadCount.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtThredCount.setText("" + (-1+Integer.parseInt(jtThredCount.getText())));
				if(Integer.parseInt(jtThredCount.getText()) == 0){
					jtThredCount.setText("1");
				}
			}
		}));
	}

	private void buildSearchDepthInfo(){
		JLabel lblSearchDepth;
		JButton jbIncrementSearchDepth;
		JButton jbDecrementSearchDepth;
		
		lblSearchDepth = new JLabel("Search Depth: ");
		lblSearchDepth.setSize(100, 25);
		lblSearchDepth.setLocation(10, 40);
		add(lblSearchDepth);
		
		jtSearchDepth = new JTextField();
		jtSearchDepth.setEditable(false);
		jtSearchDepth.setSize(35, 25);
		jtSearchDepth.setLocation(100, 40);
    	int preferredDepth = 5;
    	if(base.getGamePlay().getPreferences().containsKey(StorageConstants.Keys.DEPTH)){
    		preferredDepth = Integer.parseInt(base.getGamePlay().getPreferences().get(StorageConstants.Keys.DEPTH));
    	}
		jtSearchDepth.setText(String.valueOf(preferredDepth));
		add(jtSearchDepth);
		
		jbIncrementSearchDepth = new JButton("+");
		jbIncrementSearchDepth.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbIncrementSearchDepth.setSize(25, 25);
		jbIncrementSearchDepth.setLocation(140, 40);
		add(jbIncrementSearchDepth);
		jbIncrementSearchDepth.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtSearchDepth.setText("" + (1+Integer.parseInt(jtSearchDepth.getText())));
				if(Integer.parseInt(jtSearchDepth.getText()) > 20){
					jtSearchDepth.setText("20");
				}
			}
		}));
		
		jbDecrementSearchDepth = new JButton("-");
		jbDecrementSearchDepth.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbDecrementSearchDepth.setSize(25, 25);
		jbDecrementSearchDepth.setLocation(170, 40);
		add(jbDecrementSearchDepth);
		jbDecrementSearchDepth.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtSearchDepth.setText("" + (-1+Integer.parseInt(jtSearchDepth.getText())));
				if(Integer.parseInt(jtSearchDepth.getText()) == 0){
					jtSearchDepth.setText("1");
				}
			}
		}));
	}
	
	private void buildEnpassantInfo(){
		JLabel lblEnpassantTarget;
		JLabel lblEnpassantSquare;
		
		lblEnpassantTarget = new JLabel("Enpassant Target : ");
		lblEnpassantTarget.setSize(110, 25);
		lblEnpassantTarget.setLocation(10, 100);
		add(lblEnpassantTarget);
		 
		jtEnpassantTarget = new JTextField();
		jtEnpassantTarget.setSize(35, 25);
		jtEnpassantTarget.setLocation(130, 100);
		jtEnpassantTarget.setText("64");
		add(jtEnpassantTarget);
			
		
		lblEnpassantSquare = new JLabel("Enpassant Square : ");
		lblEnpassantSquare.setSize(120, 25);
		lblEnpassantSquare.setLocation(170, 100);
		add(lblEnpassantSquare);
		
		jtEnpassantSquare = new JTextField();
		jtEnpassantSquare.setSize(35, 25);
		jtEnpassantSquare.setLocation(285, 100);
		jtEnpassantSquare.setText("-1");
		add(jtEnpassantSquare);
		
	}
	
	private void buildFENInfo(){
		JLabel lblFEN;
		
		lblFEN = new JLabel("FEN : ");
		lblFEN.setSize(40, 25);
		lblFEN.setLocation(10, 130);
		add(lblFEN);
		 
		jtFEN = new JTextField();
		jtFEN.setSize(370, 25);
		jtFEN.setLocation(50, 130);
		add(jtFEN);
		
		jbSetFEN = new JButton("set FEN");
		jbSetFEN.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSetFEN.setSize(80, 25);
		jbSetFEN.setLocation(10, 160);
		add(jbSetFEN);
		jbSetFEN.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set FEN
				try{
					new FenOperations(base).setFenString(null);
				}catch(Exception ex){
					JOptionPane.showMessageDialog(base.getDebugFrame(),
						    ex.getMessage(),
						    "Inane error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		}));
		
		jbGetFEN = new JButton("get FEN");
		jbGetFEN.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbGetFEN.setSize(80, 25);
		jbGetFEN.setLocation(90, 160);
		add(jbGetFEN);
		jbGetFEN.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get FEN
			}
		}));
		
		jbClearFEN = new JButton("clear FEN");
		jbClearFEN.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbClearFEN.setSize(80, 25);
		jbClearFEN.setLocation(170, 160);
		add(jbClearFEN);
		jbClearFEN.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jtFEN.setText("");
			}
		}));		jbSetFEN = new JButton("set FEN");
		jbSetFEN.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbSetFEN.setSize(80, 25);
		jbSetFEN.setLocation(10, 160);
		add(jbSetFEN);
		jbSetFEN.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set FEN
			}
		}));
		
		jbGetFEN = new JButton("get FEN");
		jbGetFEN.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbGetFEN.setSize(80, 25);
		jbGetFEN.setLocation(90, 160);
		add(jbGetFEN);
		jbGetFEN.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get FEN
			}
		}));
		
		jbClearFEN = new JButton("clear FEN");
		jbClearFEN.setMargin(new java.awt.Insets(1, 2, 1, 2));
		jbClearFEN.setSize(80, 25);
		jbClearFEN.setLocation(170, 160);
		add(jbClearFEN);
		jbClearFEN.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// clear FEN
			}
		}));
		
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
	
	public void setOutputMessage(String outputMessage){
		jtpResult.setText(outputMessage);
	}
	
	public String getFenString(){
		return jtFEN.getText().trim();
	}
	
	public void setCastlingRights(byte[][] castlingRights){
		jcbWhiteQueenSideCastlingRight.setSelected(castlingRights[0][0] == 1 ? true : false);
		jcbWhiteKingSideCastlingRight.setSelected(castlingRights[0][1] == 1 ? true : false);
		jcbBlackQueenSideCastlingRight.setSelected(castlingRights[1][0] == 1 ? true : false);
		jcbBlackKingSideCastlingRight.setSelected(castlingRights[1][1] == 1 ? true : false);
	}
	
	public void setEnpassant(int epTarget, int epSquare){
		jtEnpassantTarget.setText(String.valueOf(epTarget));
		jtEnpassantSquare.setText(String.valueOf(epSquare));
	}
	
	public int getSearchDepth(){
		return Integer.parseInt(jtSearchDepth.getText());
	}
}