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
import ie.pars.parscit.parser.objects.Author;
import ie.pars.parscit.parser.objects.Figure;
import ie.pars.parscit.parser.objects.Paper;
import ie.pars.parscit.parser.objects.Paragraph;
import ie.pars.parscit.parser.objects.Reference;
import ie.pars.parscit.parser.objects.Section;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 *
 * @author Your Name <behrang.qasemizadeh at deri.org>
 */
public class SerializeContent {

    DataWriter xmlWriter;

    
    public void serialize(String xmlRepositoryPath, Paper paper) {
        try {
            File xmlFolder = new File(xmlRepositoryPath);
            if (!xmlFolder.exists()) {
                xmlFolder.mkdirs();
            }
            File xmlSerializedFile = new File(xmlRepositoryPath
                    + File.separator + paper.getUid() + ".xml");
            xmlSerializedFile.createNewFile();
            PrintWriter xmlFileWriter =
                    new PrintWriter(new BufferedWriter(new FileWriter(xmlSerializedFile)));
            xmlFileWriter.flush();
            xmlWriter = new DataWriter(xmlFileWriter);
            xmlWriter.startDocument();
            AttributesImpl attsPaper = new AttributesImpl();
            attsPaper.clear();
            attsPaper.addAttribute("", "aclid", "", "CDATA", paper.getUid());
            attsPaper.addAttribute("", "title", "", "CDATA", paper.getTitle());
            xmlWriter.startElement("", "Paper", "", attsPaper);
            xmlWriter.startElement("Title");
            xmlWriter.characters(paper.getTitle());
            xmlWriter.endElement("Title");
            writeKeywords(paper.getKeywords());
            writeAuthors(paper.getAuthorList());
            for (int i = 0; i < paper.getSectionList().size(); i++) {
                writeSection(paper.getSectionList().get(i));
            }
            writeReferences(paper.getReferenceList());
            xmlWriter.endElement("Paper");
            xmlWriter.endDocument();
            xmlFileWriter.close();


        } catch (SAXException ex) {
            Logger.getLogger(SerializeContent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SerializeContent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeSection(Section section) {
        try {
            AttributesImpl attsSection = new AttributesImpl();
            attsSection.clear();
            attsSection.addAttribute("", "position", "", "CDATA",
                    Integer.toString(section.getSectionPosition()));
            attsSection.addAttribute("", "start_page", "", "CDATA",
                    Integer.toString(section.getStartPage()));
            attsSection.addAttribute("", "end_page", "", "CDATA",
                    Integer.toString(section.getEndPage()));
            attsSection.addAttribute("", "type", "", "CDATA",
                    section.getType());
            xmlWriter.startElement("", "Section", "", attsSection);
            xmlWriter.startElement("SectionTitle");
            xmlWriter.characters(section.getTitle());
            xmlWriter.endElement("SectionTitle");
            for (int i = 0; i < section.getContent().size(); i++) {
                if (section.getContent().get(i) instanceof Paragraph) {
                    writeParagraph((Paragraph) section.getContent().get(i));
                } else if (section.getContent().get(i) instanceof Figure) {
                    writeFigure((Figure) section.getContent().get(i));
                } else if (section.getContent().get(i) instanceof Section) {
                    writeSection((Section) section.getContent().get(i));
                }
            }
            xmlWriter.endElement("Section");





        } catch (SAXException ex) {
            Logger.getLogger(SerializeContent.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    private void writeParagraph(Paragraph paragraph) {
        try {
            AttributesImpl attsParagraph = new AttributesImpl();
            attsParagraph.clear();
//            attsSection.addAttribute("", "position", "", "CDATA",
//                    Integer.toString(paragraph.getPosition()));
            attsParagraph.addAttribute("", "start_page", "", "CDATA",
                    Integer.toString(paragraph.getStartPage()));
            attsParagraph.addAttribute("", "end_page", "", "CDATA",
                    Integer.toString(paragraph.getEndPage()));
            xmlWriter.startElement("", "Paragraph", "", attsParagraph);
            xmlWriter.characters(paragraph.getParagraphText());
            xmlWriter.endElement("Paragraph");


        } catch (SAXException ex) {
            Logger.getLogger(SerializeContent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeFigure(Figure figure) {
        try {
            AttributesImpl attsParagraph = new AttributesImpl();
            attsParagraph.clear();
//            attsSection.addAttribute("", "position", "", "CDATA",
//                    Integer.toString(paragraph.getPosition()));
            attsParagraph.addAttribute("", "page", "", "CDATA",
                    Integer.toString(figure.getPageNumber()));
            xmlWriter.startElement("", "Figure", "", attsParagraph);

            xmlWriter.startElement("Caption");
            xmlWriter.characters(figure.getFigureCaption());
            xmlWriter.endElement("Caption");
            xmlWriter.endElement("Figure");


        } catch (SAXException ex) {
            Logger.getLogger(SerializeContent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeAuthors(List<Author> authorList) {
        try {
            xmlWriter.startElement("Authors");
            for (int i = 0; i < authorList.size(); i++) {
                xmlWriter.startElement("Author");
                xmlWriter.startElement("FirstName");
                xmlWriter.characters(authorList.get(i).getFirstName());
                xmlWriter.endElement("FirstName");
                xmlWriter.startElement("MiddleName");
                xmlWriter.characters(authorList.get(i).getMiddleName());
                xmlWriter.endElement("MiddleName");
                xmlWriter.startElement("LastName");
                xmlWriter.characters(authorList.get(i).getLastName());
                xmlWriter.endElement("LastName");
                if (authorList.get(i).getEmailAddress() != null) {
                    xmlWriter.startElement("Email");
                    xmlWriter.characters(authorList.get(i).getEmailAddress());
                    xmlWriter.endElement("Email");
                }
                if (authorList.get(i).getAffiliation() != null) {
                    xmlWriter.startElement("Affiliation");

                    xmlWriter.characters(authorList.get(i).getAffiliation());
                    xmlWriter.endElement("Affiliation");
                }
                xmlWriter.endElement("Author");
            }
            xmlWriter.endElement("Authors");


        } catch (SAXException ex) {
            Logger.getLogger(SerializeContent.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeKeywords(List<String> keywords) {
        try {
            xmlWriter.startElement("Keywords");
            for (int i = 0; i < keywords.size(); i++) {
                xmlWriter.dataElement("Keyword", keywords.get(i));
            }
            xmlWriter.endElement("Keywords");
        } catch (SAXException ex) {
            Logger.getLogger(SerializeContent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeReferences(List<Reference> referenceList) {
        try {
            xmlWriter.startElement("References");
            for (int i = 0; i < referenceList.size(); i++) {

                writeReference(referenceList.get(i));
            }
            xmlWriter.endElement("References");
        } catch (SAXException ex) {
            Logger.getLogger(SerializeContent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeReference(Reference ref) {
        try {
            xmlWriter.startElement("Reference");
            xmlWriter.dataElement("Title", ref.getBookTitle());
            xmlWriter.startElement("Authors");
            for (int i = 0; i < ref.getAuthor().size(); i++) {
                xmlWriter.dataElement("Title", ref.getAuthor().get(i));
            }
            xmlWriter.endElement("Authors");
            xmlWriter.endElement("Reference");
        } catch (SAXException ex) {
            Logger.getLogger(SerializeContent.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}
