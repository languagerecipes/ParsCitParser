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

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import ie.deri.nlp.parscit.parsCitParser.ParscitParser;
import ie.deri.nlp.parscit.parsCitParser.SerializeContentVertical;
import ie.pars.parscit.parser.objects.Author;
import ie.pars.parscit.parser.objects.Paper;
import java.io.File;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class TestParscitParser {

    public static void main(String[] ss) {
        ParscitParser p = new ie.deri.nlp.parscit.parsCitParser.ParscitParser();
        p.init();
        File f = new File("E:\\parscit");
        String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";
        
        MaxentTagger tagger = new MaxentTagger(taggerPath);
        Morphology m = new Morphology();

        File file = new File("E:\\A97-1019-parscit.130908.xml");
        //for (File file : f.listFiles()) {
            String[] split = file.getAbsolutePath().split("\\\\");
            String id = split[split.length - 1].split("-parscit")[0];
            System.out.println("* " + id);
            Paper parse = p.parse(file.getAbsolutePath(), id);
            SerializeContentVertical s = new SerializeContentVertical(tagger, m);
            s.serialize("e:\\test0", parse);
        //}
    }
}
