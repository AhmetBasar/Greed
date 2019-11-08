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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import chess.fhv2.SearchEngineFifty8;

// https://github.com/kwhat/jnativehook
public class KeyListenerGeneric implements NativeKeyListener {

	private KeyPressQueue keyPressQueue = new KeyPressQueue();
	private BotGamePlay botGamePlay;
	
	public KeyListenerGeneric(BotGamePlay botGamePlay) {
		this.botGamePlay = botGamePlay;
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}
		GlobalScreen.addNativeKeyListener(this);

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					if (keyPressQueue.contains(e.getKeyCode())) {
						return;
					}

					keyPressQueue.press(e.getKeyCode());

					/**
					 * This is a very inefficient implementation of inspecting
					 * keyEvents. this should be smarter and faster than this.
					 */
					if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_1)) {
						botGamePlay.startBotAsWhite();
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_2)) {
						botGamePlay.startBotAsBlack();
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_3)) {
						botGamePlay.resetAll();
						botGamePlay.resetBot();
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_4)) {
						botGamePlay.getEngineController().setDepthLimit(4);
						System.out.println("depth limit = " + botGamePlay.getEngineController().getDepthLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_5)) {
						botGamePlay.getEngineController().setDepthLimit(5);
						System.out.println("depth limit = " + botGamePlay.getEngineController().getDepthLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_6)) {
						botGamePlay.getEngineController().setDepthLimit(6);
						System.out.println("depth limit = " + botGamePlay.getEngineController().getDepthLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_7)) {
						botGamePlay.getEngineController().setDepthLimit(7);
						System.out.println("depth limit = " + botGamePlay.getEngineController().getDepthLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_8)) {
						botGamePlay.getEngineController().setDepthLimit(8);
						System.out.println("depth limit = " + botGamePlay.getEngineController().getDepthLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_9)) {
						botGamePlay.getEngineController().setDepthLimit(9);
						System.out.println("depth limit = " + botGamePlay.getEngineController().getDepthLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_0)) {
						botGamePlay.getEngineController().setDepthLimit(10);
						System.out.println("depth limit = " + botGamePlay.getEngineController().getDepthLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_UP)) {
						botGamePlay.getEngineController().incrementDepthLimit();
						System.out.println("depth limit = " + botGamePlay.getEngineController().getDepthLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_DOWN)) {
						botGamePlay.getEngineController().decrementDepthLimit();
						System.out.println("depth limit = " + botGamePlay.getEngineController().getDepthLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_COMMA)) {
						botGamePlay.getBot().getBoardInteractionManager().setFast(true);
						System.out.println("FASTTT");
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_DELETE)) {
						botGamePlay.getBot().getBoardInteractionManager().setFast(false);
						System.out.println("SLOW");
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_1)) {
						botGamePlay.getEngineController().setTimeLimit(1000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_2)) {
						botGamePlay.getEngineController().setTimeLimit(2000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_3)) {
						botGamePlay.getEngineController().setTimeLimit(3000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_4)) {
						botGamePlay.getEngineController().setTimeLimit(4000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_5)) {
						botGamePlay.getEngineController().setTimeLimit(5000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_6)) {
						botGamePlay.getEngineController().setTimeLimit(6000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_7)) {
						botGamePlay.getEngineController().setTimeLimit(7000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_8)) {
						botGamePlay.getEngineController().setTimeLimit(8000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_9)) {
						botGamePlay.getEngineController().setTimeLimit(9000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_ALT, NativeKeyEvent.VC_0)) {
						botGamePlay.getEngineController().setTimeLimit(10000);
						System.out.println("timeLimit = " + botGamePlay.getEngineController().getTimeLimit());
					} else if (keyPressQueue.matches(NativeKeyEvent.VC_DELETE)) {
						System.out.println("Del requested.");
						SearchEngineFifty8.forceTimeoutRequested = true;
					}			
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					keyPressQueue.release(e.getKeyCode());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
	}

}

class GlobalMouseWheelListenerExample implements NativeMouseWheelListener {

	private BotGamePlay botGamePlay;

	public void nativeMouseWheelMoved(NativeMouseWheelEvent e) {
		SearchEngineFifty8.forceTimeoutRequested = true;
		SearchEngineFifty8.forceTimeoutRequested = true;
	}

	public GlobalMouseWheelListenerExample(BotGamePlay botGamePlay) {
		this.botGamePlay = botGamePlay;
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.exit(1);
		}

		GlobalScreen.addNativeMouseWheelListener(this);
	}
}

class KeyPressQueue {
	private List<Integer> queue = Collections.synchronizedList(new ArrayList<Integer>());

	public void press(Integer key) {
		queue.add(key);
	}

	public void release(Integer key) {
		queue.remove(key);
	}

	public boolean matches(Integer... args) {
		if ((queue.size() < args.length) || (args[args.length - 1].intValue() != queue.get(queue.size() - 1))) {
			return false;
		}
		int previousIdx = 0;
		for (int i = 0; i < args.length; i++) {
			int idx = queue.indexOf(args[i]);
			if ((idx == -1) || (idx < previousIdx)) {
				return false;
			}
			previousIdx = idx;
		}
		return true;
	}

	public boolean contains(Integer i) {
		return queue.contains(i);
	}
}