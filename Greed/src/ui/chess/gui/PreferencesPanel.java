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

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import chess.database.DbConstants;

public class PreferencesPanel extends javax.swing.JPanel {
	
	private static final long serialVersionUID = 1L;
	private BaseGui base;
	private JSlider contrastSlider;
	private JButton btnSave;
	private JButton btnCancel;

	public PreferencesPanel(BaseGui base){
        this.base = base;
        initComponents();
	}

    private void initComponents() {
    	int preferredValue = 100;
    	if(base.getGamePlay().getPreferences().containsKey(DbConstants.Keys.CONTRAST)){
    		preferredValue = Integer.parseInt(base.getGamePlay().getPreferences().get(DbConstants.Keys.CONTRAST));
    	}
    	base.getMainOuterFrame().setOpacity(((float)preferredValue) / (100f));
    	setLayout(null);
		contrastSlider = new JSlider(JSlider.VERTICAL, 3, 100, preferredValue);
	    contrastSlider.setMinorTickSpacing(2);
	    contrastSlider.setMajorTickSpacing(10);
	    contrastSlider.setPaintTicks(true);
	    contrastSlider.setPaintLabels(true);
	    contrastSlider.setLabelTable(contrastSlider.createStandardLabels(10));
	    contrastSlider.setBounds(10, 10, 55, 223);
	    contrastSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				base.getMainOuterFrame().setOpacity(((float)contrastSlider.getValue()) / (100f));
			}
		});
	    add(contrastSlider);
	    
	    btnSave = new JButton("Apply");
	    btnSave.setMargin(new java.awt.Insets(1, 2, 1, 2));
	    btnSave.setSize(85 , 25);
	    btnSave.setLocation(10, 240);
		add(btnSave);
		btnSave.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO : this is a long-running task. should be executed on SwingWorker.
				saveChanges();
			}
		}));
		
		btnCancel = new JButton("Cancel");
		btnCancel.setMargin(new java.awt.Insets(1, 2, 1, 2));
		btnCancel.setSize(85 , 25);
		btnCancel.setLocation(110, 240);
		add(btnCancel);
		btnCancel.addActionListener((new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancelChanges();
			}
		}));
    }
    
    public JSlider getContrastSlider() {
		return contrastSlider;
	}
    
    public void saveChanges(){
		try{
			base.getGamePlay().getDbManager().save(DbConstants.Keys.CONTRAST, String.valueOf(getContrastSlider().getValue()));
			base.getGamePlay().getPreferences().put(DbConstants.Keys.CONTRAST, String.valueOf(getContrastSlider().getValue()));
			base.getPreferencesFrame().setVisible(false);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
    }
    
    public void cancelChanges(){
    	int preferredValue = 100;
    	if(base.getGamePlay().getPreferences().containsKey(DbConstants.Keys.CONTRAST)){
    		preferredValue = Integer.parseInt(base.getGamePlay().getPreferences().get(DbConstants.Keys.CONTRAST));
    	}
    	base.getMainOuterFrame().setOpacity(((float)preferredValue) / (100f));
    	contrastSlider.setValue(preferredValue);
    	base.getPreferencesFrame().setVisible(false);
    }
}
