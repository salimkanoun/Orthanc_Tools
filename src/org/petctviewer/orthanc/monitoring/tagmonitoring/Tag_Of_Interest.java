package org.petctviewer.orthanc.monitoring.tagmonitoring;

public class Tag_Of_Interest {

	/*
	Patients 
	0010,0020 = Patient ID
	0010,0010 = Patient Name
	0010,0040 = Patient's Sex
	0010,0030 = Patient Date of birth
	
	Study
	0008,0020 = StudyDate
	0008,0030 = StudyTime
	0010,1020 = Patient's Size
	0010,1010 = Patient's Age
	0010,1030 = Patient Weight
	0008,1030 = Study Description
	0008,0050 = Accession Number
	0020,000d = Study Instance UID
	0020,0010 = Study ID
	
	Series
	0008,0070= Manifacturer
	0008,1090 = Manifacturer Model
	0008,1050 = Performing Physician Name
	0008,103E = Series Description
	0008,1010 = Station Name
	0008,0023 = Content Date
	0008,0033 = Content Time
	0018,1030 = Protocol Name
	0020,000e = Series Instance UID
	0040,0310 = Comment Radiation Dose
	0054,0016 = Radiopharmaceutical sequence
	0018,0031 = Radiopharmaceutical
	0018,1072 = RadiopharmaceuticalStartTime
	0018,1074 = RadionuclideTotalDose
	0018,1075 = RadionuclideHalfLife
	0018,1076 = RadionuclidePositronFraction
	
	0040,030e (Radiation Dose Module) (tag 0018,9345 (CTDIvol))
	*/
	
	public static String[] tagOfInterestPatient = {"0010,0020","0010,0010", "0010,0040", "0010,0030"};
	public static String[] tagOfInterestStudy= {"0008,0020", "0008,0030", "0010,1020", "0010,1010",
			"0010,1030", "0008,1030", "0008,0050", "0020,000d", "0020,0010"};
	public static String[] tagOfInterestSeries= {"0008,0070","0008,1090","0008,1050","0008,103e","0008,1010","0008,0023",
			"0008,0033","0018,1030","0020,000e", "0040,0310"};
	public static String radiopharmaceuticalTag="0054,0016";
	public static String radiationDoseModule="0040,030e";
	public static String[] radiopharmaceutical= { "0018,0031", "0018,1072", "0018,1074", "0018,1075", "0018,1076"};
}
