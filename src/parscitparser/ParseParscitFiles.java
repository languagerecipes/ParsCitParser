/* 
 * Copyright (C) 2016 Behrang QasemiZadeh <me at atmykitchen.info>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package parscitparser;

import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import ie.deri.nlp.parscit.parsCitParser.ParscitParser;
import ie.deri.nlp.parscit.parsCitParser.SerializeContentVertical;
import ie.pars.parscit.parser.objects.Paper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class ParseParscitFiles {

    public static void main(String[] ss) throws IOException, Exception {
       
        
        File f = new File("e:/parscit");
        String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
        
        MaxentTagger tagger = new MaxentTagger(taggerPath);
        Morphology m = new Morphology();
        TreeSet<String> loadDictionary = loadDictionary();
         ParscitParser p = new ie.deri.nlp.parscit.parsCitParser.ParscitParser(loadDictionary);
        //File file = new File("E:\\W11-2211-parscit.130908.xml");
        for (File file : f.listFiles()) {
            String[] split = file.getAbsolutePath().split("\\\\");
            String id = split[split.length - 1].split("-parscit")[0];
            System.out.println("* " + id);
            Paper parse = p.parse(file.getAbsolutePath(), id);
            SerializeContentVertical s = new SerializeContentVertical(tagger, m);
            s.serialize("e:/parscit_processed", parse);
        }
    }
      public static TreeSet<String> loadDictionary() throws IOException{
         BufferedReader br = new BufferedReader(new FileReader(new File("data/lexicon")));
         String line;
         TreeSet<String> dic = new TreeSet();
         while((line=br.readLine())!=null){
             dic.add(line.split("\t")[1]);
         }
         return dic;
     }

}
