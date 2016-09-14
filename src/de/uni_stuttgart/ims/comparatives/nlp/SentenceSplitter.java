// (c) Wiltrud Kessler
// 11.03.2013
// This code is distributed under a Creative Commons
// Attribution-NonCommercial-ShareAlike 3.0 Unported license 
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package de.uni_stuttgart.ims.comparatives.nlp;

import java.io.Closeable;


/**
 * Wrapper around sentence splitters.
 */
public abstract class SentenceSplitter implements Closeable {
   
   /**
    * Split the string into sentences.
    * Selects the sentence splitter that has been initialized.
    * @return List of spans with the start/end positions of each sentence and covered text. 
    */
   public abstract TextSpan[] split (String document);

   
}
