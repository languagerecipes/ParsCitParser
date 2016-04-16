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
public class Section {

    private String title;
    private int sectionPosition;
    private List content;
    private String type;
    private int startPage;
    private int endPage;
    private List<Figure> figureList;
    private List<Table> tableList;

    public Section() {
        content = new ArrayList();
        tableList = new ArrayList<Table>();
        figureList = new ArrayList<Figure>();
    }

    public void addContent(List content) {
        this.content.addAll(content);
    }

    public void addContent(Object content) {
        this.content.add(content);
    }

    public void setContent(List content) {
        this.content = content;
    }

    public void setSectionPosition(int sectionPosition) {
        this.sectionPosition = sectionPosition;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public List getContent() {
        return content;
    }

    public int getEndPage() {
        return endPage;
    }

    public int getSectionPosition() {
        return sectionPosition;
    }

    public int getStartPage() {
        return startPage;
    }

    public String getType() {
        return type;
    }

    public void addFigure(Figure figure) {
        this.figureList.add(figure);
    }

    public void addTable(Table tbl) {
        this.tableList.add(tbl);
    }

    public List<Table> getTableList() {
        return tableList;
    }

    public List<Figure> getFigureList() {
        return figureList;
    }
}
