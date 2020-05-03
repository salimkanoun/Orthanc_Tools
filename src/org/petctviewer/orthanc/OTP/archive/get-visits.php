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
 * Get visits awaiting serie upload from a given study
 * 
 * $query=array(
		'username' => 'login',
		'password' => 'password',
        'studyName' =>'study')
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


// Get Data on calling users
$permissions=new User($data->username, $linkpdo);
$passwordCheck=$permissions->isPasswordCorrectAndActivitedAccount($data->password);
$studyInvestigatorAllowed=$permissions->isRoleAllowed($data->studyName, User::INVESTIGATOR);

//Check Account is active + password matching and user is investigator in the study
if ($passwordCheck && $studyInvestigatorAllowed){
    //Get Visits awaiting import from specified study
    
    $connecter = $linkpdo->prepare("SELECT visit_type FROM visits 
									WHERE study =:nometude 
									AND serie_upload ='Not Done' 
									AND status_done='Done'
                                    AND deleted=0 
									GROUP BY visit_type");

    $connecter->execute(array(
        "nometude" => $data->studyName
    ));
    
    $co = $connecter->fetchall();
    
    //Add the studies name in an array
    if(empty($co)){
        $AvailableVisites[]="None";
    }else{
        foreach($co as $value){
            $AvailableVisites[]=$value['visit_type'];
        }  
    }

}
else {
    $AvailableVisites[]="No Access";
}

//Return the available studies in JSON
echo json_encode($AvailableVisites)

?>