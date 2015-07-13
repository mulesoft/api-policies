package org.mule.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * a helper class for un/zipping files
 * @author Miroslav Rusin
 *
 */
public class Zipper {
	
	/**
	 * unzips a file to a specified folder
	 * @param zipFile a zipFile to unzip
	 * @param outputFolder a location to unzip to
	 */
	public static void unZip(File zipFile, String outputFolder){
		 
	     final byte[] buffer = new byte[1024];
	 
	     try{
	 
	    	//create output directory is not exists
	    	final File folder = new File(outputFolder);
	    	if(!folder.exists()){
	    		folder.mkdir();
	    	}
	 
	    	//get the zip file content
	    	final ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	 
	    	while (ze != null) {
	    	   	
	    	   final String fileName = ze.getName();
	           final File newFile = new File(outputFolder + File.separator + fileName);
	 	           
	           //create all non exists folders
	           //else you will hit FileNotFoundException for compressed folder
	           
	           new File(newFile.getParent()).mkdirs();
	           
	           if (!ze.isDirectory()) {		           		
	        	   final FileOutputStream fos = new FileOutputStream(newFile);             		 
		           int len;
		           
		           while ((len = zis.read(buffer)) > 0) {
		        	   fos.write(buffer, 0, len);
		           }		 
		           fos.close();   
	           }
	           ze = zis.getNextEntry();
	    	}
	 
	        zis.closeEntry();
	    	zis.close();
	 	    	
	    }catch(final IOException ex){
	       ex.printStackTrace(); 
	    }
   }
	
   /**
    * zips a folder to a file	
    * @param sourceFolder a source folder to zip
    * @param outputPath a location to zip the folder to
    */
   public static void zip(String sourceFolder, String outputPath){
	   
	   try{
		    		    
			final FileOutputStream fos = new FileOutputStream(outputPath);
			final ZipOutputStream zos = new ZipOutputStream(fos);
			final File folder = new File(sourceFolder);
			
			addZipEntries(folder.listFiles(), zos, sourceFolder);
						
			zos.close();			
		
		} catch(final IOException ex){
		   ex.printStackTrace();
		}
   }
   
   /**
    * add zip entry to zip archive and calls itself recursively
    * @param files an array of files to iterate through
    * @param zos ZipOutputStream instance to write to
    * @param preffixToRemove a prefix from the file path to ommit so the file structure is relative to the archive
    * @throws IOException if any error occurs
    */
   private static void addZipEntries(File[] files, ZipOutputStream zos, String preffixToRemove) throws IOException {
	   final byte[] buffer = new byte[1024];
	   final File a = new File(preffixToRemove);
	   
	   for (final File file : files) {
	        if (file.isDirectory()) {
	        	final String relativePath = a.toURI().relativize(file.toURI()).getPath();	        	
				zos.putNextEntry(new ZipEntry(relativePath));
				
	            addZipEntries(file.listFiles(), zos, preffixToRemove); // Calls same method again.
	        } else {
	        	final String relativePath = a.toURI().relativize(file.toURI()).getPath();	        	
				zos.putNextEntry(new ZipEntry(relativePath));
				final FileInputStream in = new FileInputStream(file.getAbsolutePath());
			
				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
			
				in.close();
				zos.closeEntry();
			
	        }
	    }
   }
      
}
