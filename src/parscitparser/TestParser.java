/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parscitparser;

import ie.deri.nlp.parscit.parsCitParser.ParscitParser;
import java.io.File;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

/**
 *
 * @author Behrang QasemiZadeh <me at atmykitchen.info>
 */
public class TestParser {

    public static void main(String[] s) throws Exception {

        TreeSet<String> loadDictionary = ParseParscitFiles.loadDictionary();

        ParscitParser p = new ie.deri.nlp.parscit.parsCitParser.ParscitParser(loadDictionary);
        File file = new File("e:/W11-2211-parscit.130908.xml");
        //for (File file : f.listFiles()) {
        String[] split = file.getAbsolutePath().split("\\\\");
        String id = split[split.length - 1].split("-parscit")[0];
        System.out.println("* " + id);
          //  Paper parse = p.parse(file.getAbsolutePath(), id);

        DocumentBuilder builder = null;
        Document doc = null;
        //     try {
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        builder.parse(file);

    }
}
