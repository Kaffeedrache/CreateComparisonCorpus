// (c) Wiltrud Kessler
// 11.03.2013
// This code is distributed under a Creative Commons
// Attribution-NonCommercial-ShareAlike 3.0 Unported license 
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package de.uni_stuttgart.ims.comparatives.nlp;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;


/**
 * Wrapper around Stanford sentence splitter.
 */
public class SentenceSplitterStanford extends SentenceSplitter {

   private TokenizerFactory<CoreLabel> ptbTokenizerFactory;
   private static String options = "normalizeParentheses=false,normalizeOtherBrackets=false,untokenizable=allKeep,escapeForwardSlashAsterisk=false,americanize=false";


   /**
    * Initialize sentence splitter.
    */
   public SentenceSplitterStanford () {
      ptbTokenizerFactory = PTBTokenizer.PTBTokenizerFactory.newCoreLabelTokenizerFactory(options);
   }
     

   /**
    * Split the string into sentences with Stanford.
    * @return List of spans with the start/end positions of each sentence. 
    */
   public TextSpan[] split (String document) {
      StringReader reader=new StringReader(document);
      DocumentPreprocessor dp = new DocumentPreprocessor(reader);
      dp.setTokenizerFactory(ptbTokenizerFactory);
      
      ArrayList<TextSpan> sentenceSpansList = new ArrayList<TextSpan>();
      for(List<HasWord> sent:dp){
         CoreLabel firstword = (CoreLabel) sent.get(0);
         CoreLabel lastword = (CoreLabel) sent.get(sent.size()-1);
         String coveredText = "";
         for (int i=0; i<sent.size(); i++) {
            CoreLabel word = (CoreLabel) sent.get(i);
            coveredText += word.value() + " "; 
         }
         sentenceSpansList.add(new TextSpan(firstword.beginPosition(),lastword.endPosition(), coveredText));
      }
      
      return sentenceSpansList.toArray(new TextSpan[0]) ;
      
   }
   
   
   /**
    * Implment Closeable.
    * Close all open resources.
    */
   @Override
   public void close() throws IOException { 
   }
   
   
   
}
