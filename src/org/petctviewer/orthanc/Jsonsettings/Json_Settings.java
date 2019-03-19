/**
Copyright (C) 2017 KANOUN Salim
This
 program is free software; you can redistribute it and/or modify
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

package org.petctviewer.orthanc.Jsonsettings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringWriter;

import org.petctviewer.orthanc.Orthanc_Tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.packtag.implementation.JSMin;


public class Json_Settings {
	
	protected JsonObject index = new JsonObject();
	
	// JsonObject des AET
	protected JsonObject dicomNode=new JsonObject();
	// JsonObject des orthancPeer
	protected JsonObject orthancPeer=new JsonObject();
	// JsonObject des contentType
	protected JsonObject contentType=new JsonObject();
	// JsonObject des dictionary
	protected JsonObject dictionary=new JsonObject();
	
	// Array des Lua folder 
	protected JsonArray luaFolder=new JsonArray();
	
	//Array des plugin folder
	protected JsonArray pluginsFolder=new JsonArray();
	
	// Object pour les users
	protected JsonObject users =new JsonObject();
	
	//Object pour les metadata
	protected JsonObject userMetadata=new JsonObject();
	
	// A modifier via des setteurs
	protected String orthancName;
	protected String storageDirectory;
	protected String indexDirectory;
	protected boolean StorageCompression;
	protected int MaximumStorageSize;
	protected int MaximumPatientCount;
	protected boolean HttpServerEnabled;
	protected int HttpPort;
	protected boolean HttpDescribeErrors;
	protected boolean HttpCompressionEnabled;
	protected boolean DicomServerEnabled;
	protected String DicomAet;
	protected boolean DicomCheckCalledAet;
	protected int DicomPort;
	protected String DefaultEncoding;
	protected boolean DeflatedTransferSyntaxAccepted;
	protected boolean JpegTransferSyntaxAccepted;
	protected boolean Jpeg2000TransferSyntaxAccepted;
	protected boolean JpegLosslessTransferSyntaxAccepted;
	protected boolean JpipTransferSyntaxAccepted;
	protected boolean Mpeg2TransferSyntaxAccepted;
	protected boolean RleTransferSyntaxAccepted;
	protected boolean UnknownSopClassAccepted;
	protected int DicomScpTimeout;
	protected boolean RemoteAccessAllowed;
	protected boolean SslEnabled;
	protected String SslCertificate;
	protected boolean AuthenticationEnabled;
	protected int DicomScuTimeout;
	protected String HttpProxy;
	protected int HttpTimeout;
	protected boolean HttpsVerifyPeers;
	protected String HttpsCACertificates;
	protected int StableAge;
	protected boolean StrictAetComparison;
	protected boolean StoreMD5ForAttachments;
	protected int LimitFindResults;
	protected int LimitFindInstances;
	protected int LimitJobs;
	protected boolean LogExportedResources;
	protected boolean KeepAlive;
	protected boolean StoreDicom;
	protected int DicomAssociationCloseDelay;
	protected int QueryRetrieveSize;
	protected boolean CaseSensitivePN;
	protected boolean LoadPrivateDictionary;
	protected boolean dicomAlwaysAllowEcho;
	protected boolean DicomAlwaysStore;
	protected boolean CheckModalityHost;
	protected boolean SynchronousCMove;
	protected int JobsHistorySize;
	protected int ConcurrentJobs;
	
	protected boolean dicomModalitiesInDb;
	protected boolean orthancPeerInDb;
	protected boolean overwriteInstances;
	protected int mediaArchiveSize;
	protected String storageAccessOnFind;
	
	
	protected File fichierInput;
	
	public Json_Settings() {
		initialiserIndex();
	}
	
	protected void initialiserIndex() {
		// on Set des valeurs par defaut
				orthancName="myOrthanc";
				storageDirectory="OrthancStorage";
				indexDirectory="OrthancStorage";
				StorageCompression=false;
				MaximumStorageSize=0;
				MaximumPatientCount=0;
				//Lua scripts
				//plugins
				ConcurrentJobs=2;
				HttpServerEnabled=true;
				HttpPort=8042;
				HttpDescribeErrors=true;
				HttpCompressionEnabled=true;
				DicomServerEnabled=true;
				DicomAet="Orthanc";
				DicomCheckCalledAet=false;
				DicomPort=4242;
				DefaultEncoding="Latin1";
				DeflatedTransferSyntaxAccepted=true;
				JpegTransferSyntaxAccepted=true;
				Jpeg2000TransferSyntaxAccepted=true;
				JpegLosslessTransferSyntaxAccepted=true;
				JpipTransferSyntaxAccepted=true;
				Mpeg2TransferSyntaxAccepted=true;
				RleTransferSyntaxAccepted=true;
				UnknownSopClassAccepted=false;
				DicomScpTimeout=30;
				RemoteAccessAllowed=false;
				SslEnabled=false;
				SslCertificate="certificate.pem";
				AuthenticationEnabled=false;
				//registred users
				//Dicom modalities
				dicomModalitiesInDb=false;
				dicomAlwaysAllowEcho=true;
				DicomAlwaysStore=true;
				CheckModalityHost=false;
				DicomScuTimeout=10;
				//Orthanc Peers
				orthancPeerInDb = false;
				HttpProxy="";
				httpVerbose=false;
				httpTimout=10;
				HttpsVerifyPeers=true;
				HttpsCACertificates="";
				//userMetadata
				//userContentType
				StableAge=60;
				StrictAetComparison=false;
				StoreMD5ForAttachments=true;
				LimitFindResults=0;
				LimitFindInstances=0;
				LimitJobs=10;
				LogExportedResources=false;
				KeepAlive=true;
				tcpNoDelay=true;
				httpThreadsCount=50;
				StoreDicom=true;
				DicomAssociationCloseDelay=5;
				QueryRetrieveSize=10;
				CaseSensitivePN=false;
				LoadPrivateDictionary=true;
				//Dictionary
				SynchronousCMove=true;
				JobsHistorySize=10;
				saveJobs=true;
				overwriteInstances=false;
				mediaArchiveSize=1;
				storageAccessOnFind="Always";
				metricEnabled=true;	
	}
	
	// permet de creer le JSON avant de l'ecrire
	public void construireIndex() {
		
		//On rentre les valeurs contenue dans les variables
		index.addProperty("Name", orthancName);
		index.addProperty("StorageDirectory", storageDirectory);
		index.addProperty("IndexDirectory", indexDirectory);
		index.addProperty("StorageCompression", StorageCompression);
		index.addProperty("MaximumStorageSize", MaximumStorageSize);
		index.addProperty("MaximumPatientCount", MaximumPatientCount);
		index.add("LuaScripts", luaFolder);
		index.add("Plugins", pluginsFolder);
		index.addProperty("ConcurrentJobs", ConcurrentJobs);
		index.addProperty("HttpServerEnabled", HttpServerEnabled);
		index.addProperty("HttpPort", HttpPort);
		index.addProperty("HttpDescribeErrors", HttpDescribeErrors);
		index.addProperty("HttpCompressionEnabled", HttpCompressionEnabled);
		index.addProperty("DicomServerEnabled", DicomServerEnabled);
		index.addProperty("DicomAet", DicomAet);
		index.addProperty("DicomCheckCalledAet", DicomCheckCalledAet);
		index.addProperty("DicomPort", DicomPort);
		index.addProperty("DefaultEncoding", DefaultEncoding);
		index.addProperty("DeflatedTransferSyntaxAccepted", DeflatedTransferSyntaxAccepted);
		index.addProperty("JpegTransferSyntaxAccepted", JpegTransferSyntaxAccepted);
		index.addProperty("Jpeg2000TransferSyntaxAccepted", Jpeg2000TransferSyntaxAccepted);
		index.addProperty("JpegLosslessTransferSyntaxAccepted", JpegLosslessTransferSyntaxAccepted);
		index.addProperty("JpipTransferSyntaxAccepted", JpipTransferSyntaxAccepted);
		index.addProperty("Mpeg2TransferSyntaxAccepted", Mpeg2TransferSyntaxAccepted);
		index.addProperty("RleTransferSyntaxAccepted", RleTransferSyntaxAccepted);
		index.addProperty("UnknownSopClassAccepted", UnknownSopClassAccepted);
		index.addProperty("DicomScpTimeout", DicomScpTimeout);
		index.addProperty("RemoteAccessAllowed", RemoteAccessAllowed);
		index.addProperty("SslEnabled", SslEnabled);
		index.addProperty("SslCertificate", SslCertificate);
		index.addProperty("AuthenticationEnabled", AuthenticationEnabled);
		index.add("RegisteredUsers", users);
		index.add("DicomModalities", dicomNode);

		
		index.addProperty("DicomAlwaysAllowEcho", dicomAlwaysAllowEcho);
		index.addProperty("DicomAlwaysAllowStore", DicomAlwaysStore);
		index.addProperty("DicomCheckModalityHost", CheckModalityHost);
		
		index.addProperty("DicomScuTimeout", DicomScuTimeout);
		index.add("OrthancPeers", orthancPeer);
		index.addProperty("HttpProxy", HttpProxy);
		index.addProperty("HttpTimeout", HttpTimeout);
		index.addProperty("HttpsVerifyPeers", HttpsVerifyPeers);
		index.addProperty("HttpsCACertificates", HttpsCACertificates);
		index.add("UserMetadata", userMetadata);
		index.add("UserContentType", contentType);
		index.addProperty("StableAge", StableAge);
		index.addProperty("StrictAetComparison", StrictAetComparison);
		index.addProperty("StoreMD5ForAttachments", StoreMD5ForAttachments);
		index.addProperty("LimitFindResults", LimitFindResults);
		index.addProperty("LimitFindInstances", LimitFindInstances);
		index.addProperty("LimitJobs", LimitJobs);
		index.addProperty("LogExportedResources", LogExportedResources);
		index.addProperty("KeepAlive", KeepAlive);
		index.addProperty("StoreDicom", StoreDicom);
		index.addProperty("DicomAssociationCloseDelay", DicomAssociationCloseDelay);
		index.addProperty("QueryRetrieveSize", QueryRetrieveSize);
		index.addProperty("CaseSensitivePN", CaseSensitivePN);
		index.addProperty("LoadPrivateDictionary", LoadPrivateDictionary);
		index.add("Dictionary", dictionary);
		index.addProperty("SynchronousCMove", SynchronousCMove);
		index.addProperty("JobsHistorySize", JobsHistorySize);
		index.addProperty("dicomModalitiesInDb", dicomModalitiesInDb);
		index.addProperty("orthancPeerInDb", orthancPeerInDb);
		index.addProperty("overwriteInstances", overwriteInstances);
		index.addProperty("mediaArchiveSize", mediaArchiveSize);
		index.addProperty("storageAccessOnFind", storageAccessOnFind);
		

	}

	/**
	 * Ajoute des utilisateurs pour Orthanc
	 * @param user
	 * @param password
	 */
	protected void addusers(String user, String password) {
		//on ajoute l'utilisateur a l'array de users
		users.addProperty(user, password);
	}
	
	/**
	 * Ajoute des metadata
	 * @param user
	 * @param number
	 */
	protected void addUserMetadata(String user, int number) {
		//on ajoute l'utilisateur a l'array des metadata
		userMetadata.addProperty(user, number);
	}
	
	
	/**
	 * Ajoute un repertoire lua a la liste des repertoire lua
	 * @param path
	 */
	protected void addLua(String path) {
		luaFolder.add(path);
	}
	
	/**
	 * Ajoute un repertoire plugin  a la liste des repertoire plugin
	 * @param path
	 */
	protected void addplugins(String path) {
		pluginsFolder.add(path);
	}
	
	/**
	 * Ajoute un AET dans la declaration
	 * @param nom
	 * @param name
	 * @param ip
	 * @param port
	 * @param wildcard
	 */
	protected void addDicomNode(String nom, String name, String ip, int port, String wildcard) {
		JsonArray dicomNode=new JsonArray();
		dicomNode.add(name);
		dicomNode.add(ip);
		dicomNode.add(port);
		dicomNode.add(wildcard);
		this.dicomNode.add(nom,dicomNode); 
	}
	/**
	 * Cree un peer Ortanc et l'ajoute dans la hashmap Orthanc Peer
	 * @param nom
	 * @param URL
	 * @param login
	 * @param password
	 */
	protected void addorthancPeer(String nom, String URL, String login, String password) {
		JsonArray orthancPeer=new JsonArray();
		orthancPeer.add(URL);
		orthancPeer.add(login);
		orthancPeer.add(password);
		this.orthancPeer.add(nom,orthancPeer); 
	}
	
	/**
	 * Ajoute les content type dans la hashmap
	 * @param name
	 * @param number
	 * @param mime
	 */
	protected void addContentType(String name, int number, String mime) {
		JsonArray contentType=new JsonArray();
		contentType.add(number);
		contentType.add(mime);
		this.contentType.add(name,contentType); 
	}
	
	/**
	 * Ajoute des valeurs dans le dictionnaire
	 * @param name
	 * @param vr
	 * @param tag
	 * @param minimum
	 * @param maximum
	 * @param privateCreator
	 */
	protected void addDictionary(String name, String vr, String tag, int minimum, int maximum, String privateCreator) {
		JsonArray dictionary=new JsonArray();
		dictionary.add(vr);
		dictionary.add(tag);
		dictionary.add(minimum);
		dictionary.add(maximum);
		dictionary.add(privateCreator);
		this.dictionary.add(name,dictionary); 
	}
	
	
	/**
	 * Permet de lire un fichier et enlever les commentaires avec JSmin
	 * @throws Exception
	 */
	public void setExistingJsonConfig(File fichierInput) throws Exception {
		 try {
			 FileReader reader= new FileReader(fichierInput);
			 this.fichierInput=fichierInput;
			 //On passe dans JSMin pour enlever les commentaire avant le parsing
			 StringWriter out = new StringWriter();
			 JSMin js= new JSMin(reader, out);
			 
			 js.jsmin();
			 
			
			 JsonParser parser = new JsonParser();
			 JsonObject orthancJson= parser.parse(out.toString()).getAsJsonObject();
		
			 parserOrthancJson(orthancJson);
		 }
	 
		 catch (FileNotFoundException e1) {	e1.printStackTrace();}
			
		
	}
	
	
	//Evnoie le resultat du parsing dans les variables de l'index qui sert a produire le nouveau Json
	private void parserOrthancJson(JsonObject orthancJson) {
		//Boucle try pour eviter de bloquer en cas d'element manquant lors du parsing (version anterieure de JSON par exemple)
		if (orthancJson.has("Name")) orthancName=orthancJson.get("Name").getAsString();
		if (orthancJson.has("StorageDirectory")) storageDirectory=orthancJson.get("StorageDirectory").getAsString();
		if (orthancJson.has("IndexDirectory")) indexDirectory=orthancJson.get("IndexDirectory").getAsString();
		if (orthancJson.has("StorageCompression")) StorageCompression=orthancJson.get("StorageCompression").getAsBoolean();
		if (orthancJson.has("MaximumStorageSize")) MaximumStorageSize=Integer.valueOf(orthancJson.get("MaximumStorageSize").getAsString());
		if (orthancJson.has("MaximumPatientCount")) MaximumPatientCount=Integer.valueOf(orthancJson.get("MaximumPatientCount").getAsString());
		if (orthancJson.has("HttpServerEnabled")) HttpServerEnabled=orthancJson.get("HttpServerEnabled").getAsBoolean();
		if (orthancJson.has("HttpPort")) HttpPort=Integer.valueOf(orthancJson.get("HttpPort").getAsString());
		if (orthancJson.has("HttpDescribeErrors")) HttpDescribeErrors=orthancJson.get("HttpDescribeErrors").getAsBoolean();
		if (orthancJson.has("HttpCompressionEnabled")) HttpCompressionEnabled=orthancJson.get("HttpCompressionEnabled").getAsBoolean();
		if (orthancJson.has("DicomServerEnabled")) DicomServerEnabled=orthancJson.get("DicomServerEnabled").getAsBoolean();
		if (orthancJson.has("DicomAet")) DicomAet=orthancJson.get("DicomAet").getAsString();
		if (orthancJson.has("DicomCheckCalledAet")) DicomCheckCalledAet=orthancJson.get("DicomCheckCalledAet").getAsBoolean();
		if (orthancJson.has("DicomPort")) DicomPort=orthancJson.get("DicomPort").getAsInt();
		if (orthancJson.has("DefaultEncoding")) DefaultEncoding=orthancJson.get("DefaultEncoding").getAsString();
		if (orthancJson.has("DeflatedTransferSyntaxAccepted")) DeflatedTransferSyntaxAccepted=orthancJson.get("DeflatedTransferSyntaxAccepted").getAsBoolean();
		if (orthancJson.has("JpegTransferSyntaxAccepted")) JpegTransferSyntaxAccepted=orthancJson.get("JpegTransferSyntaxAccepted").getAsBoolean();
		if (orthancJson.has("Jpeg2000TransferSyntaxAccepted")) Jpeg2000TransferSyntaxAccepted=orthancJson.get("Jpeg2000TransferSyntaxAccepted").getAsBoolean();
		if (orthancJson.has("JpegLosslessTransferSyntaxAccepted")) JpegLosslessTransferSyntaxAccepted=orthancJson.get("JpegLosslessTransferSyntaxAccepted").getAsBoolean();
		if (orthancJson.has("JpipTransferSyntaxAccepted")) JpipTransferSyntaxAccepted=orthancJson.get("JpipTransferSyntaxAccepted").getAsBoolean();
		if (orthancJson.has("Mpeg2TransferSyntaxAccepted")) Mpeg2TransferSyntaxAccepted=orthancJson.get("Mpeg2TransferSyntaxAccepted").getAsBoolean();
		if (orthancJson.has("RleTransferSyntaxAccepted")) RleTransferSyntaxAccepted=orthancJson.get("RleTransferSyntaxAccepted").getAsBoolean();
		if (orthancJson.has("UnknownSopClassAccepted")) UnknownSopClassAccepted=orthancJson.get("UnknownSopClassAccepted").getAsBoolean();
		if (orthancJson.has("DicomScpTimeout")) DicomScpTimeout=orthancJson.get("DicomScpTimeout").getAsInt();
		if (orthancJson.has("RemoteAccessAllowed")) RemoteAccessAllowed=orthancJson.get("RemoteAccessAllowed").getAsBoolean();
		if (orthancJson.has("SslEnabled")) SslEnabled=orthancJson.get("SslEnabled").getAsBoolean();
		if (orthancJson.has("SslCertificate")) SslCertificate=orthancJson.get("SslCertificate").getAsString();
		if (orthancJson.has("AuthenticationEnabled")) AuthenticationEnabled=orthancJson.get("AuthenticationEnabled").getAsBoolean();
		if (orthancJson.has("DicomScuTimeout")) DicomScuTimeout=orthancJson.get("DicomScuTimeout").getAsInt();
		if (orthancJson.has("HttpProxy")) HttpProxy=orthancJson.get("HttpProxy").getAsString();
		if (orthancJson.has("HttpTimeout")) HttpTimeout=orthancJson.get("HttpTimeout").getAsInt();
		if (orthancJson.has("HttpsVerifyPeers")) HttpsVerifyPeers=orthancJson.get("HttpsVerifyPeers").getAsBoolean();
		if (orthancJson.has("HttpsCACertificates")) HttpsCACertificates=orthancJson.get("HttpsCACertificates").getAsString();
		if (orthancJson.has("StableAge")) StableAge=orthancJson.get("StableAge").getAsInt();
		if (orthancJson.has("StrictAetComparison")) StrictAetComparison=orthancJson.get("StrictAetComparison").getAsBoolean();
		if (orthancJson.has("StoreMD5ForAttachments")) StoreMD5ForAttachments=orthancJson.get("StoreMD5ForAttachments").getAsBoolean();
		if (orthancJson.has("LimitFindResults")) LimitFindResults=orthancJson.get("LimitFindResults").getAsInt();
		if (orthancJson.has("LimitFindInstances")) LimitFindInstances=orthancJson.get("LimitFindInstances").getAsInt();
		if (orthancJson.has("LimitJobs")) LimitJobs=orthancJson.get("LimitJobs").getAsInt();
		if (orthancJson.has("LogExportedResources")) LogExportedResources=orthancJson.get("LogExportedResources").getAsBoolean();
		if (orthancJson.has("KeepAlive")) KeepAlive=orthancJson.get("KeepAlive").getAsBoolean();
		if (orthancJson.has("StoreDicom")) StoreDicom=orthancJson.get("StoreDicom").getAsBoolean();
		if (orthancJson.has("DicomAssociationCloseDelay")) DicomAssociationCloseDelay=orthancJson.get("DicomAssociationCloseDelay").getAsInt();
		if (orthancJson.has("QueryRetrieveSize")) QueryRetrieveSize=orthancJson.get("QueryRetrieveSize").getAsInt();
		if (orthancJson.has("CaseSensitivePN")) CaseSensitivePN=orthancJson.get("CaseSensitivePN").getAsBoolean();
		if (orthancJson.has("LoadPrivateDictionary")) LoadPrivateDictionary=orthancJson.get("LoadPrivateDictionary").getAsBoolean();
		if (orthancJson.has("DicomCheckModalityHost")) CheckModalityHost=orthancJson.get("DicomCheckModalityHost").getAsBoolean();
		if (orthancJson.has("DicomAlwaysAllowStore")) DicomAlwaysStore=orthancJson.get("DicomAlwaysAllowStore").getAsBoolean();
		if (orthancJson.has("DicomAlwaysAllowEcho")) dicomAlwaysAllowEcho=orthancJson.get("DicomAlwaysAllowEcho").getAsBoolean();
		if (orthancJson.has("SynchronousCMove")) SynchronousCMove=orthancJson.get("SynchronousCMove").getAsBoolean();
		if (orthancJson.has("JobsHistorySize")) JobsHistorySize=orthancJson.get("JobsHistorySize").getAsInt();
		if (orthancJson.has("ConcurrentJobs")) ConcurrentJobs=orthancJson.get("ConcurrentJobs").getAsInt();
		if (orthancJson.has("dicomModalitiesInDb")) dicomModalitiesInDb=orthancJson.get("dicomModalitiesInDb").getAsBoolean();
		if (orthancJson.has("orthancPeerInDb")) orthancPeerInDb=orthancJson.get("dicomPeerInDb").getAsBoolean();
		if (orthancJson.has("overwriteInstances")) overwriteInstances=orthancJson.get("overwriteInstances").getAsBoolean();
		if (orthancJson.has("mediaArchiveSize")) mediaArchiveSize=orthancJson.get("mediaArchiveSize").getAsInt();
		if (orthancJson.has("storageAccessOnFind")) storageAccessOnFind=orthancJson.get("storageAccessOnFind").getAsString();
		

		
		//On recupere les autres objet JSON dans le JSON principal
		//on recupere les AET declares par un nouveau parser
		if (orthancJson.has("DicomModalities")) dicomNode= orthancJson.get("DicomModalities").getAsJsonObject();
		
		//On recupere les users
		if (orthancJson.has("RegisteredUsers")) users= orthancJson.get("RegisteredUsers").getAsJsonObject();
		
		// On recupere les Lua scripts
		if (orthancJson.has("LuaScripts")) luaFolder= orthancJson.get("LuaScripts").getAsJsonArray();
		
		// On recupere les plugins
		if (orthancJson.has("Plugins")) pluginsFolder= orthancJson.get("Plugins").getAsJsonArray();
		
		//On recupere les metadata
		if (orthancJson.has("UserMetadata")) userMetadata= orthancJson.get("UserMetadata").getAsJsonObject();
		
		// On recupere les dictionnary
		if (orthancJson.has("Dictionary")) dictionary= orthancJson.get("Dictionary").getAsJsonObject();
		
		// On recupere les Content
		if (orthancJson.has("UserContentType")) contentType= orthancJson.get("UserContentType").getAsJsonObject();
		
		// On recupere les Peer
		if (orthancJson.has("OrthancPeers")) orthancPeer=orthancJson.get("OrthancPeers").getAsJsonObject();
	}
	
	/**
	 * Permet d'ecrire le JSON final dans un fichier
	 * @param json
	 * @param fichier
	 */
	public void writeJson(JsonObject json, File fichier) {
		//use Gson for pretty printing
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonString = gson.toJson(json);
		//write the json in the destination file
		Orthanc_Tools.writeCSV(jsonString, fichier);
	}
	
		
}
