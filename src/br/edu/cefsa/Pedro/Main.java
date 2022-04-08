package br.edu.cefsa.Pedro;

import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Main {

    public static void main(String[] args) throws Exception
    {
        try
        {
            if(!Files.exists(Path.of("jsonOut.JSON")))
            {
                Files.createFile(Path.of("jsonOut.JSON"));
            }

            Path filePath = Path.of("ExemploXML.xml");
            List<String> allLines = Files.readAllLines(filePath);
            Stack<String> tags = new Stack<String>();
            Pattern xmlOpener = Pattern.compile("^([<][\\w]+[>]){1}$");
            Pattern xmlCloser = Pattern.compile("^([<]+[/]+[\\w]+[>]+){1}$");
            Pattern xmlInfo = Pattern.compile("^([<][\\w]+[>])([\\s\\S]+)([/<][\\w]+[>])$");
            Pattern doubleNumber = Pattern.compile("([0-9]+.?[0-9]*)$");
            String jsonOut = "{ ";

            //Sintese de JSON
            for(String thisLine : allLines)
            {
                thisLine = thisLine.trim();
                char firstChar = thisLine.toCharArray()[0];

                if (firstChar != '<')
                {
                    throw new Exception("Error: Xml Malformed");
                }
                else if (xmlOpener.matcher(thisLine).find())
                {
                    String tag = thisLine.substring(1, thisLine.length() - 1);
                    tags.push(tag);
                    jsonOut += ("\r\n\"" + tag + "\": {");
                }
                else if (xmlCloser.matcher(thisLine).find())
                {
                    if (tags.peek().equals(thisLine.substring(2, thisLine.length() - 1)))
                    {
                        tags.pop();
                        if (jsonOut.toCharArray()[jsonOut.length() - 1] == ',')
                        {
                            jsonOut = jsonOut.substring(0, jsonOut.length() - 1); //Tira a vírgula
                        }
                        jsonOut += "\r\n},";
                    }
                    else
                    {
                        throw new Exception("Error: Xml Malformed");
                    }
                }
                else if (xmlInfo.matcher(thisLine).find())
                {
                    int endOfOpener = thisLine.indexOf('>');
                    int startOfCloser = thisLine.lastIndexOf('<');
                    String info = thisLine.substring(endOfOpener + 1, startOfCloser);
                    String tagName = thisLine.substring(1, endOfOpener);

                    if (doubleNumber.matcher(info).find())
                    {
                        jsonOut += ("\r\n\"" + tagName + "\": " + info + ",");
                    }
                    else
                    {
                        jsonOut += ("\r\n\"" + tagName + "\": \"" + info + "\",");
                    }
                }
            }
            if (jsonOut.toCharArray()[jsonOut.length() - 1] == ',')
            {
                jsonOut = jsonOut.substring(0, jsonOut.length() - 1); //Tira a vírgula
            }
            jsonOut += "\r\n}";
            System.out.println(jsonOut);

            Files.writeString(Path.of("jsonOut.JSON"), jsonOut);

        }

        catch(Exception e)
        {
            System.out.println(e.toString());
        }




    }
}
