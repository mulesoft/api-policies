package org.mule.ui;

import static org.mule.api.Constants.API_ID;
import static org.mule.api.Constants.API_NAME;
import static org.mule.api.Constants.API_VERSION_ID;
import static org.mule.api.Constants.API_VERSION_NAME;
import static org.mule.api.Constants.APP_NAME;
import static org.mule.api.Constants.EXPORT;
import static org.mule.api.Constants.EXPORT_PATH;
import static org.mule.api.Constants.HELP;
import static org.mule.api.Constants.KEY_FILE;
import static org.mule.api.Constants.KEY_PASSWORD;
import static org.mule.api.Constants.PASSWORD;
import static org.mule.api.Constants.PROXY;
import static org.mule.api.Constants.SSL_COMMAND;
import static org.mule.api.Constants.USER;

import java.io.Console;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.api.ScriptCommands;
import org.mule.api.Scriptable;
import org.mule.scripts.SSLPostProcessing;
import org.mule.scripts.SSLPostProcessingWithDownloadFromID;
import org.mule.scripts.SSLPostProcessingWithDownloadFromNames;

/**
 * Command-line interface to use for executing EDG Scripts
 * @author Miroslav Rusin
 *
 */
public class Main {

	public static final List<String> KEY_WORDS = Arrays.asList(new String[] {SSL_COMMAND, PROXY, KEY_FILE, KEY_PASSWORD, EXPORT, API_ID, API_VERSION_ID, API_NAME, API_VERSION_NAME });	
	public static final Map<String, String> COMMAND_ARGUMENTS = new HashMap<String, String>();
	
	/**
	 * Accepts input arguments and executes logic based on their values. It provides error messages in case of missing or invalid input parameters 
	 * @param args
	 */
	public static void main(String[] args) {
		
		final String[] copyArgs = Arrays.copyOf(args, args.length);
		
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
			if (HELP.equals(args[i]))
				printHelp();			
		} 
		
		final ScriptCommands command = extractCommand(args);
		extractArguments(args, copyArgs);
		COMMAND_ARGUMENTS.put(EXPORT, EXPORT_PATH);
		
		throwExceptionIfMissingArgument();
		
		Scriptable script = null;
		switch (command){
			case SSLENDPOINT :
				if (!notProxyPresent()){
					script = new SSLPostProcessing();					
				}
				if (!notIdsPresent()){
					script = new SSLPostProcessingWithDownloadFromID(new SSLPostProcessing());					
				}
				if (!notNamesPresent()){
					script = new SSLPostProcessingWithDownloadFromNames(new SSLPostProcessingWithDownloadFromID(new SSLPostProcessing()));					
				}
				break;
		}
		
		if (script == null)
			throw new IllegalArgumentException("No such command or no insufficient arguments provided. For help, add the flag -help");
		
		try {
			script.process(COMMAND_ARGUMENTS);
		} catch (final javax.ws.rs.ProcessingException pe){
			throw new IllegalStateException("You are probably disconnected from Internet", pe);
		}
			catch (final Exception e) {		
			e.printStackTrace();
		}
	}

	/**
	 * evaluates input arguments so that one of three possibilities is chosen: 
	 * 1. proxy
	 * 2. api names
	 * 3. api ids 
	 */
	private static void throwExceptionIfMissingArgument() {
		if (notIdsPresent()){
			if (notProxyPresent()){
				if (!notNamesPresent()){
					throwException(API_NAME);
					throwException(API_VERSION_NAME);
					askForCredentials();
					throwException(USER);
					throwException(PASSWORD);					
				}
				else
					throw new IllegalArgumentException("You have not provided " + PROXY + ", " + API_ID +" or " + API_NAME + " argument. For help, add the flag -help");		
			} 
									
		} 
		else {		// one of api ids is present			
			throwException(API_ID);
			throwException(API_VERSION_ID);
			askForCredentials();
			throwException(USER);
			throwException(PASSWORD);					
		}
		
		throwException(KEY_FILE);
		throwException(KEY_PASSWORD);	
	}

	private static void askForCredentials(){
		final Console console = System.console();
		COMMAND_ARGUMENTS.put(USER, console.readLine("Username: "));		
		COMMAND_ARGUMENTS.put(PASSWORD, new String(console.readPassword("Password: ")));
	}
	/**
	 * parametric method to throw an exception in case of missing argument
	 * @param key key of the argument
	 */
	private static void throwException(String key) {
		if (!COMMAND_ARGUMENTS.containsKey(key))
			throw new IllegalArgumentException("You have not provided " + key + " argument. For help, add the flag -help");
	}

	/**
	 * test for missing proxy argument
	 * @return true if the argument is not present
	 */
	private static boolean notProxyPresent() {
		return !COMMAND_ARGUMENTS.containsKey(PROXY);
	}

	/**
	 * test for missing ids argument 
	 * @return true if the argument is not present 
	 */
	private static boolean notIdsPresent() {
		return !COMMAND_ARGUMENTS.containsKey(API_ID) && !COMMAND_ARGUMENTS.containsKey(API_VERSION_ID);
	}
	
	/**
	 * test for missing names argument
	 * @return true if the argument is not present
	 */
	private static boolean notNamesPresent() {
		return !COMMAND_ARGUMENTS.containsKey(API_NAME) && !COMMAND_ARGUMENTS.containsKey(API_VERSION_NAME);
	}
	
	/**
	 * creates a map of input argument key/value pairs
	 * @param args
	 */
	private static void extractArguments(String[] args, String[] copyArgs) {
		for (int i = 0; i < args.length; i++) {
			for (int j = 1; j < KEY_WORDS.size(); j++){
				processArgument(args, copyArgs, i, j);
			}
		}
		
	}

	/**
	 * tests if a next value exists
	 * @param i index in array
	 * @param args array of strings
	 * @return true if the next entry exists
	 */
	private static boolean existsNextEntry(int i, String[] args) {
		return i + 1 < args.length;
	}
	
	/**
	 * creates an entry for the argument and its value
	 * @param args String array of input arguments
	 * @param index index in String array
	 * @param key_index index in key words array
	 */
	private static void processArgument(String[] args, String[] copyArgs, int index, int key_index){
		if (KEY_WORDS.get(key_index).equals(args[index]) && existsNextEntry(index, args)){
			if (!KEY_WORDS.contains(args[index + 1])){		
				COMMAND_ARGUMENTS.put(KEY_WORDS.get(key_index), copyArgs[index + 1]);
			}
		}
		
	}

	/**
	 * extracts a command from input argument array
	 * @param args String array of arguments
	 * @return a member of ScriptCommands
	 * @throws throws IllegalArgumentException if the match was not found
	 */
	private static ScriptCommands extractCommand(String[] args){
		ScriptCommands command = null;
		if (args.length == 0)
			throw new IllegalArgumentException("You have not provided any arguments. For help, add the flag -help");
		
		if (SSL_COMMAND.equals(args[0])){
			command = ScriptCommands.SSLENDPOINT;			
		}
		else {
			throw new IllegalArgumentException("You have not provided script name.");
		}
		return command;
	}
	
	/**
	 * prints help to console
	 */
	private static void printHelp(){
		System.out.println("Usage: java -jar " + APP_NAME + " " + SSL_COMMAND + " [args...]");
		System.out.println("\t" + KEY_FILE + "\t\t[mandatory] location of the keystore containing certificates");
		System.out.println("\t" + KEY_PASSWORD + "\t\t[mandatory] password to the keystore");
		System.out.println("\t" + PROXY + "\t\t\tlocation of the proxy application zip file");
		System.out.println("\t" + API_ID + "\t\t\tAPI ID acquired from Anypoint Platform");
		System.out.println("\t" + API_VERSION_ID + "\t\tAPI Version ID acquired from to Anypoint Platform");		
		System.out.println("\t" + API_NAME + "\t\tAPI Name acquired from Anypoint Platform");
		System.out.println("\t" + API_VERSION_NAME + "\tAPI Version Name acquired from to Anypoint Platform");
				
		System.exit(0);
	}

}
