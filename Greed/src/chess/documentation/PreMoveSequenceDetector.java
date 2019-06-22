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
package chess.documentation;

public class PreMoveSequenceDetector {
	
//	 => 2 change.  (why not 3 change? cause, if same piece recaptures.. say white rook recaptures the white rook with preMove.)
//		2 capture. (Normal process.)
//	 => 3 change.  (Normal process.)
//	    2 capture. (Normal process.)
	
	// 3 Moves Possibilities..
//    => 2 change 2 capture. + 3. move(capture move)(target = preMove's target.)  = 4 change + 3 capture.
//	 => 2 change 2 capture. + 3. move(capture move)(target != preMove's target.) = 4 change + 3 capture.
//	 => 2 change 2 capture. + 3. move(normal move independent)                   = 4 change + 2 capture.
//	 => 2 change 2 capture. + 3. move(normal move but source targeted)           = 3 change + 2 capture.
//	 => 2 change 2 capture. + 3. move(normal move but OWN source targeted)       = 3 change + 2 capture.
//	 => 2 change 2 capture. + 3. move(castling move)                             = 6 change + 2 capture.
	
//    => 3 change 2 capture. + 3. move(capture move)(target = preMove's target.)  = 4 change + 3 capture.
//	 => 3 change 2 capture. + 3. move(capture move)(target != preMove's target.) = 5 change + 3 capture.
//	 => 3 change 2 capture. + 3. move(normal move independent)                   = 5 change + 2 capture.
//	 => 3 change 2 capture. + 3. move(normal move but source targeted)           = 4 change + 2 capture.
//	 => 3 change 2 capture. + 3. move(normal move but OWN source targeted)       = 4 change + 2 capture.
//	 => 3 change 2 capture. + 3. move(castling move)                             = 7 change + 2 capture.

}
