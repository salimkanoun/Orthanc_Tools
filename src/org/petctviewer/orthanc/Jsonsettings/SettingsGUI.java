package org.petctviewer.orthanc.Jsonsettings;
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.petctviewer.orthanc.ParametreConnexionHttp;


public class SettingsGUI extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7041807507652097056L;
	private JTextField txtOrthanc;
	private JTextField textfield_Http_Port;
	private JTextField textField_AET_Name;
	private JTextField textField_AET_Port;
	private JTextField textField_HTTP_Proxy;
	private JLabel index_value;
	private JLabel storage_value;
	private JLabel SSL_Certif_String;
	private JLabel HTTPS_CA_Certificates;
	private JLabel plugin_value;
	private JLabel lua_value;
	private JLabel Login_pass_String;
	private JLabel label_Peer_number;
	private JLabel Dicom_Modalities_Number;
	private JLabel Dictionnary_Counter;
	private JLabel Content_Type_Counter;
	private JLabel Metadata_Counter;
	
	private JSpinner maxStorageSize, maximumPatientCount,scpTimeout, dicom_Scu_Timeout, http_Timeout,
	stable_Age,limitFindResult,dicomAssociationCloseDelay,limitFindInstance, queryRetrieveSize,limit_Jobs;
	private JCheckBox storageCompression, httpServerEnabled, httpDescribeErrors, httpCompression, allowRemoteAccess,
	ssl, enableAuthentication, serverEnabled, checkCalledAet, unknowSop, deflatedTs, jpegTs, jpeg2000Ts, jpegLoselessTs, jpipTs, mpegTs,rleTs,
	dicomAlwaysStore,checkModalityStore, allowEcho,httpsVerifyPeers,
	strictAetComparison, storeMD5, logExportedRessources, keepAlive, storeDicom, caseSensitivePatient, allowFindSop, loadPrivateDictionary;
	
	private JComboBox<String> comboBox_Encoding ;
	
	private ParametreConnexionHttp connexion=new ParametreConnexionHttp();

	private Json_Settings settings=new Json_Settings();
	

	public SettingsGUI() {
	setTitle("Orthanc JSON editor");
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	getContentPane().setLayout(new BorderLayout(0, 0));
	
	JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	getContentPane().add(tabbedPane, BorderLayout.CENTER);
	JPanel General = new JPanel();
	tabbedPane.addTab("General", null, General, null);
	General.setBorder(new LineBorder(new Color(0, 0, 0)));
	General.setLayout(new BorderLayout(0, 0));
	
	JPanel Titre = new JPanel();
	General.add(Titre, BorderLayout.NORTH);
	
	JLabel lblNewLabel_1 = new JLabel("General");
	Titre.add(lblNewLabel_1);
	
	JPanel boutton_general = new JPanel();
	General.add(boutton_general, BorderLayout.CENTER);
	
	JLabel lblNewLabel = new JLabel("Name");
	lblNewLabel.setToolTipText("The logical name of this instance of Orthanc. This one is displayed in Orthanc Explorer and at the URI \"/system\".");
	boutton_general.add(lblNewLabel);
	
	txtOrthanc = new JTextField();
	txtOrthanc.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
		settings.orthancName=txtOrthanc.getText();
		}
	});
	boutton_general.add(txtOrthanc);
	txtOrthanc.setColumns(15);
	
	JLabel lblMaxStorageSize = new JLabel("Max Storage Size");
	lblMaxStorageSize.setToolTipText("Maximum size of the storage in MB (a value of \"0\" indicates no limit on the storage size)");
	boutton_general.add(lblMaxStorageSize);
	
	maxStorageSize = new JSpinner(); 
	maxStorageSize.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	maxStorageSize.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.MaximumStorageSize=(int) maxStorageSize.getValue();
		}
	});
	maxStorageSize.setPreferredSize(new Dimension(50, 20));
	maxStorageSize.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	boutton_general.add(maxStorageSize);
	
	JLabel lblMaxiumPatientCount = new JLabel("Maxium Patient Count");
	lblMaxiumPatientCount.setToolTipText("Maximum number of patients that can be stored at a given time in the storage (a value of \"0\" indicates no limit on the number of patients)");
	boutton_general.add(lblMaxiumPatientCount);
	
	maximumPatientCount = new JSpinner();
	maximumPatientCount.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	maximumPatientCount.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.MaximumPatientCount=(int) maximumPatientCount.getValue();
		}
	});
	maximumPatientCount.setPreferredSize(new Dimension(50, 20));
	maximumPatientCount.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	boutton_general.add(maximumPatientCount);
	
	storageCompression = new JCheckBox("Storage Compression");
	storageCompression.setToolTipText("Enable the transparent compression of the DICOM instances");
	storageCompression.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StorageCompression=storageCompression.isSelected();
		}
	});
	boutton_general.add(storageCompression);
	
	JPanel panel_btns = new JPanel();
	boutton_general.add(panel_btns);
	panel_btns.setLayout(new GridLayout(0, 2, 0, 0));
	
	JButton btnNewButton_1 = new JButton("Index Directory");
	panel_btns.add(btnNewButton_1);
	btnNewButton_1.setToolTipText("Path to the directory that holds the SQLite index (if unset, the\n value of StorageDirectory is used). This index could be stored on\na RAM-drive or a SSD device for performance reasons.");
	
	JButton btnNewButton = new JButton("Storage Directory");
	panel_btns.add(btnNewButton);
	btnNewButton.setToolTipText("Path to the directory that holds the heavyweight files");
	
	JButton btnLuaScripts = new JButton("Lua Scripts");
	panel_btns.add(btnLuaScripts);
	btnLuaScripts.setToolTipText("List of paths to the custom Lua scripts that are to be loaded into this instance of Orthanc");
	
	JButton btnPlugins = new JButton("plugins");
	panel_btns.add(btnPlugins);
	btnPlugins.setToolTipText("List of paths to the plugins that are to be loaded into this instance of Orthanc");
	btnPlugins.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			FolderDialog folderDialog=new FolderDialog(false, settings);
			folderDialog.setVisible(true);
			plugin_value.setText(String.valueOf(settings.pluginsFolder.size()));
		}
	});
	btnLuaScripts.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
				FolderDialog dialoglua=new FolderDialog(true, settings);
				dialoglua.setVisible(true);
				lua_value.setText(String.valueOf(settings.luaFolder.size()));
				
			}
	});
	btnNewButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
				//On ouvre le JFileChooser pour selectionner un repertoire et on l'ajoute dans la variable adhoc
				JFileChooser fc =new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int ouvrir=fc.showOpenDialog(null);
				//Si valide
				if (ouvrir==JFileChooser.APPROVE_OPTION) {
					settings.storageDirectory=fc.getSelectedFile().getAbsolutePath().toString();
					storage_value.setText(settings.storageDirectory);
				}
			}
	});
	btnNewButton_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			//On ouvre le JFileChooser pour selectionner un repertoire et on l'ajoute dans la variable adhoc
			JFileChooser fc =new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int ouvrir=fc.showOpenDialog(null);
			//Si valide
			if (ouvrir==JFileChooser.APPROVE_OPTION) {
				settings.indexDirectory=fc.getSelectedFile().getAbsolutePath().toString();
				index_value.setText(settings.indexDirectory);
			}
			
		}});
	
	JPanel General_Strings = new JPanel();
	General.add(General_Strings, BorderLayout.SOUTH);
	General_Strings.setLayout(new GridLayout(4, 2, 0, 0));
	
	JLabel lblIndexdirectory = new JLabel("IndexDirectory");
	General_Strings.add(lblIndexdirectory);
	
	index_value = new JLabel(settings.indexDirectory);
	General_Strings.add(index_value);
	
	JLabel lblStoragedirectory = new JLabel("StorageDirectory");
	General_Strings.add(lblStoragedirectory);
	
	storage_value = new JLabel(settings.storageDirectory);
	General_Strings.add(storage_value);
	
	JLabel lblLua = new JLabel("Lua");
	General_Strings.add(lblLua);
	
	lua_value = new JLabel(String.valueOf(settings.luaFolder.size()));
	General_Strings.add(lua_value);
	
	JLabel lblNewLabel_9 = new JLabel("Plugins");
	General_Strings.add(lblNewLabel_9);
	
	plugin_value = new JLabel(String.valueOf(settings.pluginsFolder.size()));
	General_Strings.add(plugin_value);
	
	JPanel http_Config = new JPanel();
	tabbedPane.addTab("http", null, http_Config, null);
	http_Config.setBorder(new LineBorder(new Color(0, 0, 0)));
	http_Config.setLayout(new BorderLayout(0, 0));
	
	JPanel Titre_Http = new JPanel();
	http_Config.add(Titre_Http, BorderLayout.NORTH);
	
	JLabel lblHttpServer = new JLabel("HTTP Server");
	Titre_Http.add(lblHttpServer);
	
	JPanel http_bouttons = new JPanel();
	http_Config.add(http_bouttons, BorderLayout.CENTER);
	http_bouttons.setLayout(new GridLayout(0, 1, 0, 0));
	
	JPanel http = new JPanel();
	http_bouttons.add(http);
	
	httpServerEnabled = new JCheckBox("HTTP Server Enabled");
	http.add(httpServerEnabled);
	httpServerEnabled.setToolTipText("Enable the HTTP server. If this parameter is set to \"false\", Orthanc acts as a pure DICOM server. The REST API and Orthanc Explorer will not be available.");
	httpServerEnabled.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.HttpServerEnabled=httpServerEnabled.isSelected();
		}
	});
	
	JLabel lblHttpPort = new JLabel("HTTP Port");
	http.add(lblHttpPort);
	lblHttpPort.setToolTipText("HTTP port for the REST services and for the GUI");
	
	textfield_Http_Port = new JTextField();
	http.add(textfield_Http_Port);
	textfield_Http_Port.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpPort=Integer.valueOf(textfield_Http_Port.getText());
		}
	});
	textfield_Http_Port.setColumns(10);
	
	httpDescribeErrors = new JCheckBox("HTTP Describe errors");
	http.add(httpDescribeErrors);
	httpDescribeErrors.setToolTipText("When the following option is \"true\", if an error is encountered while calling the REST API, a JSON message describing the error is put in the HTTP answer. ");
	httpDescribeErrors.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpDescribeErrors=httpDescribeErrors.isSelected();
		}
	});
	
	httpCompression = new JCheckBox("HTTP Compression");
	http.add(httpCompression);
	httpCompression.setToolTipText("Enable HTTP compression to improve network bandwidth utilization, at the expense of more computations on the server");
	httpCompression.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpCompressionEnabled=httpCompression.isSelected();
		}
	});
	
	JPanel http_security = new JPanel();
	http_bouttons.add(http_security);
	http_security.setBorder(new LineBorder(new Color(0, 0, 0)));
	http_security.setLayout(new BorderLayout(0, 0));
	
	JPanel HTTP_Security_Title = new JPanel();
	http_security.add(HTTP_Security_Title, BorderLayout.NORTH);
	
	JLabel lblHttpSecurity = new JLabel("HTTP Security");
	HTTP_Security_Title.add(lblHttpSecurity);
	
	JPanel HTTP_Security_Buttons = new JPanel();
	http_security.add(HTTP_Security_Buttons, BorderLayout.CENTER);
	
	allowRemoteAccess = new JCheckBox("Allow Remote Access");
	allowRemoteAccess.setToolTipText("Whether remote hosts can connect to the HTTP server");
	allowRemoteAccess.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.RemoteAccessAllowed=allowRemoteAccess.isSelected();
		}
	});
	HTTP_Security_Buttons.add(allowRemoteAccess);
	
	ssl = new JCheckBox("SSL");
	ssl.setToolTipText("Whether or not SSL is enabled");
	ssl.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.SslEnabled=ssl.isSelected();
		}
	});
	HTTP_Security_Buttons.add(ssl);
	
	JButton btnSslCertificate = new JButton("SSL Certificate");
	btnSslCertificate.setToolTipText("Path to the SSL certificate in the PEM format (meaningful only if SSL is enabled)");
	btnSslCertificate.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			//On ouvre le JFileChooser pour selectionner un repertoire et on l'ajoute dans la variable adhoc
			JFileChooser fc =new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int ouvrir=fc.showOpenDialog(null);
			if (ouvrir==JFileChooser.APPROVE_OPTION) {
				settings.SslCertificate=fc.getSelectedFile().toString();
				SSL_Certif_String.setText(settings.SslCertificate);
			}
			
		}
	});
	HTTP_Security_Buttons.add(btnSslCertificate);
	
	enableAuthentication = new JCheckBox("Enable Authentication");
	enableAuthentication.setToolTipText("Whether or not the password protection is enabled");
	enableAuthentication.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.AuthenticationEnabled=enableAuthentication.isSelected();
		}
	});
	HTTP_Security_Buttons.add(enableAuthentication);
	
	JButton btnUsersLoginpassword = new JButton("Users Login/Password");
	btnUsersLoginpassword.setToolTipText("The list of the registered users");
	btnUsersLoginpassword.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			UserDialog userDialog=new UserDialog(settings);
			userDialog.setVisible(true);
			Login_pass_String.setText(String.valueOf(settings.users.size()));
		}
	});
	HTTP_Security_Buttons.add(btnUsersLoginpassword);
	
	JPanel HTTP_Security_Strings = new JPanel();
	http_security.add(HTTP_Security_Strings, BorderLayout.SOUTH);
	HTTP_Security_Strings.setLayout(new GridLayout(0, 2, 0, 0));
	
	JLabel lblNewLabel_6 = new JLabel("SSL Certificate");
	HTTP_Security_Strings.add(lblNewLabel_6);
	
	SSL_Certif_String = new JLabel("New label");
	SSL_Certif_String.setText(settings.SslCertificate);
	HTTP_Security_Strings.add(SSL_Certif_String);
	
	JLabel User_Login_Pass = new JLabel("User Login/Pass");
	HTTP_Security_Strings.add(User_Login_Pass);
	
	Login_pass_String = new JLabel(String.valueOf(settings.users.size()));
	HTTP_Security_Strings.add(Login_pass_String);
	
	JPanel dicomServer_config = new JPanel();
	tabbedPane.addTab("dicom", null, dicomServer_config, null);
	dicomServer_config.setBorder(new LineBorder(new Color(0, 0, 0)));
	dicomServer_config.setLayout(new BorderLayout(0, 0));
	
	JPanel Titre_DicomServer = new JPanel();
	dicomServer_config.add(Titre_DicomServer, BorderLayout.NORTH);
	
	JLabel lblNewLabel_2 = new JLabel("DICOM Server settings");
	Titre_DicomServer.add(lblNewLabel_2);
	
	JPanel Boutton_DicomServer = new JPanel();
	dicomServer_config.add(Boutton_DicomServer, BorderLayout.CENTER);
	
	serverEnabled = new JCheckBox("Server Enabled");
	serverEnabled.setToolTipText("Enable the DICOM server. If this parameter is set to \"false\", Orthanc acts as a pure REST server. It will not be possible to receive files or to do query/retrieve through the DICOM protocol.");
	serverEnabled.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpServerEnabled=serverEnabled.isSelected();
		}
	});

	Boutton_DicomServer.add(serverEnabled);
	
	checkCalledAet = new JCheckBox("Check AET");
	checkCalledAet.setToolTipText("Check whether the called AET corresponds to the AET of Orthanc during an incoming DICOM SCU request");
	checkCalledAet.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomCheckCalledAet=checkCalledAet.isSelected();
		}
	});

	Boutton_DicomServer.add(checkCalledAet);
	
	JLabel lblAetName = new JLabel("AET Name");
	lblAetName.setToolTipText("The DICOM Application Entity Title");
	Boutton_DicomServer.add(lblAetName);
	
	textField_AET_Name=new JTextField();
	textField_AET_Name.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomAet=textField_AET_Name.getText();
		}
	});
	Boutton_DicomServer.add(textField_AET_Name);
	textField_AET_Name.setColumns(10);

	
	JLabel lblNewLabel_3 = new JLabel("Port");
	lblNewLabel_3.setToolTipText("The DICOM port");
	Boutton_DicomServer.add(lblNewLabel_3);
	
	textField_AET_Port = new JTextField();
	textField_AET_Port.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomPort=Integer.valueOf(textField_AET_Port.getText());
		}
	});
	Boutton_DicomServer.add(textField_AET_Port);
	textField_AET_Port.setColumns(5);
	
	
	unknowSop = new JCheckBox("Unknow SOP");
	unknowSop.setToolTipText("Whether Orthanc accepts to act as C-Store SCP for unknown storage SOP classes (aka. \"promiscuous mode\")");
	unknowSop.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.UnknownSopClassAccepted=unknowSop.isSelected();
		}
	});

	Boutton_DicomServer.add(unknowSop);
	
	JLabel lblScpTimout = new JLabel("SCP Timout");
	lblScpTimout.setToolTipText("Set the timeout (in seconds) after which the DICOM associations are closed by the Orthanc SCP (server) if no further DIMSE command is received from the SCU (client).");
	Boutton_DicomServer.add(lblScpTimout);
	
	scpTimeout = new JSpinner();
	scpTimeout.setPreferredSize(new Dimension(50, 20));
	scpTimeout.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	scpTimeout.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomScpTimeout=(int) scpTimeout.getValue();
		}
	});
	Boutton_DicomServer.add(scpTimeout);
	
	JLabel lblTransferSyntax = new JLabel("Transfer Syntax :");
	lblTransferSyntax.setToolTipText("The transfer syntaxes that are accepted by Orthanc C-Store SCP");
	Boutton_DicomServer.add(lblTransferSyntax);
	
	JPanel panel_TS = new JPanel();
	panel_TS.setBorder(new LineBorder(new Color(0, 0, 0)));
	Boutton_DicomServer.add(panel_TS);
	panel_TS.setLayout(new GridLayout(0, 2, 0, 0));
	
	deflatedTs = new JCheckBox("Deflated TS");
	panel_TS.add(deflatedTs);
	deflatedTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DeflatedTransferSyntaxAccepted=deflatedTs.isSelected();
		}
	});
	
	jpegTs = new JCheckBox("JPEG TS");
	panel_TS.add(jpegTs);
	jpegTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.JpegTransferSyntaxAccepted=jpegTs.isSelected();
		}
	});
	
	jpeg2000Ts = new JCheckBox("JPEG 2000 TS");
	panel_TS.add(jpeg2000Ts);
	jpeg2000Ts.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.Jpeg2000TransferSyntaxAccepted=jpeg2000Ts.isSelected();
		}
	});
	
	jpegLoselessTs = new JCheckBox("JPEG loseless TS");
	panel_TS.add(jpegLoselessTs);
	jpegLoselessTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.JpegLosslessTransferSyntaxAccepted=jpegLoselessTs.isSelected();
		}
	});
	
	jpipTs = new JCheckBox("JPIP TS");
	panel_TS.add(jpipTs);
	jpipTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.JpipTransferSyntaxAccepted=jpipTs.isSelected();
		}
	});
	
	mpegTs = new JCheckBox("MPEG2 TS");
	panel_TS.add(mpegTs);
	mpegTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.Mpeg2TransferSyntaxAccepted=mpegTs.isSelected();
		}
	});
	
	rleTs = new JCheckBox("RLE TS");
	panel_TS.add(rleTs);
	rleTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.RleTransferSyntaxAccepted=rleTs.isSelected();
		}
	});
	
	JPanel panel = new JPanel();
	dicomServer_config.add(panel, BorderLayout.SOUTH);
	
	comboBox_Encoding = new JComboBox<String>();
	comboBox_Encoding.setToolTipText("the default encoding that is assumed for DICOM files without \"SpecificCharacterSet\" DICOM tag, and that is used when answering C-Find requests (including worklists).");
	comboBox_Encoding.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DefaultEncoding=comboBox_Encoding.getSelectedItem().toString();
		}
	});
	panel.add(comboBox_Encoding);
	comboBox_Encoding.setModel(new DefaultComboBoxModel<String>(new String[] {"Latin1", "Ascii", "Utf8", "Latin2", "Latin3", "Latin4", "Latin5", "Cyrillic", "Windows1251", "Arabic", "Greek", "Hebrew", "Thai", "Japanese", "Chinese"}));
	
	
		JPanel network = new JPanel();
		tabbedPane.addTab("network", null, network, null);
		network.setBorder(new LineBorder(new Color(0, 0, 0)));
		network.setLayout(new BorderLayout(0, 0));
		
		JPanel Network_Title = new JPanel();
		network.add(Network_Title, BorderLayout.NORTH);
		
		JLabel lblNetworkTopology = new JLabel("Network Topology");
		Network_Title.add(lblNetworkTopology);
		
		JPanel Network_Buttons = new JPanel();
		network.add(Network_Buttons, BorderLayout.CENTER);
		
		JButton btnDicomModalities = new JButton("Dicom Modalities");
		btnDicomModalities.setToolTipText("The list of the known DICOM modalities");
		btnDicomModalities.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// On lance la boite de dialog pour gerer les AET
				DicomDialog dicomDialog=new DicomDialog(settings);
				dicomDialog.setVisible(true);
				Dicom_Modalities_Number.setText(String.valueOf(settings.dicomNode.size()));
				
				
			}
		});
		Network_Buttons.add(btnDicomModalities);
		
		JPanel panel_dcm = new JPanel();
		panel_dcm.setBorder(new LineBorder(new Color(0, 0, 0)));
		Network_Buttons.add(panel_dcm);
		panel_dcm.setLayout(new GridLayout(0, 2, 0, 0));
		
		dicomAlwaysStore = new JCheckBox("Dicom Always Store");
		panel_dcm.add(dicomAlwaysStore);
		dicomAlwaysStore.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				settings.DicomAlwaysStore=dicomAlwaysStore.isSelected();
			}
		});
		dicomAlwaysStore.setToolTipText("Whether the Orthanc SCP allows incoming C-Store requests, even from SCU modalities it does not know about (i.e. that are not listed in the \"DicomModalities\" option above)");

		checkModalityStore = new JCheckBox("Dicom Check Modality Host");
		panel_dcm.add(checkModalityStore);
		checkModalityStore.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				settings.CheckModalityHost=checkModalityStore.isSelected();
			}
		});
		checkModalityStore.setToolTipText(" Whether Orthanc checks the IP/hostname address of the remote modality initiating a DICOM connection (as listed in the \"DicomModalities\" option above). If this option is set to \"false\", Orthanc only checks the AET of the remote modality.");
		
		JLabel lblDicomScuTimeout = new JLabel("Dicom SCU timeout");
		panel_dcm.add(lblDicomScuTimeout);
		lblDicomScuTimeout.setToolTipText("The timeout (in seconds) after which the DICOM associations are considered as closed by the Orthanc SCU (client) if the remote DICOM SCP (server) does not answer.");
		
		dicom_Scu_Timeout = new JSpinner();
		panel_dcm.add(dicom_Scu_Timeout);
		dicom_Scu_Timeout.setPreferredSize(new Dimension(50, 20));
		dicom_Scu_Timeout.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
		dicom_Scu_Timeout.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				settings.DicomScuTimeout=Integer.valueOf((dicom_Scu_Timeout.getValue().toString()));
			}
		});
		
		allowEcho = new JCheckBox("Allow echo");
		panel_dcm.add(allowEcho);
		allowEcho.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				settings.dicomAlwaysAllowEcho=allowEcho.isSelected();
			}
		});
		
		JPanel panel_peers = new JPanel();
		panel_peers.setBorder(new LineBorder(new Color(0, 0, 0)));
		Network_Buttons.add(panel_peers);
		panel_peers.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblHttpProxy = new JLabel("HTTP Proxy");
		panel_peers.add(lblHttpProxy);
		lblHttpProxy.setToolTipText("Parameters of the HTTP proxy to be used by Orthanc. If set to the empty string, no HTTP proxy is used. (\"proxyUser:proxyPassword@IP:Port\")");
		
		textField_HTTP_Proxy = new JTextField();
		panel_peers.add(textField_HTTP_Proxy);
		textField_HTTP_Proxy.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				settings.HttpProxy=textField_HTTP_Proxy.getText();
			}
		});
		textField_HTTP_Proxy.setColumns(10);
		
		JLabel lblHttpTimeout = new JLabel("HTTP timeout");
		panel_peers.add(lblHttpTimeout);
		lblHttpTimeout.setToolTipText("Set the timeout for HTTP requests issued by Orthanc (in seconds)");

		http_Timeout = new JSpinner();
		panel_peers.add(http_Timeout);
		http_Timeout.setPreferredSize(new Dimension(50, 20));
		http_Timeout.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
		http_Timeout.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				settings.HttpTimeout=Integer.valueOf(http_Timeout.getValue().toString());
			}
		});
		
		httpsVerifyPeers = new JCheckBox("HTTPS verify Peers");
		panel_peers.add(httpsVerifyPeers);
		httpsVerifyPeers.setToolTipText("Enable the verification of the peers during HTTPS requests. This option must be set to \"false\" if using self-signed certificates. Pay attention that setting this option to \"false\" results in security risks!");
		httpsVerifyPeers.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				settings.HttpsVerifyPeers=httpsVerifyPeers.isSelected();
			}
		});
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel_peers.add(horizontalStrut);
		
		JButton btnOrthancPeers = new JButton("Orthanc Peers");
		panel_peers.add(btnOrthancPeers);
		btnOrthancPeers.setToolTipText("The list of the known Orthanc peers");
		btnOrthancPeers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PeersDialog peerDialog=new PeersDialog(settings);
				peerDialog.setVisible(true);
				peerDialog.setAlwaysOnTop(true);
				label_Peer_number.setText(String.valueOf(settings.orthancPeer.size()));
			}
		});
		
		JButton btnHttpsCaCertificates = new JButton("HTTPS CA Certificates");
		panel_peers.add(btnHttpsCaCertificates);
		btnHttpsCaCertificates.setToolTipText("Path to the CA (certification authority) certificates to validate peers in HTTPS requests. ");
		btnHttpsCaCertificates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//On ouvre le JFileChooser pour selectionner un repertoire et on l'ajoute dans la variable adhoc
				JFileChooser fc =new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int ouvrir=fc.showOpenDialog(null);
				if (ouvrir==JFileChooser.APPROVE_OPTION) {
					settings.HttpsCACertificates=fc.getSelectedFile().toString();
					HTTPS_CA_Certificates.setText(settings.HttpsCACertificates);
				}
				
			}
		});
		
		JPanel Network_Strings = new JPanel();
		network.add(Network_Strings, BorderLayout.SOUTH);
		Network_Strings.setLayout(new GridLayout(3, 3, 0, 0));
		
		JLabel Dicom_Modalities = new JLabel("Dcm Modalities");
		Network_Strings.add(Dicom_Modalities);
		
		Dicom_Modalities_Number = new JLabel(String.valueOf(settings.dicomNode.size()));
		Network_Strings.add(Dicom_Modalities_Number);
		
		JLabel Orthanc_Peer = new JLabel("Orthanc Peer");
		Network_Strings.add(Orthanc_Peer);
		
		label_Peer_number = new JLabel(String.valueOf(settings.orthancPeer.size()));
		Network_Strings.add(label_Peer_number);
		
		JLabel lblNewLabel_12 = new JLabel("Certificates");
		Network_Strings.add(lblNewLabel_12);
		
		HTTPS_CA_Certificates = new JLabel("New label");
		HTTPS_CA_Certificates.setText(settings.HttpsCACertificates);
		Network_Strings.add(HTTPS_CA_Certificates);
		
		JPanel advanced = new JPanel();
		tabbedPane.addTab("advanced", null, advanced, null);
		advanced.setBorder(new LineBorder(new Color(0, 0, 0)));
		advanced.setLayout(new BorderLayout(0, 0));
		
		JPanel Advanced_Title = new JPanel();
		advanced.add(Advanced_Title, BorderLayout.NORTH);
		
		JLabel lblAdvancedOptions = new JLabel("Advanced Options");
		Advanced_Title.add(lblAdvancedOptions);
		
		JPanel Advanced_Buttons = new JPanel();
		advanced.add(Advanced_Buttons, BorderLayout.CENTER);
	Advanced_Buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
	
	JPanel panel_1 = new JPanel();
	Advanced_Buttons.add(panel_1);
	panel_1.setLayout(new GridLayout(0, 2, 5, 3));
	
	JLabel lblStableAge = new JLabel("Stable Age");
	panel_1.add(lblStableAge);
	lblStableAge.setToolTipText("Number of seconds without receiving any instance before a patient, a study or a series is considered as stable.");
	
	stable_Age = new JSpinner();
	panel_1.add(stable_Age);
	stable_Age.setPreferredSize(new Dimension(50, 20));
	stable_Age.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	stable_Age.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StableAge=Integer.valueOf(stable_Age.getValue().toString());
		}
	});
	
	JLabel lblLimitFindResults = new JLabel("Limit Find Results");
	panel_1.add(lblLimitFindResults);
	lblLimitFindResults.setToolTipText("The maximum number of results for a single C-FIND request at the Patient, Study or Series level. Setting this option to \"0\" means no limit.");
	
	limitFindResult = new JSpinner();
	panel_1.add(limitFindResult);
	limitFindResult.setPreferredSize(new Dimension(50, 20));
	limitFindResult.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	limitFindResult.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LimitFindResults=Integer.valueOf(limitFindResult.getValue().toString());
		}
	});
	
	JLabel lblDicomAssociationClose = new JLabel("Dcm Assoc. Close Delay");
	lblDicomAssociationClose.setFont(new Font("Dialog", Font.BOLD, 10));
	panel_1.add(lblDicomAssociationClose);
	lblDicomAssociationClose.setToolTipText("DICOM associations are kept open as long as new DICOM commands are issued. This option sets the number of seconds of inactivity to wait before automatically closing a DICOM association. If set to 0, the connection is closed immediately.");
	
	dicomAssociationCloseDelay = new JSpinner();
	panel_1.add(dicomAssociationCloseDelay);
	dicomAssociationCloseDelay.setPreferredSize(new Dimension(50, 20));
	dicomAssociationCloseDelay.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	dicomAssociationCloseDelay.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomAssociationCloseDelay=Integer.valueOf(dicomAssociationCloseDelay.getValue().toString());
		}
	});
	
	
	JLabel lblLimitFindInstance = new JLabel("Limit Find Instance");
	panel_1.add(lblLimitFindInstance);
	lblLimitFindInstance.setToolTipText("The maximum number of results for a single C-FIND request at the Instance level. Setting this option to \"0\" means no limit.");
	
	limitFindInstance = new JSpinner();
	panel_1.add(limitFindInstance);
	limitFindInstance.setPreferredSize(new Dimension(50, 20));
	limitFindInstance.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	limitFindInstance.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LimitFindInstances=Integer.valueOf(limitFindInstance.getValue().toString());
		}
	});
	
	JLabel lblQueryretrieveSize = new JLabel("Query/Retrieve Size");
	panel_1.add(lblQueryretrieveSize);
	lblQueryretrieveSize.setToolTipText("Maximum number of query/retrieve DICOM requests that are maintained by Orthanc. The least recently used requests get deleted as new requests are issued.");
	
	queryRetrieveSize = new JSpinner();
	panel_1.add(queryRetrieveSize);
	queryRetrieveSize.setPreferredSize(new Dimension(50, 20));
	queryRetrieveSize.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	queryRetrieveSize.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.QueryRetrieveSize=Integer.valueOf(queryRetrieveSize.getValue().toString());
		}
	});
	
	JLabel lb_limitJob = new JLabel("Limits Job");
	panel_1.add(lb_limitJob);
	
	limit_Jobs = new JSpinner();
	panel_1.add(limit_Jobs);
	limit_Jobs.setPreferredSize(new Dimension(50, 20));
	limit_Jobs.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	limit_Jobs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LimitJobs=Integer.valueOf(limit_Jobs.getValue().toString());
		}
	});
	
	JPanel panel_chkbox = new JPanel();
	Advanced_Buttons.add(panel_chkbox);
	panel_chkbox.setLayout(new GridLayout(0, 3, 0, 0));
	
	strictAetComparison = new JCheckBox("Strict AET Comparison");
	panel_chkbox.add(strictAetComparison);
	strictAetComparison.setToolTipText(" Setting this option to \"true\" will enable case-sensitive matching.");
	strictAetComparison.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StrictAetComparison=strictAetComparison.isSelected();
		}
	});
	
	storeMD5 = new JCheckBox("StoreMD5");
	panel_chkbox.add(storeMD5);
	storeMD5.setToolTipText("When the following option is \"true\", the MD5 of the DICOM files will be computed and stored in the Orthanc database. This information can be used to detect disk corruption, at the price of a small performance overhead.");
	storeMD5.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StoreMD5ForAttachments=storeMD5.isSelected();
		}
	});
	
	logExportedRessources = new JCheckBox("Log Exported Ressources");
	panel_chkbox.add(logExportedRessources);
	logExportedRessources.setToolTipText("If this option is set to \"false\", Orthanc will not log the resources that are exported to other DICOM modalities of Orthanc peers in the URI \"/exports\". ");
	logExportedRessources.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LogExportedResources=logExportedRessources.isSelected();
		}
	});
	
	keepAlive = new JCheckBox("Keep Alive");
	panel_chkbox.add(keepAlive);
	keepAlive.setToolTipText("Enable or disable HTTP Keep-Alive (deprecated). Set this option to \"true\" only in the case of high HTTP loads.");
	keepAlive.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.KeepAlive=keepAlive.isSelected();
		}
	});

	storeDicom = new JCheckBox("Store DICOM");
	panel_chkbox.add(storeDicom);
	storeDicom.setToolTipText("If this option is set to \"false\", Orthanc will run in index-only mode. The DICOM files will not be stored on the drive. Note that this option might prevent the upgrade to newer versions of Orthanc.");
	storeDicom.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StoreDicom=storeDicom.isSelected();
		}
	});
	
	caseSensitivePatient = new JCheckBox("Case Sensitive Patient Name");
	panel_chkbox.add(caseSensitivePatient);
	caseSensitivePatient.setToolTipText("When handling a C-Find SCP request, setting this flag to \"true\" will enable case-sensitive match for PN value representation (such as PatientName). By default, the search is case-insensitive, which does not follow the DICOM standard.");
	caseSensitivePatient.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.CaseSensitivePN=caseSensitivePatient .isSelected();
		}
	});
	
	allowFindSop = new JCheckBox("Allow Find SOP classe in study");
	panel_chkbox.add(allowFindSop);
	allowFindSop.setToolTipText("If set to \"true\", Orthanc will still handle \"SOP Classes in Study\" (0008,0062) in C-FIND requests, even if the \"SOP Class UID\" metadata is not available in the database.This option is turned off by default, as it requires intensive accesses to the hard drive.");
	allowFindSop.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.AllowFindSopClassesInStudy=allowFindSop.isSelected();
		}
	});
	
	
	loadPrivateDictionary = new JCheckBox("Load Private Dictionary");
	panel_chkbox.add(loadPrivateDictionary);
	loadPrivateDictionary.setToolTipText("If set to \"false\", Orthanc will not load its default dictionary of private tags.");
	loadPrivateDictionary.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LoadPrivateDictionary=loadPrivateDictionary.isSelected();
		}
	});
	
	
	JPanel panel_btn = new JPanel();
	Advanced_Buttons.add(panel_btn);
	panel_btn.setLayout(new GridLayout(0, 2, 0, 0));
	
	JButton btnUserMetadata = new JButton("User Metadata");
	panel_btn.add(btnUserMetadata);
	btnUserMetadata.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			MetadataDialog DialogMetadata=new MetadataDialog(settings);
			DialogMetadata.setVisible(true);
			Metadata_Counter.setText(String.valueOf(settings.userMetadata.size()));
		}
	});
	btnUserMetadata.setToolTipText("Dictionary of symbolic names for the user-defined metadata");
	
	JButton btnUserContentType = new JButton("User Content Type");
	panel_btn.add(btnUserContentType);
	btnUserContentType.setToolTipText("Dictionary of symbolic names for the user-defined types of attached files.");
	
	JButton btnDictionnary = new JButton("Dictionnary");
	panel_btn.add(btnDictionnary);
	btnDictionnary.setToolTipText("Register a new tag in the dictionary of DICOM tags that are known to Orthanc.");
	btnDictionnary.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Dictionary_Dialog dictionaryDialog=new Dictionary_Dialog(settings);
			dictionaryDialog.setVisible(true);
			dictionaryDialog.setAlwaysOnTop(true);
			Dictionnary_Counter.setText(String.valueOf(settings.dictionary.size()));
		}
	});
	btnUserContentType.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Content_Dialog contentDialog=new Content_Dialog(settings);
			contentDialog.setVisible(true);
			contentDialog.setAlwaysOnTop(true);
			Content_Type_Counter.setText(String.valueOf(settings.contentType.size()));
			
		}
	});
	
	JPanel Advanced_String = new JPanel();
	advanced.add(Advanced_String, BorderLayout.SOUTH);
	Advanced_String.setLayout(new GridLayout(3, 2, 0, 0));
	
	JLabel label_3 = new JLabel("User Content Type");
	Advanced_String.add(label_3);
	
	Content_Type_Counter = new JLabel(String.valueOf(settings.contentType.size()));
	Advanced_String.add(Content_Type_Counter);
	
	JLabel label_5 = new JLabel("Dictionnary");
	Advanced_String.add(label_5);
	
	Dictionnary_Counter = new JLabel(String.valueOf(settings.dictionary.size()));
	Advanced_String.add(Dictionnary_Counter);
	
	JLabel lblMetadata = new JLabel("Metadata");
	Advanced_String.add(lblMetadata);
	
	Metadata_Counter = new JLabel(String.valueOf(settings.userMetadata.size()));
	Advanced_String.add(Metadata_Counter);
	
	JPanel buttons = new JPanel();
	buttons.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(buttons, BorderLayout.SOUTH);
	buttons.setLayout(new BorderLayout(0, 0));
	
	JPanel Bouttons_Bouttons = new JPanel();
	buttons.add(Bouttons_Bouttons, BorderLayout.CENTER);
	Bouttons_Bouttons.setLayout(new GridLayout(0, 2, 0, 0));
	
	JButton btnLoadJson = new JButton("Load JSON");
	btnLoadJson.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			//On charge le File chooser pour selctionner le fichier
			JFileChooser fc =new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int ouvrir=fc.showOpenDialog(null);
			//Si valide
			if (ouvrir==JFileChooser.APPROVE_OPTION) {
				File input=fc.getSelectedFile();
				//On injecte le fichier selectionne
				try {
					settings.setExistingJsonConfig(input);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//On le parse et on relance cette fenetre
				updateGUI();
			
			}	
		}
	});
	
	JButton btnSaveJson = new JButton("Save JSON");
	Bouttons_Bouttons.add(btnSaveJson);
	btnSaveJson.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			//On ecrit le JSON defini dans Index Orthanc
			settings.construireIndex();
			JFileChooser fc =new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			//fc.setSelectedFile(settings.fichierInput);
			//System.out.println(settings.fichierInput);
			int ouvrir=fc.showSaveDialog(null);
			//Si valide
			if (ouvrir==JFileChooser.APPROVE_OPTION) {
				File output=fc.getSelectedFile();
				settings.writeJson(settings.index,output);
		}
		}
	});
	
	JLabel orthancVersion = new JLabel("For Orthanc 1.3.2");
	Bouttons_Bouttons.add(orthancVersion);
	orthancVersion.setHorizontalAlignment(SwingConstants.CENTER);
	Bouttons_Bouttons.add(btnLoadJson);
	
	JButton btnRestartOrthancServer = new JButton("Restart Orthanc Server");
	Bouttons_Bouttons.add(btnRestartOrthancServer);
	btnRestartOrthancServer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if (connexion.testConnexion()) connexion.restartOrthanc();
		}
	});
	}

	public static void main(String[] args)  {
			SettingsGUI gui=new SettingsGUI();
			//On met la fenetre au centre de l ecran
			gui.pack();
			gui.setLocationRelativeTo(null);
			gui.updateGUI();
			gui.setVisible(true);
		}
	
	
	public void updateGUI() {
		txtOrthanc.setText(settings.orthancName);
		textfield_Http_Port.setText(String.valueOf(settings.HttpPort));
		textField_AET_Name.setText(settings.DicomAet);
		textField_AET_Port.setText(String.valueOf(settings.DicomPort));
		textField_HTTP_Proxy.setText(settings.HttpProxy);
		index_value.setText(settings.indexDirectory);
		storage_value.setText(settings.storageDirectory);
		SSL_Certif_String.setText(settings.SslCertificate);
		HTTPS_CA_Certificates.setText(settings.HttpsCACertificates);
		plugin_value.setText(String.valueOf(settings.pluginsFolder.size()));
		lua_value.setText(String.valueOf(settings.luaFolder.size()));
		Login_pass_String.setText(String.valueOf(settings.users.size()));
		label_Peer_number.setText(String.valueOf(settings.orthancPeer.size()));
		Dicom_Modalities_Number.setText(String.valueOf(settings.dicomNode.size()));
		Dictionnary_Counter.setText(String.valueOf(settings.dictionary.size()));
		Content_Type_Counter.setText(String.valueOf(settings.contentType.size()));
		Metadata_Counter.setText(String.valueOf(settings.userMetadata.size()));

		maxStorageSize.setValue(settings.MaximumStorageSize);
		maximumPatientCount.setValue(settings.MaximumPatientCount);
		scpTimeout.setValue(settings.DicomScpTimeout);
		dicom_Scu_Timeout.setValue(settings.DicomScuTimeout);
		http_Timeout.setValue(settings.HttpTimeout);
		stable_Age.setValue(settings.StableAge);
		limitFindResult.setValue(settings.LimitFindResults);
		dicomAssociationCloseDelay.setValue(settings.DicomAssociationCloseDelay);
		limitFindInstance.setValue(settings.LimitFindInstances);
		queryRetrieveSize.setValue(settings.QueryRetrieveSize);
		limit_Jobs.setValue(settings.LimitJobs);

		storageCompression.setSelected(settings.StorageCompression);
		httpServerEnabled.setSelected(settings.HttpServerEnabled);
		httpDescribeErrors.setSelected(settings.HttpDescribeErrors);
		httpCompression.setSelected(settings.HttpCompressionEnabled);
		allowRemoteAccess.setSelected(settings.RemoteAccessAllowed);
		ssl.setSelected(settings.SslEnabled);
		enableAuthentication.setSelected(settings.AuthenticationEnabled);
		serverEnabled.setSelected(settings.DicomServerEnabled);
		checkCalledAet.setSelected(settings.DicomCheckCalledAet);
		unknowSop.setSelected(settings.UnknownSopClassAccepted);
		deflatedTs.setSelected(settings.DeflatedTransferSyntaxAccepted);
		jpegTs.setSelected(settings.JpegTransferSyntaxAccepted);
		jpeg2000Ts.setSelected(settings.Jpeg2000TransferSyntaxAccepted);
		jpegLoselessTs.setSelected(settings.JpegLosslessTransferSyntaxAccepted);
		jpipTs.setSelected(settings.JpipTransferSyntaxAccepted);
		mpegTs.setSelected(settings.Mpeg2TransferSyntaxAccepted);
		rleTs.setSelected(settings.RleTransferSyntaxAccepted);
		dicomAlwaysStore.setSelected(settings.DicomAlwaysStore);
		checkModalityStore.setSelected(settings.CheckModalityHost);
		allowEcho.setSelected(settings.dicomAlwaysAllowEcho);
		httpsVerifyPeers.setSelected(settings.HttpsVerifyPeers);
		strictAetComparison.setSelected(settings.StrictAetComparison);
		storeMD5.setSelected(settings.StoreMD5ForAttachments);
		logExportedRessources.setSelected(settings.LogExportedResources);
		keepAlive.setSelected(settings.KeepAlive);
		storeDicom.setSelected(settings.StoreDicom);
		caseSensitivePatient.setSelected(settings.CaseSensitivePN);
		allowFindSop.setSelected(settings.AllowFindSopClassesInStudy);
		loadPrivateDictionary.setSelected(settings.LoadPrivateDictionary);
		
		comboBox_Encoding.setSelectedItem(settings.DefaultEncoding);
		
	}

}