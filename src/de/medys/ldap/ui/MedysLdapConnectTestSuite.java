package de.medys.ldap.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
//import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;

import de.medys.ldap.event.MedysLdapConnectGuiEventListener;
import de.medys.ldap.utils.LdapConnectionFactory;
import de.medys.ldap.utils.MedysLdapConnectionConstants;

/**
 * 
 * GUI f&uuml;r das Testen der LDAP-Server Verbindung zu KV-Telematik LDAP-Referenzsystemen<br /><br />
 * in Anonymous-Modi oder benutzer-spezifischen Modi (SSL+ Benutzername + Passwort, etc.)
 * <br><br>
 * <u><b>Info</b></u>
 * <blockquot>
 * Diese GUI wird nur &uuml;ber den DEVELOPER-Modus-Aufruf erreicht.
 * <br><br>
 * Dazu muss {@link de.medys.ldap.MedysLdapConnection} wie folgt aufgerufen werden
 * <br><br>
 * <i>java MedysLdapConnection "Developer"</i>
 * </blockquot>
 * @author Hayri Emrah Kayaman, MEDYS GmbH 2015
 *
 */
public class MedysLdapConnectTestSuite extends JFrame {
	
	private static final long serialVersionUID = 4903014561862014184L;
	
	private String connectingUserName, connectingUserPassword;
	
	private StringBuilder stringBuilder;

	private JButton btnConnect, btnClearText;

	private JComboBox<String> connectionProtocolsComboBox;
	
	private JComboBox<String> ldapServerUrlComboBox, ldapServerPortsComboBox, developerCnNamesComboBox;
	
	private JLabel lbl_serverUrlInput, lbl_serverPortInput,
					lbl_Location_NameInput, lbl_SN_NameInput,
					lbl_connectionProtocol;
	
	private JLabel lbl_empty, lbl_searchAreaTitle, lbl_connectionAreaTitle, lbl_UserPassword;
	
	private JMenu dataMenue;
	private JMenuBar menuBar;
	private JMenuItem setupConnectItem, closeFrameItem;
	
	private JPanel connectionProtocolPane;
	
	private JPanel attributesTreePanel, informationShowPanel;
	
	private JEditorPane informationTextArea;
	
	private JTextField txtf_givenName, txtf_SN, txtf_UserPassword;

	private JTree attributesTree;
	
	private JScrollPane informationPrintPane;

	private LdapConnectionFactory ldapConnectionFactory;
	
	private MedysLdapConnectGuiEventListener medysUIACL;
	
	/**
	 * Erstellt eine neue Instanz von {@link MedysLdapConnectTestSuite}
	 */
	public MedysLdapConnectTestSuite() {
		
		// Verbindungsaufbau spezifische Informationen
		//

		setConnectingUserName("");
		setConnectingUserPassword("");
		
		stringBuilder = new StringBuilder();
		
		ldapConnectionFactory = new LdapConnectionFactory();
		ldapConnectionFactory.setParentFrame(this);
		
		// init nachdem die Factory-Klasse erzeugt wurde !!
		//
		medysUIACL = new MedysLdapConnectGuiEventListener(this);
				
		initComponents();
		setSystemLookAndFeel();
		
		initGUI();
	}
	
	/**
	 * Erstellt eine neue Instanz von {@link MedysLdapConnectTestSuite} mit dem
	 * LDAP-Server Verbindungsaufbau-spezifischem Benutzernamen und Password
	 *  
	 * @param connectingUserName Der Benutzernamen mit den man sich am LDAP-Server anmeldet
	 * @param connectingUserPassword Password des anmeldenden Benutzers
	 */
	public MedysLdapConnectTestSuite(String connectingUserName, String connectingUserPassword)
	{
		// Verbindungsaufbau spezifische Informationen
		//
		
		setConnectingUserName(connectingUserName);
		setConnectingUserPassword(connectingUserPassword);
		
		stringBuilder = new StringBuilder();
		
		ldapConnectionFactory = new LdapConnectionFactory();
		ldapConnectionFactory.setParentFrame(this);
		
		// init nachdem die Factory-Klasse erzeugt wurde !!
		//
		medysUIACL = new MedysLdapConnectGuiEventListener(this);
				
		initComponents();
		setSystemLookAndFeel();
		
		initGUI();
	}
	
	private void setSystemLookAndFeel() 
	{
		try
		{
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception noLF) 
		{
			
		}
	}
	
	private void initComponents() 
	{
		btnConnect = new JButton("Verbinde");
		btnConnect.setActionCommand("button_verbinde_ldap");
		btnConnect.addActionListener(medysUIACL);
		
		btnClearText = new JButton("Ausgabe bereinigen");
		btnClearText.setActionCommand("button_bereinige_inforarea");
		btnClearText.addActionListener(medysUIACL);
		
		lbl_connectionAreaTitle = new JLabel("KV-Connect LDAP-Server");
		
		lbl_connectionProtocol = new JLabel("verbinden mit ");
		
		lbl_serverUrlInput 	= new JLabel("URL");
		lbl_serverPortInput = new JLabel("PORT");
		
		lbl_searchAreaTitle 	= new JLabel("Suche");
		lbl_Location_NameInput = new JLabel("Ort");
		lbl_SN_NameInput 		= new JLabel("Nachname");
		lbl_UserPassword 		= new JLabel("Password (optional)");
		
		lbl_empty = new JLabel("");
		lbl_empty.setPreferredSize(new Dimension(200, 5));
		lbl_empty.setOpaque(true);
		
		connectionProtocolsComboBox = 
				new JComboBox<String>(MedysLdapConnectionConstants.getConnectionProtocols());
		
		connectionProtocolsComboBox.addActionListener(medysUIACL);
		
		ldapServerUrlComboBox = 
				new JComboBox<String>(MedysLdapConnectionConstants.getLdapServerUrls());
		
		ldapServerUrlComboBox.addActionListener(medysUIACL);
		
		ldapServerPortsComboBox =
				new JComboBox<String>(MedysLdapConnectionConstants.getLdapServerPorts());
		
		txtf_givenName = new JTextField(100);
		txtf_SN = new JTextField(100);
		
		txtf_UserPassword = new JTextField(100);
		txtf_UserPassword.addFocusListener(medysUIACL.getFocusListener());
		
		attributesTree = new JTree(new Hashtable<String,String>());
		
		attributesTreePanel = new JPanel();
		attributesTreePanel.add(attributesTree);
		
		connectionProtocolPane = new JPanel();
		
		connectionProtocolPane.add(lbl_connectionProtocol);
		connectionProtocolPane.add(connectionProtocolsComboBox);
		
		informationTextArea = new JEditorPane();
		informationTextArea.setPreferredSize(new Dimension(800,450));
		informationTextArea.setEditable(false);
		informationTextArea.setBackground(Color.BLACK);
		informationTextArea.setForeground(Color.CYAN);
		informationTextArea.setSelectedTextColor(Color.YELLOW);
		informationTextArea.setSelectionColor(Color.DARK_GRAY);
	    
		informationPrintPane = 
				new JScrollPane(
						informationTextArea, 
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		informationShowPanel = new JPanel();
		informationShowPanel.setLayout(new BorderLayout());
		informationShowPanel.add(informationPrintPane, "Center");
		
		developerCnNamesComboBox = new JComboBox<String>(MedysLdapConnectionConstants.getLdapCnNames());
		developerCnNamesComboBox.addItemListener(medysUIACL.getItemListener());
	}
	
	private void initGUI()
	{
		setSize(800, 900);
		setTitle("MedysLdap v.0.1 TestSuite");
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setJMenuBar(getMedysLdapGUIMenuBar());
		
		setLayout(new BorderLayout());

		getContentPane().add(getServerConnectionInputPane(), "North");
		getContentPane().add(getMedysConnectionInputPane(), "Center");
		getContentPane().add(informationShowPanel, "South");
		
		validate();
	}
	
	private JMenuBar getMedysLdapGUIMenuBar() 
	{	
		setupConnectItem = new JMenuItem("KV-Connect Ldap verbinden..");
		setupConnectItem.setActionCommand("menuitem_verbinde_ldap");
		setupConnectItem.addActionListener(medysUIACL);
		
		closeFrameItem = new JMenuItem("Schliessen");
		closeFrameItem.setActionCommand("menuitem_schliesse_ldap");
		closeFrameItem.addActionListener(medysUIACL);
		
		dataMenue = new JMenu("Datei");
		dataMenue.add(setupConnectItem);
		dataMenue.add(new JSeparator());
		dataMenue.add(closeFrameItem);
		
		menuBar = new JMenuBar();
		menuBar.add(dataMenue);
		
		return menuBar;
	}
	
	private JPanel getMedysConnectionInputPane()
	{
		JPanel pane = new JPanel();
		
		pane.setLayout(new BorderLayout());
		
		pane.add(getComposedInputComponentsPanes(), "North");
		pane.add(new JLabel("Information") ,"Center");
		
		return pane;
	}
	
	private JPanel getComposedInputComponentsPanes() 
	{
		JPanel pane = new JPanel();
		
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
		
		pane.add(new JSeparator());
		pane.add(getUserInfoInputPanel());
		pane.add(new JSeparator());

		return pane;
	}
	
	private JPanel getUserInfoInputPanel()
	{
		JPanel pane = new JPanel();
		
		pane.setLayout(new GridLayout(6,2,5,5));
		
		pane.add(lbl_searchAreaTitle);
		pane.add(lbl_empty);
		
		pane.add(lbl_Location_NameInput);
		pane.add(txtf_givenName);
		
		pane.add(lbl_SN_NameInput);
		pane.add(txtf_SN);
		
		pane.add(new JLabel("alternative ein Entwickler"));
		pane.add(developerCnNamesComboBox);
		
		pane.add(lbl_UserPassword);
		pane.add(txtf_UserPassword);
		
		pane.add(btnConnect);
		pane.add(btnClearText);
		
		return pane;
	}
	
	private JPanel getServerConnectionInputPane()
	{
		JPanel pane = new JPanel();
		
		pane.setLayout(new GridLayout(3,2,5,5));
		
		pane.add(lbl_connectionAreaTitle);
		pane.add(connectionProtocolPane);
		
		pane.add(lbl_serverUrlInput);
		pane.add(ldapServerUrlComboBox);
		
		pane.add(lbl_serverPortInput);
		pane.add(ldapServerPortsComboBox);
		
		return pane;
	}

	/**
	 * Setzt den neuen Textinhalt des Information-Anzeigebereichs (Typ: {@link javax.swing.JEditorPane})<br />
	 * dieser Anwendung
	 * 
	 * @param newText der neue Textinhalt des Informationsbereichs
	 */
	public void setInformationAreaText(String newText) 
	{
		informationTextArea.setEditable(true);
		stringBuilder.append("\n" + newText);
		informationTextArea.setText(stringBuilder.toString());
		informationTextArea.setEditable(false);
	}
	
	/**
	 * Legt die neue Hintergrundfarbe des Information-Anzeigebereichs (Typ: {@link javax.swing.JEditorPane})<br />
	 * dieser Anwendung fest
	 * 
	 * @param newColor die neue Vordergrundfarbe des Information-Anzeigebereichs
	 */
	public void setInformationAreaForegroundColor(Color newColor)
	{
		informationTextArea.setForeground(newColor);
	}
	
	/**
	 * L&ouml;scht den Inhalt des Information-Anzeigebereichs (Typ: {@link javax.swing.JEditorPane})
	 */
	public void clearText() 
	{
		informationTextArea.setText("");
		stringBuilder = new StringBuilder();
	}
	
	/**
	 * Gibt die Komponente "Information-Anzeigebereich" zur&uuml;ck
	 *  
	 * @return informationTextArea - {@link javax.swing.JEditorPane}
	 */
	public String getInformationAreaText() 
	{
		return stringBuilder.toString();
	}
	
	/**
	 * Gibt die Komponente &quot;ComboBox f&uuml;r die Bezeichner der Verbindungsprotokolle&quot; zur&uuml;ck
	 * 
	 * @return connectionProtocolsComboBox - {@link javax.swing.JComboBox}
	 */
	public JComboBox<String> getConnectionProtocolsComboBox()
	{
		return connectionProtocolsComboBox;
	}
	
	/**
	 * Gibt die Komponente &quot;ComboBox f&uuml;r die Server-Namen&quot; zur&uuml;ck
	 * 
	 * @return ldapServerUrlComboBox - {@link javax.swing.JComboBox}
	 */
	public JComboBox<String> getLdapServerUrlComboBox()
	{
		return ldapServerUrlComboBox;
	}
	
	/**
	 * Gibt die Komponente &quot;ComboBox f&uuml;r die Server-Portangaben&quot; zur&uuml;ck
	 * 
	 * @return ldapServerPortsComboBox - {@link javax.swing.JComboBox}
	 */
	public JComboBox<String> getLdapServerPortsComboBox()
	{
		return ldapServerPortsComboBox;
	}
	
	/**
	 * Gibt die Komponente &quot;ComboBox f&uuml;r die Entwicklernamen - Benutzernamen im LDAP-Server&quot; zur&uuml;ck 
	 * wodurch die Benutzerauthentifizierung beim Anmeldevorgang des LDAP-Referenzsystems durchgef&uuml;rt werden kann.
	 * 
	 * @return developerCnNamesComboBox - {@link javax.swing.JComboBox}
	 */
	public JComboBox<String> getDeveloperCnNamesComboBox() {
		return developerCnNamesComboBox;
	}

	/**
	 * Gibt die Komponente &quot;JTextField f&uuml; die Eingabe des LDAP-Suchbegriffs <i>Vorname</i>&quot; zur&uuml;ck
	 * 
	 * @return txtf_givenName - {@link javax.swing.JTextField}
	 */
	public JTextField getTxtfGivenName() {
		return txtf_givenName;
	}
	
	/**
	 * Gibt die Komponente &quot;JTextField f&uuml; die Eingabe des LDAP-Suchbegriffs <i>Nachname</i>&quot; zur&uuml;ck
	 * 
	 * @return txtf_SN - {@link javax.swing.JTextField}
	 */
	public JTextField getTxtfSN() 
	{
		return txtf_SN;
	}
	
	/**
	 * Gibt die Komponente &quot;JTextField f&uuml; die Eingabe des Passworts des anmeldenden Benutzers <i>Vorname</i>&quot; zur&uuml;ck
	 * 
	 * @return txtf_UserPassword - {@link javax.swing.JTextField}
	 */
	public JTextField getTxtfUserPassword()
	{
		return txtf_UserPassword;
	}
	
	/**
	 * Experimentel:
	 * <blockquot>
	 * Setzt die Attribute-Liste in Form eines Attribut-Baums f�r die zuk&uuml;ftige
	 * Anzeige in einer {@link javax.swing.JTree}-Komponente.
	 * <br><br>
	 * Der Inhalt des Attribut-Baums sollte idealerweise die Darstellung des LDAP-Attribut-Baums eines
	 * Suchergebnisses sein.
	 * </blockquot> 
	 * @param newAttribtuesTree
	 */
	public void setAttributesTree(JTree newAttribtuesTree) 
	{
		Component[] informationShowPanelComponents
			= getInformationShowPanel().getComponents();
		
		JPanel infoPanel = getInformationShowPanel();
		
		for(Component component : informationShowPanelComponents)
		{
			if(component.equals(infoPanel))
			{
				infoPanel.remove(attributesTree);
				infoPanel.repaint();
			}
		}
		
		attributesTree = newAttribtuesTree;
		
		getInformationShowPanel().add(attributesTree, "West");
		getInformationShowPanel().repaint();
	}
	
	/**
	 * Gibt die Komponente f&uuml;r die Darstellung des LDAP-Attribut-Baums eines
	 * Suchergebnisses zur&uuml;ck
	 * 
	 * @return attributesTree - {@link javax.swing.JTree}
	 */
	public JTree getAttributesTree() 
	{
		return attributesTree;
	}
	
	/**
	 * Gibt den Container f&uuml;r Informations-Anzeigebereich zur&uuml;ck
	 * 
	 * @return informationShowPanel - {@link javax.swing.JPanel}
	 */
	public JPanel getInformationShowPanel()
	{
		return informationShowPanel;
	}
	
	/**
	 * Liefert den MEDYS-spezifischen ActionListener {@link de.medys.ldap.event.MedysLdapConnectGuiEventListener}
	 * 
	 * @return den MEDYS-spezifischen ActionListener f&uuml;r die GUI
	 * 
	 */
	public MedysLdapConnectGuiEventListener getMedysActionListener()
	{
		return medysUIACL;
	}
	
	/**
	 * Liefert eine Referenz auf die LdapConnectionFactory-Instanz
	 * von MedysLdapConnectGUI 
	 * 
	 *{@link de.medys.ldap.utils.LdapConnectionFactory}
	 * 
	 * @return die LDAP-Connection Factory dieser Anwendung
	 */
	public final LdapConnectionFactory getLdapConnectionFactory()
	{
		return ldapConnectionFactory;
	}
	
	/**
	 * Gibt den Benutzernamen eine anmeldenden Benutzers zur&uuml;ck>
	 * 
	 * @return connectinUserName
	 */
	public String getConnectingUserName() {
		return connectingUserName;
	}

	/**
	 * Setzt den Benutzernamen des anmeldenden Benutzers
	 *  
	 * @param connectingUserName der Benutzername des anmeldenden Benutzers passend zu einem Benutzer-Account
	 */
	public void setConnectingUserName(String connectingUserName) {
		this.connectingUserName = connectingUserName;
	}

	/**
	 * Gibt das Benutzerpasswort eines anmeldenden Benutzers zur&uuml;ck
	 * 
	 * @return connectingUserPassword
	 */
	public String getConnectingUserPassword() {
		return connectingUserPassword;
	}

	/**
	 * Setzt das Benutzerpasswort eines anmeldenden Benutzers
	 * 
	 * @param connectingUserPassword das Benutzerpasswort des anmeldenden Benutzers passend zu einem Benutzernamen (Account)
	 */
	public void setConnectingUserPassword(String connectingUserPassword) {
		this.connectingUserPassword = connectingUserPassword;
	}
}
