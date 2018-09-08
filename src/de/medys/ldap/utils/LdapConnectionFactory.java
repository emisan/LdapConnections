package de.medys.ldap.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import de.medys.ldap.console.MedysLdapConnectConsoleSuite;
import de.medys.ldap.ui.MedysLdapConnectTestSuite;

/**
 * Factory f&uuml;r den Zugriff auf den LDAP-Test oder Produktiv-Server
 * der KV-Telematik GmbH und dessen Abfrage auf spezifische Anfragen aus
 * dessen LDAP-Verzeichnisstruktur.
 *<br><br> 
 * Die Factory kann sowohl in der grafischen Oberfl&auml;che </br>
 * als auch auf der System-Eingabeaufforderung (Konsole) genutzt werden.
 * <br><br>
 * grafische Oberfl&auml;che : {@link de.medys.ldap.ui.MedysLdapConnectTestSuite}
 * <br><br>
 * Konsoleneingabe : {@link de.medys.ldap.console.MedysLdapConnectConsoleSuite}
 * 
 * @author Hayri Emrah Kayaman, MEDYS GmbH, 10 Juni 2015
 * @version 0.1 beta
 */
public class LdapConnectionFactory {
	
	private boolean isConnected;
	
	// das aktuelle Wurzelverzeichnis
	//
	private String currentLdapTreeStructure;
	
	private final String MEDYS_HEADER_INFO = 
			"------ Medys KV-Connect-Ldap Verbindung ------";
	
	// Inhalt eines Verzeichnis-Knotens, wird für die
	// GUI -> JTree benötigt
	//
	private Hashtable<String, String> contentOfTreeNode;

	// Bezugspunkt zum LDAP-System
	//
	private DirContext ldapDirCtx;
	
	// Hilfsklasse zum Erhalt des installierten Server-Zertifikats
	//
	private InstallCert instCert;
	
	// visuelle Testumgebung
	//
	private MedysLdapConnectTestSuite medysLdapGui;
	
	//----------- CONSTRUCTORS
	//
	/**
	 * Erzeugt eine neue Instanz von {@link LdapConnectionFactory}
	 * 
	 */
	public LdapConnectionFactory() 
	{
		// Sicherheitsschlüssel (Chiffren, aka Schlüssel-Zertifikate)
		//
		
		isConnected = false;
		
		contentOfTreeNode = new Hashtable<String, String>();
	}
	
	//----------- SETTER/GETTER METHODS
	//
	
	public void setCurrentLdapTreeStructure(
			final String currentLdapTreeStructure)
	{
		this.currentLdapTreeStructure = currentLdapTreeStructure;
	}
	
	public String getCurrentLdapTreeStructure() {
		return currentLdapTreeStructure;
	}
	
	private void setLdapDirCtx(DirContext ldapDirCtx2) {
		this.ldapDirCtx = ldapDirCtx2;
	}
	
	private DirContext getLdapDirCtx() {
		return ldapDirCtx;
	}
	
	public void setParentFrame(MedysLdapConnectTestSuite medysLdapGui) {
		this.medysLdapGui = medysLdapGui;
	}
	
	public MedysLdapConnectTestSuite getParentFrame() {
		return medysLdapGui;
	}
	
	//----------- SETUP/CONNECTION-METHODS
	//
    
	/*
	 * Prüft, ob das Server-zertifikat in den KeyStore des JDK gespeichert wurde (erhalten) oder nicht
	 * 
	 * Wenn es icht gespeichert wurde, so werden per InstallCert-Klasse die Server-zertifikate
	 * für den TLS-Handshake heruntergeladen, lokal in den Java-Keystore geladen und
	 * daraufhin ein Handshake mit dem Server ausrobiert.
	 * 
	 * Das Resultat ergibt sich in der returnierenden Methode !
	 * 
	 * @param hostName der Name des Servers von dem das Server-Zertifikat zu erhalten ist
	 * @param serverPort der Port des Servers
	 * 
	 * @return TRUE wenn Zertifikat in den KeyStore geladen wurde, sonst FALSE
	 */
	private boolean hasReceivedCertificateFrom(String hostName, String serverPort)
	{	
		instCert = new InstallCert(hostName, serverPort);
		
		// lief alles gut, so sollte zumindestens die Return-methode erreichbar sein
		//
		return instCert.getServerHandshakeAccomplished();
	}
	
	
	/**
	 * Erstellt eine Verbindung zu dem LDAP-TEST-Server von 
	 * der KV-Telematik GmbH mit folgenden Verbindungseigenschaften:
	 * <br><br>
	 * <u>Url des LDAP-Testservers</u>:
	 * <blockquot>
	 * 	<b>&lt;SERVER URL&gt;&lt;statische PORT-Nummer&gt;</b>
	 * 	<br><br>
	 * 	<i>TEST-System</i>
	 *  <br>
	 *  <ul>
	 *  	<li>{@link de.medys.ldap.utils.MedysLdapConnectionConstants#KV_CONNECT_LDAP_TEST_SERVER_URL}</li>
	 *  	<li>{@link de.medys.ldap.utils.MedysLdapConnectionConstants#KV_CONNECT_LDAP_TEST_SERVER_2_URL}</li>
	 *  </ul>
	 *  <br><br>
	 *	<i>Produktiv-System</i>
	 *	<ul>
	 *		<li>{@link de.medys.ldap.utils.MedysLdapConnectionConstants#KV_CONNECT_LDAP_PROD_SERVER_PORT}</li>
	 *		<li>{@link de.medys.ldap.utils.MedysLdapConnectionConstants#KV_CONNECT_LDAP_PROD_SERVER_SSL_PORT}</li>
	 *	</ul>
	 *	<br>
	 *	<u>Ports</u>:
	 *	<ul>
	 *		<li>{@link de.medys.ldap.utils.MedysLdapConnectionConstants#KV_CONNECT_LDAP_TEST_SERVER_PORT}</li>
	 *		<li>{@link de.medys.ldap.utils.MedysLdapConnectionConstants#KV_CONNECT_LDAP_PROD_SERVER_PORT}</li>
	 *		<li>{@link de.medys.ldap.utils.MedysLdapConnectionConstants#KV_CONNECT_LDAP_PROD_SERVER_SSL_PORT}</li>
	 * 	</ul>
	 * </blockquot>
	 * 
	 * <u>Dom&auml;nen-Eigenschaften, sowie Benutzergruppen-Baum (ou=users ..)</u>
	 * <br>
	 * Authentification: durch Angabe von <i>connectingUserName</i> und <i>connectingUserPassword</i> (wenn keine Angabe dann ANONYM)
	 * <br><br>
	 * Verbindungsprotokoll: LDAP v3/SSL (siehe connectionProtocol)
	 * 
	 * @param serverName der Name des LDAP-SERVER in URL-Form (Beispiel: ldap.google.de ohne Verbindungsprefix (http, ldap, ftp...)
	 * @param port die Port-Nummer des jeweiligen LDAP-Servers
	 * @param connectionProtocol Standard: LDAP v3, wenn Angabe=SSL, dann sollte connectingUserName/-Password angegben werden
	 * @param connectingUserName Benutzername, welcehr eine Verbindung zum den LDAP-Server (Login) herstellt
	 * @param connectingUserPassword Password des Benutzernamen
	 */
	public void setUpKvConnectLdapConnection(
			String serverName, 
			String port,
			String connectionProtocol,
			String connectingUserName,
			String connectingUserPassword) 
	{	
		if(connectionProtocol.toLowerCase().compareTo("ssl") == 0)
		{
			if(hasReceivedCertificateFrom(serverName, port))
			{
				// zeige erhaltene Chiffren nur auf der Konsole
				//
				// falls es hier keine Einträge gibt dann kann man
				// später beim Kunden die erste Fehleursache ermitteln
				
				System.out.println("erhaltenen Chiffren\n\nAnonyme\n");
				
				if(instCert.gibAnonymeServerChiffren() != null)
				{
					System.out.println(
							gibInhalteAlsString(
									instCert.gibAnonymeServerChiffren()));
				}
				
				System.out.println("Trusted Chiffren\n");
				
				if(instCert.gibVertrauenswuerdigeServerChiffren() != null)
				{
					System.out.println(
							gibInhalteAlsString(
									instCert.gibVertrauenswuerdigeServerChiffren()));
				}
				
				// eigentliche Voraussetzung für KBV
				//
				if(instCert.gibVertrauenswuerdigeServerChiffren() != null)
				{
					doConnectLdapViaSSL(
							serverName, 
							port, 
							connectingUserName, 
							connectingUserPassword);
				}
			}
		}
		else
		{
			// einfache Verbindung, hier braucht man keine Serverzertifikate oder Chiffren
			//
			// aus historischen Gründen noch vorhanden, LDAP läuft über SSL !!
			doConnectLdap(serverName, port, connectingUserName, connectingUserPassword);
		}
	}
	
	
	/*
	 * führe eine Verbindung ohne SSL durch
	 * 
	 */
	private void doConnectLdap(
			String serverName, 
			String port, 
			String connectingUserName, 
			String connectingUserPassword)
	{
		try 
		{
			// LDAP CONNECTION PROPERTIES
			//
			Hashtable<String, String> env = new Hashtable<String, String>();

			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");

			if(serverName.toLowerCase().startsWith("ldap://"))
			{
				env.put(Context.PROVIDER_URL, serverName + ":" + port);
			}
			else
			{
				env.put(Context.PROVIDER_URL, "ldap://" + serverName + ":" + port);
			}

			if(connectingUserName.toLowerCase().compareTo("anonymous") == 0)
			{
				env.put(Context.SECURITY_AUTHENTICATION, "none");
				env.put(Context.SECURITY_PRINCIPAL,
						MedysLdapConnectionConstants.KV_CONNECT_LDAP_BASE_USERS_DN);
			}
			else
			{
				if((connectingUserName.length() > 0) && (connectingUserName != null))
				{
					env.put(Context.SECURITY_PRINCIPAL,
							"cn=" + connectingUserName 
							+ "," + MedysLdapConnectionConstants.KV_CONNECT_LDAP_BASE_USERS_DN);
					
					if((connectingUserPassword != null) && (connectingUserPassword.length() > 0))
					{
						env.put(Context.SECURITY_CREDENTIALS, connectingUserPassword);
					}
				}
			}

			printInformation(MEDYS_HEADER_INFO
					+ "\n\nBitte Warten Sie, \n\nverbinde mit " + serverName
					+ " an Port " + port + "\n\n");

			ldapDirCtx = new InitialDirContext(env);

			setLdapDirCtx(ldapDirCtx);

			setCurrentLdapTreeStructure(MedysLdapConnectionConstants.KV_CONNECT_LDAP_BASE_USERS_DN);
			
			// for DEBUG ONLY
//			MedysLdapConnectDataTransfer
//				.writeToFile(
//						MedysLdapConnectDataTransfer.getTransferFile(), 
//						getContent((Hashtable<String, String>) ldapDirCtx.getEnvironment()));
			
			isConnected = true;
		} 
		catch (NamingException e) 
		{  
			isConnected = false;
			
			printInformation(
					"Fehler aus LdapConnectionFactory."
					+ "setUpKvConnectLdapConnection(..)\n\n"
					+ e.getMessage()
					+ "\n\n");
		}
	}
	
	/*
	 * führe eine Verbindung mit SSL durch
	 */
	private void doConnectLdapViaSSL(
			String serverName, 
			String port, 
			String connectingUserName, 
			String connectingUserPassword)
	{
		try 
		{	
			// nutze den zwischen gespeicherten KeyStore in "jssecacerts" im
			// Verzeichnis dieser Applikation
			//
			File keyStore = new File("jssecacerts");

			if (keyStore.exists()) 
			{
				System.setProperty("javax.net.ssl.keyStore", keyStore.getAbsolutePath());
				System.setProperty("javax.net.ssl.keyStorePassword", "changeit");

				System.out.println(keyStore.getAbsolutePath());
				System.out.println(keyStore.length());
				
				// LDAP CONNECTION PROPERTIES
				//
				Hashtable<String, String> env = new Hashtable<String, String>();

				env.put(Context.INITIAL_CONTEXT_FACTORY,
						"com.sun.jndi.ldap.LdapCtxFactory");

				String urlSeperator = File.separator;

				if(serverName != null)
				{
					if(serverName.length() > 0)
					{
						// erhalte nur den Server-Namen
						//
						if (serverName.toLowerCase().startsWith("ldap://")
								|| serverName.toLowerCase().startsWith("ldaps://")) 
						{
							// "ldap://" und "ldaps://" abschneiden

							if (serverName.toLowerCase().startsWith("ldap://")) 
							{
								serverName = serverName.substring("ldap://".length(),
										serverName.length());
							} 
							else if (serverName.toLowerCase().startsWith("ldaps://")) 
							{
								serverName = serverName.substring("ldaps://".length(),
										serverName.length());
							}
						}
						
						// falls /-zeichen am Ende vorhanden, abschneiden
						//
						if (serverName.endsWith(urlSeperator)) 
						{
							serverName = serverName.substring(0,
									serverName.length() - 1);
						}
					}
					else
					{
						 MedysLdapConnectDataTransfer
						 .writeToFile(MedysLdapConnectDataTransfer.getSuchparameterHelpFile(),
								 "FEHLER : keine Server-URL übergeben !", true);
					}
				}
				
				// erneut prüfen, da der servername behandelt wurde !!
				//
				if ((serverName != null) && (serverName.length() > 0)) 
				{
					if(port.compareTo("636") == 0)
					{
						env.put(Context.PROVIDER_URL,"ldaps://" + serverName + ":" + port);
						
						// Anonymous Login
						//
						if ((connectingUserName.length() == 0)
								|| (connectingUserPassword.length() == 0)) {
							// Anonymous-Mode (none, hebelt diverse Properties in "env" aus)
							//
							// keine SECURITY_CREDENTIALS
							//
							env.put(Context.SECURITY_AUTHENTICATION, "none");

							// SECURITY_PRINCIPAL ist ohne CN-Angabe, nur Root-Verzeichnis in LDAP
							//
							env.put(Context.SECURITY_PRINCIPAL, 
									MedysLdapConnectionConstants.KV_CONNECT_LDAP_BASE_USERS_DN);
						}
						
						// login mit User und password
						//
						if ((connectingUserName.length() > 0)
								&& (connectingUserPassword.length() > 0)) 
						{
							env.put(Context.SECURITY_AUTHENTICATION, "simple");

							env.put(Context.SECURITY_PRINCIPAL,
									"cn=" + connectingUserName + ","
											+ MedysLdapConnectionConstants.KV_CONNECT_LDAP_BASE_USERS_DN);
							
							env.put(Context.SECURITY_CREDENTIALS,
									connectingUserPassword);
						}

						printInformation(MEDYS_HEADER_INFO
								+ "\n\nBitte Warten Sie, \n\nverbinde mit "
								+ serverName + " an Port " + port + "\n\n");

						ldapDirCtx = new InitialDirContext(env);

						setLdapDirCtx(ldapDirCtx);

						setCurrentLdapTreeStructure(MedysLdapConnectionConstants.KV_CONNECT_LDAP_BASE_USERS_DN);

						// for DEBUG ONLY
						// MedysLdapConnectDataTransfer
						// .writeToFile(
						// MedysLdapConnectDataTransfer.getTransferFile(),
						// getContent((Hashtable<String, String>)
						// ldapDirCtx.getEnvironment()));

						isConnected = true;
					}
					else
					{
						printInformation("Der LDAP-Server " + serverName
								+ " ist über den Port " + port + " nicht erreichbar!");
					}
				}
				else 
				{
					printInformation("Der LDAP-Server " + serverName
							+ " ist kein gültiger Server-Name !");
				}
			} 
			else 
			{
				printInformation("Der KeyStore \"jssecacerts\" konnte im Anwendungsordner nicht gefunden werden.\n\n"
						+ "Anwendung wird beendet.");
				return;
			}
		}
		catch (NamingException e) 
		{
			isConnected = false;

			printInformation("Fehler aus LdapConnectionFactory."
					+ "setUpKvConnectLdapConnection(..)\n\n" + e.getMessage()
					+ "\n\n");
			StackTraceElement[] errors = e.getStackTrace();
			
			for(StackTraceElement element : errors)
			{
				printInformation(element.toString());
			}
		}
	}
	
	//----------- HELPER METHODS
	//
	
	// TODO, not implemented yet
	/**
	 * Bereitet die Erstellung des visullen Verzeichnisbaums von {@link MedysLdapConnectTestSuite} aus 
	 * den Suchergebnissen vor
	 * 
	 * @param table die Hashtabelle mit den Suchergebnissen, 
	 * 		   die die Verzeichnisstruktur des Verzeichnisbaums darstellen
	 * @see javax.swing.JTree
	 * @see java.util.Hashtable
	 */
	public void prepareJTreeEntries(Hashtable<String, String> table)
	{	
		contentOfTreeNode.putAll(table);
	}

	/**
	 * DataTransfer-Methode zwischen LdapConnectionFactory und MedysLdapConnectDataTransfer.
	 * <br><br>
	 * Setzt und &Uuml;bertr&auml;gt das aktuelle Verzeichnis, von wo MedysLdapConnection.jar ausgef&uuml;hrt
	 * wird von LdapConnectionFactory zu MedysLdapConnectDataTransfer.
	 * <br><br>
	 * Diese Methode wird f&uuml;r Festlegung der absoluten Pfadangabe zur Hilfsdatei 
	 * &quot;MedysLdapConnetion_Suchparameter.txt&quot; in MedysLdapConnectDataTransfer ben&ouml;gt.
	 */
	public void setHelpTextfilePath(String path)
	{
		MedysLdapConnectDataTransfer.setSuchparameterHelpFilePath(path);
	}
	
	/**
	 * Gibt MedysLdapConnection_Suchparameter.txt-Datei zurück
	 * 
	 * @return Datei
	 */
	public final File getHelpTextfile() 
	{
		return MedysLdapConnectDataTransfer.getSuchparameterHelpFile();
	}
	
	
	/**
	 * Sendet Informationen an das Textdokument des Anzeigebereichs von
	 * MedysLdapConnectTestSuite (GUI)
	 * 
	 * @param informationText der Text welcher angezeigt werden soll
	 * @see de.medys.ldap.ui.MedysLdapConnectTestSuite
	 */
	public void sendInformationToParentFrame(String informationText)
	{
		medysLdapGui.setInformationAreaText(informationText);
	}
	
	
	//----------- SEARCH METHODS
	//
	
	/**
	 * Findet den LDAP-Eintrag (LDAP-Objekt) zu einem </br>
	 * oder mehreren LDAP-Attributen in dem aktuellen LDAP-Referenzsystem.
	 * <br><br>
	 * <b>Parameter ldapEntryAttributes</b> kann sein :
	 * <blockquot>
	 * 	<ul>
	 * 		<li>sn=&lt;wert&gt;</li>
	 * 		<li>givenName=&lt;wert&gt; (zusammengesetzter Wert)</li>
	 * 		<li>mail=&lt;email-adresse&gt;  (einzelner Wert)</li>
	 * 	</ul>
	 * </blockquot>
	 * 
	 * siehe auch: {@link <a href='https://tools.ietf.org/html/rfc2253#section-2.3'>
	 * 						LDAP v3-Spezifikation RFC 2253 Attribute
	 * 				</a>}
	 *
	 * @param ldapEntryAttribute komma-separierte Zeichenkette mit LDAP-Attributen
	 * @return eine ArrayList, welche die gesammelte Information des gefundenen 
	 * 		   LDAP-Eintrags (LDAP-Objekts) beinhaltet 
	 */
	public Attributes findAttributesOf(final String ldapEntryAttribute)
	{
		Attributes attrs = null;
		
		SearchControls ctrls = new SearchControls();
		
		
		/*
		 * null = alle Attribute aus Suche liefern
		 * 
		 * leerer String-Parameter = "" -> leeres Suchergebnis
		 * 
		 * irgend ein LDAP-Eintrag Attriute wie "mail",
		 * liefert die E-Mail Adresse, falls "ldapEntryAttribute"
		 * ein existierendes Attribute von einem LDAP-Eintrag (Objekt)
		 * ist
		 * 
		 */
	    ctrls.setReturningAttributes(null);
	    
	    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

	    try 
	    {	    	
	    	NamingEnumeration<SearchResult> answers = 
					getLdapDirCtx().search(
							getCurrentLdapTreeStructure(), 
							ldapEntryAttribute, 
							ctrls);

			attrs = answers.next().getAttributes();
	    }
	    catch(Exception noAnswers) 
	    {
	    	printInformation(
	    			"Fehler aus LdapConnectionFactory.findAttributesOf(..)\n\n"
	    			+ "Kann die Informationen für " + ldapEntryAttribute 
	    			+ " auf den LDAP-Server nicht finden.\n\n"
	    			+ noAnswers.getMessage()
	    			+ "\n\n");
	    }
	    
		return attrs;
	}
	
	/**
	 * Findet den LDAP-Eintrag (LDAP-Objekt) anhand des LDAP-Attributes
	 * und liefert alle gefilterten LDAP-Attribute eines oder mehrerer LDAP-Eintr&auml;ge.
	 * <br><br>
	 * <b>Parameter ldapEntryAttributes</b> kann sein :
	 * <blockquot>
	 * 	<ul>
	 * 		<li>sn=&lt;wert&gt;</li>
	 * 		<li>givenName=&lt;wert&gt; (zusammengesetzter Wert)</li>
	 * 		<li>mail=&lt;email-adresse&gt;  (einzelner Wert)</li>
	 * 	</ul>
	 * </blockquot>
	 * <br>
	 * siehe auch: {@link <a href='https://tools.ietf.org/html/rfc2253#section-2.3'>
	 * 						LDAP v3-Spezifikation RFC 2253 Attribute
	 * 				</a>}
	 *
	 * @param ldapEntryAttribute das einzelne LDAP-Attribute wonach gesucht wird
	 * @param FILTER die LDAP-Attribute die aus der Suche gefiltert heraus gehen
	 * @return die gefilterten Attribute 
	 */
	public Attributes findAttributesOfWithFilter(
			final String ldapEntryAttribute,
			final String[] FILTER)
	{
		Attributes attrs = null;
		
		SearchControls ctrls = new SearchControls();
		
		
		/*
		 * null = alle Attribute aus Suche liefern
		 * 
		 * leerer String-Parameter = "" -> leeres Suchergebnis
		 * 
		 * irgend ein LDAP-Eintrag Attriute wie "mail",
		 * liefert die E-Mail Adresse, falls "ldapEntryAttribute"
		 * ein existierendes Attribute von einem LDAP-Eintrag (Objekt)
		 * ist
		 * 
		 */
	    ctrls.setReturningAttributes(FILTER);
	    
	    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

	    try 
	    {	    	
	    	NamingEnumeration<SearchResult> answers = 
					getLdapDirCtx().search(
							getCurrentLdapTreeStructure(), 
							ldapEntryAttribute, 
							ctrls);

			attrs = answers.next().getAttributes();
	    }
	    catch(Exception noAnswers) 
	    {
	    	printInformation(
	    			"Fehler aus LdapConnectionFactory.findAttributesOf(..)\n\n"
	    			+ "Kann die Informationen für " + ldapEntryAttribute 
	    			+ " auf den LDAP-Server nicht finden.\n\n"
	    			+ noAnswers.getMessage()
	    			+ "\n\n");
	    }
	    
		return attrs;
	}
	
	/**
	 * Findet die Email-Adresse von einem LDAP-Eintrag anhand der Attribute
	 * Ortsangabe (l=location) oder Nachname(givenName) eines LDAP-Eintrags.
	 * <br><br>
	 * Der Ort/Nachname referenziert entweder eine Person oder ein Unternehmen.
	 * <br><br>
	 * siehe auch: {@link <a href='https://tools.ietf.org/html/rfc2253#section-2.3'>
	 * 						LDAP v3-Spezifikation RFC 2253 Attribute
	 * 				</a>}
	 * <br><br>
	 * Da eine Instanz/Person mehrere LDAP-Eintr&auml;ge vorweisen kann,
	 * k&ouml;nnen ebenfalls mehrere EMails gefunden werden, so dass die Speicherung
	 * von gefundenen E-Mail-Adressen im String-Array (R&uuml;ckgabewert dieser Methode) 
	 * erforderlich ist.
	 * 
	 * @param location die Ortsangbae zu einer Person oder eines Unternehmens
	 * @param sn der Nachname einer Person / der Name eines Unternehmens
	 * 
	 * @return eine oder mehrere EMail-Adressen der LDAP-Instanz/Person
	 */
	public ArrayList<String> findEmailAddressFrom(
				String location, 
				String sn) 
	{
		ArrayList<String> emails = new ArrayList<String>();
		
		// spezifiziere jene Einträge die zurückgeliefert werden sollen
		//
		// Beispiel für weitere Einträge
		//
		// { "givenName", "sn", "street", "postalCode", "l", "mail" }
		//
	    String[] rtAttr = {"mail"};
	    
	    SearchControls ctls = new SearchControls();
	    
	    ctls.setReturningAttributes(rtAttr);
	    
	    ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

	    // Suchfilter setzen für LDAP-Attribute Eigenschaften
	    //
	    // 'sn=user-name/company-name'
	    // 'l(location)=user-location/company-location'
	    //
	    // Filter liefert bei Erfolg die jeweiligen Werte hinter den
	    // Attributen zurück, die in der "Attribute.getId()" angegeben sind
	    //
	    String filter = "";
	    
	   
	    if((sn.length()>0) && (location.length()==0))
	    {
	    	filter = "(sn="+ sn  + "*)";
	    }
	    else if((sn.length()==0) && (location.length()>0))
	    {
	    	filter = "(l=" + location + "*)";
	    }
	    else
	    {
    		filter = "(&(sn=" + sn + "*)(l=" + location + "*))";
    	}
	    
	    try 
	    {
			// traversiere den aktuellen LDAP-Verzeichnisbaum
	    	//
			NamingEnumeration<SearchResult> answer = 
					ldapDirCtx.search(getCurrentLdapTreeStructure(), filter, ctls);
			
			while(answer.hasMore()) 
			{
				SearchResult rs = answer.next();
				
				emails.add(rs.getAttributes()
						  .getAll()
						   .next().getAll().next()
						    .toString());
				
				// DEBUG ONLY
				//
				// Ansicht {mail:<wert der mail>}
				
				//System.out.println(rs.getAttributes().getAll().next());
				
				// Ansicht <wert der mail>
				
				//System.out.println(rs.getAttributes().getAll().next().getAll().next());
			}
		}
	    catch(NamingException noAttribute) 
	    {
	    	printInformation(
	    			"Ausnahmefehler aus LdapConnectionFactory"
	    			+ ".findEmailAdressFrom(..)\n\n"
	    			+ "Kann keine Email-Adresse von " + sn + " in " + location
	    			+ " ermitteln\n\nFehler:\n" + noAttribute.getMessage() 
	    			+ "\n\n");
	    }
	    catch(NullPointerException emptyMails) 
	    {
	    	printInformation("Ausnahmefehler aus LdapConnectionFactory"
	    			+ ".findEmailAdressFrom(..)\n\n"
	    			+ "Kann keine Email-Adresse von " + sn + " in " + location 
	    			+ " ermitteln\n\nFehler:\n" + emptyMails.getMessage() 
	    			+ "\n\n");
	    }
	    // Print the answer
	    return emails;
	}	
	
	/**
	 * Liefert die Email-Adresse(n) von einer Person/LDAP-Eintrags von einer 
	 * LDAP-Verzeichnisbaumstruktur anhand der &quot;<i>ldapAttributeID</i>&quot;.
	 * <br><br> 
	 * <u>Die LDAP-Attribute (ID's) welche hier abgefragt werden sind</u>:
	 * <blockquot>
	 * 	<ul>
	 * 		<li>Ortsangabe(l=location) einer Person oder Name eines Unternehmens&gt;</li>
	 * 	</ul>
	 * </blockquot>
	 * <br>
	 * <u>Nachname einer Person oder eines Unternehmens&gt;</u>
	 * <blockquot>
	 * 	<ul>
	 * 		<li>{@link <a href='https://tools.ietf.org/html/rfc2253#section-2.3'>LDAP v3-Spezifikation RFC 2253 Attribute</a>}</li>
	 * 		<li>{@link <a href="https://de.wikipedia.org/wiki/Lightweight_Directory_Access_Protocol#/media/File:Ldapentry.gif">Bildliche Darstellung</a>}</li>
	 * 		<li>{@link <a href="https://de.wikipedia.org/wiki/Lightweight_Directory_Access_Protocol#/media/File:Datenstruktur.png">Verzeichnisbaum</a>}</li>
	 * 	</ul>
	 * </blockquot>
	 * 
	 * Da die Attribute-ID in vielen LDAP-Eintr&auml;gen vorkommen, k&ouml;nnen 
	 * ebenfalls unter einer gegebenen Attribute-ID einer Person/Instanz (Unternehmen) 
	 * mehrere EMail-Adressen eingetragen worden sein.
	 * <br><br>
	 * Dies bez&uuml;glich ist der R&uuml;ckgabewert-Inhalt, eine Liste von potentiell existierenden </br>
	 * EMail-Adressen einer Attribute-ID
	 * <br><br>
	 * Return: {@link java.util.ArrayList}
	 *
	 * @param location die Ortsangbae zu einer Person oder eines Unternehmens
	 * @param sn der Nachname einer Person/der Name eines Unternehmen
	 * @return keine oder mehrere EMail-Adressen der LDAP-Instanz/Person
	 */
	public ArrayList<String> getEmailAddressFrom(String location, String sn) 
	{	
		return findEmailAddressFrom(location, sn);
	} 
	
	/**
	 * Liefert die Email-Adresse von einem LDAP-Eintrag (Attribute) in einer 
	 * LDAP-Verzeichnisbaumstruktur anhand der &quot;<i>ldapAttributeID</i>&quot;,  
	 * welche folgendes Schema entspricht
	 * <blockquot>
	 * 	<ul>
	 * 		<li><b>cn=&lt;Personen-ID&gt; ODER &lt;Firmenname-ID&gt;</b></li>
	 * 		<li><b>sn=&lt;Nachname einer Person&gt; ODER &lt;Firmenname&gt;&gt;</b></li>
	 * 		<li><b>givenName=&lt;Vorname einer Person&gt; ODER &lt;Firmenname&gt;</b></li>
	 * 	</ul>
	 * 	<br>
	 * 	oder aus einem anderen LDAP-validen Attributes.
	 * </blockquot>
	 * <u>Siehe auch</u>:
	 * <blockquot>
	 * 	<ul>
	 * 		<li>@see <a href='https://tools.ietf.org/html/rfc2253#section-2.3'>LDAP v3-Spezifikation RFC 2253 Attribute</a></li>
	 * 		<li>@see <a href="https://de.wikipedia.org/wiki/Lightweight_Directory_Access_Protocol#/media/File:Ldapentry.gif">Bildliche Darstellung</a></li>
	 * 		<li>@see <a href="https://de.wikipedia.org/wiki/Lightweight_Directory_Access_Protocol#/media/File:Datenstruktur.png">Verzeichnisbaum</a></li>
	 * 	</ul>
	 * </blockquot>
	 * Da die Attribute-ID in vielen LDAP-Eintr&auml;gen vorkommt, k&ouml;nnen 
	 * ebenfalls unter einer gegebenen Attribute-ID einer Person/Instanz (Unternehmen) 
	 * mehrere EMail-Adressen eingetragen worden sein.
	 * <br><br>
	 * Dies bez&uuml;glich ist der R&uuml;ckgabewert-Inhalt, eine Liste von potentiell existierenden 
	 * EMail-Adressen einer Attribute-ID.
	 * 
	 * @param attrs die LDAP-Attribute eines LDAP-Eintrags aus denen ein/mehrere Attribute ausgelesen werden k&ouml;nen
	 * @return keine oder mehrere EMail-Adressen der LDAP-Instanz/Person der Attribute-ID
	 */
	public String getEmailAddressFrom(Attributes attrs) 
	{
		String email = "";
		
		try 
		{
			NamingEnumeration<? extends Attribute> ne = attrs.getAll();
			
			while(ne.hasMore())
			{
				Attribute attribute = ne.next();
				
				if(attribute.getID().compareTo("mail")==0) 
				{
					// verwirrend mit getAll:Enumeration, aber 
					// ein LDAP-Eintrag hat nunmal Hash-Werte und hier
					// auch nur einen Wert pro Attribute-Eintrag
					//
					// getAll kann in einem anderen Kontext, wie
					// Betriebssystem-Verzeichnisstrukturen sinnvoller
					// genutzt werden, hier reicht dies aber aus in der
					// Form aus, um den Wert hinter der ID=mail zu ehalten
					//
					// @Author Hayri Emrah Kayaman
					
					email = attribute.getAll().next().toString();
				}
			}
		}
		catch(NamingException noAttribute) 
		{
			printInformation(
					"Fehler aus LdapConnectionFactory."
					+ "getEmailAddressFromCN(...)\n\n"
					+ "Die E-Mail konnte nicht gefunden werden.\n\n"
					+ noAttribute.getMessage());
		}
	    return email;
	}
	
	
	//----------- PRINT METHODS
	//
	
	/**
	 * Schreibt den Inhalt eines LDAP-Eintrags in eine Datei
	 * oder auf der Konsole aus und liefert zus&auml;tzlich einen
	 * neuen String, der Doppelpunkt-Zeichen separiert die
	 * Attribute-ID und den zugeh&uuml;rigen Wert enth&auml;lt.
	 * 
	 * @param attr der LDAP-Eintrag
	 * @return die Attribute-ID (name) und der zugeh&ouml;rige Wert
	 *         in einem String, Doppelpunkt-Zeichen separiert
	 */
	public String printAttribute(Attribute attr)
	{
		String idValue = "";
		
		try
		{
			// Attribute haben weitere Zusatzinformation, gepackt in
			// einer Enumeration<t>-Collection (wie ID und Value)
			//
			NamingEnumeration<?> e = attr.getAll();
			
			while(e.hasMore())
			{
				// DEBUG ONLY
				//System.out.println("attribute-id: " + attr.getID());
				//System.out.println("value: " + e.next());
				
				idValue = e.next().toString();
				
				idValue = attr.getID() + ":" + idValue + ",";
				
				//@deprecated aber immer noch nützlich, nicht löschen !!
				//
//				if (idValue.startsWith("-----BEGIN CERTIFICATE-----")) 
//				{
//					idValue = attr.getID() + ":\n\n" + idValue + "\n";	
//				} 
//				else
//				{
//					idValue = attr.getID() + ": " + idValue + "\n";
//				}
			}
			
			// DEBUG ONLY
			printInformation(idValue);
			
		}
		catch(NamingException noAttribute) 
		{
			printInformation(
					"Fehler aus LdapConnectionFactory.printAttribute(...)\n\n"
					+ "Nicht alle Attribute konnten erfragt werden.\n\n"
					+ noAttribute.getMessage()
					+ "\n\n");
		}
		
		return idValue;
	}
	
	/**
	 * Schreibt die Inhalte von mehreren LDAP-Eintr&auml;gen (Attributes) 
	 * in <b>eine lokale Datei</b> (siehe {@link MedysLdapConnectDataTransfer}.
	 * 
	 * @param attrs die LDAP-Eintr&auml;ge (Attribute), die in die <b>lokale Datei</b> geschrieben werden sollen
	 */
	public void printAttributes(Attributes attrs) 
	{
		String values = "";
		String seperator = "\n";
		
		if(attrs != null)
		{
			try 
			{
				// erhalte alle Attribute
				//
				NamingEnumeration<? extends Attribute> attributes = attrs.getAll();
				
				while(attributes.hasMore())
				{
					values += printAttribute((Attribute) attributes.next());
				}
				
				if(values.endsWith(","))
				{
					// letztes Zeichen (komma) abschneiden

					values = values.substring(0, values.length()-1);
				}
			} 
			catch (NamingException noAttribute) 
			{
				printInformation(
						"Fehler aus LdapConnectionFactory.printAttributes(...)"
						+ "\n\n"
						+ "Nicht alle Attribute konnten erfragt werden.\n\n"
						+ noAttribute.getMessage()
						+ "\n\n");
			}
			
			values += ";" + seperator;
				
			MedysLdapConnectDataTransfer
			.writeToFile(
					MedysLdapConnectDataTransfer.getTransferFile(), 
					values,
					true);
		}
	}
	
	/**
	 * Sendet (Druckt) den Inhalt der &uuml;bergebenen ArrayList in die jeweilige
	 * Konsole/Eingabeaufforderung der {@link MedysLdapConnectConsoleSuite}.
	 * 
	 * @param list enth&auml;lt die Informationen, welche angezeigt werden sollen 
	 */
	public void printArray(ArrayList<String> list) 
	{
		String text = "";
		Iterator<String> iterator = list.iterator();
		
		while(iterator.hasNext()) 
		{
			text += iterator.next();
			
			if (text.startsWith("-----BEGIN CERTIFICATE-----")) 
			{
				text = "\n" + text + "\n";
			} 
			else
			{
				text = text + "\n";	
			}
		}
		
		// DEBUG ONLY
		printInformation(text);
		
		MedysLdapConnectDataTransfer
		.writeToFile(
				MedysLdapConnectDataTransfer.getTransferFile(), 
				text,
				true);
	}
	
	/**
	 * Hilfsmethode um die Ausgabe einer Information
	 * entweder auf der Konsole oder in der TestSuite anzuzeigen.
	 * 
	 * @param message der Inhalt der Information
	 */
	public void printInformation(String message) 
	{
		if(medysLdapGui != null)
		{
			sendInformationToParentFrame(message);
		}
		else
		{
			System.out.println(message);
		}
	}
	
	
	/**
	 * Erstellt anhand der &uuml;bergebenen Konsolen-Parameter eine Verbindung 
	 * zu einem LDAP-Server </br> und liefert von dort die Informationen 
	 * zu einem LDAP-Eintrag anhand eines Such-Ausdrucks &quot;ldapQuery&quot;
	 * <br><br>
	 * <u><b>ldapQuery</b> kann sein</u>:
	 * <blockquot>
	 * 	<ul>
	 * 		<li>sn=&lt;wert&gt;</li>
	 * 		<li>(&(givenName=&lt;Wert aus der Hilfsdatei&gt;)(sn=&lt;Wert aus der Hilfsdatei&gt;))</li>
	 * 	</ul>
	 * 	-&gt; Treffer, wenn Attribute &quot;givenName&quot; <b>und/oder</b> &quot;sn&quot; gefunden werden.
	 * </blockquot>
	 * <br>
	 * siehe auch: {@link <a href='https://tools.ietf.org/html/rfc2253#section-2.3'>
	 * 						LDAP v3-Spezifikation RFC 2253 Attribute
	 * 				</a>}
	 * <br><br>
	 * siehe f&uuml;r Hilfsdatei Info in: {@link de.medys.ldap.MedysLdapConnection}
	 * 
	 * @param serverName der Name des LDAP-SERVER in URL-Form (Beispiel: ldap.google.de ohne Verbindungsprefix (http, ldap, ftp...)
	 * @param port die PORT-Adresse des Servers
	 * @param connectionProtocol Standard: LDAP v3, wenn Angabe=SSL, dann sollte connectingUserName/-Password angegeben werden
	 * @param connectingUserName registrierter Benutzername auf dem LDAP-Server
	 * @param connectingUserPassword Password des registrierten Benutzers
	 * @param medysLdapConnectionJarPath das Verzeichnis in dem die MedysLdapConncetion.jar liegt
	 */
	public void retrieveLdapInformation(
									String serverName,
									String port,
									String connectionProtocol,
									String connectingUserName,
									String connectingUserPassword,
									String medysLdapConnectionJarPath) 
	{	
		setHelpTextfilePath(medysLdapConnectionJarPath);
		
//		printInformation("Servername=" + serverName);
//		printInformation("Serverport=" + port);
//		printInformation("Connectionprotocol=" + connectionProtocol);
//		printInformation("UserName=" + connectingUserName);
//		printInformation("Password=" + connectingUserPassword);
//		printInformation("Helpfile=" + getHelpTextfile().getName());
		
		MedysLdapConnectDataTransfer.setTransferFilePath(medysLdapConnectionJarPath);
		
//		printInformation("Helpfile Path=" + medysLdapConnectionJarPath);
//		printInformation("Transferfile Path=" + MedysLdapConnectDataTransfer.getTransferFile().getPath());
		
		MedysLdapConnectDataTransfer.clearMedysTextFileContent();
		
		printInformation(MEDYS_HEADER_INFO + "\n\nLDAP Connection Test");

		// siehe Kommentar in HEADER dieser Klasse
		//
		if (connectingUserName.toLowerCase().compareTo("no_param") == 0) 
		{
			connectingUserName = "";
		}
		if (connectingUserPassword.toLowerCase().compareTo("no_param") == 0) 
		{
			connectingUserPassword = "";
		}
		
		setUpKvConnectLdapConnection(serverName, port, connectionProtocol,
				connectingUserName, connectingUserPassword);

		// Parameter Nachname und Ort aus der Hilfsdatei extrahieren
		//
		String sn = getPersonNameFromInternalHelpfile();
		String location = getResidentLocationOfPersonFromInternalHelpfile();
		
		printInformation("SN=" + sn);
		printInformation("Location=" + location);
		
		if(isConnected)
		{
			printInformation("Verbindung erfolgreich hergestellt !"
					+ "Suche LDAP-Eintrag für " + sn + " in " + location
					+ "");
			
			// sonst
			//
			if ((location.length() > 0) || (sn.length() > 0)) 
			{
				// DEBUG only
				//
				// zeig die Hashtable ALLER Inhhalte
				// eines Verzeichnisbaums
				//
				// printInformation(getContents(getLdapSubTreeEntries()));

				// E-Mail nur erhalten
				// printArray(getEmailAddressFrom(givenName, sn));

				ArrayList<String> emails = getEmailAddressFrom(location, sn);

				String[] filter = { "title", "givenName", "sn", "street",
						"postalCode", "l", "mail", "LANR", "BSNR" };

				if ( (emails != null) && (emails.size() > 0) ) 
				{
					for (String email : emails) 
					{
						printAttributes(findAttributesOfWithFilter("mail="
								+ email, filter));
					}

					emails.clear();
				}
				else 
				{
					printInformation("Konnte die E-Mail von " + sn 
							+ " in " + location + " im LDAP-Server nicht finden !");
				}
			}
			else 
			{
				printInformation("Bitte geben Sie mindestens einen Vornamen oder einen Nachnamen an !!");
			}
		}
		else {
			
			printInformation("Verbindung zum Server fehlgeschlagen\n|");
			
			// schreibe diese Information in die Datenaustauschdatei für das Auslesen
			// in MEDYS
			//
			MedysLdapConnectDataTransfer.writeToFile(
					MedysLdapConnectDataTransfer.getTransferFile(), 
					"KEINE_LDAP_VERBINDUNG", 
					false);
		}
	}
	
	/**
	 * wie {@link de.medys.ldap.utils.LdapConnectionFactory#retrieveLdapInformation(String, String, String, String, String)} 
	 * au&szlig;er das diese Methode f&uuml;r den <b>DEVELOPER-Modus (GUI)</b> gedacht ist.
	 * <br><br>
	 * <u>Suchbergriffe</u>
	 * <blockquot>
	 * 	<ul>
	 * 		<li>givenName=&lt;Wert&gt;</li>
	 * 		<li>sn=&lt;Wert&gt;</li>
	 * 	</ul>
	 * </blockquot>
	 * -&gt; Treffer, wenn Attribute &quot;givenName&quot; <b>und/oder</b> &quot;sn&quot; gefunden werden.
	 * <br><br>
	 * siehe auch: {@link <a href='https://tools.ietf.org/html/rfc2253#section-2.3'>
	 * 						LDAP v3-Spezifikation RFC 2253 Attribute
	 * 				</a>}
	 * 
	 * @param serverName der Name des LDAP-SERVER in URL-Form (Beispiel: ldap.google.de ohne Verbindungsprefix (http, ldap, ftp...)
	 * @param port die PORT-Adresse des Servers
	 * @param connectionProtocol Standard: LDAP v3, wenn Angabe=SSL, dann sollte connectingUserName/-Password angegeben werden
	 * @param connectingUserName registrierter Benutzername auf dem LDAP-Server
	 * @param connectingUserPassword Password des registrierten Benutzers
	 * @param location Wohnort der Person
	 * @param sn Nachname der Person
	 */
	public void retrieveLdapInformationDEVELOPER(
									String serverName,
									String port,
									String connectionProtocol,
									String connectingUserName,
									String connectingUserPassword,
									String location,
									String sn)
	{	
		MedysLdapConnectDataTransfer.clearMedysTextFileContent();
		
		printInformation(MEDYS_HEADER_INFO + "\n\nLDAP Connection Test");

		// siehe Kommentar in HEADER dieser Klasse
		//
		if (connectingUserName.toLowerCase().compareTo("no_param") == 0) 
		{
			connectingUserName = "";
		}
		if (connectingUserPassword.toLowerCase().compareTo("no_param") == 0) 
		{
			connectingUserPassword = "";
		}
		
		setUpKvConnectLdapConnection(serverName, port, connectionProtocol,
				connectingUserName, connectingUserPassword);
		
		if(isConnected)
		{
			printInformation("Verbindung erfolgreich hergestellt !\n\n"
					+ "Suche LDAP-Eintrag für \"" + sn + " in " + location
					+ "\"\n\n");
			
			if ((location.length() > 0) || (sn.length() > 0)) 
			{
				// DEBUG only
				//
				// zeig die Hashtable ALLER Inhhalte
				// eines Verzeichnisbaums
				//
				// printInformation(getContents(getLdapSubTreeEntries()));

				// E-Mail nur erhalten
				// printArray(getEmailAddressFrom(givenName, sn));

				ArrayList<String> emails = getEmailAddressFrom(location, sn);

				String[] filter = { "title", "givenName", "sn", "street",
						"postalCode", "l", "mail", "LANR", "BSNR" };

				if ( (emails != null) && (emails.size() > 0) ) 
				{
					for (String email : emails) 
					{
						printAttributes(findAttributesOfWithFilter("mail="
								+ email, filter));
					}

					emails.clear();
				}
				else 
				{
					printInformation("Konnte die E-Mail von " + sn 
							+ " in " + location + " im LDAP-Server nicht finden !");
				}
			}
			else 
			{
				printInformation("Bitte geben Sie mindestens einen Vornamen oder einen Nachnamen an !!");
			}
		}
		else {
			
			printInformation("Verbindung zum Server fehlgeschlagen\n|");
			
			// schreibe diese Information in die Datenaustauschdatei f�r das Auslesen
			// in MEDYS
			//
			MedysLdapConnectDataTransfer.writeToFile(
					MedysLdapConnectDataTransfer.getTransferFile(), 
					"KEINE_LDAP_VERBINDUNG", 
					false);
		}
	}
	
	/**
	 * wie {@link de.medys.ldap.utils.LdapConnectionFactory#retrieveLdapInformation(String, String, String, String, String)} 
	 * au&szlig;er das diese Methode f&uuml;r den <b>DEBUGGER-Modus (GUI)</b> gedacht ist und den Ausf&uuml;hrungspfad 
	 * der MedysLdapConnection.JAR als zus&auml;tzlichen Parameter ben&ouml;tigt.
	 * <br><br>
	 * <u>Suchbergriffe</u>
	 * <blopckquot>
	 * 	<ul>
	 * 		<li>givenName=&lt;Wert&gt;</li>
	 * 		<li>sn=&lt;Wert&gt;</li>
	 * 	</ul>
	 * </blockquot>
	 * -&gt; Treffer, wenn Attribute &quot;givenName&quot; <b>und/oder</b> &quot;sn&quot; gefunden werden.
	 * <br><br>
	 * siehe auch: {@link <a href='https://tools.ietf.org/html/rfc2253#section-2.3'>
	 * 						LDAP v3-Spezifikation RFC 2253 Attribute
	 * 				</a>}
	 * 
	 * @param serverName der Name des LDAP-SERVER in URL-Form (Beispiel: ldap.google.de ohne Verbindungsprefix (http, ldap, ftp...)
	 * @param port die PORT-Adresse des Servers
	 * @param connectionProtocol Standard: LDAP v3, wenn Angabe=SSL/TLS, dann sollte connectingUserName/-Password angegeben werden
	 * @param connectingUserName registrierter Benutzername auf dem LDAP-Server
	 * @param connectingUserPassword Password des registrierten Benutzers
	 * @param location Wohnort der Person
	 * @param sn Nachname der Person
	 * @param medysLdapConnectionJarPath das Verzeichnis in dem die MedysLdapConncetion.jar liegt
	 */
	public void retrieveLdapInformation(
									String serverName,
									String port,
									String connectionProtocol,
									String connectingUserName,
									String connectingUserPassword,
									String sn,
									String location,
									String medysLdapConnectionJarPath)
	{	
		setHelpTextfilePath(medysLdapConnectionJarPath);
		
		MedysLdapConnectDataTransfer.setTransferFilePath(medysLdapConnectionJarPath);
		MedysLdapConnectDataTransfer.clearMedysTextFileContent();
		
		printInformation(MEDYS_HEADER_INFO + "\n\nLDAP Connection Test");

		// siehe Kommentar in HEADER dieser Klasse
		//
		if (connectingUserName.toLowerCase().compareTo("no_param") == 0) 
		{
			connectingUserName = "";
		}
		if (connectingUserPassword.toLowerCase().compareTo("no_param") == 0) 
		{
			connectingUserPassword = "";
		}
		
		setUpKvConnectLdapConnection(serverName, port, connectionProtocol,
				connectingUserName, connectingUserPassword);
		
		if(isConnected)
		{
			printInformation("Verbindung erfolgreich hergestellt !\n\n"
					+ "Suche LDAP-Eintrag für \"" + sn + " in " + location
					+ "\"\n\n");
			
			if ((location.length() > 0) || (sn.length() > 0)) 
			{
				// DEBUG only
				//
				// zeig die Hashtable ALLER Inhhalte
				// eines Verzeichnisbaums
				//
				// printInformation(getContents(getLdapSubTreeEntries()));

				// E-Mail nur erhalten
				// printArray(getEmailAddressFrom(givenName, sn));

				ArrayList<String> emails = getEmailAddressFrom(location, sn);

				String[] filter = { "title", "givenName", "sn", "street",
						"postalCode", "l", "mail", "LANR", "BSNR" };

				if ( (emails != null) && (emails.size() > 0) ) 
				{
					for (String email : emails) 
					{
						printAttributes(findAttributesOfWithFilter("mail="
								+ email, filter));
					}

					emails.clear();
				}
				else 
				{
					printInformation("Konnte die E-Mail von " + sn 
							+ " in " + location + " im LDAP-Server nicht finden !");
				}
			}
			else 
			{
				printInformation("Bitte geben Sie mindestens einen Vornamen oder einen Nachnamen an !!");
			}
		}
		else {
			
			printInformation("Verbindung zum Server fehlgeschlagen\n|");
			
			// schreibe diese Information in die Datenaustauschdatei f�r das Auslesen
			// in MEDYS
			//
			MedysLdapConnectDataTransfer.writeToFile(
					MedysLdapConnectDataTransfer.getTransferFile(), 
					"KEINE_LDAP_VERBINDUNG", 
					false);
		}
	}
	
	// --------- VALUE RETURNING CUSTOM METHODS
	//
	
	/*
	 * Liefert alle LDAP-Einträge unterhalb eines LDAP-Eintrags (root of),
	 * 
	 * @param fromTreeNode der LDAP-Eintrag als Verzeichnisknoten, dessen Sub-Knoten
	 * 					  zurückgeliefert werden sollen
	 * 
	 * @return ein Satz aus Hashtabellen mit den Sub-Knoten Schlüssel-Werte Paaren,
	 * 		   oder NULL, wenn <b>fromTreeNode</b> keine Sub-knoten besitzt
	 */
	@SuppressWarnings("unused")
	private Set<Hashtable<String,String>> getLdapSubTreeEntries()
	{
		Set<Hashtable<String,String>> subContext = 
				new HashSet<Hashtable<String,String>>();
		
		try
		{
			NamingEnumeration<Binding> subTreeNodes = getLdapDirCtx().listBindings(getCurrentLdapTreeStructure());
			
			while(subTreeNodes.hasMore())
			{
				Binding subTreeNode = subTreeNodes.next();
				
				// erhalte CN-Name des Knoten
				//
				String subTreeNodeName = subTreeNode.getName();
				
				// DEBUG ONLY
				//
				printInformation(
							"Sub-Knoteninahlt von \"" + subTreeNodeName
							+ "\"\n\n" + getNodeContent(subTreeNodeName));
				
				subContext.add(getAttributes(findAttributesOf(subTreeNodeName)));
			}
		}
		catch(NamingException noSubTreeNodes)
		{
			printInformation("Ausnahmefehler aus LdapConnetionFactory"
					+ ".getLdapSubTreeEntries():\n\n"
					+ "Es existieren keine weiteren Sub-Knoten unter "
					+ getCurrentLdapTreeStructure() + "\n\n"
					+ "Fehler: " + noSubTreeNodes.getMessage());
		}
		
		return subContext;
	}
	
	/**
	 * Liefert den Inhalt eines Attributes.
	 * 
	 * @param attr das Attribute-Objekt, welches ein Schl&uuml;sselpaar mit ID's 
	 * 			und ID-Werten (intern) enth&auml;lt
	 * 
	 * @return eine Hashtabelle mit den Attribute-ID's 
	 * 			(als Hash-Schl&uuml;sselwert KEY) und ihren Werten (als Hash-KEY-VALUE)
	 * 
	 * @see java.util.Hashtable
	 */
	public Hashtable<String,String> getAttributeContent(Attribute attr) 
	{
		Hashtable<String, String> inhalt = new Hashtable<String,String>();
		
		try 
		{
			NamingEnumeration<?> ne = attr.getAll();
			
			while(ne.hasMore())
			{
				inhalt.put(attr.getID(), ne.next().toString());
				
			}
		}
		catch(NamingException noContent) 
		{
			printInformation(
					"Fehler aus LdapConnectionFactory."
					+ "getAttributeContent(...)\n\n"
					+ "Die Attributeeigenschaften des Attributes konnten "
					+ "nicht erfragt werden.\n\n"
					+ noContent.getMessage());
		}
		
		return inhalt;
	}
	
	/**
	 * Gibt die LDAP-Attribute einer LDAP-Suchanfrage zur&uuml;ck.
	 * 
	 * @param attrs die LDAP-Attribute des LDAP-Referenzsystems
	 * @return {@link java.util.Hashtable} mit den LDAP-Attributen
	 */
	public Hashtable<String,String> getAttributes(Attributes attrs)
	{
		Hashtable<String,String> content = new Hashtable<String,String>();
		
		try
		{
			NamingEnumeration<? extends Attribute> neAttr = attrs.getAll();

			while (neAttr.hasMore()) {
				Attribute attr = neAttr.next();

				NamingEnumeration<?> idValues = attr.getAll();

				while (idValues.hasMore())

				{
					content.put(attr.getID(), idValues.next().toString());
				}
			}
		}
		catch(NamingException noAttributes)
		{
			printInformation(
					"Ausnahmefehler aus LdapConnectionFactory"
					+ ".getAttributes(..)\n\nEs existieren keine"
					+ "Attribute-Werte oder zugehörige Attribute-ID's"
					+ "\n\nFehler: \n\n" + noAttributes.getMessage());
		}
		
		return content;
	}
	
	/**
	 * Liefert eine Hashtable mit den Inhalten der Attribute.
	 * 
	 * @param attrs die Attribute
	 * @return eine generische Hashtable mit Schl&uuml;ssel und Wertepaar aus dem Attribute
	 */
	public Set<Hashtable<String, String>> getAttributesContents(Attributes attrs)
	{
		Set<Hashtable<String, String>> rtTables = 
				new HashSet<Hashtable<String,String>>();
		
		NamingEnumeration<? extends Attribute> ne = attrs.getAll();
		
		try 
		{
			while (ne.hasMore()) 
			{	
				Hashtable<String, String> nextEntry = getAttributeContent(ne.next());
				
				prepareJTreeEntries(nextEntry);
				
				rtTables.add(nextEntry);
			}
			
		}
		catch(NamingException notHasMore) 
		{
			printInformation(
					"Fehler aus LdapConnectionFactory."
					+ "getAttributesContents(...)\n\n"
					+ "Nicht alle Attribute konnten erfragt werden.\n\n"
					+ notHasMore.getMessage());
		}
		
		return rtTables;
	}

	/**
	 * Liefert, zeilenweise separiert, den Inhalt der Hashtable.
	 * 
	 * @param env die Hashtable
	 * @return eine Zeichenkette, zeilenweise separiert, mit dem Inhalt der Hashtable
	 */
	public String getContent(Hashtable<String,String> env) 
	{ 
		
		String output = "";
		
		Set<Entry<String,String>> inhalt = env.entrySet();
		
		Iterator<Entry<String,String>> iterator = inhalt.iterator();
		
		while(iterator.hasNext()) 
		{
			Entry<String,String> entry = iterator.next();
			
			// erstelle zeilenweise R&uuml;ckgabe
			//
			output += entry.getKey().toString();
			output += ", " + entry.getValue().toString() + "\n";
		}
		
		return output;
	}
	
	/**
	 * Liefert, zeilenweise separiert, den Inhalt der Hashtablen eines Datensatzes (Set.
	 * 
	 * @param envs die Hashtablen (ein Set aus Hashtable)
	 * @see java.util.Set
	 * @see java.util.Hashtable
	 * @return eine Zeichenkette, zeilenweise separiert, mit dem Inhalt der Hashtable
	 */
	public String getContents(Set<Hashtable<String, String>> envs) 
	{
		String output = "";
		
		Iterator<Hashtable<String,String>> iterator = envs.iterator();
		
		while(iterator.hasNext())
		{
			Hashtable<String, String> content = iterator.next();
			
			prepareJTreeEntries(content);
			
			output += getContent(content); 
		}
		
		
		return output;
	}
	
	/*
	 * Liefert das Arbeitsverzeichnis, wo die MedysLdapConnection_Suchparameter.txt-Datei
	 * und MedysLdapConnection.jar-Datei sich befindet
	 * 
	 * @return Verzeichnispfadangabe zu den oben genannten Dateien 
	 */
	public String getMedysLdapConnectionJarPath()
	{
		String pfad = MedysLdapConnectDataTransfer.readFromFile(getHelpTextfile());
		
		printInformation("pfad aus getMedysLdapConnectionJarPath()=" + pfad);
		
		StringTokenizer stok = new StringTokenizer(pfad, ",");
		
		// rückgabe default-wert sichern
		//
		pfad = "path=";
		
		while(stok.hasMoreTokens())
		{
			String token = stok.nextToken();
			
			if(token.contains(pfad))
			{
				pfad = token.substring(pfad.length(), token.length());
				break;
			}
		}
		
		return pfad;
	}
	
	/*
	 * Liefert den Nachnamen aus der Hilfsdatei, die Medys-seitig beschrieben wurde.
	 * 
	 * Verzeichnis und Name der Datei sind festgelegte Werte !!
	 * 
	 * @return der Nachname einer Person oder leeren String, wenn nicht vorhanden
	 */
	public String getPersonNameFromInternalHelpfile()
	{	
		String name = MedysLdapConnectDataTransfer.readFromFile(getHelpTextfile());
		
		StringTokenizer stok = new StringTokenizer(name, ",");
		
		// rückgabe default-wert sichern
		//
		name = "sn=";
		
		while(stok.hasMoreTokens())
		{
			String token = stok.nextToken();
			
			if(token.contains(name))
			{
				int index = token.indexOf(name);
				name = token.substring(name.length() + index, token.length());
				break;
			}
		}
		
		return name;
	}
	
	/*
	 * Liefert den Wohnort einer Person aus der Hilfsdatei
	 * 
	 * Verzeichnis und Name der Datei sind festgelegte Werte !!
	 * 
	 * @return der Ort einer Person oder leeren String, wenn nicht vorhanden 
	 */
	public String getResidentLocationOfPersonFromInternalHelpfile()
	{	
		String location = MedysLdapConnectDataTransfer.readFromFile(getHelpTextfile());
		
		StringTokenizer stok = new StringTokenizer(location, ",");
		
		// rückgabe default-wert sichern
		//
		location = "l=";
		
		while(stok.hasMoreTokens())
		{
			String token = stok.nextToken();
			
			if(token.contains(location))
			{
				location = token.substring(location.length(), token.length());
				break;
			}
			
		}
		return location;
	}
	
	/**
	 * Liefert den Inhalt eines LDAP-Knotens von dem LDAP-Verzeichnisbaum, 
	 * welcher durch {@link #getCurrentLdapTreeStructure()} festgelegt ist.
	 * 
	 * @param subTreeNode
	 * @return der Inhalt des Sub-Knoten in einer {@link java.util.Hashtable}
	 */
	public String getNodeContent(String subTreeNode)
	{
		return getContent(getAttributes(findAttributesOf(subTreeNode)));
	}
    
	/**
	 * Liefert eine komma-separierte Darstellung des gegebenen Inhalts.
	 * 
	 * @param inhalt 
	 * @return komma-separierte Darstellung als String
	 */
	public synchronized static String gibInhalteAlsString(String[] inhalt)
	{
		StringBuilder sb = new StringBuilder();
		
		if(inhalt != null)
		{
			for(String s : inhalt)
			{
				sb.append(s);
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * @deprecated use {@link #findAttributesOf(String)}
	 * <br><br>
     * Gibt alle Attribute einer Person/Unternehmens zur&uuml;ck, die einen
     * LDAP-Eintrag besitzen.
     * 
     * @param givenName Vorname der Person oder des Unternehmens
     * @param sn Nachname der Person oder des Unternehmens 
     * @return die Attribute des LDAP-Eintrags der Person/Unternehmens
     */ 
    public List<Attributes> getAllAttributesOf(String givenName, String sn) 
    { 
        List<Attributes> users = new ArrayList<Attributes>();
        
        try 
        { 
            SearchControls sctr = new SearchControls();
            
            sctr.setReturningAttributes(null);
            
            String filter = "(&(givenName=" + givenName + ")(sn=" + sn + "))";
            
            NamingEnumeration<SearchResult> searchResults = 
            		getLdapDirCtx()
            		 .search(getCurrentLdapTreeStructure(), filter , sctr); 
            
            while (searchResults.hasMore()) 
            { 
                SearchResult sr = (SearchResult) searchResults.next(); 
                users.add(sr.getAttributes()); 
            } 
        } 
        catch (NamingException er) 
        { 
            printInformation(
            		"Ausnahmefehler in LdapConnectionFcatory."
            	   + ".getAllAttributesOf\n\n"
            	   + "Kann keinen Benutzer " + givenName + " " + sn
            	   + " finden\n\nFehler: " + er.getMessage()); 
        } 
        catch (Exception e) 
        { 
            printInformation(
            		"Ausnahmefehler in LdapConnectionFcatory."
             	   + ".getAllAttributesOf\n\nFehler: "
             	   + e.getMessage()); 
        } 
        return users; 
    } 
}



