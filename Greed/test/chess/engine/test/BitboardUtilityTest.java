package chess.engine.test;

import chess.evaluation.BitboardUtility;

public class BitboardUtilityTest {

	public static void main(String[] args) {
		testAll();
	}

	public static void testAll() {
		testWFrontSpans();

		testBFrontSpans();

		testNorthFill();

		testSouthFill();

		testNorthOne();

		testSouthOne();

		testEastOne();

		testWestOne();

		testFileFill();

		testWEastAttackFrontSpans();

		testWWestAttackFrontSpans();

		testBEastAttackFrontSpans();

		testBWestAttackFrontSpans();

		testNorthEastOne();

		testSouthEastOne();

		testSouthWestOne();

		testNorthWestOne();
	}

	public static void testWFrontSpans() {
		if (BitboardUtility.wFrontSpans(-8606343699237961087L) != -5502409718846226176L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testBFrontSpans() {
		if (BitboardUtility.bFrontSpans(-8606343699237961087L) != 38448515990010299L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testNorthFill() {
		if (BitboardUtility.northFill(-8606343699237961087L) != -4921410182543342719L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testSouthFill() {
		if (BitboardUtility.southFill(-8606343699237961087L) != -8603923980266914885L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testNorthOne() {
		if (BitboardUtility.northOne(-8606343699237961087L) != -8061442233481395968L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testSouthOne() {
		if (BitboardUtility.southOne(-8606343699237961087L) != 38439063962779650L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testEastOne() {
		if (BitboardUtility.eastOne(-8606343699237961087L) != 1161999081195701250L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testWestOne() {
		if (BitboardUtility.westOne(-8606343699237961087L) != 4920200185088311616L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testFileFill() {
		if (BitboardUtility.fileFill(-8606343699237961087L) != -4919131752989213765L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testWEastAttackFrontSpans() {
		if (BitboardUtility.wEastAttackFrontSpans(-8606343699237961087L) != 7369584463179022848L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testWWestAttackFrontSpans() {
		if (BitboardUtility.wWestAttackFrontSpans(-8606343699237961087L) != 6435997091012624384L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testBEastAttackFrontSpans() {
		if (BitboardUtility.bEastAttackFrontSpans(-8606343699237961087L) != 4556859141943926L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testBWestAttackFrontSpans() {
		if (BitboardUtility.bWestAttackFrontSpans(-8606343699237961087L) != 19224257986583645L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testNorthEastOne() {
		if (BitboardUtility.northEastOne(-8606343699237961087L) != 2323859606746694144L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testSouthEastOne() {
		if (BitboardUtility.southEastOne(-8606343699237961087L) != 4539058910920708L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testSouthWestOne() {
		if (BitboardUtility.southWestOne(-8606343699237961087L) != 19219531973001217L) {
			throw new RuntimeException("Failed.");
		}
	}

	public static void testNorthWestOne() {
		if (BitboardUtility.northWestOne(-8606343699237961087L) != 5192650370358263808L) {
			throw new RuntimeException("Failed.");
		}
	}

}
