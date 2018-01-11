package no.hiof.arcade.io.filehandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author JonAre
 */
public class FileHandler
{
    public static ArrayList<String> getLinesFromFile(String file) throws FileNotFoundException, IOException
    {
        ArrayList<String> strings = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = br.readLine();
        
        while(line!=null)
        {
            strings.add(line);
            line=br.readLine();
        }
        
        br.close();
        
        return strings;
    }
}
