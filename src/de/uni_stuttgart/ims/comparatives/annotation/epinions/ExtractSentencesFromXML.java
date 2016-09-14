// (c) Wiltrud Kessler
// 02.12.2013
// This code is distributed under a Creative Commons
// Attribution-NonCommercial-ShareAlike 3.0 Unported license 
// http://creativecommons.org/licenses/by-nc-sa/3.0/


package de.uni_stuttgart.ims.comparatives.annotation.epinions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.uni_stuttgart.ims.comparatives.nlp.SentenceSplitter;
import de.uni_stuttgart.ims.comparatives.nlp.SentenceSplitterStanford;
import de.uni_stuttgart.ims.comparatives.nlp.TextSpan;


/**
 * Extract all sentences and texts from review in XML file in format
 * (Branavan, Chen, Eisenstein, Barzilay 2009)
 * 
 * @author kesslewd
 *
 */
public class ExtractSentencesFromXML {
   

   /**
    * 
    * Extract all sentences and texts from review in XML file in format
    * (Branavan, Chen, Eisenstein, Barzilay 2009)
    * 
    * Two input files:
    * - xmlEpinionsFile: Reviews in XML format
    * - idIgnoreFile: review ids that should be ignored
    *    Format: 1 id per line
    * 
    * Two output files:
    * - outputSentences: sentences as split by sentence splitter, tokenized
    *    Format: id \t sentence tokenized, one sentence per line
    * - outputTexts: complete text of all reviews, one sentence per line, separated by empty lines    * 
    *    Format: id \n sentence tokenized, one sentence per line\n \n
    *   
    * @author kesslewd
    * 
    * @param args [1] XML input file name, 
    *    [2] file that contains the ids of reviews to be ignored
    *    [3] output file name for sentences
    *    [4] (optional) output file name for texts
    */
   public static void main(String[] args) {
      
      
      // ===== GET USER INPUT =====
      
      if (args.length < 3) {
         System.err.println("Usage: ExtractSentencesFromXML <input XML file name>" +
         		" <ignore ids file name> <output sentences file name> <output texts file name>");
         System.exit(1);
      }
      
      System.out.println("Processing...");
      String xmlEpinionsFile = args[0];
      String idIgnoreFile = args[1];
      String outputSentences = args[2];
      String outputTexts = null;
      if (args.length > 3) {
         outputTexts = args[3];
      }

      
      
      // === GET REVIEWS TO IGNORE ===
      ArrayList<String> ignoreList = new ArrayList<String>();

      // Open input file
      BufferedReader br = null;
      try {

         FileInputStream in = new FileInputStream(idIgnoreFile);
         br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
      
         // Put the ids into the list
         String strLine = "";
         while (strLine != null) {
            strLine = br.readLine();
            if (strLine != null)
               ignoreList.add(strLine.trim());
         }
         br.close();
         
         System.out.println("Ignore reviews with ids: " + ignoreList);
         
      } catch (FileNotFoundException e) {
         System.out.println("Error, file with ids to ignore not found: " + e.getMessage());
         System.out.println("Ignore list will be set to empty list.");
      } catch (IOException e) {
         System.out.println("Error while reading file with ids to ignore: " + e.getMessage());
         System.out.println("Ignore list might be incomplete.");
      }

      
      
      // === OPEN FILES ===
      
      // Open output file (text)
      BufferedWriter outText = null;
      if (outputTexts != null) {
         System.out.println("Output texts to file " + outputTexts);
         try {
            FileWriter fstream1 = new FileWriter(outputTexts);
            outText = new BufferedWriter(fstream1);
         } catch (IOException e) {
            System.out.println("Error when creating output file for texts: " + e.getMessage());
            System.out.println("Abort.");
            return;
         }
      }

      // Open output file (sentences)
      BufferedWriter outSentences;
      System.out.println("Output sentences to file " + outputSentences);
      try {
         FileWriter fstream1 = new FileWriter(outputSentences);
         outSentences = new BufferedWriter(fstream1);
      } catch (IOException e) {
         System.out.println("Error when creating output file for sentences: " + e.getMessage());
         System.out.println("Abort.");
         return;
      }
      
     
      
      // === CREATE XML READER ===
      XMLReader xr = null;
      try {
         xr = XMLReaderFactory.createXMLReader();
      } catch (SAXException e) {
         System.out.println("Error when creating XML reader: " + e.getMessage());
         System.out.println("Abort.");
         return;
      }
      EpinionsReviewHandler handler = new EpinionsReviewHandler();
      handler.setOutputTexts(outText);
      handler.setOutputSentences(outSentences);
      handler.setIdIgnoreList(ignoreList);
      xr.setContentHandler(handler);
      xr.setErrorHandler(handler);
      
      
      
      // === READ FILE ===
      try {
         FileReader r2 = new FileReader(xmlEpinionsFile);
         xr.parse(new InputSource(r2));
      } catch (FileNotFoundException e) {
         System.out.println("Error, XML file not found: " + e.getMessage());
         System.out.println("Abort.");
      } catch (IOException e) {
         System.out.println("Error while reading XML file: " + e.getMessage());
         System.out.println("Abort.");
      } catch (SAXException e) {
         System.out.println("Error while reading XML file: " + e.getMessage());
         System.out.println("Abort.");
      }

      

      // === CLEANUP ===
      System.out.println("...done.");
      if (outText != null) {
         try {
            outText.close();
         } catch (IOException e) {
         }
      }
      try {
         outSentences.close();
      } catch (IOException e) {
      }
      
   }

   
   
   
   /**
   Read the XML files in format
   (Branavan, Chen, Eisenstein, Barzilay 2009)
   
   <review>
      <id>20</id>
      <title>&quot;Entry Level&quot;</title>
      <date>2006/8/25</date>
      <feature_ranks>
         <feature>Battery Life</feature><rank>5.0</rank>
         <feature>Portability</feature><rank>4.0</rank>
         <feature>Clarity</feature><rank>5.0</rank>
         <feature>Durability</feature><rank>4.0</rank>
         <feature>Product Rating</feature><rank>5.0</rank>
      </feature_ranks>
      <procons>
         <pro>carl zeiss lens</pro>
         <con>minolta would have attracted more photographers</con>
      </procons>
      <text>
      Using the Sony name will probably give it an "entry level" status. It's a great toy for people who will buy their first DSLR. But dont expect the camera to attract Canon or Nikon enthusiasts.
      </text>
   </review>
   
   The file must be in UTF-8.
   
   Split into sentences.
   Extract all sentences with ID formed by review-id + running sentence number 
   
    * @author kesslewd
    *
    */
   public static class EpinionsReviewHandler extends DefaultHandler {
   
      // Debug/bookkeeping
      private int numberReviews = 0;
      private int numberReviewsIgnored = 0;
      private int numberSentences = 0;
   
      // Sentence splitter
      private SentenceSplitter sentenceSplitter;
      
      // Output files
      private BufferedWriter outSentences;
      private BufferedWriter outText;
      
      // Document-level
      private boolean inReview = false;
      private boolean inText = false;
      private boolean inID = false;
      
      // Review-level
      private String text = "";
      private String id = "";
      private List<String> idIgnoreList;
      
      
      
      /**
       * Initialize sentence splitter.
       */
      public EpinionsReviewHandler () {
         // Initialize sentence splitter (Stanford)
         sentenceSplitter = new SentenceSplitterStanford();
      }
   
   
      // Output files
         
   
      /**
       * Output complete texts of reviews to this file.
       * Format:
       * id\n
       * sentence 1\n
       * ...
       * sentence n\n
       * 
       * id\n
       * @param outputFile
       */
      public void setOutputTexts(BufferedWriter outputFile) {
         this.outText = outputFile;
      }      
      
   
      /**
       * Output all sentences of reviews to this file,
       * one sentence per line,
       * id \t sentence tokenized.
       * @param outputFile
       */
      public void setOutputSentences(BufferedWriter outputFile) {
         this.outSentences = outputFile;      
      }
   
      
      /**
       * List of review ids to ignore
       * There will be no sentences extracted from reviews on this list.
       * @param idIgnoreList
       */
      public void setIdIgnoreList(List<String> idIgnoreList) {
         this.idIgnoreList = idIgnoreList;
      }
   
   
      /**
       * Write a line to the file given.
       * @param out file to write to
       * @param line String to write (linebreak will be added at the end)
       */
      private void writeLine (BufferedWriter out, String line) {
         if (out == null)
            return;
         
         try {
            out.write(line);
            out.newLine();
         } catch (IOException e) {
            System.out.println("Error when writing to output file: " + e.getMessage());
         }
      }
      
      
      
      // Process XML document
      
      
      /**
      Called at the start of an element.
      Do things for 'review', 'text', 'id'
      Set flags, reset/update variables.
       **/
      public void startElement (String uri, String name, String qName, Attributes atts) 
               throws SAXException {
         
         // Review
         if (name.equalsIgnoreCase("review")) {
            this.inReview = true;
            this.numberReviews += 1;
            this.text = "";
            this.id = "";
            // cannot ignore reviews here, because we don't
            // have the id yet
   
         // Review text
         // There might be uses of text outside of reviews, 
         // so check if we are in a review.
         } else if (this.inReview & name.equalsIgnoreCase("text")) {
            this.inText = true;
            
         // Review ID.
         // Products also have an id with the same tag, 
         // so check if we are in a review.
         } else if (this.inReview & name.equalsIgnoreCase("id")) { 
            this.inID = true;
         }
      }
   
   
      /**
      Called at the end of an element.
      Do things for 'review', 'text', 'id'
      Delete flags, reset/update variables.
       **/
      public void endElement (String uri, String name, String qName) 
               throws SAXException {
         
         // Review
         // Have read everything in a review,
         // pass on to further processing.
         if (name.equalsIgnoreCase("review")) {
            this.inReview = false;
            
            // ignore reviews not about the topic at hand or otherwise bad
            // (manually determined and given in list this.idIgnoreList)
            if (this.idIgnoreList.contains(this.id)) { 
               //System.out.println("ignore review id " + this.id);
               this.numberReviews -= 1;
               this.numberReviewsIgnored += 1;
               return;
            }
            
            // Remove HTML tags
            // Split into sentences
            this.text = this.text.replaceAll("<[^>]+>", " ");
            TextSpan[] sentenceSpans = sentenceSplitter.split(this.text);
            
            // Write to files
            this.writeLine(outText, this.id);
            int reviewSentences = 0;
            for (TextSpan spanny : sentenceSpans) {
               numberSentences += 1; // all reviews
               reviewSentences += 1; // this review
               this.writeLine(outText, spanny.coveredText);
               this.writeLine(outSentences, this.id + "-" + reviewSentences + "\t" + spanny.coveredText);
            }
            this.writeLine(outText, "");
            //System.out.println("write review id " + this.id);
   
   
         // Review text
         } else if (name.equalsIgnoreCase("text")) { 
            this.inText = false;
   
         // Review ID.
         } else if (name.equalsIgnoreCase("id")) { 
            this.inID = false;
         }
   
      }
   
      
      /**
      Called with the contents of an element.
      Save content for 'text' and' id'.
       **/
      public void characters (char ch[], int start, int length) 
            throws SAXException {
         
         // Only do something if we are interested in the element
         if (!this.inText & !this.inID)
            return;
         
         String content = new String(ch, start, length);
         
         // Append to corresponding variable,
         // Don't add empty lines, but otherwise don't do strip() to preserve
         // spaces at the end of a set of characters that is read and newlines.
         if (content.trim() != "") {
   
            // Review text
            if (this.inText) {
               this.text = this.text + content;
            }
   
            // Review id
            if (this.inID) {
               this.id = this.id + content;
            }
   
         }
         
      }
      
   
      
      // End of XML document
      
      
   
      /**
       * Called at the end of the document.
       * Streams must be closed in caller.
       */
      public void endDocument () {
         System.out.println( "Processed " + numberReviews + " reviews "
               + "with " + numberSentences + " sentences. "
               + "Ignored " + numberReviewsIgnored + " reviews.");
      }
   
   }
   
   
   
}
