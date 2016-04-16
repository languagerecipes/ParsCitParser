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
public class Reference {

    double refDBID;
    String title;
    String citeStr;
    List<String> author;
    String date;
    String publisher;
    String journal;
    String volume;
    String location;
    String pages;
    String marker;
    String rawString;
    String bookTitle;
    String institution;
    String editors;

    public Reference() {
        author = new ArrayList<String>();
        title = "";
        citeStr = "";
        date = "";
        publisher = "";
        journal = "";
        volume = "";
        location = "";
        pages = "";
        marker = "";
        rawString = "";
        bookTitle = "";
        institution = "";
        editors = "";

    }

    public void setRefDBID(double refDBID) {
        this.refDBID = refDBID;
    }

    public double getRefDBID() {
        return refDBID;
    }




    public void addAuthor(String author) {
        this.author.add(author);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public void setRawString(String rawString) {
        this.rawString = rawString;
    }

    public void setCiteStr(String citeStr) {
        this.citeStr = citeStr;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void setLocation(String location) {
        this.location = location;
    }



    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setEditors(String editors) {
        this.editors = editors;
    }

    public List<String> getAuthor() {
        return author;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getCiteStr() {
        return citeStr;
    }

    public String getDate() {
        return date;
    }

    public String getEditors() {
        return editors;
    }

    public String getInstitution() {
        return institution;
    }

    public String getJournal() {
        return journal;
    }

    public String getLocation() {
        return location;
    }

    public String getMarker() {
        return marker;
    }

    public String getPages() {
        return pages;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getRawString() {
        return rawString;
    }

    public String getTitle() {
        return title;
    }

    public String getVolume() {
        return volume;
    }







}
