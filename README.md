# Create Comparison Corpus

Get the sentences for the IMS Comparison annotation.
Contact me if you have any problems in getting the data.


## 1. Download epinions data set

Download the original epinions file with camera reviews from
[http://groups.csail.mit.edu/rbg/code/precis/](http://groups.csail.mit.edu/rbg/code/precis/)


## 2. Convert the file to valid XML in UTF-8

Compile the file `EpinionsCleaner.java`:

    javac src/de/uni_stuttgart/ims/comparatives/annotation/epinions/EpinionsCleaner.java

Run `EpinionsCleaner` with two arguments:
- argument 1: the file you downloaded, e.g., cameras.xml,
- argument 2: the output file that will be generated, e.g., cameras_new.xml.


    java -cp src de.uni_stuttgart.ims.comparatives.annotation.epinions.EpinionsCleaner cameras.xml cameras_new.xml


## 3. Extract sentences

Compile the file `ExtractSentencesFromXML.java`:

    javac -cp src:lib/stanford-corenlp-3.2.0.jar src/de/uni_stuttgart/ims/comparatives/annotation/epinions/ExtractSentencesFromXML.java

Run `ExtractSentencesFromXML` with 
- argument 1: the XML file, i.e., the output from previous step, e.g., cameras_new.xml.
- argument 2: the list of review ids to ignore, e.g., ignoreCameraReviewIds.txt.
- argument 3: the output file with 1 sentence per line, e.g., cameras.allsentences.txt.
- argument 4: [optional] the output file with the texts, id in one line, then one sentence per line, separated by an empty line, e.g., cameras.allsentences.txt.

The file with the list of review ids to ignore can be empty, then all reviews are extracted.
The fourth parameter is optional, if none is given only the sentences are extracted.


    java -cp src:lib/stanford-corenlp-3.2.0.jar de.uni_stuttgart.ims.comparatives.annotation.epinions.ExtractSentencesFromXML cameras_new.xml ignoreCameraReviewIds.txt cameras.allsentences.txt 

You should get the following output:

    Processed 12539 reviews with 296529 sentences. Ignored 47 reviews.


## References

Wiltrud Kessler and Jonas Kuhn (2014)
"A Corpus of Comparisons in Product Reviews"
In Proceedings of the 9th Language Resources and Evaluation Conference (LREC 2014).

S.R.K. Branavan, Harr Chen, Jacob Eisenstein, Regina Barzilay (2009) 
"Learning Document-Level Semantic Properties from Free-Text Annotations"
Journal of Artificial Intelligence Research archive, 
Volume 34 Issue 1, January 2009.


## Licence

(c) Wiltrud Kessler

This code is distributed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported license
[http://creativecommons.org/licenses/by-nc-sa/3.0/](http://creativecommons.org/licenses/by-nc-sa/3.0/)
