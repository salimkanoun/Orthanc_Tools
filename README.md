# Orthanc_Tools
DICOM tools built on Orthanc API in Java

Features : 

- Anonymization : Fine tunable anonymization and sharing services of Anonymized DICOMs (FTP/SSH/WebDAV)

- Modification : Edit DICOM tags in Patient / Study / Serie levels

- Export : 
   - Zip Export of DICOMs stored in Orthanc (Hierachical or DICOMDIR)
   - CD/DVD image generation with patient's DICOM and ImageJ viewer (zip or ISO)
   
 - Manage : 
   - Single and Batch deletion of Patients / Studies / Series in Orthanc
   
 - Query : 
   - Query / Retrieve from remote AET
   - Automatic / Batch retrieve of studies (with Schedule feature)
      - Possibility to make series based filters for selective auto - retrieve
      - CSV report of auto-retrieve procedure
   
 - Import :
   - Recursive import to Orthanc of local DICOMs
   
 - Monitoring : Work In Progress
   - Auto-Fetch : Automatically retrieve patient's history for each new study/patient recieved in Orthanc
   - CD-Burner : Generate DVD burning intruction for Epson PP100II diskmaker
   - Tag-Monitoring : Autocollection of DICOM tag value of recieved patients/studies/series (monitoring injected dose, dlp...)
   - Auto-Routing (Not desgigned yet)
   
 Contribution from http://petctviewer.org, free and open source PET/CT viewer based on Fiji/ImageJ
 
 GPL v.3 Licence
 
 Salim Kanoun & Anousone Vongsalat
 
