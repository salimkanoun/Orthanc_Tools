package org.petctviewer.orthanc.reader;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.petctviewer.orthanc.setup.OrthancRestApis;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import ij.util.DicomTools;

/**
 * Read a DICOM serie and return it in an ImagePlus
 * @author kanoun_s
 *
 */
public class Read_Orthanc {
	
	private JsonParser parser=new JsonParser();
	private OrthancRestApis connexion;
	
	public Read_Orthanc(OrthancRestApis connexion) {
		this.connexion=connexion;
	}
	
	private int getFrameNumber(String instanceID) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/instances/"+instanceID+"/frames/");
		JsonArray nbframe=parser.parse(sb.toString()).getAsJsonArray();
		return nbframe.size();
		
	}
	
	public ImagePlus readSerie(String uuid) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/series/"+uuid);
		JsonObject seriesDetails=(JsonObject)parser.parse(sb.toString());
		JsonArray instanceIDList=(JsonArray) seriesDetails.get("Instances");
		
		boolean screenCapture=false;
		int nbFrameInInstance = 0;
		ImageStack stack = null;
		
		for(int i=0 ; i<instanceIDList.size(); i++) {
			String metadata = this.extractDicomInfo(instanceIDList.get(i).getAsString());
			
			if(i==0) {
				StringBuilder sop=connexion.makeGetConnectionAndStringBuilder("/instances/"+instanceIDList.get(i).getAsString()+"/metadata/SopClassUid");
				nbFrameInInstance=getFrameNumber(instanceIDList.get(i).getAsString());
				//If it is a screen capture change the boolean
				if(sop.toString().startsWith("1.2.840.10008.5.1.4.1.1.7")) screenCapture=true;
				if(sop.toString().equals("1.2.840.10008.5.1.4.1.1.6.1")) screenCapture=true;
			}
			
			if(nbFrameInInstance==1) {
				ImageProcessor ip=readInstance(instanceIDList.get(i).getAsString(), screenCapture);
				if(i==0) {
					stack= new ImageStack(ip.getWidth(), ip.getHeight(), ip.getColorModel());
				}
				stack.addSlice(metadata, ip);
				IJ.showProgress((double) (i+1)/instanceIDList.size());
			} else {
				ImagePlus imp=readMultiFrameImage(instanceIDList.get(i).getAsString(), nbFrameInInstance, metadata, screenCapture);
				return imp;
			}
			
		}
		
		ImagePlus imp=generateFinalImagePlus(stack);
		return imp;
		
	}
	
	private ImagePlus readMultiFrameImage(String instanceID, int nbFrameInInstance, String metadata, boolean sc) {
		ImageStack stack = null;
		for(int i=0; i<nbFrameInInstance; i++) {
			ImageProcessor ip=readFrameInInstance(instanceID, i,sc );
			if(i==0) {
				stack= new ImageStack(ip.getWidth(), ip.getHeight(), ip.getColorModel());
			}
			stack.addSlice(metadata, ip);
			IJ.showProgress((double) (i+1)/nbFrameInInstance);
		}
		
		ImagePlus imp=generateFinalImagePlus(stack);
		return imp;
	}
	
	private ImagePlus generateFinalImagePlus(ImageStack stack) {
		
		ImagePlus imp=new ImagePlus();
		imp.setStack(stack);
		
		ImageStack stackSorted=this.sortStack(imp);
		imp.close();
		ImagePlus imp2=new ImagePlus();
		imp2.setStack(stackSorted);
		updateCalibration(imp2);
		imp2.setProperty("Info", imp2.getStack().getSliceLabel(1));
		
		//Parse Metadata to create Title
		String patientName="";
		if(getDicomValue(imp2.getInfoProperty(), "0010,0010")!=null) {
			patientName=getDicomValue(imp2.getInfoProperty(), "0010,0010");
		}
		
		String studyDate="";
		if(getDicomValue(imp2.getInfoProperty(), "0008,0022")!=null) {
			studyDate=getDicomValue(imp2.getInfoProperty(), "0008,0022");
		}else if(getDicomValue(imp2.getInfoProperty(), "0008,0020")!=null){
			studyDate=getDicomValue(imp2.getInfoProperty(), "0008,0020");
		}
		
		String serieDescription="";
		if(getDicomValue(imp2.getInfoProperty(), "0008,103E")!=null) {
			serieDescription=getDicomValue(imp2.getInfoProperty(), "0008,103E");
		}
		
		imp2.setTitle(patientName+"-"+studyDate+"-"+serieDescription);
		
		return imp2;
		
	}

	private ImageProcessor readInstance(String uuid, boolean SC ) {
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

			
		} catch (Exception e
				//SK ICI ESAYER METHODE AVEC LE FICHIER BRUTE
				) { e.printStackTrace();}
		
		return slice;
		
	}
	
	private ImageProcessor readFrameInInstance(String uuid, int frameNb, boolean SC ) {
		ImageProcessor slice=null;
		try {
			String uri=null;
			if(SC) {
				uri = "/instances/" + uuid +"/frames/" +frameNb+"/preview";
			}else {
				uri = "/instances/" + uuid +"/frames/"+frameNb+"/image-uint16";

			}
			BufferedImage bi = ImageIO.read( connexion.openImage(uri));
		
			if(SC) slice = new ColorProcessor(bi);
			else slice = new ShortProcessor(bi);

			
		} catch (Exception e) { e.printStackTrace();}
		
		return slice;
		
	}
	
	private String extractDicomInfo(String uuid) {
		StringBuilder sb=connexion.makeGetConnectionAndStringBuilder("/instances/" + uuid + "/tags");
		JsonObject tags = (JsonObject) parser.parse(sb.toString());
		if (tags == null || tags.size()==0) return "";
		String info = new String();
		String type1;

		ArrayList<String> tagsIndex = new ArrayList<String>();
		for (Object tag : tags.keySet()) {
			tagsIndex.add((String) tag);
		}

		Collections.sort(tagsIndex);
		for (String tag : tagsIndex) {
			JsonObject value = (JsonObject) tags.get(tag);
			type1 = value.get("Type").getAsString();
			if (type1.equals("String")) {
				info += (tag + " " + value.get("Name").getAsString()
					+ ": " + value.get("Value").getAsString() + "\n");
			} else {
				if( type1.equals("Sequence")) {
					info = addSequence(info, value, tag);
				}
			}
		}
		return info;
	}
	
	private int seqDepth = 0;
	
	private String addSequence(String info0, JsonObject value, Object tag) {
		String type2, info = info0;
		JsonArray seq0;
		JsonObject seqVal, vals;
		seq0 = (JsonArray) value.get("Value");
		if( seq0 == null || seq0.size()==0) {
			return info;	// ignore empty sequences
		}
		info += tag + getIndent() + value.get("Name").getAsString() +"\n";
		seqDepth++;
		seqVal = (JsonObject) seq0.get(0);

		ArrayList<String> tagsIndex = new ArrayList<String>();
		for( Object tag0 : seqVal.keySet()) {
			tagsIndex.add((String) tag0);
		}
		Collections.sort(tagsIndex);

		for( Object tag1 : tagsIndex) {
			vals = (JsonObject) seqVal.get((String) tag1);
			type2 = vals.get("Type").getAsString();
			if( type2.equals("String")) {
				info += tag1 + getIndent() + vals.get("Name").getAsString()
					+ ": " + vals.get("Value").getAsString()+ "\n";
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
	
	/**
	 * Sort stack according to ImageNumber
	 * In uparseable ImageNumber or non labelled image in stack, return the orignal stack without change
	 * @param imp : Original Image plus
	 * @return ImageStack : New ordered stack image by ImageNumber
	 */
	private ImageStack sortStack(ImagePlus imp) {
		
		ImageStack stack2 = new ImageStack(imp.getWidth(), imp.getHeight(), imp.getStack().getColorModel());
		
		HashMap<Integer,Integer> sliceMap=new HashMap<Integer,Integer>();
		
		for(int i=1; i<=imp.getImageStackSize(); i++) {
			
			try {
				imp.setSlice(i);
				String imageNumber=DicomTools.getTag(imp, "0020,0013").trim();
				sliceMap.put(Integer.parseInt(imageNumber), i);	
			
			}catch(Exception e1){
				//If parse error of slice number return the original stack
				return imp.getStack();
			}
		}
		
		//Check that the number of parsed image number is matching the number of slice
		if(sliceMap.size() ==imp.getStackSize()) {
			for(int i=1; i<=imp.getStackSize(); i++){
				int sliceToadd=sliceMap.get(i);
				stack2.addSlice(imp.getStack().getSliceLabel(sliceToadd),imp.getStack().getProcessor(sliceToadd));	
			}
		//Else return original stack	
		}else {
			return imp.getStack();
		}
		
		
		return stack2;
	}

}
