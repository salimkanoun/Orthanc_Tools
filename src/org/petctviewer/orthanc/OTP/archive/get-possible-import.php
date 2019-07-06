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
 * return patients matching study / visits and awaiting upload returning patients matches details
 * $query=array(
		'username' => 'login',
		'password' => 'password',
        'studyName' =>'study',
        'visit'=>'TEP0')
        
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

// Get Data of calling user
$permissions=new User($data->username, $linkpdo);
$passwordCheck=$permissions->isPasswordCorrectAndActivitedAccount($data->password);
$studyInvestigatorAllowed=$permissions->isRoleAllowed($data->studyName, User::INVESTIGATOR);
$usercenters=$permissions->getInvestigatorsCenters();

if ($passwordCheck && $studyInvestigatorAllowed){

    //Get Visits awaiting import from specified study/Visit Type
	$studyObject=new Study($data->studyName, $linkpdo);
	$VisitArrayWaintingUpload=$studyObject->getAwaitingUploadVisit();
    
    $AvailablePatients=[];
    //Add the studies name in an array
    foreach($VisitArrayWaintingUpload as $visit){
    	$patientObject=$visit->getPatient();
    	$patientCenter=$patientObject->getPatientCenter();
        //Check If patient center is included in user's centers before filling the answer table
    	if (in_array($patientCenter->code, $usercenters)){
    		$patient['numeroPatient']=$patientObject->patientCode;
    		$patient['firstName']=$patientObject->patientFirstName;
    		$patient['lastName']=$patientObject->patientLastName;
    		$patient['patientSex']=$patientObject->patientGender;
    		$patient['patientDOB']=$patientObject->patientBirthDate;
    		$patient['investigatorName']=$patientObject->patientInvestigatorName;
    		$patient['country']=$patientObject->getPatientCountry();
    		$patient['centerNumber']=$patientCenter->code;
    		$dateAcquisition=date('m-d-Y',strtotime($visit->acquisitionDate));
            $patient['acquisitionDate']=$dateAcquisition;
            $AvailablePatients[]=$patient;
        }
    }
    
}
else {
    $AvailablePatients=[];
}

//Add Orthanc Credential to answer
$response['AvailablePatients']=$AvailablePatients;

$preferences=Preferences::getPreferences($linkpdo);
$response['OrthancServer']=$preferences['Orthanc_Exposed_Address'];
$response['OrthancPort']=$preferences['Orthanc_Exposed_Port'];
$response['OrthancLogin']=$preferences['Orthanc_Exposed_ExternalLogin'];
$response['OrthancPassword']=$preferences['Orthanc_Exposed_ExternalPassword'];

echo(json_encode($response));


?>