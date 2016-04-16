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
package ie.pars.parscit.parser.objects;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Your Name <behrang.qasemizadeh at deri.org>
 */
public class Paper {

    private String uid;
    private String title;
    private List<String> email;
    private List<String> affiliation;
    private List<Author> authorList;
    private List<Section> sectionList;
    private List<Reference> referenceList;
    private List<String> keywords;


    public Paper() {
        title = "";
        email = new ArrayList<String>();
        affiliation = new ArrayList<String>();
        sectionList = new ArrayList<Section>();
        authorList = new ArrayList<Author>();
        keywords = new ArrayList<String>();
        referenceList = new ArrayList<Reference>();
    }


    public void setAffiliation(String affiliation) {
        this.affiliation.add(affiliation);
    }

   

    public void setTitle(String title) {
        this.title = title;
    }

    public void addEmail(String email) {
        this.email.add(email);
    }

    public void addAffiliation(String affiliation) {
        this.affiliation.add(affiliation);
    }


    public List<String> getAffiliation() {
        return affiliation;
    }



    public List<String> getEmail() {
        return email;
    }

    public String getTitle() {
        return title;
    }

    

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public void setAffiliation(List<String> affiliation) {
        this.affiliation = affiliation;
    }

    

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public List<Section> getSectionList() {
        return sectionList;
    }


    public String getUid() {
        return uid;
    }

    public void addAuthor(Author author) {
        this.authorList.add(author);
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getKeywords() {
        return keywords;
    }
 public void addKeywords(String keywords) {
        this.keywords.add(keywords);
    }

    public List<Reference> getReferenceList() {
        return referenceList;
    }

    public void setReferenceList(List<Reference> referenceList) {
        this.referenceList = referenceList;
    }







}
