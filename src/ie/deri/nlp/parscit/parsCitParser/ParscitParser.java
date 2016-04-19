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

import ie.pars.parscit.parser.objects.Author;
import ie.pars.parscit.parser.objects.Equation;
import ie.pars.parscit.parser.objects.Figure;
import ie.pars.parscit.parser.objects.Reference;
import ie.pars.parscit.parser.objects.Section;
import ie.pars.parscit.parser.objects.Paper;
import ie.pars.parscit.parser.objects.Paragraph;
import ie.pars.parscit.parser.objects.Table;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.xml.sax.ext.DefaultHandler2;


/**
 *
 * @author Your Name <behrang.qasemizadeh at deri.org>
 */
public class ParscitParser {

    private Paper currentPaper;
    TreeSet<String> dictionary;
    private int currentPage;
    private boolean isAbstract = false;
    private int sectionNumber;
    private int subSectionNumber;

    public ParscitParser(TreeSet<String> dictionary) {
        this.dictionary = dictionary;
    }

    private void init() {
        currentPaper = new Paper();

        currentPage = 0;
        sectionNumber = 1;
        subSectionNumber = 1;

    }

    /**
     *
     * @param filePath: the input text file for parscit logical sectioning and
     * segmentation
     * @param uid
     * @return Paper Object
     */
    public Paper parse(String filePath, String uid) throws Exception {
        init();
        File file = new File(filePath);
        currentPaper.setUid(uid);
        DocumentBuilder builder = null;
        Document doc = null;
       
        //     try {
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        builder.setEntityResolver(new DefaultHandler2());
       
        // BQ HERE
        try {
            doc = builder.parse(file);
            NodeList algorithmNodes = doc.getElementsByTagName("algorithm");
            currentPaper = parseParsHed(algorithmNodes.item(1), uid);
            currentPaper.setReferenceList(parseParsCitResults(algorithmNodes.item(2)));
            // System.out.println("Paper: " + filePath + "/" + currentPaper.getTitle() + " | " + currentPaper.getUid());
            List<Section> sectionList = parseSectLabel(algorithmNodes.item(0));
            //System.err.println("Parse Sections Done!");
            currentPaper.setSectionList(sectionList);
//            } catch (Exception ex) {
//                System.err.println(">>> Error while Parsing the ParsCitFile" + ex);
//            }
        } catch (org.xml.sax.SAXParseException ex) {
            System.err.println("Error while parsing xml");
           //Logger.getLogger(ParscitParser.class.getName()).log(Level.SEVERE, null, ex);

        }

        // StyleTable Node
        return currentPaper;

    }

    private List<Section> parseSectLabel(Node sectionNode) {
        List<Section> sectionList = new ArrayList<Section>();
        NodeList sectionVariantNodes = sectionNode.getChildNodes();
        for (int i = 0; i < sectionVariantNodes.getLength(); i++) {
            if ("variant".equals(sectionVariantNodes.item(i).getNodeName())) {
                sectionList = parseSectLableVariant(sectionVariantNodes.item(i));
                break;
            }
        }
        return sectionList;
    }

    private List<Section> parseSectLableVariant(Node variantSectLabelNode) {
        List<Section> sections = new ArrayList<Section>();
        NodeList paperNodeList = variantSectLabelNode.getChildNodes();

        // list of sections
        List<List<Node>> nodeList = getSections(paperNodeList);

        // now process each section
        if (nodeList.size() > 0) {

            Section firstSection = processFirstSection(nodeList.get(0));
            if (firstSection.getContent().size() > 0) {
                sections.add(firstSection);
                sectionNumber++;
            }
            for (int i = 1; i < nodeList.size(); i++) {
                List<Node> curNodeList = nodeList.get(i);
                Section s = processSection(curNodeList);
                if (s == null || "categories and subject descriptors".equalsIgnoreCase(s.getType()) || "keywords".equalsIgnoreCase(s.getType())) {
                    // System.err.println("ignor section: null or keyword or category description");
                } else {
                    sections.add(s);
                }

            }
            return sections;
        } else {
            return sections;
        }
    }

    private boolean checkIfNumber(String in) {
        try {
            Integer.parseInt(in);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }

    private Section processFirstSection(List<Node> sectionNodeList) {
        Section firstSection = new Section();
        firstSection.setStartPage(currentPage);
        String finalContentForAbstact = "";
        List<List<Node>> subSections = getSubSections(sectionNodeList);
        List<Node> sectionNode = subSections.get(0);

        for (int i = 0; i < sectionNode.size(); i++) {
            if ("bodyText".equals(sectionNode.get(i).getNodeName())) {
                finalContentForAbstact += sectionNode.get(i).getTextContent();
            } else if ("page".equals(sectionNode.get(i).getNodeName())) {
                if (checkIfNumber(sectionNode.get(i).getTextContent().trim())) {
                    // here is the error
                    int offeredPageNumber = Integer.parseInt(sectionNode.get(i).getTextContent().trim());
                    if (offeredPageNumber > currentPage) {
                        currentPage = offeredPageNumber;
                    }
                }
            } else if ("listItem".equals(sectionNode.get(i).getNodeName())) {
                finalContentForAbstact += sectionNode.get(i).getTextContent();
            } else if ("figureCaption".equals(sectionNode.get(i).getNodeName())) {
                Figure fig = processFigure(sectionNode.get(i));
                firstSection.addContent(fig);
            } else if ("tableCaption".equals(sectionNode.get(i).getNodeName())) {
                Table tbl = new Table();
                tbl.setTableCaption(sectionNode.get(i).getTextContent().trim());
                tbl.setTablePageNumber(currentPage);
                firstSection.addTable(tbl);

            } else if ("figure".equals(sectionNode.get(i).getNodeName())) {
                Figure fig = processFigure(sectionNode.get(i));
                firstSection.addContent(fig);
            } else if ("title".equals(sectionNode.get(i).getNodeName())) {
                Table tbl = new Table();
                tbl.setTableCaption(sectionNode.get(i).getTextContent().trim());
                tbl.setTablePageNumber(currentPage);
                firstSection.addTable(tbl);
            } else if ("author".equals(sectionNode.get(i).getNodeName())) {
            } else if ("address".equals(sectionNode.get(i).getNodeName())) {
            } else if ("affiliation".equals(sectionNode.get(i).getNodeName())) {
            } else if ("email".equals(sectionNode.get(i).getNodeName())) {
            } else if ("construct".equals(sectionNode.get(i).getNodeName())) {

                String constructText = sectionNode.get(i).getTextContent();
                finalContentForAbstact += constructText;

            } else if (!"#text".equals(sectionNode.get(i).getNodeName())) {
              //  System.err.println(">>> Unseen type of node processFirstSection>>> "
                //    + sectionNode.get(i).getNodeName());
            }
        }

        List<Paragraph> pList = getCleanAbstractText(finalContentForAbstact);
        if (pList != null) {
            firstSection.addContent(pList);
        }
        firstSection.setType("opening");
        if (isAbstract) {
            firstSection.setTitle("abstract");
        } else {
            firstSection.setTitle("no title");
        }
        firstSection.setSectionPosition(1);
        firstSection.setEndPage(currentPage);

        subSectionNumber = 1;
        for (int i = 1; i < subSections.size(); i++) {
            //System.err.println("Sub section in opening section");
            Section subSection = getSubSectionContentList(subSections.get(i));
            firstSection.addContent(subSection);
        }
        subSectionNumber = 1;

        return firstSection;
    }

    private List<Paragraph> getCleanAbstractText(String abstractText) {
        //System.err.println("Getting Clean abstract file");
        List<Paragraph> paragraphList = new ArrayList<Paragraph>();
        if ("".equals(abstractText.trim())) {
            return null;
        } else {
            String lines[] = abstractText.trim().split("\\n");

            // see if the line start with the word abstract, if yes remove it
            String abstractPtrn = "^\\s*(Abstract|abstract|Summary)(\\.|\\:|\\?|-|--)*";
            Pattern abstractPattern = Pattern.compile(abstractPtrn, Pattern.CANON_EQ);
            Matcher nm1 = abstractPattern.matcher(lines[0]);
            if (nm1.find()) {
                isAbstract = true;
            }
            String firstLine = nm1.replaceFirst("");
            lines[0] = firstLine;
            Paragraph currentParagraph = new Paragraph();
            int avgLineLength = 0;
            int sum = 0;
            double newLineThreshold = 0;
            String currentParagraphText = "";
            for (int i = 0; i < lines.length; i++) {
                //sum += lines[i].length();
                //avgLineLength = sum / (i + 1);
                //newLineThreshold = avgLineLength - (lines[i].length() * .1);
                if (lines.length > i + 1 && lines[i].endsWith("-")) {
                    String[] split = lines[i + 1].split(" ");
                    // if there is any token in the next line
                    if (split.length > 0) {
                        //try to see if we can attach the two tokens
                        String[] tokens = lines[i].split(" ");
                        String preTok = tokens[tokens.length - 1];
                        preTok = preTok.substring(0, preTok.length() - 1);
                        if (dictionary.contains(preTok + split[0])) {

                            int sfx = lines[i].lastIndexOf(" ");
                            if (sfx < 0) {
                                sfx = 0;
                            }
                            lines[i] = lines[i].substring(0, sfx) + " ";
                            lines[i + 1] = preTok + lines[i + 1];
                        }

                    }

                }

                currentParagraphText += " ";
                currentParagraphText += lines[i];
            }
            currentParagraph.setParagraphText(currentParagraphText);
            currentParagraph.setPosition(paragraphList.size() + 1);
            paragraphList.add(currentParagraph);
            // System.err.println("Done Processing Abstract");
            return paragraphList;
        }
    }

    /**
     * The method get a list of nodes and generates a list of list where the
     * inner list is the content for a section, note that reference section will
     * be filtered maybe a bit of change to accept txt files with more than one
     * paper, I should try the journals in the ACL corpus
     *
     * @param contentNodeList
     * @return List<List<Node>>
     */
    private static List<List<Node>> getSections(NodeList contentNodeList) {
        List<List<Node>> newNodeList = new ArrayList<List<Node>>();
        List<Node> currentContentList = new ArrayList<Node>();
        boolean breakByRefernce = false;
        for (int i = 0; i < contentNodeList.getLength(); i++) {
            if ("sectionHeader".equals(contentNodeList.item(i).getNodeName())) {
                newNodeList.add(currentContentList);
                currentContentList = new ArrayList<Node>();
                currentContentList.add(contentNodeList.item(i));
            } else if ("reference".equals(contentNodeList.item(i).getNodeName())) {
                breakByRefernce = true;
                break;
            } else {
                currentContentList.add(contentNodeList.item(i));
            }
        }
        if (!breakByRefernce && currentContentList.size() > 0) {
            newNodeList.add(currentContentList);
        }
        return newNodeList;
    }

    /**
     * This is a speciall case
     *
     * @param sectionNodeList
     * @return
     */
    private Section processSection(List<Node> sectionNodeList) {
        Section section = new Section();
        section.setStartPage(currentPage);
        section.setSectionPosition(sectionNumber);
        sectionNumber++;
        // the first node will be considered as the content of section and
        // the rest will be the subsections
        List<List<Node>> subSections = getSubSections(sectionNodeList);

        // the first node of the first list is necessary sectionHeader
        // else there is no header
        if (sectionNodeList.size() > 0
                && "sectionHeader".equals(
                        subSections.get(0).get(0).getNodeName())) {
            section.setTitle(subSections.get(0).get(0).getTextContent());
            NamedNodeMap sectionHeaderAttribute = subSections.get(0).get(0).getAttributes();
            Node genericHeader = sectionHeaderAttribute.getNamedItem("genericHeader");
            // categories and subject descriptors and keywords

            if (genericHeader != null) {
                section.setType(genericHeader.getTextContent());
            } else {
                section.setType("other");
            }
            subSections.get(0).remove(0);
            // get the parscit info for generic header
        } else {
            section.setTitle("none");
            section.setType("other");
        }
        // process the main section node
        getSectionContentList(section, subSections.get(0));

        subSectionNumber = 1;
        for (int i = 1; i < subSections.size(); i++) {
            Section subSection = getSubSectionContentList(subSections.get(i));
            section.addContent(subSection);
        }
        subSectionNumber = 1;
        section.setEndPage(currentPage);
        return section;

    }

    /**
     * the output for this should be a vector
     *
     * @param sectionNode
     */
    private void getSectionContentList(Section section, List<Node> sectionNode) {
        //igonor the first node which is the header
        String sectionTextContent = "";
        for (int i = 0; i < sectionNode.size(); i++) {
            if ("bodyText".equals(sectionNode.get(i).getNodeName())) {
                sectionTextContent += sectionNode.get(i).getTextContent();
            } else if ("page".equals(sectionNode.get(i).getNodeName())) {
                if (checkIfNumber(sectionNode.get(i).getTextContent().trim())) {
                    int offeredPageNumber = Integer.parseInt(sectionNode.get(i).getTextContent().trim());
                    if (offeredPageNumber > currentPage) {
                        currentPage = offeredPageNumber;
                    }
                }
            } else if ("listItem".equals(sectionNode.get(i).getNodeName())) {
                sectionTextContent += sectionNode.get(i).getTextContent();
            } else if ("figureCaption".equals(sectionNode.get(i).getNodeName())) {
                Figure fig = processFigure(sectionNode.get(i));
                section.addFigure(fig);
            } else if ("keyword".equals(sectionNode.get(i).getNodeName())) {
                String[] keywordList = sectionNode.get(i).getTextContent().split(",|;|\\.");
                for (int l = 0; l < keywordList.length; l++) {
                    currentPaper.addKeywords(keywordList[l].trim());
                }
            } else if ("figure".equals(sectionNode.get(i).getNodeName())) {
            } else if ("category".equals(sectionNode.get(i).getNodeName())) {
            } else if ("tableCaption".equals(sectionNode.get(i).getNodeName())) {
                Table tbl = new Table();
                tbl.setTableCaption(sectionNode.get(i).getTextContent().trim());
                tbl.setTablePageNumber(currentPage);
                section.addTable(tbl);

            } else if ("construct".equals(sectionNode.get(i).getNodeName())) {

                String constructText = sectionNode.get(i).getTextContent();
                sectionTextContent += constructText;

            } else if ("footnote".equals(sectionNode.get(i).getNodeName())) {
            } else if ("email".equals(sectionNode.get(i).getNodeName())) {
            } else if ("equation".equals(sectionNode.get(i).getNodeName())) {
                List<Paragraph> pList = new ArrayList<Paragraph>();
                pList = getCleanSectionText(sectionTextContent);
                sectionTextContent = "";
                if (pList != null) {
                    section.addContent(pList);
                }
                section.addContent(processEquation(sectionNode.get(i)));

            } else if (!"#text".equals(sectionNode.get(i).getNodeName())) {
//                System.err.println(">>> Unseen type of node getSectionContentList>>> "
//                        + sectionNode.get(i).getNodeName());
            }

        }

        List<Paragraph> pList = getCleanSectionText(sectionTextContent);
        section.addContent(pList);
    }

    private Section getSubSectionContentList(List<Node> sectionNode) {
        Section currentSection = new Section();
        currentSection.setStartPage(currentPage);
        currentSection.setSectionPosition(subSectionNumber);
        subSectionNumber++;
        // in soal hast ke vaghti sub sub sub section dashte bashim chi mishe
        // bayad test koni!
        //check whether the first node is subsectionHeader

        if (sectionNode.size() > 0
                && "subsectionHeader".equalsIgnoreCase(sectionNode.get(0).getNodeName())) {
            currentSection.setTitle(sectionNode.get(0).getTextContent());
            currentSection.setType("sub_section");
            sectionNode.remove(0);
        }

        String sectionTextContent = "";
        for (int i = 0; i < sectionNode.size(); i++) {
            if ("bodyText".equals(sectionNode.get(i).getNodeName())) {
                sectionTextContent += sectionNode.get(i).getTextContent();
            } else if ("page".equals(sectionNode.get(i).getNodeName())) {
                ////// here
                if (checkIfNumber(sectionNode.get(i).getTextContent().trim())) {
                    int offeredPageNumber = Integer.parseInt(sectionNode.get(i).getTextContent().trim());
                    if (offeredPageNumber > currentPage) {
                        currentPage = offeredPageNumber;
                    }
                }
            } else if ("listItem".equals(sectionNode.get(i).getNodeName())) {
                sectionTextContent += sectionNode.get(i).getTextContent();
            } else if ("figureCaption".equals(sectionNode.get(i).getNodeName())) {
                Figure fig = processFigure(sectionNode.get(i));
                currentSection.addFigure(fig);
            } else if ("tableCaption".equals(sectionNode.get(i).getNodeName())) {
                Table tbl = new Table();
                tbl.setTableCaption(sectionNode.get(i).getTextContent().trim());
                tbl.setTablePageNumber(currentPage);
                currentSection.addTable(tbl);

            } else if ("construct".equals(sectionNode.get(i).getNodeName())) {

                String constructText = sectionNode.get(i).getTextContent();
                sectionTextContent += constructText;

            } else if ("figure".equals(sectionNode.get(i).getNodeName())) {
            } else if ("footnote".equals(sectionNode.get(i).getNodeName())) {
            } else if ("email".equals(sectionNode.get(i).getNodeName())) {
            } else if ("equation".equals(sectionNode.get(i).getNodeName())) {
                List<Paragraph> pList = new ArrayList<Paragraph>();
                pList = getCleanSectionText(sectionTextContent);
                sectionTextContent = "";
                if (pList != null) {
                    currentSection.addContent(pList);
                }
                currentSection.addContent(processEquation(sectionNode.get(i)));
            } else if (!"#text".equals(sectionNode.get(i).getNodeName())) {
//                System.err.println(">>> Unseen type of node in get getSubSectionContentList>>> "
//                        + sectionNode.get(i).getNodeName());
            }

        }
        List<Paragraph> pList = getCleanSectionText(sectionTextContent);
        currentSection.addContent(pList);
        currentSection.setEndPage(currentPage);
        return currentSection;
    }

    private List<List<Node>> getSubSections(List<Node> sectionNodeList) {
        List<List<Node>> newNodeList = new ArrayList<List<Node>>();
        List<Node> currentContentList = new ArrayList<Node>();
        for (int i = 0; i < sectionNodeList.size(); i++) {
            if ("subsectionHeader".equals(sectionNodeList.get(i).getNodeName())) {
                newNodeList.add(currentContentList);
                currentContentList = new ArrayList<Node>();
                currentContentList.add(sectionNodeList.get(i));
            } else {
                currentContentList.add(sectionNodeList.get(i));
            }
        }
        if (currentContentList.size() > 0) {
            newNodeList.add(currentContentList);
        }
        return newNodeList;
    }

    private Paper parseParsHed(Node parseHedNode, String uid) {
        List<Node> authorEmailAffliationList = new ArrayList<Node>();
        Paper paper = new Paper();
        if (parseHedNode != null) {
            NodeList parsHeadNodeList = parseHedNode.getChildNodes();
            for (int i = 0; i < parsHeadNodeList.getLength(); i++) {
                if ("variant".equals(parsHeadNodeList.item(i).getNodeName())) {
                    NodeList variantNodeList
                            = parsHeadNodeList.item(i).getChildNodes();
                    for (int j = 0; j < variantNodeList.getLength(); j++) {
                        if ("title".equals(variantNodeList.item(j).getNodeName())) {
                            paper.setTitle(variantNodeList.item(j).getTextContent());
                        } else if ("author".equals(variantNodeList.item(j).getNodeName())) {
                            authorEmailAffliationList.add(variantNodeList.item(j));
//                        Author author = processAuthorString(variantNodeList.item(j).getTextContent());
//                        paper.addAuthor(author);
                        } else if ("affiliation".equals(variantNodeList.item(j).getNodeName())) {
                            authorEmailAffliationList.add(variantNodeList.item(j));
//                        paper.addAffiliation(variantNodeList.item(j).getTextContent());
                        } else if ("email".equals(variantNodeList.item(j).getNodeName())) {
                            authorEmailAffliationList.add(variantNodeList.item(j));
//                        paper.addEmail(variantNodeList.item(j).getTextContent());
                        }
                    }
                    break;
                }
            }
        }
        List<Author> authorList = processListOfAuthorAffiliationEmail(authorEmailAffliationList);
        paper.setAuthorList(authorList);
        paper.setUid(uid);
        return paper;

    }

    private List<Reference> parseParsCitResults(Node parseCitNode) {

        List<Reference> references = new ArrayList<Reference>();
        if (parseCitNode != null && parseCitNode.hasChildNodes()) {
            NodeList parsCitNodeList = parseCitNode.getChildNodes();
            for (int i = 0; i < parsCitNodeList.getLength(); i++) {
                if ("citationList".equals(parsCitNodeList.item(i).getNodeName())) {
                    references = parseCitationList(parsCitNodeList.item(i).getChildNodes());
                }
            }
        }
        return references;

    }

    private List<Reference> parseCitationList(NodeList citationList) {
        List<Reference> references = new ArrayList<Reference>();
        for (int i = 0; i < citationList.getLength(); i++) {
            if ("citation".equals(citationList.item(i).getNodeName())) {
                Reference rf = parseCitation(citationList.item(i));
                references.add(rf);
            }
        }
        return references;
    }

    private Reference parseCitation(Node citation) {
        NodeList citationContent = citation.getChildNodes();
        Reference reference = new Reference();
        for (int i = 0; i < citationContent.getLength(); i++) {
            if ("authors".equals(citationContent.item(i).getNodeName())) {
                NodeList authorList = citationContent.item(i).getChildNodes();
                for (int j = 0; j < authorList.getLength(); j++) {
                    if ("author".equals(authorList.item(j).getNodeName())) {
                        reference.addAuthor(authorList.item(j).getTextContent());
                    }
                }
            } else if ("title".equals(citationContent.item(i).getNodeName())) {
                reference.setTitle(citationContent.item(i).getTextContent());
            } else if ("date".equals(citationContent.item(i).getNodeName())) {
                reference.setDate(citationContent.item(i).getTextContent());
            } else if ("booktitle".equals(citationContent.item(i).getNodeName())) {
                reference.setBookTitle(citationContent.item(i).getTextContent());
            } else if ("pages".equals(citationContent.item(i).getNodeName())) {
                reference.setPages(citationContent.item(i).getTextContent());
            } else if ("marker".equals(citationContent.item(i).getNodeName())) {
                reference.setMarker(citationContent.item(i).getTextContent());
            } else if ("contexts".equals(citationContent.item(i).getNodeName())) {
                NodeList contextsChildNodes = citationContent.item(i).getChildNodes();
                for (int k = 0; k < contextsChildNodes.getLength(); k++) {
                    if ("context".equals(contextsChildNodes.item(k).getNodeName())) {
                        NamedNodeMap npAttrib = contextsChildNodes.item(k).getAttributes();
                        Node citStr = npAttrib.getNamedItem("citStr");
                        reference.setCiteStr(citStr.getNodeValue());
                        break;
                    }
                }
            } else if ("publisher".equals(citationContent.item(i).getNodeName())) {
                reference.setPublisher(citationContent.item(i).getTextContent());
            } else if ("rawString".equals(citationContent.item(i).getNodeName())) {
                reference.setRawString(citationContent.item(i).getTextContent());
            } else if ("note".equals(citationContent.item(i).getNodeName())) {
                // do nothing for notes
            } else if ("editor".equals(citationContent.item(i).getNodeName())) {
                reference.setEditors(citationContent.item(i).getTextContent());
            } else if ("journal".equals(citationContent.item(i).getNodeName())) {
                reference.setJournal(citationContent.item(i).getTextContent());
            } else if ("volume".equals(citationContent.item(i).getNodeName())) {
                reference.setVolume(citationContent.item(i).getTextContent());
            } else if ("location".equals(citationContent.item(i).getNodeName())) {
                reference.setLocation(citationContent.item(i).getTextContent());
            } else if ("institution".equals(citationContent.item(i).getNodeName())) {
                reference.setInstitution(citationContent.item(i).getTextContent());
            } else if ("tech".equals(citationContent.item(i).getNodeName())) {
                reference.setJournal(citationContent.item(i).getTextContent());
            } else if (!"#text".equals(citationContent.item(i).getNodeName())) {
//                System.err.println("Not seen node for refrence of type and value"
//                        + citationContent.item(i).getNodeName());

            }
        }
        return reference;
    }

    private List<Paragraph> getCleanSectionText(String sectionText) {
        List<Paragraph> paragraphList = new ArrayList<Paragraph>();
        if ("".equals(sectionText.trim())) {
            Paragraph p = new Paragraph();
            p.setParagraphText("");
            paragraphList.add(p);
            return paragraphList;
        } else {
            String lines[] = sectionText.trim().split("\\n");

            Paragraph currentParagraph = new Paragraph();
            int avgLineLength = 0;
            int sum = 0;
            double newLineThreshold = 0;
            String currentParagraphText = "";
            for (int i = 0; i < lines.length; i++) {
                //sum += lines[i].length();
                //avgLineLength = sum / (i + 1);
                //newLineThreshold = avgLineLength - (lines[i].length() * .1);
                if (lines.length > i + 1 && lines[i].endsWith("-")) {
                    String[] split = lines[i + 1].split(" ");
                    // if there is any token in the next line
                    if (split.length > 0) {
                        //try to see if we can attach the two tokens
                        String[] tokens = lines[i].split(" ");
                        String preTok = tokens[tokens.length - 1];
                        preTok = preTok.substring(0, preTok.length() - 1);
                        if (dictionary.contains(preTok + split[0])) {
                            int spaceIdx = lines[i].lastIndexOf(" ");
                            
                            if (spaceIdx < 0) {
                               
                                  //System.out.println("\t"+ spaceIdx + " " + lines[i] + "  "+ lines[i+1]);
                                spaceIdx = 0;
                            }
                            lines[i] = lines[i].substring(0, spaceIdx) + " ";
                            lines[i + 1] = preTok + lines[i + 1];
                           
                        }

                    }

                }

                currentParagraphText += " ";
                currentParagraphText += lines[i];
            }
            currentParagraph.setParagraphText(currentParagraphText);
            paragraphList.add(currentParagraph);
            return paragraphList;
        }
    }

    private Figure processFigure(Node figureCaptionNode) {
        // we may need more processes
        Figure fig = new Figure();
        fig.setFigureCaption(figureCaptionNode.getTextContent());
        fig.setPageNumber(currentPage);
        return fig;
    }

    private Equation processEquation(Node equationNode) {
        // we may need more processes
        Equation eq = new Equation();
        eq.setEquationText(equationNode.getTextContent());
        eq.setPageNumber(currentPage);
        return eq;
    }

    private Author processAuthorString(String authorString) {
        Author author = new Author();
        author.setRawText(authorString);
        String[] authorName = authorString.split(" ");
        if (authorName.length >= 3) {
            author.setFirstName(authorName[0]);
            author.setMiddleName(authorName[1]);
            String lastName = "";
            for (int i = 2; i < authorName.length; i++) {
                lastName += authorName[i];
                lastName += " ";
            }
            author.setLastName(lastName.trim());
        } else if (authorName.length == 2) {
            author.setFirstName(authorName[0]);
            author.setMiddleName("");
            author.setLastName(authorName[1]);
        } else if (authorName.length == 1) {
            author.setFirstName("");
            author.setMiddleName("");
            author.setLastName(authorName[0]);
        }
        return author;

    }

    private List<Author> processListOfAuthorAffiliationEmail(List<Node> authorEmailAffliationList) {
        // this incomplete
        List<String> authorNodeList = new ArrayList<String>();
        List<String> affiliationNodeList = new ArrayList<String>();
        List<String> emailNodeList = new ArrayList<String>();

        for (int i = 0; i < authorEmailAffliationList.size(); i++) {
            if ("author".equals(authorEmailAffliationList.get(i).getNodeName())) {
                authorNodeList.add((authorEmailAffliationList.get(i).getTextContent()));
            } else if ("affiliation".equals(authorEmailAffliationList.get(i).getNodeName())) {
                affiliationNodeList.add((authorEmailAffliationList.get(i).getTextContent()));
            } else if ("email".equals(authorEmailAffliationList.get(i).getNodeName())) {
                emailNodeList.add((authorEmailAffliationList.get(i).getTextContent()));
            }
        }
        List<Author> authorList = new ArrayList<Author>();
        // if the number of email address and author is equal
        if (authorNodeList.size() == emailNodeList.size()) {
            for (int i = 0; i < authorNodeList.size(); i++) {
                Author author = processAuthorString(authorNodeList.get(i));
                author.setEmailAddress(emailNodeList.get(i));
                authorList.add(author);
            }
            // if the number of affiliation is equal to email and author
            if (authorNodeList.size() == affiliationNodeList.size()) {
                for (int i = 0; i < authorNodeList.size(); i++) {
                    authorList.get(i).setAffiliation(affiliationNodeList.get(i));

                }
                // if the number of affiliation is 1
            } else if (affiliationNodeList.size() == 1) {
                for (int i = 0; i < authorNodeList.size(); i++) {
                    authorList.get(i).setAffiliation(affiliationNodeList.get(0));

                }
                // if the number of affiliation is lower than authors
            } else if (affiliationNodeList.size() < authorNodeList.size()) {
                for (int i = 0; i < affiliationNodeList.size(); i++) {
                    authorList.get(i).setAffiliation(affiliationNodeList.get(i));

                }
                for (int i = affiliationNodeList.size(); i < authorNodeList.size(); i++) {
                    if (affiliationNodeList.size() > 0) {
                        authorList.get(i).setAffiliation(affiliationNodeList.get(affiliationNodeList.size() - 1));
                    }
                }
            }
            // ignoring the case that affiliation is more than author
        } else if (authorNodeList.size() > emailNodeList.size()) {
            for (int i = 0; i < authorNodeList.size(); i++) {
                Author author = processAuthorString(authorNodeList.get(i));
                authorList.add(author);
            }

            if (affiliationNodeList.size() == 1) {
                for (int i = 0; i < authorNodeList.size(); i++) {
                    authorList.get(i).setAffiliation(affiliationNodeList.get(0));
                }
            }
            // continue the process
        } else if (authorNodeList.size() < emailNodeList.size()) {
            for (int i = 0; i < authorNodeList.size(); i++) {
                Author author = processAuthorString(authorNodeList.get(i));
                authorList.add(author);
            }
            // continue the process

            if (affiliationNodeList.size() == 1) {
                for (int i = 0; i < authorNodeList.size(); i++) {
                    authorList.get(i).setAffiliation(affiliationNodeList.get(0));

                }
            }
        }

        return authorList;
    }
}
