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

import java.util.Vector;

/**
 *
 * @author Your Name <behrang.qasemizadeh at deri.org>
 */
public class Body {

    private long confidence;
    private String content;
    private int startPage;
    private int endPage;


    public void setConfidence(long confidence) {
        this.confidence = confidence;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getConfidence() {
        return confidence;
    }

    public String getContent() {
        return content;
    }



}
