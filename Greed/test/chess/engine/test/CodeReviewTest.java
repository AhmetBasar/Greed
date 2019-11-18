package chess.engine.test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeReviewTest {
	
	public static void main(String[] args) {
		testAll();
	}
	
	public static void testAll() {
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
		
		whiteListFields.add("chess.engine.test.ThreadPool.instance");
		whiteListFields.add("chess.engine.OpeningBook.instance");
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
		whiteListFields.add("chess.fhv2.SearchEngineFifty_PREMOVEFINDER.instance");
		whiteListFields.add("chess.fhv2.SearchEngineFifty_PREMOVEFINDER.newInstances");

		whiteListFields.add("chess.fhv2.SearchEngineFifty11.instance");
		whiteListFields.add("chess.fhv2.SearchEngineFifty11.newInstances");
		
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
		
	}

}
