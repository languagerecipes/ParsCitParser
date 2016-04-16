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

/**
 *
 * @author Your Name <behrang.qasemizadeh at deri.org>
 */
public class Table {
    private String tableCaption;
    private int tablePageNumber;

    public void setTableCaption(String tableCaption) {
        this.tableCaption = tableCaption;
    }

    public void setTablePageNumber(int tablePageNumber) {
        this.tablePageNumber = tablePageNumber;
    }

    public String getTableCaption() {
        return tableCaption;
    }

    public int getTablePageNumber() {
        return tablePageNumber;
    }




}
