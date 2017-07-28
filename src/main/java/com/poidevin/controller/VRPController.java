package com.poidevin.controller;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.table.DefaultTableModel;

import com.poidevin.UI.VRPUI;
import com.poidevin.beans.org.openstreetmap.gui.jmapviewer.Coordinate;
import com.poidevin.beans.org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import com.poidevin.beans.org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import com.poidevin.helpers.OSRMHelper;
import com.poidevin.helpers.VRPHelper;
import com.poidevin.inbound.OSRM.GeoRoutingJson;
import com.poidevin.models.Spot;
import com.poidevin.models.SpotType;

public class VRPController {
	
	private static VRPUI view;
	
	private List<MapMarkerDot> lstDepotMarker = new ArrayList<>();
	private List<MapMarkerDot> lstLocationMarker = new ArrayList<>();
	
	private GeoRoutingJson clsOSRMResult = null;
	private MapPolygonImpl clsPolyOSRM = null;
	private List<MapMarkerDot> lstMapMarkersOSRM = new ArrayList<>();

	/**
	 * 
	 * @param vrpui
	 */
	public void setView(VRPUI vrpui) {
		view = vrpui;
		
	}

	/**
	 * 
	 */
	public void init() {
		initCommandSettings();
		initCommandEvent();
		initMapEvent();
		
	}
	
	/**
	 * 
	 * @return
	 */
	private static  VRPUI getView() {
		return view;
	}
	
	/**
	 * 
	 */
	private void initCommandSettings()
	{
		//Nothing to do yet
	}
	
	/**
	 * 
	 */
	private void initCommandEvent()
	{
		getView().getjChkBoxInfFleetFlag().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(getView().getjChkBoxInfFleetFlag().isSelected())
				{
					getView().getGrdDepot().setEnabled(false);
				}else
				{
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
				getView().getMap().removeAllMapPolygons();
				
				/**
				 * Command clearing 
				 */
				getView().getjChkBoxInfFleetFlag().setSelected(false);
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
				dmDepot.addRow(new Object[]{null, 
						null, 
						null,
						null,
						null,
						null});
        		
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
				dmLocation.addRow(new Object[]{null, 
						null, 
						null,
						null,
						null,
						null,
						null,
						null});
				/**
				 * clearing List
				 */
				lstDepotMarker.clear();
				lstLocationMarker.clear();
				clsOSRMResult = null;
				clsPolyOSRM = null;
				lstMapMarkersOSRM = new ArrayList<>();
			}
		});
		
		getView().getGrdDepot().addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				getView().getGrdDepot().clearSelection();
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// Nothing to do
				
			}
		});

		getView().getGrdLocation().addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				getView().getGrdLocation().clearSelection();
				
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// Nothing to do
				
			}
		});
		
		getView().getBtnProcess().addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
            	new Thread(new Runnable() {
					@Override
					public void run() {
						processVRP();
				
					}
				}).start();
				
			}
		});
		
		getView().getChkTripDisplay().addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(getView().getChkTripDisplay().isSelected() && clsPolyOSRM != null)
				{
					lstMapMarkersOSRM = new ArrayList<>();
					//add markers to the map
					for( int i = 0 ; i < clsOSRMResult.getWaypoints().size(); i++)
					{
						if( i != clsOSRMResult.getWaypoints().size() -1 )
						{
							MapMarkerDot clsOSRMMarker = new MapMarkerDot(clsOSRMResult.getWaypoints().get(i).getLocation().get(1), 
									clsOSRMResult.getWaypoints().get(i).getLocation().get(0));
							clsOSRMMarker.setName(String.valueOf(i));
							clsOSRMMarker.setBackColor(Color.RED);
							lstMapMarkersOSRM.add(clsOSRMMarker);	
							getView().getMap().addMapMarker(clsOSRMMarker);
						}
					}
					
					//add the polyline to the map
					clsPolyOSRM.setColor(Color.RED);
					clsPolyOSRM.setPolyOpen(true);
					getView().getMap().addMapPolygon( clsPolyOSRM );
					
				}else
				{
					//remove the OSRM polyline
					getView().getMap().removeMapPolygon(clsPolyOSRM);
					//remove all OSRm markers
					for(MapMarkerDot clsMarker : lstMapMarkersOSRM)
					{
						getView().getMap().removeMapMarker(clsMarker);
					}
				}
				
			}
		});
	}

	/**
	 * 
	 */
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
                    	clsMarker.setBackColor(Color.GREEN);
                    	getView().getMap().addMapMarker( clsMarker );
                    	lstDepotMarker.add(clsMarker);
                    	//logs
                    	addInfo(String.format("Add new Depot (ID:%s) : %s ", 1, clsMarker.getCoordinate().toString() ) );
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
                			//Remove rows one by one from the end of the table
            				for (int i = model.getRowCount() - 1; i >= 0; i--) {
            					model.removeRow(i);
            				}
                		}
                			
                		
                		model.addRow(new Object[]{
                				iLastestID, 
                				"1", 
                				((Coordinate)getView().getMap().getPosition( e.getPoint() )).getLat(),
                				((Coordinate)getView().getMap().getPosition( e.getPoint() )).getLon(),
                				false,
                				"300",
                				"",
                				""});
                		
                		MapMarkerDot clsMarker = new MapMarkerDot((Coordinate)getView().getMap().getPosition( e.getPoint() ));
                    	clsMarker.setName(String.valueOf(iLastestID));
                    	getView().getMap().addMapMarker( clsMarker );
                    	getView().getGrdLocation().setRowSelectionInterval(0, 0);
                    	//logs
                    	addInfo(String.format("Add new Location (ID:%s) : %s ", iLastestID, clsMarker.getCoordinate().toString() ) );
                	}
                }
            }
        });
	}
	
	/**
	 * 
	 */
	private void processVRP()
	{
    	List<Spot> lstSpot = new ArrayList<>();
    	
    	//validate existing datas
		DefaultTableModel grdDepotModel = (DefaultTableModel) getView().getGrdDepot().getModel();
		DefaultTableModel grdLocationModel = (DefaultTableModel) getView().getGrdLocation().getModel();
		
		if( grdDepotModel.getValueAt(0, 0) != null &&
			grdLocationModel.getValueAt(0, 0) != null )
		{
			try
			{
				//Collect datas
				lstSpot = collectDatas(grdDepotModel, grdLocationModel);
				
				//logs
		    	addInfo("Start OSRM Processing" );
				//calculate the optimize OSRM routing
				OSRMHelper clsOSRMHelper = new OSRMHelper();
				clsOSRMHelper.setLstPoint(lstSpot);
				clsOSRMResult = clsOSRMHelper.solveproblem();
				clsPolyOSRM = new MapPolygonImpl(decode(clsOSRMHelper.getPolyline()));
				//logs
		    	addInfo("End OSRM Processing" );
				
				//logs
		    	addInfo("Start VRP Processing" );
				//calculate the optimize VRP routing
				//instanciate the VRP worker
				VRPHelper clsVRPHelper = new VRPHelper();
				clsVRPHelper.setLstPoint(lstSpot);
				
				// VRP processing here
				List<Spot>lstOrdonnateSpot_VRP = clsVRPHelper.solveProblem();
				//logs
		    	addInfo("End VRP Processing" );
				
				
			}catch(Exception ex)
			{
				JOptionPane.showMessageDialog(getView(),"Error : " + ex.getMessage() );
				//logs
		    	addInfo( "Error OSRM or VRP : " + ex.getMessage() );
			}
			
		}else
		{
			JOptionPane.showMessageDialog(getView(),"Please define Depot and Location");
		}

		
	}
	
	/**
	 * 
	 * @param _grdDepotModel
	 * @param _grdLocationModel
	 * @return
	 * @throws Exception
	 */
	private List<Spot> collectDatas(DefaultTableModel _grdDepotModel,
									DefaultTableModel _grdLocationModel) throws Exception
	{
		//TODO check here the datas validation format
		List<Spot> lstSpot = new ArrayList<>();
		/**
		 * Depot fetching
		 */
		for(int i = 0 ; i < _grdDepotModel.getRowCount() ; i++ )
		{
			lstSpot.add(new Spot(	_grdDepotModel.getValueAt(i, 0).toString(), 
									SpotType.DEPOT, 
									Double.valueOf(_grdDepotModel.getValueAt(i, 2).toString()), 
									Double.valueOf(_grdDepotModel.getValueAt(i, 3).toString()), 
									null, 
									null, 
									null, 
									null ) );
		}	
		
		/**
		 * Locations fetching
		 */
		for(int i = 0 ; i < _grdLocationModel.getRowCount() ; i++ )
		{
			lstSpot.add(new Spot(	_grdLocationModel.getValueAt(i, 0).toString(), 
									((boolean)_grdLocationModel.getValueAt(i, 4)) ? SpotType.PICKUP : SpotType.DELIVERY, 
									Double.valueOf(_grdLocationModel.getValueAt(i, 2).toString()), 
									Double.valueOf(_grdLocationModel.getValueAt(i, 3).toString()), 
									Integer.valueOf(_grdLocationModel.getValueAt(i, 6).toString().isEmpty() ? "0" : _grdLocationModel.getValueAt(i, 6).toString() ), 
									Integer.valueOf(_grdLocationModel.getValueAt(i, 7).toString().isEmpty() ? "0" : _grdLocationModel.getValueAt(i, 7).toString() ), 
									Integer.valueOf(_grdLocationModel.getValueAt(i, 1).toString().isEmpty() ? "0" : _grdLocationModel.getValueAt(i, 1).toString() ), 
									Integer.valueOf(_grdLocationModel.getValueAt(i, 5).toString().isEmpty() ? "0" : _grdLocationModel.getValueAt(i, 5).toString() ) ) );
		}
		
		return lstSpot;
	}
	
	/**
	 * Allow to decode a OSRM polyline for displayed it onto the OSM map
	 * 
	 * @param _encoded
	 * @return
	 */
	public static List<Coordinate> decode(final String encodedPath) {
        int len = encodedPath.length();

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        final List<Coordinate> path = new ArrayList<Coordinate>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new Coordinate(lat * 1e-5, lng * 1e-5));
        }

        return path;
	}
	

	/**
	 * 
	 * @param strText
	 */
	public static void addInfo(String strText)
	{
		SimpleDateFormat clsDateFOrmatter = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss - ");
		
		String strExistingText = getView().getTxtAreaInfo().getText();
		getView().getTxtAreaInfo().setText(strExistingText + clsDateFOrmatter.format(new Date()) + strText + "\n" );
		JScrollBar vertical = getView().getScrollPaneInfo().getVerticalScrollBar();
		vertical.setValue( vertical.getMaximum() );
	}
}
	