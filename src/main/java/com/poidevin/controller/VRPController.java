package com.poidevin.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.poidevin.UI.VRPUI;
import com.poidevin.beans.org.openstreetmap.gui.jmapviewer.Coordinate;
import com.poidevin.beans.org.openstreetmap.gui.jmapviewer.MapMarkerDot;

public class VRPController {
	
	private VRPUI view;
	
	private List<MapMarkerDot> lstDepotMarker = new ArrayList<>();
	private List<MapMarkerDot> lstLocationMarker = new ArrayList<>();


	public void setView(VRPUI vrpui) {
		view = vrpui;
		
	}

	public void init() {
		initCommandSettings();
		initCommandEvent();
		initMapEvent();
		
	}
	
	private VRPUI getView() {
		return view;
	}
	

	private void initCommandSettings()
	{

	}
	private void initCommandEvent()
	{
		getView().getjChkBoxInfFleetFlag().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(getView().getjChkBoxInfFleetFlag().isSelected())
				{
					getView().getjBtnAddDepot().setEnabled(false);
					getView().getGrdDepot().setEnabled(false);
				}else
				{
					getView().getjBtnAddDepot().setEnabled(true);
					getView().getGrdDepot().setEnabled(true);
					
				}
				
			}
		});
		
		getView().getBtnReset().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/**
				 * Map clearing
				 */
				getView().getMap().removeAllMapMarkers();
				
				/**
				 * Command clearing 
				 */
				getView().getjChkBoxInfFleetFlag().setSelected(false);
				getView().getjBtnAddDepot().setEnabled(true);
				getView().getGrdDepot().setEnabled(true);
				
				/**
				 * Remove Depot rows
				 */
				DefaultTableModel dmDepot = (DefaultTableModel) getView().getGrdDepot().getModel();
				int rowCountDepot = dmDepot.getRowCount();
				//Remove rows one by one from the end of the table
				for (int i = rowCountDepot - 1; i >= 0; i--) {
				    dmDepot.removeRow(i);
				}
				/**
				 * Add empty row in depot grid
				 */
				dmDepot.addRow(new Object[]{"", 
        				"", 
        				"",
        				"",
        				"",
        				""});
        		
        		/**
				 * Remove Depot rows
				 */
				DefaultTableModel dmLocation = (DefaultTableModel) getView().getGrdLocation().getModel();
				int rowCountLocation = dmLocation.getRowCount();
				//Remove rows one by one from the end of the table
				for (int i = rowCountLocation - 1; i >= 0; i--) {
				    dmLocation.removeRow(i);
				}
				/**
				 * Add empty row in depot grid
				 */
				dmLocation.addRow(new Object[]{"", 
        				"", 
        				"",
        				"",
        				"",
        				"",
        				""});
				/**
				 * clearing MapMarkerDot List
				 */
				lstDepotMarker.clear();
				lstLocationMarker.clear();
			}
		});
		
		getView().getGrdDepot().addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				getView().getGrdDepot().clearSelection();
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		getView().getGrdLocation().addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				getView().getGrdLocation().clearSelection();
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void initMapEvent()
	{
		getView().getMap().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
					
                	if(getView().getGrdDepot().getSelectedRowCount() != 0 )
                	{
                		/**
                		 * remove existing Depot marker on map
                		 */
                		for(MapMarkerDot clsMarker : lstDepotMarker)
                		{
                			getView().getMap().removeMapMarker(clsMarker);
                		}
                		
                		/**
                		 * Adding new Marker item in Grid
                		 */
                		DefaultTableModel model = (DefaultTableModel) getView().getGrdDepot().getModel();
                		int rowCount = model.getRowCount();
        				//Remove rows one by one from the end of the table
        				for (int i = rowCount - 1; i >= 0; i--) {
        					model.removeRow(i);
        				}
        				
                		model.addRow(new Object[]{"1", 
                				"1", 
                				((Coordinate)getView().getMap().getPosition( e.getPoint() )).getLat(),
                				((Coordinate)getView().getMap().getPosition( e.getPoint() )).getLon(),
                				"1",
                				"1"});
                		
                		MapMarkerDot clsMarker = new MapMarkerDot((Coordinate)getView().getMap().getPosition( e.getPoint() ));
                    	clsMarker.setName("1");
                    	clsMarker.setBackColor(Color.RED);
                    	getView().getMap().addMapMarker( clsMarker );
                    	lstDepotMarker.add(clsMarker);
                	}

                	if(getView().getGrdLocation().getSelectedRowCount() != 0 )
                	{
                		int iLastestID = 1;
                		DefaultTableModel model = (DefaultTableModel) getView().getGrdLocation().getModel();
                		//get the lastest ID
                		try
                		{
                			int rowCount = model.getRowCount();
                			iLastestID = Integer.valueOf(model.getValueAt(rowCount-1, 0).toString());
                			iLastestID++;
                			
                		}catch(Exception ex)
                		{
                			
                		}
                			
                		
                		model.addRow(new Object[]{
                				iLastestID, 
                				"1", 
                				((Coordinate)getView().getMap().getPosition( e.getPoint() )).getLat(),
                				((Coordinate)getView().getMap().getPosition( e.getPoint() )).getLon(),
                				"300",
                				"",
                				""});
                		
                		MapMarkerDot clsMarker = new MapMarkerDot((Coordinate)getView().getMap().getPosition( e.getPoint() ));
                    	clsMarker.setName(String.valueOf(iLastestID));
                    	getView().getMap().addMapMarker( clsMarker );
                    	
                	}
                }
            }
        });
	}
}
	