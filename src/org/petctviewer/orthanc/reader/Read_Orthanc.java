package org.petctviewer.orthanc.reader;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;


public class Read_Orthanc {
	
	JSONParser parser=new JSONParser();
	ParametreConnexionHttp connexion;
	
	public Read_Orthanc(ParametreConnexionHttp connexion) {
		this.connexion=connexion;
		
	}
	
	public ImagePlus readSerie(String uuid) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/series/"+uuid);
		JSONObject seriesDetails = null;
		try {
			seriesDetails=(JSONObject)parser.parse(sb.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageStack stack = null;
		
		JSONArray instanceIDList=(JSONArray) seriesDetails.get("Instances");
		boolean screenCapture=false;
		for(int i=0 ; i<instanceIDList.size(); i++) {
			
			if(i==0) {
				StringBuilder sop=connexion.makeGetConnectionAndStringBuilder("/instances/"+instanceIDList.get(i)+"/metadata/SopClassUid");
				//If it is a screen capture change the boolean
				if(sop.toString().startsWith("1.2.840.10008.5.1.4.1.1.7")) screenCapture=true;
			}
			
			ImageProcessor ip=readCompressed(instanceIDList.get(i).toString(), screenCapture);
			String metadata = "Compressed \n" + this.extractDicomInfo(instanceIDList.get(i).toString());
			
			if(i==0) {
				
				stack= new ImageStack(ip.getWidth(), ip.getHeight(), ip.getColorModel());
			}


			stack.addSlice(metadata, ip);
			System.out.println((double) i+1/instanceIDList.size());
			IJ.showProgress((double) i+1/instanceIDList.size());
		}
		ImagePlus imp=new ImagePlus();
		imp.setStack(stack);
		updateCalibration(imp);
		imp.show();
		return imp;
		
	}

	private ImageProcessor readCompressed(String uuid, boolean SC ) {
		ImageProcessor slice=null;
		try {
			
			String uri=null;
			if(SC) {
				uri = "/instances/" + uuid +  "/preview";
			}else {
				uri = "/instances/" + uuid +  "/image-uint16";

			}
			
			BufferedImage bi = ImageIO.read( connexion.openImage(uri));
		
			if( SC) slice = new ColorProcessor(bi);
			else slice = new ShortProcessor(bi);

			
		} catch (Exception e) { e.printStackTrace();}
		
		return slice;
		
	}
	
	private String extractDicomInfo(String uuid) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/instances/" + uuid + "/tags");
		
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
	
	private void updateCalibration( ImagePlus img) {
		String meta=img.getStack().getSliceLabel(1);
		double[] coeff = new double[2];
		float[] spacing;
		String tmp1;
		tmp1 = getDicomValue(meta, "0028,1052");
		if( tmp1 == null) return;
		coeff[0] = Double.parseDouble(tmp1);
		tmp1 = getDicomValue(meta, "0028,1053");
		if( tmp1 == null) return;
		coeff[1] = Double.parseDouble(tmp1);
		img.getCalibration().setFunction(Calibration.STRAIGHT_LINE, coeff, "Gray Value");
		tmp1 = getDicomValue(meta, "0028,0030");
		if( tmp1 == null) return;
		spacing = parseMultFloat(tmp1);
		img.getCalibration().pixelWidth = spacing[0];
		img.getCalibration().pixelHeight = spacing[1];
		img.getCalibration().setUnit("mm");
		
		img.setTitle(getDicomValue(meta, "0010,0010")+"-"+getDicomValue(meta, "0008,0022")+"-"+getDicomValue(meta, "0008,0103E"));
	}
	
	private String getDicomValue( String meta, String key1) {
		String tmp1, key2 = key1, ret1 = null;
		int k1, k0 = 0;
		if( meta == null) return ret1;
		if( key1 != null) {
			k0 = meta.indexOf(key1);
			if( k0 <= 0) key2 = key1.toLowerCase();
			k0 = meta.indexOf(key2);
		}
		if( k0 > 0 || key2 == null) {
			// here we have a problem that the key may appear more than once.
			// for example a SeriesUID may appear in a sequence. Look for ">".
			if( k0 > 0) {
				tmp1 = meta.substring(k0+4, k0+16);
				k1 = tmp1.indexOf(">");
				while(k1 > 0) {	// do search last value
					k1 = meta.indexOf(key2, k0+4);
					if( k1 > 0) k0 = k1;
				}
			}
			k1 = meta.indexOf("\n", k0);
			if( k1 < 0) return null;
			tmp1 = meta.substring(k0, k1);
			k1 = tmp1.indexOf(": ");
			if( k1 > 0) ret1 = tmp1.substring(k1+2);
			else ret1 = tmp1;
			ret1 = ret1.trim();
			if( ret1.isEmpty()) ret1 = null;
		}
		return ret1;
	}
	
	private float[] parseMultFloat( String tmp1) {
		float [] ret1 = null;
		double[] val = new double[32];	// arbitrary limit of 32
		int i, n = 0;
		if( tmp1 == null) return null;
		String tmp2 = tmp1.replace("\\ ", "\\");
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(tmp2).useDelimiter("\\\\");
		sc.useLocale(Locale.US);
		while(sc.hasNextDouble() && n < 32) {
			val[n++] = sc.nextDouble();
		}
		sc.close();
		if( n>0) {
			ret1 = new float[n];
			for( i=0; i<n; i++) ret1[i] = (float) val[i];
		}
		
		return ret1;
	}

}
