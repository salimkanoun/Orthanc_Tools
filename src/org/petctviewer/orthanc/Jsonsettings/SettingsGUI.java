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
	
	private ParametreConnexionHttp connexion=new ParametreConnexionHttp();

	private Json_Settings settings=new Json_Settings();
	

	public SettingsGUI() {
	setTitle("Orthanc JSON editor");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
	
	JCheckBox rdbtnHttpServerEnabled = new JCheckBox("HTTP Server Enabled");
	http.add(rdbtnHttpServerEnabled);
	rdbtnHttpServerEnabled.setToolTipText("Enable the HTTP server. If this parameter is set to \"false\", Orthanc acts as a pure DICOM server. The REST API and Orthanc Explorer will not be available.");
	rdbtnHttpServerEnabled.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			settings.HttpServerEnabled=rdbtnHttpServerEnabled.isSelected();
		}
	});
	rdbtnHttpServerEnabled.setSelected(settings.HttpServerEnabled);
	
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
	
	JCheckBox rdbtnHttpDescribeErrors = new JCheckBox("HTTP Describe errors");
	http.add(rdbtnHttpDescribeErrors);
	rdbtnHttpDescribeErrors.setToolTipText("When the following option is \"true\", if an error is encountered while calling the REST API, a JSON message describing the error is put in the HTTP answer. ");
	rdbtnHttpDescribeErrors.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpDescribeErrors=rdbtnHttpDescribeErrors.isSelected();
		}
	});
	rdbtnHttpDescribeErrors.setSelected(settings.HttpDescribeErrors);
	
	JCheckBox rdbtnHttpCompression = new JCheckBox("HTTP Compression");
	http.add(rdbtnHttpCompression);
	rdbtnHttpCompression.setToolTipText("Enable HTTP compression to improve network bandwidth utilization, at the expense of more computations on the server");
	rdbtnHttpCompression.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.HttpCompressionEnabled=rdbtnHttpCompression.isSelected();
		}
	});
	rdbtnHttpCompression.setSelected(settings.HttpCompressionEnabled);
	
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
	
	JLabel lblTransferSyntax = new JLabel("Transfer Syntax :");
	lblTransferSyntax.setToolTipText("The transfer syntaxes that are accepted by Orthanc C-Store SCP");
	Boutton_DicomServer.add(lblTransferSyntax);
	
	JPanel panel_TS = new JPanel();
	panel_TS.setBorder(new LineBorder(new Color(0, 0, 0)));
	Boutton_DicomServer.add(panel_TS);
	panel_TS.setLayout(new GridLayout(0, 2, 0, 0));
	
	JCheckBox chckbxDeflatedTs = new JCheckBox("Deflated TS");
	panel_TS.add(chckbxDeflatedTs);
	chckbxDeflatedTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DeflatedTransferSyntaxAccepted=chckbxDeflatedTs.isSelected();
		}
	});
	chckbxDeflatedTs.setSelected(settings.DeflatedTransferSyntaxAccepted);
	
	JCheckBox chckbxJpegTs = new JCheckBox("JPEG TS");
	panel_TS.add(chckbxJpegTs);
	chckbxJpegTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.JpegTransferSyntaxAccepted=chckbxJpegTs.isSelected();
		}
	});
	chckbxJpegTs.setSelected(settings.JpegTransferSyntaxAccepted);
	
	JCheckBox chckbxJpegTs_1 = new JCheckBox("JPEG 2000 TS");
	panel_TS.add(chckbxJpegTs_1);
	chckbxJpegTs_1.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.Jpeg2000TransferSyntaxAccepted=chckbxJpegTs_1.isSelected();
		}
	});
	chckbxJpegTs_1.setSelected(settings.Jpeg2000TransferSyntaxAccepted);
	
	JCheckBox chckbxJpegLoselessTs = new JCheckBox("JPEG loseless TS");
	panel_TS.add(chckbxJpegLoselessTs);
	chckbxJpegLoselessTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.JpegLosslessTransferSyntaxAccepted=chckbxJpegLoselessTs.isSelected();
		}
	});
	chckbxJpegLoselessTs.setSelected(settings.JpegLosslessTransferSyntaxAccepted);
	
	JCheckBox chckbxJpipTs = new JCheckBox("JPIP TS");
	panel_TS.add(chckbxJpipTs);
	chckbxJpipTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.JpipTransferSyntaxAccepted=chckbxJpipTs.isSelected();
		}
	});
	chckbxJpipTs.setSelected(settings.JpipTransferSyntaxAccepted);
	
	JCheckBox chckbxMpegTs = new JCheckBox("MPEG2 TS");
	panel_TS.add(chckbxMpegTs);
	chckbxMpegTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.Mpeg2TransferSyntaxAccepted=chckbxMpegTs.isSelected();
		}
	});
	chckbxMpegTs.setSelected(settings.Mpeg2TransferSyntaxAccepted);
	
	JCheckBox chckbxRleTs = new JCheckBox("RLE TS");
	panel_TS.add(chckbxRleTs);
	chckbxRleTs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.RleTransferSyntaxAccepted=chckbxRleTs.isSelected();
		}
	});
	chckbxRleTs.setSelected(settings.RleTransferSyntaxAccepted);
	
	JPanel panel = new JPanel();
	dicomServer_config.add(panel, BorderLayout.SOUTH);
	
	JComboBox<String> comboBox_Encoding = new JComboBox<String>();
	comboBox_Encoding.setToolTipText("the default encoding that is assumed for DICOM files without \"SpecificCharacterSet\" DICOM tag, and that is used when answering C-Find requests (including worklists).");
	if (settings.DefaultEncoding==null) comboBox_Encoding.setSelectedIndex(0);
	comboBox_Encoding.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DefaultEncoding=comboBox_Encoding.getSelectedItem().toString();
		}
	});
	panel.add(comboBox_Encoding);
	comboBox_Encoding.setModel(new DefaultComboBoxModel<String>(new String[] {"Latin1", "Ascii", "Utf8", "Latin2", "Latin3", "Latin4", "Latin5", "Cyrillic", "Windows1251", "Arabic", "Greek", "Hebrew", "Thai", "Japanese", "Chinese"}));
	comboBox_Encoding.setSelectedItem(settings.DefaultEncoding);
	
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
		
		JCheckBox chckbx_Dicom_Always_Store = new JCheckBox("Dicom Always Store");
		panel_dcm.add(chckbx_Dicom_Always_Store);
		chckbx_Dicom_Always_Store.setSelected(settings.DicomAlwaysStore);
		chckbx_Dicom_Always_Store.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				settings.DicomAlwaysStore=chckbx_Dicom_Always_Store.isSelected();
			}
		});
		chckbx_Dicom_Always_Store.setToolTipText("Whether the Orthanc SCP allows incoming C-Store requests, even from SCU modalities it does not know about (i.e. that are not listed in the \"DicomModalities\" option above)");
		
		JCheckBox chckbx_Check_Modality_Store = new JCheckBox("Dicom Check Modality Host");
		panel_dcm.add(chckbx_Check_Modality_Store);
		chckbx_Check_Modality_Store.setSelected(settings.CheckModalityHost);
		chckbx_Check_Modality_Store.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				settings.CheckModalityHost=chckbx_Check_Modality_Store.isSelected();
			}
		});
		chckbx_Check_Modality_Store.setToolTipText(" Whether Orthanc checks the IP/hostname address of the remote modality initiating a DICOM connection (as listed in the \"DicomModalities\" option above). If this option is set to \"false\", Orthanc only checks the AET of the remote modality.");
		
		JLabel lblDicomScuTimeout = new JLabel("Dicom SCU timeout");
		panel_dcm.add(lblDicomScuTimeout);
		lblDicomScuTimeout.setToolTipText("The timeout (in seconds) after which the DICOM associations are considered as closed by the Orthanc SCU (client) if the remote DICOM SCP (server) does not answer.");
		
		JSpinner spinner_Dicom_Scu_Timeout = new JSpinner();
		panel_dcm.add(spinner_Dicom_Scu_Timeout);
		spinner_Dicom_Scu_Timeout.setPreferredSize(new Dimension(50, 20));
		spinner_Dicom_Scu_Timeout.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
		spinner_Dicom_Scu_Timeout.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				settings.DicomScuTimeout=Integer.valueOf((spinner_Dicom_Scu_Timeout.getValue().toString()));
			}
		});
		spinner_Dicom_Scu_Timeout.setValue(settings.DicomScuTimeout);
		
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
				settings.HttpProxy=textField_HTTP_Proxy.toString();
			}
		});
		textField_HTTP_Proxy.setColumns(10);
		
		JLabel lblHttpTimeout = new JLabel("HTTP timeout");
		panel_peers.add(lblHttpTimeout);
		lblHttpTimeout.setToolTipText("Set the timeout for HTTP requests issued by Orthanc (in seconds)");
		
		JSpinner spinner_Http_Timeout = new JSpinner();
		panel_peers.add(spinner_Http_Timeout);
		spinner_Http_Timeout.setPreferredSize(new Dimension(50, 20));
		spinner_Http_Timeout.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
		spinner_Http_Timeout.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				settings.HttpTimeout=Integer.valueOf(spinner_Http_Timeout.getValue().toString());
			}
		});
		spinner_Http_Timeout.setValue(settings.HttpTimeout);
		
		JCheckBox chckbxHttpsVerifyPeers = new JCheckBox("HTTPS verify Peers");
		panel_peers.add(chckbxHttpsVerifyPeers);
		chckbxHttpsVerifyPeers.setToolTipText("Enable the verification of the peers during HTTPS requests. This option must be set to \"false\" if using self-signed certificates. Pay attention that setting this option to \"false\" results in security risks!");
		chckbxHttpsVerifyPeers.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				settings.HttpsVerifyPeers=chckbxHttpsVerifyPeers.isSelected();
			}
		});
		chckbxHttpsVerifyPeers.setSelected(settings.HttpsVerifyPeers);
		
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
	
	JSpinner spinner_Stable_Age = new JSpinner();
	panel_1.add(spinner_Stable_Age);
	spinner_Stable_Age.setPreferredSize(new Dimension(50, 20));
	spinner_Stable_Age.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_Stable_Age.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StableAge=Integer.valueOf(spinner_Stable_Age.getValue().toString());
		}
	});
	spinner_Stable_Age.setValue(settings.StableAge);
	
	JLabel lblLimitFindResults = new JLabel("Limit Find Results");
	panel_1.add(lblLimitFindResults);
	lblLimitFindResults.setToolTipText("The maximum number of results for a single C-FIND request at the Patient, Study or Series level. Setting this option to \"0\" means no limit.");
	
	JSpinner spinner_limit_Find_Result = new JSpinner();
	panel_1.add(spinner_limit_Find_Result);
	spinner_limit_Find_Result.setPreferredSize(new Dimension(50, 20));
	spinner_limit_Find_Result.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_limit_Find_Result.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LimitFindResults=Integer.valueOf(spinner_limit_Find_Result.getValue().toString());
		}
	});
	spinner_limit_Find_Result.setValue(settings.LimitFindResults);
	
	JLabel lblDicomAssociationClose = new JLabel("Dcm Assoc. Close Delay");
	lblDicomAssociationClose.setFont(new Font("Dialog", Font.BOLD, 10));
	panel_1.add(lblDicomAssociationClose);
	lblDicomAssociationClose.setToolTipText("DICOM associations are kept open as long as new DICOM commands are issued. This option sets the number of seconds of inactivity to wait before automatically closing a DICOM association. If set to 0, the connection is closed immediately.");
	
	JSpinner spinner_DICOM_Association_Close_Delay = new JSpinner();
	panel_1.add(spinner_DICOM_Association_Close_Delay);
	spinner_DICOM_Association_Close_Delay.setPreferredSize(new Dimension(50, 20));
	spinner_DICOM_Association_Close_Delay.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_DICOM_Association_Close_Delay.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.DicomAssociationCloseDelay=Integer.valueOf(spinner_DICOM_Association_Close_Delay.getValue().toString());
		}
	});
	spinner_DICOM_Association_Close_Delay.setValue(settings.DicomAssociationCloseDelay);
	
	JLabel lblLimitFindInstance = new JLabel("Limit Find Instance");
	panel_1.add(lblLimitFindInstance);
	lblLimitFindInstance.setToolTipText("The maximum number of results for a single C-FIND request at the Instance level. Setting this option to \"0\" means no limit.");
	
	JSpinner spinner_limit_Find_Instance = new JSpinner();
	panel_1.add(spinner_limit_Find_Instance);
	spinner_limit_Find_Instance.setPreferredSize(new Dimension(50, 20));
	spinner_limit_Find_Instance.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_limit_Find_Instance.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LimitFindInstances=Integer.valueOf(spinner_limit_Find_Instance.getValue().toString());
		}
	});
	spinner_limit_Find_Instance.setValue(settings.LimitFindInstances);
	
	JLabel lblQueryretrieveSize = new JLabel("Query/Retrieve Size");
	panel_1.add(lblQueryretrieveSize);
	lblQueryretrieveSize.setToolTipText("Maximum number of query/retrieve DICOM requests that are maintained by Orthanc. The least recently used requests get deleted as new requests are issued.");
	
	JSpinner spinner_QueryRetrieve_Size = new JSpinner();
	panel_1.add(spinner_QueryRetrieve_Size);
	spinner_QueryRetrieve_Size.setPreferredSize(new Dimension(50, 20));
	spinner_QueryRetrieve_Size.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_QueryRetrieve_Size.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.QueryRetrieveSize=Integer.valueOf(spinner_QueryRetrieve_Size.getValue().toString());
		}
	});
	spinner_QueryRetrieve_Size.setValue(settings.QueryRetrieveSize);
	
	JLabel lb_limitJob = new JLabel("Limits Job");
	panel_1.add(lb_limitJob);
	
	JSpinner spinner_limit_Jobs = new JSpinner();
	panel_1.add(spinner_limit_Jobs);
	spinner_limit_Jobs.setPreferredSize(new Dimension(50, 20));
	spinner_limit_Jobs.setModel(new SpinnerNumberModel (0.0, 0.0, null, 1.0));
	spinner_limit_Jobs.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LimitJobs=Integer.valueOf(spinner_limit_Jobs.getValue().toString());
		}
	});
	spinner_limit_Jobs.setValue(settings.LimitJobs);
	
	JPanel panel_chkbox = new JPanel();
	Advanced_Buttons.add(panel_chkbox);
	panel_chkbox.setLayout(new GridLayout(0, 3, 0, 0));
	
	JCheckBox chckbxStrictAetComparison = new JCheckBox("Strict AET Comparison");
	panel_chkbox.add(chckbxStrictAetComparison);
	chckbxStrictAetComparison.setToolTipText(" Setting this option to \"true\" will enable case-sensitive matching.");
	chckbxStrictAetComparison.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StrictAetComparison=chckbxStrictAetComparison.isSelected();
		}
	});
	chckbxStrictAetComparison.setSelected(settings.StrictAetComparison);
	
	JCheckBox chckbxStoremd = new JCheckBox("StoreMD5");
	panel_chkbox.add(chckbxStoremd);
	chckbxStoremd.setToolTipText("When the following option is \"true\", the MD5 of the DICOM files will be computed and stored in the Orthanc database. This information can be used to detect disk corruption, at the price of a small performance overhead.");
	chckbxStoremd.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StoreMD5ForAttachments=chckbxStoremd.isSelected();
		}
	});
	chckbxStoremd.setSelected(settings.StoreMD5ForAttachments);
	
	JCheckBox chckbxLogExportedRessources = new JCheckBox("Log Exported Ressources");
	panel_chkbox.add(chckbxLogExportedRessources);
	chckbxLogExportedRessources.setToolTipText("If this option is set to \"false\", Orthanc will not log the resources that are exported to other DICOM modalities of Orthanc peers in the URI \"/exports\". ");
	chckbxLogExportedRessources.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LogExportedResources=chckbxLogExportedRessources.isSelected();
		}
	});
	chckbxLogExportedRessources.setSelected(settings.LogExportedResources);
	
	JCheckBox chckbxKeepAlive = new JCheckBox("Keep Alive");
	panel_chkbox.add(chckbxKeepAlive);
	chckbxKeepAlive.setToolTipText("Enable or disable HTTP Keep-Alive (deprecated). Set this option to \"true\" only in the case of high HTTP loads.");
	chckbxKeepAlive.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.KeepAlive=chckbxKeepAlive.isSelected();
		}
	});
	chckbxKeepAlive.setSelected(settings.KeepAlive);
	
	JCheckBox chckbxStoreDicom = new JCheckBox("Store DICOM");
	panel_chkbox.add(chckbxStoreDicom);
	chckbxStoreDicom.setToolTipText("If this option is set to \"false\", Orthanc will run in index-only mode. The DICOM files will not be stored on the drive. Note that this option might prevent the upgrade to newer versions of Orthanc.");
	chckbxStoreDicom.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.StoreDicom=chckbxStoreDicom.isSelected();
		}
	});
	chckbxStoreDicom.setSelected(settings.StoreDicom);
	
	JCheckBox chckbxCaseSensitivePatient = new JCheckBox("Case Sensitive Patient Name");
	panel_chkbox.add(chckbxCaseSensitivePatient);
	chckbxCaseSensitivePatient.setToolTipText("When handling a C-Find SCP request, setting this flag to \"true\" will enable case-sensitive match for PN value representation (such as PatientName). By default, the search is case-insensitive, which does not follow the DICOM standard.");
	chckbxCaseSensitivePatient.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.CaseSensitivePN=chckbxCaseSensitivePatient .isSelected();
		}
	});
	chckbxCaseSensitivePatient.setSelected(settings.CaseSensitivePN);
	
	JCheckBox chckbxAllowFindSop = new JCheckBox("Allow Find SOP classe in study");
	panel_chkbox.add(chckbxAllowFindSop);
	chckbxAllowFindSop.setToolTipText("If set to \"true\", Orthanc will still handle \"SOP Classes in Study\" (0008,0062) in C-FIND requests, even if the \"SOP Class UID\" metadata is not available in the database.This option is turned off by default, as it requires intensive accesses to the hard drive.");
	chckbxAllowFindSop.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.AllowFindSopClassesInStudy=chckbxAllowFindSop.isSelected();
		}
	});
	chckbxAllowFindSop.setSelected(settings.AllowFindSopClassesInStudy);
	
	JCheckBox chckbxLoadPrivateDictionary = new JCheckBox("Load Private Dictionary");
	panel_chkbox.add(chckbxLoadPrivateDictionary);
	chckbxLoadPrivateDictionary.setToolTipText("If set to \"false\", Orthanc will not load its default dictionary of private tags.");
	chckbxLoadPrivateDictionary.addFocusListener(new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent e) {
			settings.LoadPrivateDictionary=chckbxLoadPrivateDictionary.isSelected();
		}
	});
	chckbxLoadPrivateDictionary.setSelected(settings.LoadPrivateDictionary);
	
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
				//System.out.println(settings.fichierInput);
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
	
	JLabel lblNewLabel_5 = new JLabel("For Orthanc 1.3.2");
	Bouttons_Bouttons.add(lblNewLabel_5);
	lblNewLabel_5.setHorizontalAlignment(SwingConstants.CENTER);
	Bouttons_Bouttons.add(btnLoadJson);
	
	JButton btnRestartOrthancServer = new JButton("Restart Orthanc Server");
	Bouttons_Bouttons.add(btnRestartOrthancServer);
	btnRestartOrthancServer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			connexion.restartOrthanc();
		}
	});
	}

	public static void main(String[] args)  {
			SettingsGUI gui=new SettingsGUI();
			//On met la fenetre au centre de l ecran
			gui.pack();
			gui.setLocationRelativeTo(null);
			gui.setSize(gui.getPreferredSize());
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
	}

}