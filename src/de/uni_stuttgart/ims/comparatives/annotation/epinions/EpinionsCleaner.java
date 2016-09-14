// (c) Wiltrud Kessler
// 07.03.2014
// This code is distributed under a Creative Commons
// Attribution-NonCommercial-ShareAlike 3.0 Unported license 
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package de.uni_stuttgart.ims.comparatives.annotation.epinions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


/**
 * Take the original epinions file provided by
 * Branavan, Chen, Eisenstein, Barzilay (2009) 
 * in "Learning Document-Level Semantic Properties from Free-Text Annotations"
 * downloaded from
 * http://groups.csail.mit.edu/rbg/code/precis/
 * 
 * Convert to valid XML and UTF-8 encoding.
 * 
 * @author kesslewd
 *
 */
public class EpinionsCleaner {
   
   
   /**
    * Take the original epinions file provided by
    * Branavan, Chen, Eisenstein, Barzilay (2009) 
    * in "Learning Document-Level Semantic Properties from Free-Text Annotations"
    * downloaded from
    * http://groups.csail.mit.edu/rbg/code/precis/
    * 
    * Convert to valid XML and UTF-8 encoding.
    * (Add missing root tag, remove wrong closing tags,
    * remove invalid characters, add CDATA tags)
    * 
    * @author kesslewd
    * 
    * @param args [1] input file name, [2] output file name
    *
    */
   public static void main(String[] args) {


      // ===== GET USER INPUT =====
      
      if (args.length < 2) {
         System.err.println("Usage: EpinionsCleaner <input file name> <output file name>");
         System.exit(1);
      }
      
      String inputFileName = args[0];
      String outputFileName = args[1];
      System.out.println("Processing...");

      
      
      // === OPEN FILES ===

      // Open input file (broken XML) and convert to UTF-8.
      // Assume input encoding as Windows-1250 based on the occurrence of some 
      // ' and " characters encoded as hex(92) hex(93).
      BufferedReader inputReader = null;
      try {
         FileInputStream in = new FileInputStream(inputFileName);
         inputReader = new BufferedReader(new InputStreamReader(in, "Windows-1250"));
      } catch (FileNotFoundException e) {
         System.out.println("Error, input file not found: " + e.getMessage());
         System.out.println("Abort.");
         return;
      } catch (UnsupportedEncodingException e) {
         System.out.println("Error, something is wrong with the encoding used: " + e.getMessage());
         System.out.println("Abort.");
         // Should never happen, 'Windows-1250' is known.
         return;
      }
           
      // Open output file (good XML)
      BufferedWriter outputWriter = null;
      try {
         FileOutputStream out = new FileOutputStream(outputFileName);
         outputWriter = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
      } catch (IOException e) {
         System.out.println("Error when creating output file: " + e.getMessage());
         System.out.println("Abort.");
         return;
      }
      
      

      // === ADD ROOT ELEMENT AND XML OPENING ===
      
      // Write XML opening to output file
      writeLine(outputWriter, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      
      // Add opening <products> tag (original file contains no root element)
      writeLine(outputWriter, "<products>");
           
      
      
      // === COPY CONTENT ===
      try {
         String strLine = inputReader.readLine();
         while (strLine != null) {
            
            // Delete control bytes that would kill the XML reader.
            // \p{Cc} are all control characters, including line breaks and tabs.
            // Line breaks and tabs should be kept, so take the intersection of
            // the complement of the whitespace category with the controls.
            strLine = strLine.replaceAll("[\\p{Cc}&&[^\\p{Space}]]", "");
            
            // Remove misplaced tags (unmotivated closing tag </review> after an opening tag </text>)
            // Ordinary </review> tags always occur on a line by themselves,
            // so just delete all others.
            strLine = strLine.replaceAll("(.{4})</review>", "$1");
            
          
            // Add <![CDATA[ and ]]> around the entries in element <text> because content is HTML
            strLine = strLine.replaceAll("<text>", "<text><![CDATA[");
            strLine = strLine.replaceAll("</text>", "]]></text>");

            // Write line to output
            writeLine(outputWriter, strLine);

            // Get next line
            strLine = inputReader.readLine();
         }

      } catch (IOException e) {
         System.out.println("Error while reading input file: " + e.getMessage());
         System.out.println("Abort.");
      }
      
      

      // === ADD CLOSING TAG FOR ROOT ELEMENT ===
      writeLine(outputWriter, "</products>");
      
      
      
      // === CLEANUP ===
      System.out.println("...done.");
      try {
         inputReader.close();
      } catch (IOException e) {
      }
      try {
         outputWriter.close();
      } catch (IOException e) {
      }
      
   }

   
   
   /**
    * Write a line to the file writer that is given.
    * @param out file to write to
    * @param line String to write (linebreak will be added at the end)
    */
   private static void writeLine (BufferedWriter out, String line) {
      // Do nothing if the writer is null
      if (out == null)
         return;
      
      // Otherwise write line + an empty line
      try {
         out.write(line);
         out.newLine();
      } catch (IOException e) {
         System.out.println("Error when writing to output file: " + e.getMessage());
      }
   }
   


}
