package com.datapath.telepath;
//I don't remember what this is for exactly. Something about packaging region information for region list or overview. 
public class Region {
	String regionName;
	
	float[] xVertices;
	float[] yVertices;
	public Region(String regionName, float[] xVertices, float[] yVertices){
		this.regionName = regionName;
		this.xVertices = xVertices;
		this.yVertices = yVertices;
	}
	public Region(){
		
	}
}
