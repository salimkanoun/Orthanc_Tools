<?php
/**
 Copyright (C) 2018 KANOUN Salim and PROUDHOM Bastien
 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public v.3 License as published by
 the Free Software Foundation;
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along
 with this program; if not, write to the Free Software Foundation, Inc.,
 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

/**
 * Validate upload after RestFull Peer Transfer
 * input  : Username, Password, Patient number, Visit Name, StudyUID, Number of instance, originalOrthancId
 */


header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

require_once($_SERVER['DOCUMENT_ROOT'].DIRECTORY_SEPARATOR.'POO'.DIRECTORY_SEPARATOR.'autoload.php');

$linkpdo=Session::getLinkpdo();

// get posted data in a PHP Object
$data = json_decode(file_get_contents("php://input"));

$permissions=new User($data->username, $linkpdo);
$passwordCheck=$permissions->isPasswordCorrectAndActivitedAccount($data->password);

//Check Account is active and password is matching
if ($passwordCheck){
   
    //Studies array contain for each DICOM study sent :
    //"visitName "StudyInstanceUID" "patientNumber" "instanceNumber",
    foreach($data->studies as $value){
    	
        $study=get_object_vars($value);
        //Connect to the exposed Orthanc server
        $orthancExposed=new Orthanc(true);
        //Connect to the protected PACS Orthanc (definitive storage)
        $orthanc = new Orthanc() ;
        
        //Search in Orthanc the recieved study with it's StudyInstanceUID (unique key)
        $studyIDExposed=$orthancExposed->searchInOrthanc('studies','*','*','*',$study['StudyInstanceUID'],'*','*');

            //Get Visit ID of the current call (+check status)
            $getVisitID = $linkpdo->prepare('SELECT * FROM visits
                                             WHERE patient_code=:patientnumber 
                                                AND serie_upload ="Not Done" 
                                                AND visit_type=:visitType
                                                AND deleted=0 
                                                AND status_done="Done" ');
            $getVisitID->execute(array(
                "patientnumber" => $study['patientNumber'],
                "visitType" =>$study['visitName']
            ));
            
            //Fetch the only possible answer
            $results=$getVisitID->fetch(PDO::FETCH_ASSOC);
            $id_visit=$results['id_visit'];
            $visitObject=new Visit($id_visit, $linkpdo);
            
            //For each patient's study check that the user has the investigator role and the permissions on the uploaded patient
            $investigatorCheck=$permissions->isRoleAllowed($results['study'], User::INVESTIGATOR);
            $patientCheck=$permissions->isPatientAllowed($results['patient_code'], User::INVESTIGATOR);
            
            if($investigatorCheck && $patientCheck){
            	//Move the study from Expose to Pacs Orthanc
            	$orthancExposed->sendToPeer("OrthancPacs", $studyIDExposed);
            	//Remove the moved study from the exposed server
            	$orthancExposed->deleteFromOrthanc("studies", $studyIDExposed[0]);
            	//Search the moved study in the Pacs Orthanc to get it's detail
            	$studyID=$orthanc->searchInOrthanc('studies','*','*','*',$study['StudyInstanceUID'],'*','*');
	            //Object to fill database
	            $fillTable=new Fill_Orthanc_Table($id_visit, $data->username, $linkpdo);
	            $fillTable->parseData($studyID[0]);
	            
	            //If correct number of instance recived, write in the database and validate upload
	            if($studyDetails['countInstances'] == $study['instanceNumber']){
	                //Fill database
	            	$envoieOK['recivedConfirmation']=$fillTable->fillDB($data->originalOrthancId);
	                
	                //Notification email, will be sent only if form also uploaded
	                $visitObject->sendUploadedVisitEmail($data->username);
	                
	                $jsonResponse=json_encode($envoieOK);
	                
	            }
	            //If non matching number of instances
	            else{
	                $envoieOK['recivedConfirmation']=false;
	                $jsonResponse=json_encode($envoieOK);
	                //Delete incomplete Orthanc ressource
	                $orthanc->deleteFromOrthanc("studies", $studyID[0]);
	                break 1;
            }
            //Not permited upload
            }else{
            	$envoieOK['recivedConfirmation']=false;
            	$jsonResponse=json_encode($envoieOK);
            	//Delete incomplete Orthanc ressource from exposed server
            	$orthancExposed->deleteFromOrthanc("studies", $studyIDExposed[0]);
            	break 1;
            }

    
    }

    //Log activity
    $actionDetails=$data->studies;
    User::logActivity($data->username, User::INVESTIGATOR, null, null, "Upload Series", $actionDetails);

//If no permissions
}else{
    $envoieOK['recivedConfirmation']=false;
    $jsonResponse=json_encode($envoieOK);
}

echo $jsonResponse;
?>
