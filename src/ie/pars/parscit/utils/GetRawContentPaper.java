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
package ie.pars.parscit.utils;

//import com.megginson.sax.DataWriter;


import ie.pars.parscit.parser.objects.Equation;
import ie.pars.parscit.parser.objects.Figure;
import ie.pars.parscit.parser.objects.Paper;
import ie.pars.parscit.parser.objects.Paragraph;
import ie.pars.parscit.parser.objects.Section;
import ie.pars.parscit.parser.objects.Table;
import java.util.ArrayList;
import java.util.List;





/**
 *
 * @author Your Name <behrang.qasemizadeh at deri.org>
 */
public class GetRawContentPaper {

    public GetRawContentPaper(Paper paper) {
        this. paper=paper;
    }

   
    
    
Paper paper;
    

    
    
   StringBuilder sb;
    public StringBuilder getRawText() {
    
           this.sb = new StringBuilder();
            
            
           sb.append(
                    paper.getTitle()).append("\n");
            
            
            for (int i = 0; i < paper.getSectionList().size(); i++) {
                writeSection(paper.getSectionList().get(i));
            }
            return sb;
    }

    private void writeSection(Section section) {
        
            
            sb.append(section.getTitle()).append("\n");
            
            List captions = new ArrayList();
            for (int i = 0; i < section.getContent().size(); i++) {
                
                if (section.getContent().get(i) instanceof Paragraph) {
                 Paragraph p=   (Paragraph) section.getContent().get(i);
                    sb.append(p.getParagraphText()).append("\n");
                } else if (section.getContent().get(i) instanceof Figure || section.getContent().get(i) instanceof Table ) {
                    captions.add(section.getContent().get(i));
                   
                } else if (section.getContent().get(i) instanceof Section) {
                   
                    writeSection((Section) section.getContent().get(i));
                   
                }else if (section.getContent().get(i) instanceof Equation) {
                    Equation e=((Equation) section.getContent().get(i));
                    sb.append(e.getEquationText());
                }
            }
            
            for (Object p : captions) {
                if (p instanceof Figure) {
                  Figure f =      (Figure) p;
                  sb.append(f.getFigureCaption()).append("\n");
                   
                } else if (p instanceof Table) {
                     Table f =      (Table) p;
                  sb.append(f.getTableCaption()).append("\n");
                }
            }
            

    }


   
//    private void writeVertical(String textContent) {
//
//        StringReader sr = new StringReader(textContent);
//        
//        List<Word> tokenize = PTBTokenizer.newPTBTokenizer(sr).tokenize();
//        WordToSentenceProcessor wpt = new WordToSentenceProcessor();
//        List<List<Word>> process = wpt.process(tokenize);
//        List<List<TaggedWord>> taggedSent = tagger.process(process);
//
//        
//        for (int i = 0; i < taggedSent.size(); i++) {
//            try {
//                xmlWriter.startElement("s");
//                List<TaggedWord> get = taggedSent.get(i);
//                xmlWriter.characters("\n");
//                for (TaggedWord tw : get) {
//                    xmlWriter.characters(
//                                    PTBTokenizer.ptbToken2Text(
//                            tw.word()) + "\t" + m.lemma(PTBTokenizer.ptbToken2Text(tw.word()), tw.tag()) + "\t" + tw.tag()+"\n");
//                }
//                xmlWriter.endElement("s");
//            } catch (SAXException ex) {
//                Logger.getLogger(GetRawContentPaper.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//    }
}
