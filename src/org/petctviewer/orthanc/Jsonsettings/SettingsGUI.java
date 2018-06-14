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

import javax.swing.JFrame;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JSpinner;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.border.LineBorder;

import org.json.simple.parser.ParseException;
import org.petctviewer.orthanc.ParametreConnexionHttp;

import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.SpinnerNumberModel;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;


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
	
	private ParametreConnexionHttp connexion=new ParametreConnexionHttp();

	private Json_Settings settings=new Json_Settings();
	

	public SettingsGUI() {
		
	getContentPane().setLayout(new GridLayout(3, 3, 0, 0));
	setTitle("Orthanc JSON editor");
	setPreferredSize(new Dimension(1400, 700));
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JPanel General = new JPanel();
	General.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(General);
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
	txtOrthanc.setText(settings.orthancName);
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
	
	JSpinner spinner_Max_Storage_Size = new JSpinner(); 
	spinner_Max_Storage_Size.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_Max_Storage_Size.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.MaximumStorageSize=(int) spinner_Max_Storage_Size.getValue();
		}
	});
	spinner_Max_Storage_Size.setPreferredSize(new Dimension(50, 20));
	spinner_Max_Storage_Size.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_Max_Storage_Size.setValue(settings.MaximumStorageSize);
	boutton_general.add(spinner_Max_Storage_Size);
	
	JButton btnNewButton_1 = new JButton("Index Directory");
	btnNewButton_1.setToolTipText("Path to the directory that holds the SQLite index (if unset, the\n value of StorageDirectory is used). This index could be stored on\na RAM-drive or a SSD device for performance reasons.");
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
	boutton_general.add(btnNewButton_1);
	
	JLabel lblMaxiumPatientCount = new JLabel("Maxium Patient Count");
	lblMaxiumPatientCount.setToolTipText("Maximum number of patients that can be stored at a given time in the storage (a value of \"0\" indicates no limit on the number of patients)");
	boutton_general.add(lblMaxiumPatientCount);
	
	JSpinner spinner_Max_Patient_Count = new JSpinner();
	spinner_Max_Patient_Count.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_Max_Patient_Count.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.MaximumPatientCount=(int) spinner_Max_Patient_Count.getValue();
		}
	});
	spinner_Max_Patient_Count.setPreferredSize(new Dimension(50, 20));
	spinner_Max_Patient_Count.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_Max_Patient_Count.setValue(settings.MaximumPatientCount);
	boutton_general.add(spinner_Max_Patient_Count);
	
	JButton btnNewButton = new JButton("Storage Directory");
	btnNewButton.setToolTipText("Path to the directory that holds the heavyweight files");
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
	boutton_general.add(btnNewButton);
	
	JCheckBox rdbtnNewRadioButton = new JCheckBox("Storage Compression");
	rdbtnNewRadioButton.setToolTipText("Enable the transparent compression of the DICOM instances");
	rdbtnNewRadioButton.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StorageCompression=rdbtnNewRadioButton.isSelected();
		}
	});
	rdbtnNewRadioButton.setSelected(settings.StorageCompression);
	boutton_general.add(rdbtnNewRadioButton);
	
	JButton btnLuaScripts = new JButton("Lua Scripts");
	btnLuaScripts.setToolTipText("List of paths to the custom Lua scripts that are to be loaded into this instance of Orthanc");
	btnLuaScripts.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
				FolderDialog dialoglua=new FolderDialog(true, settings);
				dialoglua.setVisible(true);
				lua_value.setText(String.valueOf(settings.luaFolder.size()));
				
			}
	});
	boutton_general.add(btnLuaScripts);
	
	JButton btnPlugins = new JButton("plugins");
	btnPlugins.setToolTipText("List of paths to the plugins that are to be loaded into this instance of Orthanc");
	btnPlugins.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			FolderDialog folderDialog=new FolderDialog(false, settings);
			folderDialog.setVisible(true);
			plugin_value.setText(String.valueOf(settings.pluginsFolder.size()));
		}
	});
	boutton_general.add(btnPlugins);
	
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
	http_Config.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(http_Config);
	http_Config.setLayout(new BorderLayout(0, 0));
	
	JPanel Titre_Http = new JPanel();
	http_Config.add(Titre_Http, BorderLayout.NORTH);
	
	JLabel lblHttpServer = new JLabel("HTTP Server");
	Titre_Http.add(lblHttpServer);
	
	JPanel http_bouttons = new JPanel();
	http_Config.add(http_bouttons, BorderLayout.CENTER);
	
	JCheckBox rdbtnHttpServerEnabled = new JCheckBox("HTTP Server Enabled");
	rdbtnHttpServerEnabled.setToolTipText("Enable the HTTP server. If this parameter is set to \"false\", Orthanc acts as a pure DICOM server. The REST API and Orthanc Explorer will not be available.");
	rdbtnHttpServerEnabled.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.HttpServerEnabled=rdbtnHttpServerEnabled.isSelected();
		}
	});
	rdbtnHttpServerEnabled.setSelected(settings.HttpServerEnabled);
	http_bouttons.add(rdbtnHttpServerEnabled);
	
	textfield_Http_Port = new JTextField();
	textfield_Http_Port.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpPort=Integer.valueOf(textfield_Http_Port.getText());
		}
	});
	
	JLabel lblHttpPort = new JLabel("HTTP Port");
	lblHttpPort.setToolTipText("HTTP port for the REST services and for the GUI");
	http_bouttons.add(lblHttpPort);
	textfield_Http_Port.setText(String.valueOf(settings.HttpPort));
	http_bouttons.add(textfield_Http_Port);
	textfield_Http_Port.setColumns(10);
	
	JCheckBox rdbtnHttpDescribeErrors = new JCheckBox("HTTP Describe errors");
	rdbtnHttpDescribeErrors.setToolTipText("When the following option is \"true\", if an error is encountered while calling the REST API, a JSON message describing the error is put in the HTTP answer. ");
	rdbtnHttpDescribeErrors.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpDescribeErrors=rdbtnHttpDescribeErrors.isSelected();
		}
	});
	rdbtnHttpDescribeErrors.setSelected(settings.HttpDescribeErrors);
	http_bouttons.add(rdbtnHttpDescribeErrors);
	
	JCheckBox rdbtnHttpCompression = new JCheckBox("HTTP Compression");
	rdbtnHttpCompression.setToolTipText("Enable HTTP compression to improve network bandwidth utilization, at the expense of more computations on the server");
	rdbtnHttpCompression.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpCompressionEnabled=rdbtnHttpCompression.isSelected();
		}
	});
	rdbtnHttpCompression.setSelected(settings.HttpCompressionEnabled);
	http_bouttons.add(rdbtnHttpCompression);
	
	JPanel dicomServer_config = new JPanel();
	dicomServer_config.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(dicomServer_config);
	dicomServer_config.setLayout(new BorderLayout(0, 0));
	
	JPanel Titre_DicomServer = new JPanel();
	dicomServer_config.add(Titre_DicomServer, BorderLayout.NORTH);
	
	JLabel lblNewLabel_2 = new JLabel("DICOM Server settings");
	Titre_DicomServer.add(lblNewLabel_2);
	
	JPanel Boutton_DicomServer = new JPanel();
	dicomServer_config.add(Boutton_DicomServer, BorderLayout.CENTER);
	
	JCheckBox rdbtnServerEnabled = new JCheckBox("Server Enabled");
	rdbtnServerEnabled.setToolTipText("Enable the DICOM server. If this parameter is set to \"false\", Orthanc acts as a pure REST server. It will not be possible to receive files or to do query/retrieve through the DICOM protocol.");
	rdbtnServerEnabled.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpServerEnabled=rdbtnServerEnabled.isSelected();
		}
	});
	rdbtnServerEnabled.setSelected(settings.DicomServerEnabled);
	Boutton_DicomServer.add(rdbtnServerEnabled);
	
	JCheckBox rdbtnCheckCalledAet = new JCheckBox("Check AET");
	rdbtnCheckCalledAet.setToolTipText("Check whether the called AET corresponds to the AET of Orthanc during an incoming DICOM SCU request");
	rdbtnCheckCalledAet.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomCheckCalledAet=rdbtnCheckCalledAet.isSelected();
		}
	});
	rdbtnCheckCalledAet.setSelected(settings.DicomCheckCalledAet);
	Boutton_DicomServer.add(rdbtnCheckCalledAet);
	
	JLabel lblAetName = new JLabel("AET Name");
	lblAetName.setToolTipText("The DICOM Application Entity Title");
	Boutton_DicomServer.add(lblAetName);
	
	textField_AET_Name = new JTextField();
	textField_AET_Name.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomAet=textField_AET_Name.getText();
		}
	});
	Boutton_DicomServer.add(textField_AET_Name);
	textField_AET_Name.setColumns(10);
	textField_AET_Name.setText(settings.DicomAet);
	
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
	textField_AET_Port.setText(String.valueOf(settings.DicomPort));
	
	JCheckBox rdbtnUnknowSop = new JCheckBox("Unknow SOP");
	rdbtnUnknowSop.setToolTipText("Whether Orthanc accepts to act as C-Store SCP for unknown storage SOP classes (aka. \"promiscuous mode\")");
	rdbtnUnknowSop.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.UnknownSopClassAccepted=rdbtnUnknowSop.isSelected();
		}
	});
	rdbtnUnknowSop.setSelected(settings.UnknownSopClassAccepted);
	Boutton_DicomServer.add(rdbtnUnknowSop);
	
	JLabel lblScpTimout = new JLabel("SCP Timout");
	lblScpTimout.setToolTipText("Set the timeout (in seconds) after which the DICOM associations are closed by the Orthanc SCP (server) if no further DIMSE command is received from the SCU (client).");
	Boutton_DicomServer.add(lblScpTimout);
	
	JSpinner spinner_SCP_Timeout = new JSpinner();
	spinner_SCP_Timeout.setPreferredSize(new Dimension(50, 20));
	spinner_SCP_Timeout.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_SCP_Timeout.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomScpTimeout=(int) spinner_SCP_Timeout.getValue();
		}
	});
	Boutton_DicomServer.add(spinner_SCP_Timeout);
	
	JCheckBox chckbxDeflatedTs = new JCheckBox("Deflated TS");
	chckbxDeflatedTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DeflatedTransferSyntaxAccepted=chckbxDeflatedTs.isSelected();
		}
	});
	
	JLabel lblTransferSyntax = new JLabel("Transfer Syntax :");
	lblTransferSyntax.setToolTipText("The transfer syntaxes that are accepted by Orthanc C-Store SCP");
	Boutton_DicomServer.add(lblTransferSyntax);
	chckbxDeflatedTs.setSelected(settings.DeflatedTransferSyntaxAccepted);
	Boutton_DicomServer.add(chckbxDeflatedTs);
	
	JCheckBox chckbxJpegTs = new JCheckBox("JPEG TS");
	chckbxJpegTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.JpegTransferSyntaxAccepted=chckbxJpegTs.isSelected();
		}
	});
	chckbxJpegTs.setSelected(settings.JpegTransferSyntaxAccepted);
	Boutton_DicomServer.add(chckbxJpegTs);
	
	JCheckBox chckbxJpegTs_1 = new JCheckBox("JPEG 2000 TS");
	chckbxJpegTs_1.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.Jpeg2000TransferSyntaxAccepted=chckbxJpegTs_1.isSelected();
		}
	});
	chckbxJpegTs_1.setSelected(settings.Jpeg2000TransferSyntaxAccepted);
	Boutton_DicomServer.add(chckbxJpegTs_1);
	
	JCheckBox chckbxJpegLoselessTs = new JCheckBox("JPEG loseless TS");
	chckbxJpegLoselessTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.JpegLosslessTransferSyntaxAccepted=chckbxJpegLoselessTs.isSelected();
		}
	});
	chckbxJpegLoselessTs.setSelected(settings.JpegLosslessTransferSyntaxAccepted);
	Boutton_DicomServer.add(chckbxJpegLoselessTs);
	
	JCheckBox chckbxJpipTs = new JCheckBox("JPIP TS");
	chckbxJpipTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.JpipTransferSyntaxAccepted=chckbxJpipTs.isSelected();
		}
	});
	chckbxJpipTs.setSelected(settings.JpipTransferSyntaxAccepted);
	Boutton_DicomServer.add(chckbxJpipTs);
	
	JCheckBox chckbxMpegTs = new JCheckBox("MPEG2 TS");
	chckbxMpegTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.Mpeg2TransferSyntaxAccepted=chckbxMpegTs.isSelected();
		}
	});
	chckbxMpegTs.setSelected(settings.Mpeg2TransferSyntaxAccepted);
	Boutton_DicomServer.add(chckbxMpegTs);
	
	JCheckBox chckbxRleTs = new JCheckBox("RLE TS");
	chckbxRleTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.RleTransferSyntaxAccepted=chckbxRleTs.isSelected();
		}
	});
	chckbxRleTs.setSelected(settings.RleTransferSyntaxAccepted);
	Boutton_DicomServer.add(chckbxRleTs);
	
	JPanel panel = new JPanel();
	dicomServer_config.add(panel, BorderLayout.SOUTH);
	
	JComboBox<String> comboBox_Encoding = new JComboBox<String>();
	comboBox_Encoding.setToolTipText("the default encoding that is assumed for DICOM files without \"SpecificCharacterSet\" DICOM tag, and that is used when answering C-Find requests (including worklists).");
	comboBox_Encoding.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DefaultEncoding=comboBox_Encoding.getSelectedItem().toString();
		}
	});
	panel.add(comboBox_Encoding);
	comboBox_Encoding.setModel(new DefaultComboBoxModel<String>(new String[] {"Latin1", "Ascii", "Utf8", "Latin2", "Latin3", "Latin4", "Latin5", "Cyrillic", "Windows1251", "Arabic", "Greek", "Hebrew", "Thai", "Japanese", "Chinese"}));
	comboBox_Encoding.setSelectedItem(settings.DefaultEncoding);
	if (settings.DefaultEncoding==null) comboBox_Encoding.setSelectedIndex(0);
	
	JPanel http_security = new JPanel();
	http_security.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(http_security);
	http_security.setLayout(new BorderLayout(0, 0));
	
	JPanel HTTP_Security_Title = new JPanel();
	http_security.add(HTTP_Security_Title, BorderLayout.NORTH);
	
	JLabel lblHttpSecurity = new JLabel("HTTP Security");
	HTTP_Security_Title.add(lblHttpSecurity);
	
	JPanel HTTP_Security_Buttons = new JPanel();
	http_security.add(HTTP_Security_Buttons, BorderLayout.CENTER);
	
	JCheckBox chckbxAllowRemoteAccess = new JCheckBox("Allow Remote Access");
	chckbxAllowRemoteAccess.setToolTipText("Whether remote hosts can connect to the HTTP server");
	chckbxAllowRemoteAccess.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.RemoteAccessAllowed=chckbxAllowRemoteAccess.isSelected();
		}
	});
	chckbxAllowRemoteAccess.setSelected(settings.RemoteAccessAllowed);
	HTTP_Security_Buttons.add(chckbxAllowRemoteAccess);
	
	JCheckBox chckbxSsl = new JCheckBox("SSL");
	chckbxSsl.setToolTipText("Whether or not SSL is enabled");
	chckbxSsl.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.SslEnabled=chckbxSsl.isSelected();
		}
	});
	chckbxSsl.setSelected(settings.SslEnabled);
	HTTP_Security_Buttons.add(chckbxSsl);
	
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
	
	JCheckBox chckbxEnableAuthentication = new JCheckBox("Enable Authentication");
	chckbxEnableAuthentication.setToolTipText("Whether or not the password protection is enabled");
	chckbxEnableAuthentication.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.AuthenticationEnabled=chckbxEnableAuthentication.isSelected();
		}
	});
	chckbxEnableAuthentication.setSelected(settings.AuthenticationEnabled);
	HTTP_Security_Buttons.add(chckbxEnableAuthentication);
	
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

	JPanel network = new JPanel();
	network.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(network);
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
	
	JCheckBox chckbx_Dicom_Always_Store = new JCheckBox("Dicom Always Store");
	chckbx_Dicom_Always_Store.setSelected(settings.DicomAlwaysStore);
	chckbx_Dicom_Always_Store.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.DicomAlwaysStore=chckbx_Dicom_Always_Store.isSelected();
		}
	});
	chckbx_Dicom_Always_Store.setToolTipText("Whether the Orthanc SCP allows incoming C-Store requests, even from SCU modalities it does not know about (i.e. that are not listed in the \"DicomModalities\" option above)");
	Network_Buttons.add(chckbx_Dicom_Always_Store);
	
	JCheckBox chckbx_Check_Modality_Store = new JCheckBox("Dicom Check Modality Host");
	chckbx_Check_Modality_Store.setSelected(settings.CheckModalityHost);
	chckbx_Check_Modality_Store.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.CheckModalityHost=chckbx_Check_Modality_Store.isSelected();
		}
	});
	chckbx_Check_Modality_Store.setToolTipText(" Whether Orthanc checks the IP/hostname address of the remote modality initiating a DICOM connection (as listed in the \"DicomModalities\" option above). If this option is set to \"false\", Orthanc only checks the AET of the remote modality.");
	Network_Buttons.add(chckbx_Check_Modality_Store);
	
	JLabel lblDicomScuTimeout = new JLabel("Dicom SCU timeout");
	lblDicomScuTimeout.setToolTipText("The timeout (in seconds) after which the DICOM associations are considered as closed by the Orthanc SCU (client) if the remote DICOM SCP (server) does not answer.");
	Network_Buttons.add(lblDicomScuTimeout);
	
	JSpinner spinner_Dicom_Scu_Timeout = new JSpinner();
	spinner_Dicom_Scu_Timeout.setPreferredSize(new Dimension(50, 20));
	spinner_Dicom_Scu_Timeout.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_Dicom_Scu_Timeout.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.DicomScuTimeout=Integer.valueOf((spinner_Dicom_Scu_Timeout.getValue().toString()));
		}
	});
	spinner_Dicom_Scu_Timeout.setValue(settings.DicomScuTimeout);
	Network_Buttons.add(spinner_Dicom_Scu_Timeout);
	
	JButton btnOrthancPeers = new JButton("Orthanc Peers");
	btnOrthancPeers.setToolTipText("The list of the known Orthanc peers");
	btnOrthancPeers.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			PeersDialog peerDialog=new PeersDialog(settings);
			peerDialog.setVisible(true);
			peerDialog.setAlwaysOnTop(true);
			label_Peer_number.setText(String.valueOf(settings.orthancPeer.size()));
		}
	});
	Network_Buttons.add(btnOrthancPeers);
	
	JLabel lblHttpProxy = new JLabel("HTTP Proxy");
	lblHttpProxy.setToolTipText("Parameters of the HTTP proxy to be used by Orthanc. If set to the empty string, no HTTP proxy is used. (\"proxyUser:proxyPassword@IP:Port\")");
	Network_Buttons.add(lblHttpProxy);
	
	textField_HTTP_Proxy = new JTextField();
	textField_HTTP_Proxy.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.HttpProxy=textField_HTTP_Proxy.toString();
		}
	});
	Network_Buttons.add(textField_HTTP_Proxy);
	textField_HTTP_Proxy.setText(settings.HttpProxy);
	textField_HTTP_Proxy.setColumns(10);
	
	JLabel lblHttpTimeout = new JLabel("HTTP timeout");
	lblHttpTimeout.setToolTipText("Set the timeout for HTTP requests issued by Orthanc (in seconds)");
	Network_Buttons.add(lblHttpTimeout);
	
	JSpinner spinner_Http_Timeout = new JSpinner();
	spinner_Http_Timeout.setPreferredSize(new Dimension(50, 20));
	spinner_Http_Timeout.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_Http_Timeout.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpTimeout=Integer.valueOf(spinner_Http_Timeout.getValue().toString());
		}
	});
	spinner_Http_Timeout.setValue(settings.HttpTimeout);
	Network_Buttons.add(spinner_Http_Timeout);
	
	JCheckBox chckbxHttpsVerifyPeers = new JCheckBox("HTTPS verify Peers");
	chckbxHttpsVerifyPeers.setToolTipText("Enable the verification of the peers during HTTPS requests. This option must be set to \"false\" if using self-signed certificates. Pay attention that setting this option to \"false\" results in security risks!");
	chckbxHttpsVerifyPeers.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpsVerifyPeers=chckbxHttpsVerifyPeers.isSelected();
		}
	});
	chckbxHttpsVerifyPeers.setSelected(settings.HttpsVerifyPeers);
	Network_Buttons.add(chckbxHttpsVerifyPeers);
	
	JButton btnHttpsCaCertificates = new JButton("HTTPS CA Certificates");
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
	Network_Buttons.add(btnHttpsCaCertificates);
	
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
	advanced.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(advanced);
	advanced.setLayout(new BorderLayout(0, 0));
	
	JPanel Advanced_Title = new JPanel();
	advanced.add(Advanced_Title, BorderLayout.NORTH);
	
	JLabel lblAdvancedOptions = new JLabel("Advanced Options");
	Advanced_Title.add(lblAdvancedOptions);
	
	JPanel Advanced_Buttons = new JPanel();
	advanced.add(Advanced_Buttons, BorderLayout.CENTER);
	 
	JButton btnUserContentType = new JButton("User Content Type");
	btnUserContentType.setToolTipText("Dictionary of symbolic names for the user-defined types of attached files.");
	btnUserContentType.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Content_Dialog contentDialog=new Content_Dialog(settings);
			contentDialog.setVisible(true);
			contentDialog.setAlwaysOnTop(true);
			Content_Type_Counter.setText(String.valueOf(settings.contentType.size()));
			
		}
	});
	
	JButton btnUserMetadata = new JButton("User Metadata");
	btnUserMetadata.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			MetadataDialog DialogMetadata=new MetadataDialog(settings);
			DialogMetadata.setVisible(true);
			Metadata_Counter.setText(String.valueOf(settings.userMetadata.size()));
		}
	});
	btnUserMetadata.setToolTipText("Dictionary of symbolic names for the user-defined metadata");
	Advanced_Buttons.add(btnUserMetadata);
	Advanced_Buttons.add(btnUserContentType);
	
	JLabel lblStableAge = new JLabel("Stable Age");
	lblStableAge.setToolTipText("Number of seconds without receiving any instance before a patient, a study or a series is considered as stable.");
	Advanced_Buttons.add(lblStableAge);
	
	JSpinner spinner_Stable_Age = new JSpinner();
	spinner_Stable_Age.setPreferredSize(new Dimension(50, 20));
	spinner_Stable_Age.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_Stable_Age.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StableAge=Integer.valueOf(spinner_Stable_Age.getValue().toString());
		}
	});
	spinner_Stable_Age.setValue(settings.StableAge);
	Advanced_Buttons.add(spinner_Stable_Age);
	
	JCheckBox chckbxStrictAetComparison = new JCheckBox("Strict AET Comparison");
	chckbxStrictAetComparison.setToolTipText(" Setting this option to \"true\" will enable case-sensitive matching.");
	chckbxStrictAetComparison.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StrictAetComparison=chckbxStrictAetComparison.isSelected();
		}
	});
	chckbxStrictAetComparison.setSelected(settings.StrictAetComparison);
	Advanced_Buttons.add(chckbxStrictAetComparison);
	
	JCheckBox chckbxStoremd = new JCheckBox("StoreMD5");
	chckbxStoremd.setToolTipText("When the following option is \"true\", the MD5 of the DICOM files will be computed and stored in the Orthanc database. This information can be used to detect disk corruption, at the price of a small performance overhead.");
	chckbxStoremd.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StoreMD5ForAttachments=chckbxStoremd.isSelected();
		}
	});
	chckbxStoremd.setSelected(settings.StoreMD5ForAttachments);
	Advanced_Buttons.add(chckbxStoremd);
	
	JLabel lblLimitFindResults = new JLabel("Limit Find Results");
	lblLimitFindResults.setToolTipText("The maximum number of results for a single C-FIND request at the Patient, Study or Series level. Setting this option to \"0\" means no limit.");
	Advanced_Buttons.add(lblLimitFindResults);
	
	JSpinner spinner_limit_Find_Result = new JSpinner();
	spinner_limit_Find_Result.setPreferredSize(new Dimension(50, 20));
	spinner_limit_Find_Result.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_limit_Find_Result.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LimitFindResults=Integer.valueOf(spinner_limit_Find_Result.getValue().toString());
		}
	});
	spinner_limit_Find_Result.setValue(settings.LimitFindResults);
	Advanced_Buttons.add(spinner_limit_Find_Result);
	
	JLabel lblLimitFindInstance = new JLabel("Limit Find Instance");
	lblLimitFindInstance.setToolTipText("The maximum number of results for a single C-FIND request at the Instance level. Setting this option to \"0\" means no limit.");
	Advanced_Buttons.add(lblLimitFindInstance);
	
	JSpinner spinner_limit_Find_Instance = new JSpinner();
	spinner_limit_Find_Instance.setPreferredSize(new Dimension(50, 20));
	spinner_limit_Find_Instance.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_limit_Find_Instance.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LimitFindInstances=Integer.valueOf(spinner_limit_Find_Instance.getValue().toString());
		}
	});
	spinner_limit_Find_Instance.setValue(settings.LimitFindInstances);
	Advanced_Buttons.add(spinner_limit_Find_Instance);
	
	JLabel lblLimitsJobs = new JLabel("Limits Jobs");
	lblLimitsJobs.setToolTipText("The maximum number of active jobs in the Orthanc scheduler. When this limit is reached, the addition of new jobs is blocked until some job finishes");
	Advanced_Buttons.add(lblLimitsJobs);
	
	JSpinner spinner_limit_Jobs = new JSpinner();
	spinner_limit_Jobs.setPreferredSize(new Dimension(50, 20));
	spinner_limit_Jobs.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_limit_Jobs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LimitJobs=Integer.valueOf(spinner_limit_Jobs.getValue().toString());
		}
	});
	spinner_limit_Jobs.setValue(settings.LimitJobs);
	Advanced_Buttons.add(spinner_limit_Jobs);
	
	JCheckBox chckbxLogExportedRessources = new JCheckBox("Log Exported Ressources");
	chckbxLogExportedRessources.setToolTipText("If this option is set to \"false\", Orthanc will not log the resources that are exported to other DICOM modalities of Orthanc peers in the URI \"/exports\". ");
	chckbxLogExportedRessources.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LogExportedResources=chckbxLogExportedRessources.isSelected();
		}
	});
	chckbxLogExportedRessources.setSelected(settings.LogExportedResources);
	Advanced_Buttons.add(chckbxLogExportedRessources);
	
	JCheckBox chckbxKeepAlive = new JCheckBox("Keep Alive");
	chckbxKeepAlive.setToolTipText("Enable or disable HTTP Keep-Alive (deprecated). Set this option to \"true\" only in the case of high HTTP loads.");
	chckbxKeepAlive.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.KeepAlive=chckbxKeepAlive.isSelected();
		}
	});
	chckbxKeepAlive.setSelected(settings.KeepAlive);
	Advanced_Buttons.add(chckbxKeepAlive);
	
	JCheckBox chckbxStoreDicom = new JCheckBox("Store DICOM");
	chckbxStoreDicom.setToolTipText("If this option is set to \"false\", Orthanc will run in index-only mode. The DICOM files will not be stored on the drive. Note that this option might prevent the upgrade to newer versions of Orthanc.");
	chckbxStoreDicom.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StoreDicom=chckbxStoreDicom.isSelected();
		}
	});
	chckbxStoreDicom.setSelected(settings.StoreDicom);
	Advanced_Buttons.add(chckbxStoreDicom);
	
	JLabel lblDicomAssociationClose = new JLabel("DICOM Association Close Delay");
	lblDicomAssociationClose.setToolTipText("DICOM associations are kept open as long as new DICOM commands are issued. This option sets the number of seconds of inactivity to wait before automatically closing a DICOM association. If set to 0, the connection is closed immediately.");
	Advanced_Buttons.add(lblDicomAssociationClose);
	
	JSpinner spinner_DICOM_Association_Close_Delay = new JSpinner();
	spinner_DICOM_Association_Close_Delay.setPreferredSize(new Dimension(50, 20));
	spinner_DICOM_Association_Close_Delay.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_DICOM_Association_Close_Delay.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomAssociationCloseDelay=Integer.valueOf(spinner_DICOM_Association_Close_Delay.getValue().toString());
		}
	});
	spinner_DICOM_Association_Close_Delay.setValue(settings.DicomAssociationCloseDelay);
	Advanced_Buttons.add(spinner_DICOM_Association_Close_Delay);
	
	JPanel Options_Strings = new JPanel();
	advanced.add(Options_Strings, BorderLayout.SOUTH);
	Options_Strings.setLayout(new GridLayout(2, 2, 0, 0));
	
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
	
	JPanel about = new JPanel();
	about.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(about);
	about.setLayout(new BorderLayout(0, 0));
	
	JPanel About_About = new JPanel();
	about.add(About_About, BorderLayout.SOUTH);
	About_About.setLayout(new GridLayout(2, 2, 0, 0));
	
	JPanel About_Title = new JPanel();
	about.add(About_Title, BorderLayout.NORTH);
	
	JLabel lblAbout = new JLabel("Advanced Options (2)");
	About_Title.add(lblAbout);
	
	JPanel About_Buttons = new JPanel();
	about.add(About_Buttons, BorderLayout.CENTER);
	About_Buttons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
	
	JLabel lblQueryretrieveSize = new JLabel("Query/Retrieve Size");
	lblQueryretrieveSize.setToolTipText("Maximum number of query/retrieve DICOM requests that are maintained by Orthanc. The least recently used requests get deleted as new requests are issued.");
	About_Buttons.add(lblQueryretrieveSize);
	
	JSpinner spinner_QueryRetrieve_Size = new JSpinner();
	spinner_QueryRetrieve_Size.setPreferredSize(new Dimension(50, 20));
	spinner_QueryRetrieve_Size.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	About_Buttons.add(spinner_QueryRetrieve_Size);
	spinner_QueryRetrieve_Size.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.QueryRetrieveSize=Integer.valueOf(spinner_QueryRetrieve_Size.getValue().toString());
		}
	});
	spinner_QueryRetrieve_Size.setValue(settings.QueryRetrieveSize);
	
	JCheckBox chckbxCaseSensitivePatient = new JCheckBox("Case Sensitive Patient Name");
	chckbxCaseSensitivePatient.setToolTipText("When handling a C-Find SCP request, setting this flag to \"true\" will enable case-sensitive match for PN value representation (such as PatientName). By default, the search is case-insensitive, which does not follow the DICOM standard.");
	About_Buttons.add(chckbxCaseSensitivePatient);
	chckbxCaseSensitivePatient.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.CaseSensitivePN=chckbxCaseSensitivePatient .isSelected();
		}
	});
	chckbxCaseSensitivePatient.setSelected(settings.CaseSensitivePN);
	
	JCheckBox chckbxAllowFindSop = new JCheckBox("Allow Find SOP classe in study");
	chckbxAllowFindSop.setToolTipText("If set to \"true\", Orthanc will still handle \"SOP Classes in Study\" (0008,0062) in C-FIND requests, even if the \"SOP Class UID\" metadata is not available in the database.This option is turned off by default, as it requires intensive accesses to the hard drive.");
	About_Buttons.add(chckbxAllowFindSop);
	chckbxAllowFindSop.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.AllowFindSopClassesInStudy=chckbxAllowFindSop.isSelected();
		}
	});
	chckbxAllowFindSop.setSelected(settings.AllowFindSopClassesInStudy);
	
	JCheckBox chckbxLoadPrivateDictionary = new JCheckBox("Load Private Dictionary");
	chckbxLoadPrivateDictionary.setToolTipText("If set to \"false\", Orthanc will not load its default dictionary of private tags.");
	About_Buttons.add(chckbxLoadPrivateDictionary);
	chckbxLoadPrivateDictionary.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LoadPrivateDictionary=chckbxLoadPrivateDictionary.isSelected();
		}
	});
	chckbxLoadPrivateDictionary.setSelected(settings.LoadPrivateDictionary);
	
	JButton btnDictionnary = new JButton("Dictionnary");
	btnDictionnary.setToolTipText("Register a new tag in the dictionary of DICOM tags that are known to Orthanc.");
	btnDictionnary.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Dictionary_Dialog dictionaryDialog=new Dictionary_Dialog(settings);
			dictionaryDialog.setVisible(true);
			dictionaryDialog.setAlwaysOnTop(true);
			Dictionnary_Counter.setText(String.valueOf(settings.dictionary.size()));
		}
	});
	About_Buttons.add(btnDictionnary);
	
	JPanel panel_1 = new JPanel();
	panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(panel_1);
	panel_1.setLayout(new BorderLayout(0, 0));
	
	JPanel panel_4 = new JPanel();
	panel_1.add(panel_4, BorderLayout.CENTER);
	panel_4.setLayout(new GridLayout(0, 1, 0, 0));
	
	JLabel label_1 = new JLabel("GPL v.3");
	panel_4.add(label_1);
	
	JLabel label_2 = new JLabel("Developper : Salim Kanoun");
	panel_4.add(label_2);
	
	JLabel lblContributionFromPetctviewerorg = new JLabel("Contribution from petctviewer.org");
	panel_4.add(lblContributionFromPetctviewerorg);
	
	JLabel lblNewLabel_4 = new JLabel(" Free and open source PET/CT Viewer");
	panel_4.add(lblNewLabel_4);
	
	JPanel panel_5 = new JPanel();
	panel_1.add(panel_5, BorderLayout.NORTH);
	
	JLabel label = new JLabel("Orthanc JSON Editor");
	panel_5.add(label);
	
	JPanel panel_2 = new JPanel();
	panel_1.add(panel_2, BorderLayout.WEST);
	
	JPanel buttons = new JPanel();
	buttons.setBorder(new LineBorder(new Color(0, 0, 0)));
	getContentPane().add(buttons);
	buttons.setLayout(new BorderLayout(0, 0));
	
	JPanel Bouttons_Title = new JPanel();
	buttons.add(Bouttons_Title, BorderLayout.NORTH);
	
	JLabel lblLoadSave = new JLabel("Load / Save JSON");
	Bouttons_Title.add(lblLoadSave);
	
	JPanel Bouttons_Bouttons = new JPanel();
	buttons.add(Bouttons_Bouttons, BorderLayout.CENTER);
	Bouttons_Bouttons.setLayout(new GridLayout(0, 2, 0, 0));
	
	JPanel Orthanc_Version_Panel = new JPanel();
	Bouttons_Bouttons.add(Orthanc_Version_Panel);
	Orthanc_Version_Panel.setLayout(new BorderLayout(0, 0));
	
	JLabel lblNewLabel_5 = new JLabel("For Orthanc 1.3.0");
	lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
	Orthanc_Version_Panel.add(lblNewLabel_5, BorderLayout.SOUTH);
	
	JPanel panel_6 = new JPanel();
	Bouttons_Bouttons.add(panel_6);
	
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
				//settings.fichierInput=input;
				//System.out.println(settings.fichierInput);
				//On le parse et on relance cette fenetre
				try {
					//settings.definitionFichier();
					//settings.IndexOrthanc();
					//On lance une nouvelle fenetre
					SettingsGUI gui=new SettingsGUI();
					gui.pack();
					gui.setLocationRelativeTo(null);
					gui.setSize(gui.getPreferredSize());
					gui.setVisible(true);
				} catch (Exception e) {e.printStackTrace();}
			
			}	
		}
	});
	Bouttons_Bouttons.add(btnLoadJson);
	
	JButton btnSaveJson = new JButton("Save JSON");
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
	Bouttons_Bouttons.add(btnSaveJson);
	
	JPanel Orthanc_Connection = new JPanel();
	buttons.add(Orthanc_Connection, BorderLayout.SOUTH);
	
	JButton btnRestartOrthancServer = new JButton("Restart Orthanc Server");
	btnRestartOrthancServer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			connexion.restartOrthanc();
		}
	});
	Orthanc_Connection.setLayout(new GridLayout(0, 2, 0, 0));
	
	Orthanc_Connection.add(btnRestartOrthancServer);
	}

public static void main(String[] args)  {
		SettingsGUI gui=new SettingsGUI();
		//On met la fenetre au centre de l ecran
		gui.pack();
		gui.setLocationRelativeTo(null);
		gui.setSize(gui.getPreferredSize());
		gui.setVisible(true);
	}

}
