package org.petctviewer.orthanc.reader;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;


public class Read_Orthanc {
	
	ParametreConnexionHttp connexion=new ParametreConnexionHttp();
	
	public static void main(String[] args) {
		Read_Orthanc orthancReader= new Read_Orthanc();
		orthancReader.readCompressed("87c4fd56-6d4e7573-c3c640ca-b33a09b9-5adfa7ee");
	}

	ImageProcessor readCompressed(String uuid) {
		ImageProcessor slice=null;
		try {
			
			String imgType = "/image-uint16";
			//int bufType = BufferedImage.TYPE_USHORT_GRAY;
			/*if(SC) {
				imgType = "/preview";
				bufType = BufferedImage.TYPE_3BYTE_BGR;
			}*/
			String uri = "/instances/" + uuid +  "/image-uint16";
			BufferedImage bi = ImageIO.read( connexion.openImage(uri));
			
			//if( SC) slice = new ColorProcessor(bi);
			//else 
			slice = new ShortProcessor(bi);
			/*
			if( stack == null) {
				ColorModel cm = slice.getColorModel();
				stack = new ImageStack(slice.getWidth(), slice.getHeight(), cm);
			}
			stack.addSlice(tmp1, slice);
			*/
			String tmp1 = "Compressed \n" + this.extractDicomInfo(uuid);
			ImagePlus ip=new ImagePlus(tmp1,slice);
			ip.show();
		} catch (Exception e) { e.printStackTrace();}
		return slice;
	}
	
	private String extractDicomInfo(String uuid) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/instances/" + uuid + "/tags");
		JSONParser parser=new JSONParser();
		JSONObject tags=null;
		try {
			tags = (JSONObject) parser.parse(sb.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (tags == null || tags.isEmpty()) return "";
		String info = new String();
		String type1;

		ArrayList<String> tagsIndex = new ArrayList<String>();
		for (Object tag : tags.keySet()) {
			tagsIndex.add((String) tag);
		}

		Collections.sort(tagsIndex);
		for (String tag : tagsIndex) {
			JSONObject value = (JSONObject) tags.get(tag);
			type1 = (String) value.get("Type");
			if (type1.equals("String")) {
				info += (tag + " " + (String) value.get("Name")
					+ ": " + (String) value.get("Value") + "\n");
			} else {
				if( type1.equals("Sequence")) {
					info = addSequence(info, value, tag);
				}
			}
		}
		return info;
	}
	
	private int seqDepth = 0;
	
	private String addSequence(String info0, JSONObject value, Object tag) {
		String type2, info = info0;
		JSONArray seq0;
		JSONObject seqVal, vals;
		seq0 = (JSONArray)value.get("Value");
		if( seq0 == null || seq0.isEmpty()) {
			return info;	// ignore empty sequences
		}
		info += tag + getIndent() + (String) value.get("Name") +"\n";
		seqDepth++;
		seqVal = (JSONObject) seq0.get(0);

		ArrayList<String> tagsIndex = new ArrayList<String>();
		for( Object tag0 : seqVal.keySet()) {
			tagsIndex.add((String) tag0);
		}
		Collections.sort(tagsIndex);

		for( Object tag1 : tagsIndex) {
			vals = (JSONObject) seqVal.get(tag1);
			type2 = (String) vals.get("Type");
			if( type2.equals("String")) {
				info += tag1 + getIndent() + (String) vals.get("Name")
					+ ": " + (String) vals.get("Value")+ "\n";
			} else {
				if(type2.equals("Sequence")) {
					info = addSequence(info, vals, tag1);
				}
			}
		}
		seqDepth--;
		return info;
	}
	
	private String getIndent() {
		String indent = " ";
		for( int i=0; i<seqDepth; i++) indent += ">";
		return indent;
	}

}
