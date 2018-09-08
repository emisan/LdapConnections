package de.medys.ldap.console;

import java.io.File;

import de.medys.ldap.utils.LdapConnectionFactory;
import de.medys.ldap.utils.MedysLdapConnectionConstants;

/**
 * 
 * Ausf&uuml;hrung der LDAP-Serververbindung zu KV-Telematik LDAP-Referenzsystemen<br><br>
 * in Anonymous-Modi oder Benutzer-spezifischen Modi (SSL+ Benutzername + Passwort, etc.)
 * <br><BR>
 * <u><b>Info</b></u>
 * <blockquot>
 * <ul>
 * 	<li>{@link de.medys.ldap.MedysLdapConnection} f&uuml;r die Parameter</li>
 * </ul>
 * </blockquot>
 * <br>
 * <u><b>zus&auml;tzliche Info zu den Paramteren</b></u>
 * <blockquot>
 *  * Falls einer der Parameter ein leerer String ist, so werden es in
 * MEDYS mit dem Wert <i>NO_PARAM</i> initialisiert und an MedysLdapConnection.jar &uuml;bergeben.
 * <br><br>
 * Diese Notwendigkeit ergibt sich aus der Ausarbeitung, da&szlig; das Apple-Skript Kommando 
 * <i>do shell script</i> Probleme mit leeren String-Parametern bereitet.
 * <br>
 * Daher kann zwar Java-seitig eine leere String-&Uuml;berpr&uuml;fung stattfinden, 
 * jedoch w&auml;hrend der Parameter&uuml;bergabe aus Medys an das Apple-Skript nicht gew&auml;hrleistet 
 * werden.
 * </blockquot>
 *  
 * @author Hayri Emrah Kayaman, MEDYS GmbH 2015
 *
 */
public class MedysLdapConnectConsoleSuite {
	
	/**
	 * Erzeugt eine neue Instanz von {@link MedysLdapConnectConsoleSuite}</br>
	 * mit den Startparametern der {@link de.medys.ldap.MedysLdapConnection}-Anwendung
	 * <br><br>
	 * F&uuml; die Startup-Parameter, siehe: {@link de.medys.ldap.MedysLdapConnection}
	 * 
	 * @param startupArguments die Startup-Parameter
	 */
	public MedysLdapConnectConsoleSuite(String[] startupArguments)
	{
		LdapConnectionFactory ldapConnectionFactory = 
				new LdapConnectionFactory();
		
		String actualPath = MedysLdapConnectionConstants.getExecutionPath();
		
		File parentFolder = new File(actualPath).getParentFile();
		
		if(parentFolder != null)
		{
			// <pfad>/BIN
			if(parentFolder.isDirectory())
			{
				actualPath = parentFolder.getPath();
				
				File parentOfparentFolder = new File(actualPath);
				
				// nur <pfad>
				if(parentOfparentFolder != null)
				{
					actualPath = parentOfparentFolder.getPath();
				}
			}
		}
		
		if (startupArguments.length > 0)
		{
			// für den Debugger Modus
			//
			if (startupArguments.length == 8)
			{
				ldapConnectionFactory.retrieveLdapInformation(
						startupArguments[0], startupArguments[1],
						startupArguments[2], startupArguments[3],
						startupArguments[4], startupArguments[5],
						startupArguments[6], actualPath);
			} 
			else 
			{
				// Produktiv-Modus
				//
				ldapConnectionFactory.retrieveLdapInformation(
						startupArguments[0], startupArguments[1],
						startupArguments[2], startupArguments[3],
						startupArguments[4], actualPath);
			}
		}
	}
}
