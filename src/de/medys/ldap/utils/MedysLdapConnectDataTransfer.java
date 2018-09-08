package de.medys.ldap.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Klasse f&uuml;r den Datenaustausch zwischen einer Java-Anwendung und MEDYS.
 * <br><br>
 * Die Daten werden in eine vorgesehene, fest benannte Txt-Datei, die man dann in MEDYS auslesen kann.
 * <br><br>
 * <u><b>Notiz</b></u>
 * <blockquot>
 * Diese Klasse als Standard-Routine f&uuml;r den Datenaustausch Java zu MEDYS umschreiben 
 * und eventuell auch den R&uuml;ckschluss zu programmieren (MEDYS zu Java)
 * </blockquot>
 * @see #MEDYS_LDAP_OUTPUT_FILE_NAME
 * @see #writeToFile(File, String, boolean)
 * @author Hayri Emrah Kayaman, MEDYS GmbH 2015
 */
public class MedysLdapConnectDataTransfer {

	/*
	 * Vorgabe aus MEDYS:
	 * ------------------
	 * 
	 * in "Tools.oJavaJarStarter.$starteJar(..)" Methode
	 * 
	 * wird mit "lvScriptObj.$setReturnDateiName(con(ivJarName,'_output'))"
	 * 
	 * die Datei/Dateiname festgelegt wo man sich die Ergebnisse  (Outputs)
	 * 
	 * von MedysLdapConnection-Java abgreifen und in MEDYS verwerten kann
	 *  
	 */
	private static final String MEDYS_LDAP_OUTPUT_FILE_NAME = 
									"MedysLdapConnection.jar_output.txt";
	
	// standard: wenn nicht gesetz in der Ausführung, sonst Verzeichnisangabe
	//
	private static String transferFilePath = "";
	
	/*
	 * Hilfsdatei, die die Suchparameter (mit Umlauten) aus MEDYS erhält
	 */
	private static final String MEDYS_HELP_FILE_NAME  = "MedysLdapConnection_Suchparameter.txt";
	
	// Verzeichnispfad, wo sich die Hilfsdatei befindet (wird in der Startup von LdapConncetionFactory gesetzt)
	private static String helpFilePath = "";
	
	/**
	 * Schreibt neuen Textinhalt in eine Textdatei.
	 * <br><br>
	 * Falls die angegebene Datei nicht existiert, so wird eine Textdatei 
	 * erzeugt und daraufhin beschrieben.
	 * <br><br>
	 * Die erzeugt Textdatei befindet sich falls nicht anders angegeben 
	 * im Wurzel-Verzeichnis der jeweiligen aufrufenden Anwendung.
	 * <br><br>
	 * Ist der Parameter &quot;<i>append</i>&quot; <code>FALSE</code> 
	 * so wird der gesamte vorherige Textinhalt der Textdatei mit dem 
	 * Textinhalt &uuml;berschrieben (L&ouml;sch-Funktion)
	 * 
	 * @param file die Textdatei
	 * @param newEntry der neue Textinhalt
	 * @param append </br>wenn <code>TRUE</code>, dann wird der neue Textinhalt 
	 * 				 an den alten Textinhalt angeh&auml;ngt,</br>
	 * 				 bei <code>FALSE</code> wird der Inhalt der Textdatei 
	 * 				 gel&ouml;scht
	 */
	public static void writeToFile(
			File file, String newEntry, boolean append) 
	{
		try 
		{
			if((file != null) 
					&& (!file.isDirectory()) 
						&& (file.getName().endsWith(".txt")))
			{	
				PrintWriter pw = 
						new PrintWriter(
								new OutputStreamWriter(
										new FileOutputStream(file, true), "UTF-8"));
				
				if(!newEntry.isEmpty())
					
				{
					pw.write(newEntry);
				}
				
				pw.flush();
				
				pw.close();
				
			}
			else 
			{
				System.out.println("Schreibfehler - MedysLDapConnectDataTranfser :" 
						+ " transferFile existiert nicht !");
			}
		}
		catch(Exception except)
		{
			except.printStackTrace();
		}
	}

	/**
	 * reads the bytes from a File-Object in a FileInputStream
	 * 
	 * @param file to be read
	 * @return the content of a file as a byte-Array
	 */
	public static byte[] readFileToBytes(File file) 
	{
		if (file.isDirectory())
		{
	    	throw new RuntimeException("Unsupported operation, file "
	    			+ file.getAbsolutePath() + " is a directory");
		}
	    if (file.length() > Integer.MAX_VALUE)
	    {
	    	throw new RuntimeException("Unsupported operation, file "
	    			+ file.getAbsolutePath() + " is too big");
	    }
	    
	    Throwable pending = null;
	    
	    FileInputStream in = null;
	    
	    final byte buffer[] = new byte[(int) file.length()];
	    
	    try
	    {
	    	in = new FileInputStream(file);
	    	in.read(buffer);
	    }
	    catch (Exception e)
	    {
	    	pending = new RuntimeException("Exception occured on reading file "
	    			+ file.getAbsolutePath(), e);
	    }
	    finally
	    {
	    	if (in != null)
	    	{
	    		try
	    		{
	    			in.close();
	    		}
	    		catch (Exception e)
	    		{
	    			if (pending == null)
	    			{
	    				pending = new RuntimeException(
	    					"Exception occured on closing file" 
	                             + file.getAbsolutePath(), e);
	    			}
	    		}
	    	}
	    	if (pending != null) {
	    		throw new RuntimeException(pending);
	    	}
	    }
	    return buffer;
	}
	
	/**
	 * writes the given bytes into a file
	 * 
	 * @param bytes
	 * @param destFile
	 * @throws IOException
	 */
	public static void writeBytesToFile(byte[] bytes, File destFile) throws IOException
	{
		if(destFile.exists())
		{
			if(!destFile.isDirectory())
			{
				byte[] previousContent = readFileToBytes(destFile);
				
				byte[] newContent = new byte[previousContent.length + bytes.length];
				
				newContent = Arrays.copyOf(previousContent, previousContent.length);
				
				newContent = Arrays.copyOf(bytes, bytes.length);
				
				destFile.delete();
				
				FileWriter fw = new FileWriter(destFile);
				fw.close();
				
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
				
				bos.write(newContent);
				
				bos.flush();
				
				bos.close();
				
			}
			else
			{
				throw new IOException("IOException in MedysLdapConnectDataTransfer.writeBytesToFile(..):\n"
										+ destFile.getName() + " is a Directory !");
			}
		}
	}
	
	/**
	 * Liest den Inhalt einer Datei zweilenweise aus und gibt diesen zur&uuml;ck.
	 * 
	 * @param file die Datei, die ausgelesen werden soll
	 * @return der Inhalt der Datei
	 */
	public static String readFromFile(File file) 
	{
		String str = "", retStr = "";
		
		try 
		{
			if(file.exists())
			{
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
			
				while((str = br.readLine()) != null)
				{
					retStr += str;
				}
				
				fr.close();
				br.close();
			}
			else
			{
				System.out.println(
						"\n\nLesefehler - "
						+ "MedysLDapConnectDataTranfser : "
						+ "transferFile existiert nicht !");
			}
		}
		catch(Exception except)
		{
			except.printStackTrace();
		}
		
		return retStr;
	}
	
	/**
	 * internal use, sets the absoulte path to a transfer file objekt
	 * 
	 * @param path
	 */
	public static void setTransferFilePath(String path)
	{
		transferFilePath = path;
	}
	
	/**
	 * Liefert die Transferdatei zur&uuml;ck, welche die Inhalte 
	 * f&uuml;r den Datenaustausch von Java zu MEDYS beinhaltet.
	 * 
	 * @return transferFile - {@link java.io.File}ys
	 */
	public static File getTransferFile()
	{
		if((transferFilePath.length()>0)
			&& !transferFilePath.equals("."))
		{
			return new File(
				MedysLdapConnectDataTransfer.transferFilePath +
				System.getProperty("file.separator") + MEDYS_LDAP_OUTPUT_FILE_NAME);
		}
		else
		{
			return new File(MEDYS_LDAP_OUTPUT_FILE_NAME);
		}
	}
	
	/**
	 * Setzt den Pfad zur Hilfsdatei
	 * 
	 * @param path
	 */
	public static void setSuchparameterHelpFilePath(String path)
	{
		helpFilePath = path;
	}
	
	/**
	 * @return liefert die Suchparameter-Hilfsdateis
	 */
	public static File getSuchparameterHelpFile()
	{
		if(MedysLdapConnectDataTransfer.helpFilePath.length()>0)
		{
			return new File(MedysLdapConnectDataTransfer.helpFilePath + System.getProperty("file.separator") + MEDYS_HELP_FILE_NAME);
		}
		else
		{
			return new File(MEDYS_HELP_FILE_NAME);
		}
	}
	
	/**
	 * L&ouml;scht den Inhalt der Textdatei, welcher f&uuml; den
	 * Datenaustausch zwischen Java und MEDYS fungiert.
	 * 
	 * @see #getTransferFile()
	 */
	public static void clearMedysTextFileContent()
	{
		File file = MedysLdapConnectDataTransfer.getTransferFile();
		
		if(file != null)
		{
			file.delete();
			
			writeToFile(file, "", false);
		}
	}
	
}
