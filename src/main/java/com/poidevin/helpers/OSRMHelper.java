package com.poidevin.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.poidevin.models.Spot;
import com.poidevin.models.SpotType;
import com.poidevin.inbound.OSRM.GeoRoutingJson;
import com.poidevin.inbound.OSRM.Waypoint;

public class OSRMHelper {
	
	private CloseableHttpClient httpclient = HttpClients.custom()
														.useSystemProperties()
														.build();

	private List<Spot> lstSpot = null;
	private String polyline = null;
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws RuntimeException
	 */
	public GeoRoutingJson solveproblem() throws IOException, RuntimeException
	{
		GeoRoutingJson clsResultJson = null;
		
		if( lstSpot != null && !lstSpot.isEmpty() )
		{
			StringBuilder strParam = new StringBuilder();
			//construct the URL parameter (Lon,Lat;Lon,Lat;...)
			//repeat the first depot point at last position for polyline closed routing
			Spot clsDepotSpot = null;
			for( Spot clsSpot : lstSpot )
			{
				if(SpotType.DEPOT.equals(clsSpot.getType()))
				{
					clsDepotSpot = clsSpot;
				}
				strParam.append(clsSpot.getLon()).append(",").append(clsSpot.getLat()).append(";");
			}
			if( clsDepotSpot != null )
			{
				strParam.append(clsDepotSpot.getLon()).append(",").append(clsDepotSpot.getLat()).append(";");
			}
			strParam.delete(strParam.length()-1, strParam.length() );
			
			// Create new getRequest with below mentioned URL
			HttpGet getHttpRequest = new HttpGet("http://router.project-osrm.org/route/v1/driving/"+strParam.toString()+"?overview=full" );
			
			// Add additional header to getRequest which accepts application/json data
			getHttpRequest.addHeader("accept", "application/json");
			
			// Execute the http request
			CloseableHttpResponse clsResponseReq = httpclient.execute( getHttpRequest );
			// Check for HTTP response code: 200 = success
			if (clsResponseReq.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + clsResponseReq.getStatusLine().getStatusCode());
			}
			
			String strJSonVal = convertStreamToString( clsResponseReq.getEntity().getContent() );
			
			Gson clsGSon = new Gson();
			
			clsResultJson = clsGSon.fromJson( strJSonVal, GeoRoutingJson.class);
			
			if(clsResultJson != null)
			{
				polyline = clsResultJson.getRoutes().get(0).getGeometry();
			}
			
		}
		
		
		return clsResultJson;
	}

	/**
	 * 
	 * @param _lstSpot
	 */
	public void setLstPoint(List<Spot> _lstSpot) {
		this.lstSpot = _lstSpot;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPolyline()
	{
		return this.polyline;
	}
	
	/**
	 * convertStreamToString - construct the Json String from the http response
	 * <p>
	 * <br><br><b>author</b> frpo25003473 2016-08-19
	 * <p>
	 * @return String Json
	 */
	private String convertStreamToString(InputStream is) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader( is ));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} finally 
		{
			is.close();
		}
		return sb.toString();
	}

}
