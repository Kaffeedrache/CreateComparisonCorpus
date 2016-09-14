// (c) Wiltrud Kessler
// 24.07.2013
// This code is distributed under a Creative Commons
// Attribution-NonCommercial-ShareAlike 3.0 Unported license 
// http://creativecommons.org/licenses/by-nc-sa/3.0/


package de.uni_stuttgart.ims.comparatives.nlp;

/**
 * Text span
 * @param begin start of span.
 * @param end end of span, which is +1 more than the last element in the span.
 * 
 * @author kesslewd
 *
 */
public class TextSpan {

   public int begin;
   public int end;
   public String coveredText;
   
   public TextSpan (int begin, int end) {
      this.begin = begin;
      this.end = end;
   }
   public TextSpan (int begin, int end, String coveredText) {
      this.begin = begin;
      this.end = end;
      this.coveredText = coveredText; 
   }

   public boolean contains(int index) {
      return (index >= begin) && (index < end);
   }

   public boolean contains (
         int begin, int end) {
      return (this.contains(begin) & this.contains(end));
   }
   
   public String getCoveredText (String text) {
      return text.substring(begin, end);
   }
   
   public String toString() {
      return "[" + begin + ", " + end + ")";
   }
      
}
