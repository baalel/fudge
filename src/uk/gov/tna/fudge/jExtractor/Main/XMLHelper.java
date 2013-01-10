/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.gov.tna.fudge.jExtractor.Main;

import java.util.ArrayList;

/**
 *
 * @author sprice
 */
public class XMLHelper {

/**
 * Returns the string where all non-ascii and <, &, > are encoded as numeric entities. I.e. "&lt;A &amp; B &gt;"
 * .... (insert result here). The result is safe to include anywhere in a text field in an XML-string. If there was
 * no characters to protect, the original string is returned.
 * 
 * @param originalUnprotectedString
 *            original string which may contain characters either reserved in XML or with different representation
 *            in different encodings (like 8859-1 and UFT-8)
 * @return
 */
    public static String safeText(String originalUnprotectedString) {
        if (originalUnprotectedString == null) {
            return null;
        }
        boolean anyCharactersProtected = false;

        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < originalUnprotectedString.length(); i++) {
            char ch = originalUnprotectedString.charAt(i);

            boolean controlCharacter = ch < 32;
            boolean unicodeButNotAscii = ch > 126;
            boolean characterWithSpecialMeaningInXML = ch == '<' || ch == '&' || ch == '>';

            if (characterWithSpecialMeaningInXML || unicodeButNotAscii || controlCharacter) {
                stringBuffer.append("&#").append((int) ch).append(";");
                anyCharactersProtected = true;
            } else {
                stringBuffer.append(ch);
            }
        }
        if (anyCharactersProtected == false) {
            return originalUnprotectedString;
        }

        return stringBuffer.toString();
    }
    
    public static ArrayList<String> safeText(ArrayList<String> originalStrings) {
        if(originalStrings==null){
            return null;
        }
        ArrayList<String> safeStrings=new ArrayList<String>(originalStrings.size());
        for(String s : originalStrings)
        {
            safeStrings.add(XMLHelper.safeText(s));
        }
        return safeStrings;
    }

}
