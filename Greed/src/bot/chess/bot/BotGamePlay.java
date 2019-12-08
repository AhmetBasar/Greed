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
package chess.bot;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chess.bot.interpreting.BotMove;
import chess.debug.DebugUtility;
import chess.engine.CompileTimeConstants;
import chess.engine.EngineConstants;
import chess.engine.MoveGenerationOnlyQueenPromotions;
import chess.engine.Transformer;
import chess.engine.TranspositionTable;
import chess.gui.GuiConstants;

public class BotGamePlay implements IGameController {
	private int side;
	private int epTarget;
	private int epSquare;
	private byte[][] castlingRights = { { 1, 1 }, { 1, 1 } };
	private byte[] kingPositions = { 4, 60 };
	private byte[][] rookPositions = { { 0, 7 }, { 56, 63 } };
	private MoveGenerationOnlyQueenPromotions moveGeneration = new MoveGenerationOnlyQueenPromotions();
	private int perspective;
	private long zobristKey;
	private long pawnZobristKey;
	private int fiftyMoveCounter = 0;
	private ArrayList<BotGamePlayMove> moveHistory = new ArrayList<BotGamePlayMove>();
	
	private ChessBot bot;
	private Thread botThread;
	
	long[] bitboard;
	private byte[] pieces;
	
	private EngineController engineController;
	private EngineControllerPreMove engineControllerPreMove;
	private Thread engineThread;
	private Thread engineThreadPreMove;
	private Map<Long, Integer> boardStateHistory = new HashMap<Long, Integer>();
	private List<Long> zobristKeyHistory = new ArrayList<Long>();
	
	public static void main(String[] args) throws Exception{
		new SecureRandom().nextInt();
		if (CompileTimeConstants.ENABLE_ASSERTION) {
			throw new RuntimeException("ENABLE_ASSERTION");
		}
		new BotGamePlay();
	}
	
	public void updateZobristKey(long val) {
		zobristKey = zobristKey ^ val;
	}
	
	public void updatePawnZobristKey(long val) {
		pawnZobristKey = pawnZobristKey ^ val;
	}

	public BotGamePlay() {
		
		Robot.getInstance();
		
		setSide(GuiConstants.WHITES_TURN);
		
		//
		bot = new ChessBot(this);
		botThread = new Thread(bot);
		botThread.start();
		//
		
		engineController = new EngineController(this);
		engineController.setCallback(new ICallBack() {
			@Override
			public void execute(int move) {
				doMove(move);
				bot.doMove(move);
			}
		});
		engineThread = new Thread(engineController);
		engineThread.start();
		
		//**
		engineControllerPreMove = new EngineControllerPreMove(this);
		engineControllerPreMove.setCallbackPreMove(new ICallBackPreMove() {
			@Override
			public void execute(int preMove) {
				bot.doPreMove(preMove);
			}
		});
		engineThreadPreMove = new Thread(engineControllerPreMove);
		engineThreadPreMove.start();
		//**
		
		resetAll();
		bot.suspend();
		new KeyListenerGeneric(this);
		
		zobristKey = TranspositionTable.getZobristKey(getBitboard(), getEpTarget(), getCastlingRights(), getSide());
		pawnZobristKey = TranspositionTable.getPawnZobristKey(getBitboard());
	}

	
	public boolean doMove(BotMove botMove) {
		return doMove(botMove, true);
	}
	
	public boolean doMove(BotMove botMove, boolean throwExceptionIfFailure) {
		boolean success = doMove(botMove.getFrom(), botMove.getTo());
		if (throwExceptionIfFailure && !success) {
			throw new RuntimeException("move failure " + botMove);
		}
		return success;
	}
	
	private boolean doMove(int source, int target) {
		int move = getValidMove(source, target);
		if (move == 0) {
			return false;			
		}
		doMove(move);
		return true;
	}
	
	public void doMove(int move) {
		incrementBoardStateCount();
		BotGamePlayMove gamePlayMove = new BotGamePlayMove(move, this);
		incrementOrResetFiftyMoveCounterIfNecessary(gamePlayMove);
		moveHistory.add(gamePlayMove);
		gamePlayMove.implement();
		updateCastlingRights();
		reverseTurn();
	}
	
	private void incrementOrResetFiftyMoveCounterIfNecessary(BotGamePlayMove gamePlayMove) {
		if (gamePlayMove.isCaptureMove() || gamePlayMove.isPawnMove()) {
			fiftyMoveCounter = 0;
		} else {
			fiftyMoveCounter++;
		}
	}
	
	private void decrementFiftyMoveCounterIfNecessary(BotGamePlayMove gamePlayMove) {
		if (fiftyMoveCounter > 0) {
			fiftyMoveCounter--;
		}
	}
	
	public void undoMove() {
		if (moveHistory.size() > 0) {
			BotGamePlayMove botGamePlayMove = moveHistory.remove(moveHistory.size() - 1);
			decrementFiftyMoveCounterIfNecessary(botGamePlayMove);
			botGamePlayMove.unImplement();
			updateCastlingRights();
			reverseTurn();
			decrementBoardStateCount();
		} else {
			throw new RuntimeException("There is no move to undo.");
		}
	}
	
	private void incrementBoardStateCount() {
		Integer boardStateCount = boardStateHistory.get(zobristKey);
		if (boardStateCount == null) {
			boardStateHistory.put(zobristKey, 1);
		} else {
			boardStateHistory.put(zobristKey, boardStateCount.intValue() + 1);
		}
		zobristKeyHistory.add(zobristKey);
	}
	
	private void decrementBoardStateCount() {
		Integer boardStateCount = boardStateHistory.get(zobristKey);
		boardStateHistory.put(zobristKey, boardStateCount.intValue() - 1);
		zobristKeyHistory.remove(zobristKeyHistory.size() - 1);
	}

	public void setSide(int turn) {
		this.side = turn;
	}

	public int getSide() {
		return side;
	}

	public boolean isWhitesTurn() {
		boolean whitesTurn = false;
		if (getSide() == GuiConstants.WHITES_TURN) {
			whitesTurn = true;
		}
		return whitesTurn;
	}

	public boolean isBlacksTurn() {
		boolean blacksTurn = false;
		if (getSide() == GuiConstants.BLACKS_TURN) {
			blacksTurn = true;
		}
		return blacksTurn;
	}

	public void reverseTurn() {
		side = side ^ 1;
	}

	public void resetEnPassantFlags() {
		epTarget = 64; // 64 means no epTarget. because we assume 1L<<64 = 0
		epSquare = -1; // -1 means no ep square exists
	}

	public void setEpTarget(int epTarget) {
		this.epTarget = epTarget;
	}

	public int getEpTarget() {
		return epTarget;
	}

	public void setEpSquare(int epSquare) {
		this.epSquare = epSquare;
	}

	public int getEpSquare() {
		return epSquare;
	}

	public byte[][] getCastlingRights() {
		return castlingRights;
	}

	public void setCastlingRights(byte[][] castlingRights) {
		byte whiteQueenSideCastlingRight = this.castlingRights[0][0];
		byte whiteKingSideCastlingRight = this.castlingRights[0][1];
		byte blackQueenSideCastlingRight = this.castlingRights[1][0];
		byte blackKingSideCastlingRight = this.castlingRights[1][1];
		
		this.castlingRights = castlingRights;
		
		if(whiteQueenSideCastlingRight != this.castlingRights[0][0]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][0];
		}
		
		if(whiteKingSideCastlingRight != this.castlingRights[0][1]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][1];
		}
		
		if(blackQueenSideCastlingRight != this.castlingRights[1][0]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][0];
		}
		
		if(blackKingSideCastlingRight != this.castlingRights[1][1]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][1];
		}
		
	}
	
	private void updateCastlingRights() {
		long[] bitboard = getBitboard();
		int opSide = this.side ^ 1;
		
		byte whiteQueenSideCastlingRight = castlingRights[0][0];
		byte whiteKingSideCastlingRight = castlingRights[0][1];
		byte blackQueenSideCastlingRight = castlingRights[1][0];
		byte blackKingSideCastlingRight = castlingRights[1][1];
		
		long kings = bitboard[(side) | EngineConstants.KING];
		castlingRights[side][0] = (byte) (castlingRights[side][0] & (kings >>> kingPositions[side]));
		castlingRights[side][1] = (byte) (castlingRights[side][1] & (kings >>> kingPositions[side]));
		
		long rooks = bitboard[(side) | EngineConstants.ROOK];
		castlingRights[side][0] = (byte) (castlingRights[side][0] & (rooks >>> rookPositions[side][0]));
		castlingRights[side][1] = (byte) (castlingRights[side][1] & (rooks >>> rookPositions[side][1]));
		
		kings = bitboard[(opSide) | EngineConstants.KING];
		castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (kings >>> kingPositions[opSide]));
		castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (kings >>> kingPositions[opSide]));
		
		rooks = bitboard[(opSide) | EngineConstants.ROOK];
		castlingRights[opSide][0] = (byte) (castlingRights[opSide][0] & (rooks >>> rookPositions[opSide][0]));
		castlingRights[opSide][1] = (byte) (castlingRights[opSide][1] & (rooks >>> rookPositions[opSide][1]));
		
		if(whiteQueenSideCastlingRight != castlingRights[0][0]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][0];
		}
		
		if(whiteKingSideCastlingRight != castlingRights[0][1]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[0][1];
		}
		
		if(blackQueenSideCastlingRight != castlingRights[1][0]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][0];
		}
		
		if(blackKingSideCastlingRight != castlingRights[1][1]){
			zobristKey = zobristKey ^ TranspositionTable.zobristCastlingArray[1][1];
		}
		
	}
	
	public BotGamePlay getGamePlay() {
		return this;
	}
	
	public void startBotAsWhite() {
		setPerspective(GuiConstants.WHITE_PERSPECTIVE);
		System.out.println("Start as White.");
		suggestMoveForUs();
	}
	
	public void startBotAsBlack() {
		setPerspective(GuiConstants.BLACK_PERSPECTIVE);
		System.out.println("Start as Black.");
		bot.resume();
	}
	
	public int getPerspective() {
		return perspective;
	}

	public void setPerspective(int perspective) {
		this.perspective = perspective;
	}

	public void resetAll() {
		setSide(GuiConstants.WHITES_TURN);
		resetEnPassantFlags();
		fiftyMoveCounter = 0;
		castlingRights = new byte[][] { { 1, 1 }, { 1, 1 } };
		bitboard = Transformer.getBitboardStyl(DebugUtility.getDefaultBoard());
		pieces = Transformer.getByteArrayStyl(bitboard);
		engineController.suspend();
		engineControllerPreMove.suspend();
		zobristKey = TranspositionTable.getZobristKey(getBitboard(), getEpTarget(), getCastlingRights(), getSide());
		pawnZobristKey = TranspositionTable.getPawnZobristKey(getBitboard());

		moveHistory.clear();
		boardStateHistory.clear();
		zobristKeyHistory.clear();
	}
	
	public void resetBot() {
		System.out.println("Bot Restarted !");
		bot.restart();
		engineController.reset();
		engineControllerPreMove.reset();
		bot.resume();
	}
	
	public int getValidMove(int source, int target) {
		ArrayList<Integer> validMoveList = new ArrayList<Integer>();
		int validMove = 0;
		int move = source | (target << 8);
		int[] validMoves = moveGeneration.generateMoves(getBitboard(), side, epTarget, castlingRights);
		for (int i = 0; i < EngineConstants.MOVE_LIST_SIZE; i++) {
			if (move == (validMoves[i] & 0x0000FFFF)) {
				validMoveList.add(validMoves[i]);
			}
		}
		// check King Safety
		int validMoveListSize = validMoveList.size();
		for (int i = 0; i < validMoveListSize; i++) {
			validMove = validMoveList.get(i);
			BotGamePlayMove gamePlayMove = new BotGamePlayMove(validMove, this);
			if (gamePlayMove.isKingInCheck()) {
				return 0;
			}
		}
		// choose item to be promoted
		if (validMoveListSize > 1) {
			throw new RuntimeException("Not Yet Implemented!!!");
		}
		
		return validMove;
	}

	@Override
	public byte[][] getBoard() {
		return Transformer.getTwoDimByteArrayStyl(getBitboard());
	}

	@Override
	public void suggestMoveForUs() {
		engineController.resume();
	}
	
	@Override
	public void suggestPreMoveForUs() {
		engineControllerPreMove.resume();
	}

	@Override
	public Map<Long, Integer> getBoardStateHistory() {
		return boardStateHistory;
	}
	
	public List<Long> getZobristKeyHistory() {
		return zobristKeyHistory;
	}

	@Override
	public long getZobristKey() {
		return zobristKey;
	}
	
	public ArrayList<BotGamePlayMove> getMoveHistory() {
		return moveHistory;
	}

	public long[] getBitboard() {
		return bitboard;
	}
	
	public byte[] getPieces() {
		return pieces;
	}

	@Override
	public boolean isLastMoveCapture() {
		if (moveHistory.size() > 0) {
			BotGamePlayMove move = moveHistory.get(moveHistory.size() - 1);
			return move.getCapturedPiece() != 0;
		}
		return false;
	}

	@Override
	public int getLastMove() {
		if (moveHistory.size() > 0) {
			BotGamePlayMove move = moveHistory.get(moveHistory.size() - 1);
			return move.getMove();
		}
		return 0;
	}

	@Override
	public int getFiftyMoveCounter() {
		return fiftyMoveCounter;
	}

	public EngineController getEngineController() {
		return engineController;
	}

	public ChessBot getBot() {
		return bot;
	}

	@Override
	public int getMoveCount() {
		return bot.getMoveCount();
	}

	public long getPawnZobristKey() {
		return pawnZobristKey;
	}

	public void setPawnZobristKey(long pawnZobristKey) {
		this.pawnZobristKey = pawnZobristKey;
	}
	
}