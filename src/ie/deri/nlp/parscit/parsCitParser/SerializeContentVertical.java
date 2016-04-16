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
package ie.deri.nlp.parscit.parsCitParser;

//import com.megginson.sax.DataWriter;


import com.megginson.sax.DataWriter;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ling.WordLemmaTag;
import edu.stanford.nlp.parser.common.ParserGrammar;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import ie.pars.parscit.parser.objects.Author;
import ie.pars.parscit.parser.objects.Equation;
import ie.pars.parscit.parser.objects.Figure;
import ie.pars.parscit.parser.objects.Paper;
import ie.pars.parscit.parser.objects.Paragraph;
import ie.pars.parscit.parser.objects.Reference;
import ie.pars.parscit.parser.objects.Section;
import ie.pars.parscit.parser.objects.Table;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;





/**
 *
 * @author Your Name <behrang.qasemizadeh at deri.org>
 */
public class SerializeContentVertical {

    private DataWriter xmlWriter;
    private MaxentTagger tagger;
    private Morphology m;
    
    int paraCounter;
    int sectionCounter;
    public SerializeContentVertical( MaxentTagger tagger, Morphology m) {
        this.tagger = tagger;
        this.m = m;
        
    }

    

    int subsection=0;
    
    public void serialize(String xmlRepositoryPath, Paper paper) {
        try {
            File xmlFolder = new File(xmlRepositoryPath);
            if (!xmlFolder.exists()) {
                xmlFolder.mkdirs();
            }
            File xmlSerializedFile = new File(xmlRepositoryPath
                    + File.separator + paper.getUid() + ".xml");
            xmlSerializedFile.createNewFile();
            PrintWriter xmlFileWriter
                    = new PrintWriter(new BufferedWriter(new FileWriter(xmlSerializedFile)));
            xmlFileWriter.flush();
            xmlWriter = new DataWriter(xmlFileWriter);
            sectionCounter=0;
            paraCounter=0;
            xmlWriter.startDocument();
            AttributesImpl attsPaper = new AttributesImpl();
            attsPaper.clear();
            attsPaper.addAttribute("", "title", "", "CDATA", paper.getTitle());
            attsPaper.addAttribute("", "aclid", "", "CDATA", paper.getUid());
            xmlWriter.startElement("", "doc", "", attsPaper);
            xmlWriter.startElement("title");
            writeVertical(
                    paper.getTitle());
            
            xmlWriter.endElement("title");
            //writeKeywords(paper.getKeywords());
            writeAuthors(paper.getAuthorList());
            for (int i = 0; i < paper.getSectionList().size(); i++) {
                writeSection(paper.getSectionList().get(i));
            }
            //writeReferences(paper.getReferenceList());
            xmlWriter.endElement("doc");
            xmlWriter.endDocument();
            xmlFileWriter.close();

        } catch (SAXException ex) {
            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeSection(Section section) {
        try {
            sectionCounter++;
            AttributesImpl attsSection = new AttributesImpl();
            attsSection.clear();
            attsSection.addAttribute("", "position", "", "CDATA",
                    Integer.toString(section.getSectionPosition()));
            attsSection.addAttribute("", "type", "", "CDATA",
                    section.getType());
            String sectionNameTag = "section";
            if(subsection>0){
                sectionNameTag+=subsection;
            }
            xmlWriter.startElement("",sectionNameTag , "", attsSection);
            xmlWriter.startElement("sectiontitle");
            //xmlWriter.characters();
            writeVertical(section.getTitle());
            xmlWriter.endElement("sectiontitle");
            List captions = new ArrayList();
            for (int i = 0; i < section.getContent().size(); i++) {
                
                if (section.getContent().get(i) instanceof Paragraph) {
                    writeParagraph((Paragraph) section.getContent().get(i));
                } else if (section.getContent().get(i) instanceof Figure || section.getContent().get(i) instanceof Table ) {
                    captions.add(section.getContent().get(i));
                   // writeFigure((Figure) section.getContent().get(i));
                } else if (section.getContent().get(i) instanceof Section) {
                    subsection++;
                    writeSection((Section) section.getContent().get(i));
                    subsection--;
                }else if (section.getContent().get(i) instanceof Equation) {
                    writeEquation((Equation) section.getContent().get(i));
                }
            }
            
            for (Object p : captions) {
                if (p instanceof Figure) {

                    writeFigure((Figure) p);
                } else if (p instanceof Table) {
                    writeTable((Table) p);
                }
            }
            xmlWriter.endElement(sectionNameTag);

        } catch (SAXException ex) {
            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeParagraph(Paragraph paragraph) {
        try {
            AttributesImpl attsParagraph = new AttributesImpl();
            attsParagraph.clear();
            attsParagraph.addAttribute("", "position", "", "CDATA",
                    Integer.toString(++this.paraCounter));
//            attsParagraph.addAttribute("", "start_page", "", "CDATA",
//                    Integer.toString(paragraph.getStartPage()));
//            attsParagraph.addAttribute("", "end_page", "", "CDATA",
//                    Integer.toString(paragraph.getEndPage()));
            xmlWriter.startElement("", "paragraph", "", attsParagraph);
            
            writeVertical(paragraph.getParagraphText());
            
            xmlWriter.endElement("paragraph");

        } catch (SAXException ex) {
            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeFigure(Figure figure) {
        try {
            AttributesImpl attsParagraph = new AttributesImpl();
            attsParagraph.clear();
            

            xmlWriter.startElement("figurecaption");
            
            writeVertical(figure.getFigureCaption());
            
            
            xmlWriter.endElement("figurecaption");
            

        } catch (SAXException ex) {
            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeEquation(Equation equation) {
        try {
            AttributesImpl attsParagraph = new AttributesImpl();
            attsParagraph.clear();
            xmlWriter.startElement("", "equation", "", attsParagraph);
            writeVertical(equation.getEquationText());
            xmlWriter.endElement("equation");

        } catch (SAXException ex) {
            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
   private void writeTable(Table table) {
        try {
            AttributesImpl attsParagraph = new AttributesImpl();
            attsParagraph.clear();
            xmlWriter.startElement("", "tablecaption", "", attsParagraph);
            writeVertical(table.getTableCaption());
            xmlWriter.endElement("tablecaption");

        } catch (SAXException ex) {
            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
   
    
    
    private void writeAuthors(List<Author> authorList) {
        try {
            xmlWriter.startElement("authors");
            for (int i = 0; i < authorList.size(); i++) {
//                xmlWriter.startElement("author");
//                xmlWriter.startElement("firstname");
//                xmlWriter.characters(authorList.get(i).getFirstName());
//                xmlWriter.endElement("FirstName");
//                xmlWriter.startElement("MiddleName");
//                xmlWriter.characters(authorList.get(i).getMiddleName());
//                xmlWriter.endElement("MiddleName");
//                xmlWriter.startElement("LastName");
//                xmlWriter.characters(authorList.get(i).getLastName());
//                xmlWriter.endElement("LastName");
//                if (authorList.get(i).getEmailAddress() != null) {
//                    xmlWriter.startElement("Email");
//                    xmlWriter.characters(authorList.get(i).getEmailAddress());
//                    xmlWriter.endElement("Email");
//                }
//                if (authorList.get(i).getAffiliation() != null) {
//                    xmlWriter.startElement("Affiliation");
//
//                    xmlWriter.characters(authorList.get(i).getAffiliation());
//                    xmlWriter.endElement("Affiliation");
//                }
//                xmlWriter.endElement("Author");
                writeVertical(authorList.get(i).getRawText());
            }
            
            xmlWriter.endElement("authors");

        } catch (SAXException ex) {
            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    private void writeKeywords(List<String> keywords) {
//        try {
//            xmlWriter.startElement("keywords");
//            for (int i = 0; i < keywords.size(); i++) {
//                xmlWriter.startElement("keyword");
//                writeVertical( keywords.get(i)));
//                xmlWriter.endElement("keyword");
//            }
//            xmlWriter.endElement("keywords");
//        } catch (SAXException ex) {
//            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

//    private void writeReferences(List<Reference> referenceList) {
//        try {
//            xmlWriter.startElement("References");
//            for (int i = 0; i < referenceList.size(); i++) {
//
//                writeReference(referenceList.get(i));
//            }
//            xmlWriter.endElement("References");
//        } catch (SAXException ex) {
//            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

//    private void writeReference(Reference ref) {
//        try {
//            xmlWriter.startElement("Reference");
//            xmlWriter.dataElement("Title", ref.getBookTitle());
//            xmlWriter.startElement("Authors");
//            for (int i = 0; i < ref.getAuthor().size(); i++) {
//                xmlWriter.dataElement("Title", ref.getAuthor().get(i));
//            }
//            xmlWriter.endElement("Authors");
//            xmlWriter.endElement("Reference");
//        } catch (SAXException ex) {
//            Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }

    private void writeVertical(String figureCaption) {

        StringReader sr = new StringReader(figureCaption);
        
        List<Word> tokenize = PTBTokenizer.newPTBTokenizer(sr).tokenize();
        WordToSentenceProcessor wpt = new WordToSentenceProcessor();
        List<List<Word>> process = wpt.process(tokenize);
        List<List<TaggedWord>> taggedSent = tagger.process(process);

        
        for (int i = 0; i < taggedSent.size(); i++) {
            try {
                xmlWriter.startElement("s");
                List<TaggedWord> get = taggedSent.get(i);
                xmlWriter.characters("\n");
                for (TaggedWord tw : get) {
                    xmlWriter.characters(
                                    PTBTokenizer.ptbToken2Text(
                            tw.word()) + "\t" + m.lemma(PTBTokenizer.ptbToken2Text(tw.word()), tw.tag()) + "\t" + tw.tag()+"\n");
                }
                xmlWriter.endElement("s");
            } catch (SAXException ex) {
                Logger.getLogger(SerializeContentVertical.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
