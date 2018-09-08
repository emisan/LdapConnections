package de.medys.ldap.utils;

import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;

import de.medys.ldap.console.MedysLdapConnectConsoleSuite;
import de.medys.ldap.ui.MedysLdapConnectTestSuite;

/**
 * Die Klasse beinhaltet konstante Verbindungswerte.
 * <br><br>
 * Prim&auml;r wird es f&uuml;r die {@link MedysLdapConnectTestSuite} genutzt, kann jedoch in anderen Klassen 
 * wie f&uuml;r die {@link MedysLdapConnectConsoleSuite} brauchbar sein.
 * <br><br>
 * <u><b>Verbindungswerte</b></u>
 * <blockquote>
 * <ol>
 * 	<li>LDAP-Server URL-Angaben</li>
 *  <li>LDAP-Server PortangabenEntwickler-CN</li>
 *  <li>LDAP-Verbindungsverzeichnis (BASE-DN zu entsprechenden Server URL-Angaben)</li>
 *  <li>usw.</li>
 * </ol>
 * </blockquote>
 *  
 * @author Hayri Emrah Kayaman, MEDYS GmbH 2015
 *
 */
public class MedysLdapConnectionConstants {

	// alle statischen Felder (ausser die String-Arrays) "public" belassen, wird für die JAVADOC benötigt
	//
	
	public final static String KV_CONNECT_LDAP_BASE_USERS_DN = "ou=users,dc=kv-safenet,dc=de";
	
	public static final String KV_CONNECT_LDAP_TEST_SERVER_URL = "kvc-1.kvtg.kbv.de";

	public static final String KV_CONNECT_LDAP_TEST_SERVER_2_URL = "kvc-2.kvtg.kbv.de";

	public static final String KV_CONNECT_LDAP_PROD_SERVER_URL = "kvlink1.kv-safenet.de";

	public static final String KV_CONNECT_LDAP_USER_ANONYMOUS = "Anonymous";
	
	public static final String KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_1 = "medys.1";

	public static final String KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_2 = "medys.2";

	public static final String KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_3 = "medys.3";

	public static final String KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_1_WITH_MAIL = "medys.1@kv-safenet.de";

	public static final String KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_2_WITH_MAIL = "medys.2@kv-safenet.de";

	public static final String KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_3_WITH_MAIL = "medys.3@kv-safenet.de";
	
	public static final String KV_CONNECT_LDAP_TEST_SERVER_2_CN_MEDYS = "medys.1.ref2";

	public static final String KV_CONNECT_LDAP_TEST_SERVER_2_CN_MEDYS_WITH_MAIL = "medys.1.ref2@kv-safenet.de";

	public static final int KV_CONNECT_LDAP_TEST_SERVER_PORT = 10389;

	public static final int KV_CONNECT_LDAP_PROD_SERVER_PORT = 8849;

	public static final int KV_CONNECT_LDAP_PROD_SERVER_SSL_PORT = 636; // aktueller KVConnect-LDAP Server SSL-Port
	
	private static final String[] CONNECTION_SECURITY_PROTOCOLS =
		{
			"TLSv1"
		};
	
	private static final String[] CONNECTION_PROTOCOLS =
		{
			"ssl",
			"LDAP v2",
			"LDAP v3",
			"HTTP",
			"HTTPS",
			"FTP"
		};
	
	private static final String[] SERVER_URLS = 
		{
			KV_CONNECT_LDAP_TEST_SERVER_URL, 
			KV_CONNECT_LDAP_TEST_SERVER_2_URL,
			KV_CONNECT_LDAP_PROD_SERVER_URL 
		};

	private static final String[] CN_NAMES = 
		{
			KV_CONNECT_LDAP_USER_ANONYMOUS,
			KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_1,
			KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_2,
			KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_3,
			KV_CONNECT_LDAP_TEST_SERVER_2_CN_MEDYS,
			KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_1_WITH_MAIL,
			KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_2_WITH_MAIL,
			KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_3_WITH_MAIL,
			KV_CONNECT_LDAP_TEST_SERVER_2_CN_MEDYS_WITH_MAIL
		};

	private static final String[] SERVER_PORTS = 
		{
			"" + KV_CONNECT_LDAP_TEST_SERVER_PORT,
			"" + KV_CONNECT_LDAP_PROD_SERVER_PORT,
			"" + KV_CONNECT_LDAP_PROD_SERVER_SSL_PORT
		};

	/**
	 * Liefert alle unterstützten, verbindungsspezifischen Sicherheitsprotokolle.
	 * 
	 * @return String-Array mit allen aktuell unterstützenden Protokollen
	 */
	public static String[] getConnectionSecurityProtocols()
	{
		return CONNECTION_SECURITY_PROTOCOLS;
	}
	
	/**
	 * Liefert alle g&auml;ngigen Server-Verbindung Protokollarten zur&uuml;ck.
	 * <br><br>
	 * SSL, LDAP v2, LDAP v3, HTTP, HTTPS, FTP
	 * <br><br>
	 * Die letzten drei Protokolle werden seltener angesprochen, der Nutzen liegt hier verst&auml;rkt auf LDAP und SSL.
	 *
	 * @return String-Array, das die oben aufgef&uuml;hrten Server-Verbindungs Protokollarten beinhaltet
	 */
	public static String[] getConnectionProtocols()
	{
		return CONNECTION_PROTOCOLS;
	}
	
	/**
	 * Gibt die LDAP-Servernamen (URI ohne Verbindungsprefixe, wie &quot;ldap://&quot;) zur&uuml;ck.
	 * <br><br>
	 * <u>Servernamen dieser Anwendung:</u>
	 * <blockquote>
	 * 	<ul>
	 * 		<li>{@link #KV_CONNECT_LDAP_PROD_SERVER_URL} - Produktivsystem</li>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_URL} - Referenzsystem 1</li>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_2_URL} - Referenzsystem 2 (Audit)</li>
	 * 	</ul>
	 * </blockquote>
	 *
	 * @return String-Array, das die Servernamen beinhaltet
	 */
	public static String[] getLdapServerUrls() {
		return SERVER_URLS;
	}

	/**
	 * Gibt die Verbindungsportnummern zur&uuml;ck.
	 * <br><br>
	 * <u>Verbindungsortnummern in dieser Anwendung</u>
	 * <blockquote>
	 * 	<ul>
	 * 		<li>{@link #KV_CONNECT_LDAP_PROD_SERVER_PORT} - Produktivsystem Port</li>
	 * 		<li>{@link #KV_CONNECT_LDAP_PROD_SERVER_SSL_PORT} - Produktivsystem SSL-Port</li>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_PORT} - Port f&uuml;r LDAP v3 (f&uuml; alle KV-Telematik Referenzsysteme)</li>
	 * 	</ul>
	 * </blockquote>
	 * 
	 * @return String-Array, das die m&ouml;glichen Server-Portnummern beinhaltet
	 */
	public static String[] getLdapServerPorts() {
		return SERVER_PORTS;
	}

	/**
	 * Gibt die Benutzernamen f&uuml;r eine Anmeldung an einem der Server zur&uuml;ck.
	 * <br><br>
	 * <u>Benutzer 1 in Referenzsystem 1</u>
	 * <blockquote>
	 * 	<ul>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_1}</li>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_1_WITH_MAIL}</li>
	 * 	<ul>
	 * </blockquote>
	 * <u>Benutzer 2 in Referenzsystem 1</u>
	 * <blockquote>
	 * 	<ul>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_2}</li>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_2_WITH_MAIL}</li>
	 * 	</ul>
	 * </blockquote>
	 * <u>Benutzer 3 in Referenzsystem 1</u>
	 * <blockquote>
	 * 	<ul>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_3}</li>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_CN_MEDYS_3_WITH_MAIL}</li>
	 * 	</ul>
	 * </blockquote>
	 * <u>Benutzer 4 in Referenzsystem 2</u>
	 * <blockquote>
	 * 	<ul>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_2_CN_MEDYS}</li>
	 * 		<li>{@link #KV_CONNECT_LDAP_TEST_SERVER_2_CN_MEDYS_WITH_MAIL}</li>
	 * 	</ul>
	 * </blockquote>
	 * <u>Anonymous Benutzer</u>
	 * <blockquote>
	 * 	<ul>
	 * 		<li>{@link #KV_CONNECT_LDAP_USER_ANONYMOUS}</li>
	 * 	</ul>
	 * </blockquote>
	 * 
	 * @return String-Array, das die m&ouml;glichen Anmeldenamen von Benutzern der Referenzysysteme beinhaltet
	 */
	public static String[] getLdapCnNames() {
		return CN_NAMES;
	}
	
	/**
	 * Liefert die absolute Verzeichnispfadangabe zu der ausführenden 
	 * MedysLdapConnection.jar, zw. zu dieser Klasse {@link MedysLdapConnectionConstants}.
	 * 
	 * @return absolute Pfadangabe zu dieser Klasse oder zu der MedysLdapConnection.jar, sonst NULL
	 */
	public static String getExecutionPath()
	{
		String absolutePath = null; 
	    
		ProtectionDomain domain = MedysLdapConnectConsoleSuite.class.getProtectionDomain();
		
		if(domain != null)
		{
			CodeSource codeSource = domain.getCodeSource();
		    
			if(codeSource != null)
			{
				URL location = codeSource.getLocation();
				
				if(location != null)
				{
					absolutePath = location.getPath();
					
					if(absolutePath != null)
					{
						absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
					    absolutePath = absolutePath.replaceAll("%20"," "); 
					}
				}
			}
		}
		return absolutePath;
	}
}
