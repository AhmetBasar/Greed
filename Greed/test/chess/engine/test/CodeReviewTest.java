package chess.engine.test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chess.util.Utility;

public class CodeReviewTest {
	
	private static final String SIGNED_RIGHT_SHIFT_REGEX = "[^>]>>[^>]";
	private static final String GREATER_THAN_REGEX = ">(\\s)*0";
	private static final String LONG_SIGN = "1 <<";
	private static final Map<String, Map<String, List<String>>> misusedOperatorsWhiteList = new HashMap<String, Map<String, List<String>>>();
	private static final String LICENCE_HEADER = "/**********************************************\r\n" + 
			" * Greed, a chess engine written in java.\r\n" + 
			" * Copyright (C) 2019 Ahmet Ba�ar\r\n" + 
			" * \r\n" + 
			" * This file is part of Greed.\r\n" + 
			" * \r\n" + 
			" * Greed is free software: you can redistribute it and/or modify\r\n" + 
			" * it under the terms of the GNU General Public License as published by\r\n" + 
			" * the Free Software Foundation, either version 3 of the License, or\r\n" + 
			" * (at your option) any later version.\r\n" + 
			" * \r\n" + 
			" * Greed is distributed in the hope that it will be useful,\r\n" + 
			" * but WITHOUT ANY WARRANTY; without even the implied warranty of\r\n" + 
			" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\r\n" + 
			" * GNU General Public License for more details.\r\n" + 
			" * \r\n" + 
			" * You should have received a copy of the GNU General Public License\r\n" + 
			" * along with Greed.  If not, see <https://www.gnu.org/licenses/>.\r\n" + 
			" **********************************************/";
	
	static {
		Map<String, List<String>> signedRightShiftOperatorWhiteList = new HashMap<String, List<String>>();
		misusedOperatorsWhiteList.put(SIGNED_RIGHT_SHIFT_REGEX, signedRightShiftOperatorWhiteList);
		
		List<String> signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("int r1 = (rgb1 >> 16) & 0xff;");
		signedRightShiftOperatorWhiteListLines.add("int g1 = (rgb1 >> 8) & 0xff;");
		signedRightShiftOperatorWhiteListLines.add("int r2 = (rgb2 >> 16) & 0xff;");
		signedRightShiftOperatorWhiteListLines.add("int g2 = (rgb2 >> 8) & 0xff;");
		signedRightShiftOperatorWhiteList.put("src\\bot\\chess\\bot\\Utility.java", signedRightShiftOperatorWhiteListLines);
		
		signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("private Map<Integer, Map<Integer, Integer>> blackListMap = new HashMap<>();");
		signedRightShiftOperatorWhiteListLines.add("Map<Integer, Set<Integer>> map = premoveMap.get(firstMove);");
		signedRightShiftOperatorWhiteListLines.add("map = new HashMap<Integer, Set<Integer>>();");
		signedRightShiftOperatorWhiteListLines.add("Map<Integer, Set<Integer>> map = premoveMap.get(move);");
		signedRightShiftOperatorWhiteList.put("src\\chess\\fhv2\\SearchEngineFifty_PREMOVEFINDER.java", signedRightShiftOperatorWhiteListLines);
		
		signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("List<List<Integer>> splittedMoveLists = splitList(moves, splittedMoveListSize);");
		signedRightShiftOperatorWhiteListLines.add("private static List<List<Integer>> splitList(List<Integer> list, final int length) {");
		signedRightShiftOperatorWhiteListLines.add("List<List<Integer>> subLists = new ArrayList<List<Integer>>();");
		signedRightShiftOperatorWhiteList.put("src\\chess\\perft\\PerformanceTestingMultiThreaded.java", signedRightShiftOperatorWhiteListLines);
		
		signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("public static List<List<String>> splitList(List<String> list, final int maxLength) {");
		signedRightShiftOperatorWhiteListLines.add("List<List<String>> parts = new ArrayList<List<String>>();");
		signedRightShiftOperatorWhiteList.put("src\\chess\\util\\Utility.java", signedRightShiftOperatorWhiteListLines);
		
		signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("Map<String, List<String>> operations = new LinkedHashMap<String, List<String>>();");
		signedRightShiftOperatorWhiteListLines.add("public Map<String, List<String>> getOperations() {");
		signedRightShiftOperatorWhiteList.put("test\\chess\\engine\\test\\suites\\EpdOperations.java", signedRightShiftOperatorWhiteListLines);
		
		signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("Map<String, List<String>> fromAmbiguities = ambiguities.get(to);");
		signedRightShiftOperatorWhiteList.put("test\\chess\\engine\\test\\suites\\SanGenerator.java", signedRightShiftOperatorWhiteListLines);
		
		signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("List<List<String>> splitted = Utility.splitList(listAll, listAll.size() / ThreadPool.POOL_SIZE);");
		signedRightShiftOperatorWhiteList.put("test\\chess\\engine\\test\\TestSuitesTest.java", signedRightShiftOperatorWhiteListLines);
		
		signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("List<Callable<Object>> callables = convertToCallable(runnables);");
		signedRightShiftOperatorWhiteListLines.add("private List<Callable<Object>> convertToCallable(List<Runnable> runnables) {");
		signedRightShiftOperatorWhiteListLines.add("List<Callable<Object>> callables = new ArrayList<Callable<Object>>();");
		signedRightShiftOperatorWhiteList.put("test\\chess\\engine\\test\\ThreadPool.java", signedRightShiftOperatorWhiteListLines);
		
		signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("List<Callable<Object>> callables = convertToCallable(runnables);");
		signedRightShiftOperatorWhiteListLines.add("private List<Callable<Object>> convertToCallable(List<Runnable> runnables) {");
		signedRightShiftOperatorWhiteListLines.add("List<Callable<Object>> callables = new ArrayList<Callable<Object>>();");
		signedRightShiftOperatorWhiteList.put("test\\chess\\engine\\test\\ThreadPool4Workers.java", signedRightShiftOperatorWhiteListLines);
		
		signedRightShiftOperatorWhiteListLines = new ArrayList<String>();
		signedRightShiftOperatorWhiteListLines.add("List<List<String>> chopped = Utility.splitList(mainList, 3);");
		signedRightShiftOperatorWhiteList.put("test\\chess\\engine\\test\\UtilityTest.java", signedRightShiftOperatorWhiteListLines);
	}
	
	static {
		Map<String, List<String>> greaterThanOperatorWhiteList = new HashMap<String, List<String>>();
		misusedOperatorsWhiteList.put(GREATER_THAN_REGEX, greaterThanOperatorWhiteList);
		
		List<String> greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("fiftyMoveCounter > 0");
		greaterThanOperatorWhiteListLines.add("moveHistory.size() > 0");
		greaterThanOperatorWhiteList.put("src\\bot\\chess\\bot\\BotGamePlay.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("capturedPiece > 0");
		greaterThanOperatorWhiteList.put("src\\bot\\chess\\bot\\BotGamePlayMove.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("getCapturedPiece() > 0");
		greaterThanOperatorWhiteList.put("src\\bot\\chess\\bot\\EngineController.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("capturedPiece > 0");
		greaterThanOperatorWhiteListLines.add("moveCapturedPiece > 0");
		greaterThanOperatorWhiteList.put("src\\chess\\engine\\BoardV7.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("capturedPiece > 0");
		greaterThanOperatorWhiteList.put("src\\chess\\perft\\PerformanceTestingMultiThreaded.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("capturedPiece > 0");
		greaterThanOperatorWhiteList.put("src\\chess\\perft\\PerformanceTestingSingleThreaded.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("fiftyMoveCounter > 0");
		greaterThanOperatorWhiteListLines.add("moveHistory.size() > 0");
		greaterThanOperatorWhiteList.put("src\\ui\\chess\\game\\GamePlay.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("capturedPiece > 0");
		greaterThanOperatorWhiteList.put("src\\ui\\chess\\game\\GamePlayMove.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add(".size() > 0");
		greaterThanOperatorWhiteList.put("src\\ui\\chess\\gui\\PieceEffects.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("emptySquareCount > 0");
		greaterThanOperatorWhiteList.put("test\\chess\\engine\\test\\suites\\FenGenerator.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("fiftyMoveCounter > 0");
		greaterThanOperatorWhiteListLines.add("moveHistory.size() > 0");
		greaterThanOperatorWhiteList.put("test\\chess\\engine\\test\\tournament\\ChessBoard.java", greaterThanOperatorWhiteListLines);
		
		greaterThanOperatorWhiteListLines = new ArrayList<String>();
		greaterThanOperatorWhiteListLines.add("capturedPiece > 0");
		greaterThanOperatorWhiteList.put("test\\chess\\engine\\test\\tournament\\ChessMove.java", greaterThanOperatorWhiteListLines);
	}
	
	static {
		Map<String, List<String>> longSignWhiteList = new HashMap<String, List<String>>();
		misusedOperatorsWhiteList.put(LONG_SIGN, longSignWhiteList);
		
		List<String> longSignWhiteListLines = new ArrayList<String>();
		longSignWhiteListLines.add("public static final int[] PIECE_VALUES = {0, 0, 1 << 0, 1 << 4, 1 << 8, 1 << 11, 1 << 14, 1 << 17, 1 << 20, 1 << 23, 1 << 26, 1 << 29 };");
		longSignWhiteList.put("src\\chess\\engine\\Material.java", longSignWhiteListLines);
	}
	
	public static void main(String[] args) {
		testAll();
	}
	
	public static void testAll() {
		testSharedDatas();
		
		testMisusedOperators();
	}
	
	public static void testMisusedOperators() {
		URL a = CodeReviewTest.class.getClassLoader().getResource(".");
		File binaryFile = new File(a.getPath());
		File projectFile = binaryFile.getParentFile();
		List<File> javaFiles = new ArrayList<File>();
		retrieveJavaFiles(projectFile, javaFiles);
		
		for (File javaFile : javaFiles) {
			if (javaFile.getName().equals("CodeReviewTest.java")) {
				continue;
			}
			String javaFilePath = javaFile.getAbsolutePath();
			String javaFileContent = Utility.readFile(javaFilePath);
			
			
			if (!javaFileContent.contains(LICENCE_HEADER)) {
				System.err.println(javaFile.getName() + " has no licence header.");
				throw new RuntimeException("Failed.");
			}
			
			String javaFilePathPartial = javaFilePath.substring(javaFilePath.indexOf("Greed") + 12);
			
			for (Map.Entry<String, Map<String, List<String>>> entry : misusedOperatorsWhiteList.entrySet()) {
				String regex = entry.getKey();
				Map<String, List<String>> whiteList = entry.getValue();
				if (whiteList.containsKey(javaFilePathPartial)) {
					List<String> whiteListLines = whiteList.get(javaFilePathPartial);
					for (String str : whiteListLines) {
						javaFileContent = javaFileContent.replace(str, "");
					}
				}
				
				if (Utility.containsRegex(javaFileContent, regex)) {
					throw new RuntimeException("Failed. " + javaFile + " contains possible misused " + regex + " operator.");
				}
			}

		}
	}
	
	public static void testSharedDatas() {
		URL a = CodeReviewTest.class.getClassLoader().getResource(".");
		File binaryFile = new File(a.getPath());
		File projectFile = binaryFile.getParentFile();
		List<File> javaFiles = new ArrayList<File>();
		retrieveJavaFiles(projectFile, javaFiles);
		String projectName = projectFile.getName();
		
		int counter = 0;
		for (File javaFile : javaFiles) {
			String javaFilePath = javaFile.getAbsolutePath();
			javaFilePath = javaFilePath.substring(javaFilePath.lastIndexOf(projectName) + projectName.length() + 1, javaFilePath.length());
			javaFilePath = javaFilePath.replace("\\", ".");
			String fullyQualifiedClassName = javaFilePath.substring(0, javaFilePath.lastIndexOf(".java"));
			
			if (fullyQualifiedClassName.startsWith("src.resources.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("src.resources.".length());
			} else if (fullyQualifiedClassName.startsWith("src.bot.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("src.bot.".length());
			} else if (fullyQualifiedClassName.startsWith("src.ui.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("src.ui.".length());
			} else if (fullyQualifiedClassName.startsWith("test.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("test.".length());
			} else if (fullyQualifiedClassName.startsWith("src.")) {
				fullyQualifiedClassName = fullyQualifiedClassName.substring("src.".length());
			}
			
//			System.out.println(fullyQualifiedClassName);
			
			try {
				Class<?> clazz = Class.forName(fullyQualifiedClassName);
				Field[] declaredFields = clazz.getDeclaredFields();
				for (Field field : declaredFields) {
					boolean isPrimitive = field.getType().isPrimitive() || field.getType().equals(java.lang.String.class);
					boolean isFinal = Modifier.isFinal(field.getModifiers());
					if (Modifier.isStatic(field.getModifiers()) && (!isPrimitive || !isFinal)
							&& !whiteListFields.contains(field.getDeclaringClass().getCanonicalName() + "." + field.getName())
							&& !whiteListClasses.contains(field.getDeclaringClass().getCanonicalName())) {
						System.out.println(field);
						counter++;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		if (counter != 0) {
			System.err.println("counter = " + counter);
			throw new RuntimeException("Failed.");
			
		}
	}
	
	private static void retrieveJavaFiles(File dir, List<File> lst) {
		File[] files = dir.listFiles();
		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isDirectory()) {
					retrieveJavaFiles(file, lst);
				} else {
					if (file.getAbsolutePath().endsWith(".java")) {
						lst.add(file);
					}
				} 
			}
		}
	}
	
	private static final Set<String> whiteListFields = new HashSet<String>();
	private static final Set<String> whiteListClasses = new HashSet<String>();
	static {
		
		whiteListClasses.add("chess.bot.BotConstants");
		whiteListClasses.add("chess.bot.image.ImageType");
		whiteListClasses.add("chess.engine.EngineConstants");
		whiteListClasses.add("chess.engine.PrecalculatedAttackTables");
		whiteListClasses.add("chess.evaluation.PieceSquareTable");
		whiteListClasses.add("chess.gui.GuiConstants");
		whiteListClasses.add("chess.engine.test.CodeReviewTest");
		whiteListClasses.add("chess.engine.MoveGenerationConstants");
		
		
		whiteListFields.add("chess.bot.interpreting.CellCoordinateFactory.cache");
		whiteListFields.add("chess.bot.Robot.robot");
		whiteListFields.add("chess.debug.DebugUtility.DEFAULT_BOARD");
		whiteListFields.add("chess.debug.DebugUtility.EMPTY_BOARD");
		whiteListFields.add("chess.debug.DebugUtility.BOARD_2");
		whiteListFields.add("chess.debug.DebugUtility.POSSIBLE_TYPES");
		
		whiteListFields.add("chess.debug.PerformanceTesting.aliveThreadCount");
		whiteListFields.add("chess.debug.PerformanceTesting.deadThreadCount");
		whiteListFields.add("chess.debug.PerformanceTesting.moveCount");
		whiteListFields.add("chess.debug.PerformanceTesting.castlingCount");
		whiteListFields.add("chess.debug.PerformanceTesting.epCount");
		whiteListFields.add("chess.debug.PerformanceTesting.captureCount");
		whiteListFields.add("chess.debug.PerformanceTesting.checkMateCount");
		whiteListFields.add("chess.debug.PerformanceTesting.promotionCount");
		whiteListFields.add("chess.debug.PerformanceTesting.checkCount");
		whiteListFields.add("chess.debug.PerformanceTesting.ilk");
		whiteListFields.add("chess.debug.PerformanceTesting.isFromScreen");
		whiteListFields.add("chess.debug.PerformanceTesting.base");
		
		whiteListFields.add("chess.debug.PerformanceTestingSimple.ilk");
		whiteListFields.add("chess.debug.PerformanceTestingSimple.isFromScreen");
		whiteListFields.add("chess.debug.PerformanceTestingSimple.base");
		whiteListFields.add("chess.debug.PerformanceTestingSimple.aliveThreadCount");
		whiteListFields.add("chess.debug.PerformanceTestingSimple.deadThreadCount");
		whiteListFields.add("chess.debug.PerformanceTestingSimple.moveCount");
		
		whiteListFields.add("chess.debug.PerformanceTestingSingleThreadedWithBoardInfaThreaded.base");
		whiteListFields.add("chess.debug.PerformanceTestingSingleThreadedWithBoardInfaThreaded.ilk");
		whiteListFields.add("chess.debug.PerformanceTestingSingleThreadedWithBoardInfaThreaded.isFromScreen");
		whiteListFields.add("chess.debug.PerformanceTestingSingleThreadedWithBoardInfaThreaded.aliveThreadCount");
		whiteListFields.add("chess.debug.PerformanceTestingSingleThreadedWithBoardInfaThreaded.deadThreadCount");
		whiteListFields.add("chess.debug.PerformanceTestingSingleThreadedWithBoardInfaThreaded.moveCount");
		
		whiteListFields.add("chess.engine.TranspositionTable.zobristPositionArray");
		whiteListFields.add("chess.engine.TranspositionTable.zobristEnPassantArray");
		whiteListFields.add("chess.engine.TranspositionTable.zobristCastlingArray");
		whiteListFields.add("chess.engine.TranspositionTable.secureRandom");
		
		whiteListFields.add("chess.engine.ZobristHashingPolyGlot.zobristPositionArray");
		whiteListFields.add("chess.engine.ZobristHashingPolyGlot.zobristEnPassantArray");
		whiteListFields.add("chess.engine.ZobristHashingPolyGlot.zobristCastlingArray");
		whiteListFields.add("chess.engine.ZobristHashingPolyGlot.secureRandom");
		
		whiteListFields.add("chess.engine.Transformer.rIndexMap");
		whiteListFields.add("chess.engine.Transformer.indexMap");
		
		whiteListFields.add("chess.engine.test.suites.SanGenerator.FILES");
		whiteListFields.add("chess.engine.test.suites.SanGenerator.PIECES");
		
		whiteListFields.add("chess.gui.PieceEffects.timerPool");
		whiteListFields.add("chess.gui.ChessImageCache.instance");
		whiteListFields.add("chess.util.Utility.SINGLE_BIT");
		whiteListFields.add("chess.util.Utility.LINE");
		whiteListFields.add("chess.util.Utility.PINNED_MOVEMENT");
		
		whiteListFields.add("chess.engine.test.ThreadPool.instance");
		whiteListFields.add("chess.engine.test.ThreadPool4Workers.instance");
		whiteListFields.add("chess.engine.OpeningBook.instance");
		whiteListFields.add("chess.engine.OpeningBook.newInstances");
		whiteListFields.add("chess.engine.OpeningBook.rgn");
		
		whiteListFields.add("chess.engine.test.equality.EngineEqualityComparator.positionCountDraw");
		whiteListFields.add("chess.engine.test.equality.EngineEqualityComparator.positionCountWhite");
		whiteListFields.add("chess.engine.test.equality.EngineEqualityComparator.positionCountBlack");
		whiteListFields.add("chess.engine.test.equality.EngineEqualityComparator.mapTimeConsumeds");
		whiteListFields.add("chess.engine.test.tournament.TournamentManagerEngineBasedThreaded.positionCountDraw");
		whiteListFields.add("chess.engine.test.tournament.TournamentManagerEngineBasedThreaded.positionCountWhite");
		whiteListFields.add("chess.engine.test.tournament.TournamentManagerEngineBasedThreaded.positionCountBlack");
		whiteListFields.add("chess.engine.test.tournament.TournamentManagerEngineBasedThreaded.engine1Score");
		whiteListFields.add("chess.engine.test.tournament.TournamentManagerEngineBasedThreaded.engine2Score");
		
		whiteListFields.add("chess.fhv2.SearchEngineFifty10.instance");
		whiteListFields.add("chess.fhv2.SearchEngineFifty10.newInstances");
		whiteListFields.add("chess.fhv2.SearchEngineFifty10.STATIC_NULLMOVE_MARGIN");
		whiteListFields.add("chess.fhv2.SearchEngineFifty_PREMOVEFINDER.instance");
		whiteListFields.add("chess.fhv2.SearchEngineFifty_PREMOVEFINDER.newInstances");

		whiteListFields.add("chess.fhv2.SearchEngineFifty11.instance");
		whiteListFields.add("chess.fhv2.SearchEngineFifty11.newInstances");
		
		whiteListFields.add("chess.fhv2.SearchEngineMordering.instance");
		whiteListFields.add("chess.fhv2.SearchEngineMordering.newInstances");
		whiteListFields.add("chess.fhv2.SearchEngineMordering.STATIC_NULLMOVE_MARGIN");
		
		whiteListFields.add("chess.movegen.MagicBitboard.rookMasks");
		whiteListFields.add("chess.movegen.MagicBitboard.bishopMasks");
		whiteListFields.add("chess.movegen.MagicBitboard.rookShifts");
		whiteListFields.add("chess.movegen.MagicBitboard.bishopShifts");
		whiteListFields.add("chess.movegen.MagicBitboard.rookMoves");
		whiteListFields.add("chess.movegen.MagicBitboard.bishopMoves");
		whiteListFields.add("chess.movegen.MagicBitboard.rookOccVariations");
		whiteListFields.add("chess.movegen.MagicBitboard.bishopOccVariations");
		whiteListFields.add("chess.movegen.MagicBitboard.rookMagicNumbers");
		whiteListFields.add("chess.movegen.MagicBitboard.bishopMagicNumbers");
		
		whiteListFields.add("chess.engine.Material.PIECE_VALUES");
		whiteListFields.add("chess.engine.Material.PIECE_MASKS");
		whiteListFields.add("chess.engine.Material.PIECE_SHIFTS");
		whiteListFields.add("chess.engine.Material.SLIDING_PIECE_MASKS");
		whiteListFields.add("chess.engine.Material.MAJOR_PIECE_MASKS");
		
		whiteListFields.add("chess.engine.test.suites.FenGenerator.PIECES");
		whiteListFields.add("chess.engine.test.suites.FenGenerator.FILES");
		whiteListFields.add("chess.engine.test.suites.FenGenerator.SIDE_TO_MOVE");
		
		whiteListFields.add("chess.fhv2.SearchEngineFifty12.instance");
		whiteListFields.add("chess.fhv2.SearchEngineFifty12.newInstances");
		whiteListFields.add("chess.fhv2.SearchEngineFifty12.STATIC_NULLMOVE_MARGIN");
		
		whiteListFields.add("chess.evaluation.EvaluationAdvancedV4.PAWN_CONNECTED");
		whiteListFields.add("chess.evaluation.EvaluationAdvancedV4.PAWN_NEIGHBOUR");
		whiteListFields.add("chess.fhv2.SearchEngineFifty13.instance");
		whiteListFields.add("chess.fhv2.SearchEngineFifty13.newInstances");
		whiteListFields.add("chess.fhv2.SearchEngineFifty13.STATIC_NULLMOVE_MARGIN");
		whiteListFields.add("chess.fhv2.SearchEngineFifty13.RAZORING_MARGIN");
		
		whiteListFields.add("chess.fhv2.SearchEngineFifty11.STATIC_NULLMOVE_MARGIN");
		
		whiteListFields.add("chess.util.Defaults.BOOLEAN");
		whiteListFields.add("chess.util.Defaults.BYTE");
		whiteListFields.add("chess.util.Defaults.SHORT");
		whiteListFields.add("chess.util.Defaults.INT");
		whiteListFields.add("chess.util.Defaults.LONG");
		whiteListFields.add("chess.util.Defaults.FLOAT");
		whiteListFields.add("chess.util.Defaults.DOUBLE");
		
	}

}
