package com.appsofawesome.soundcontrol;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WavReader {
    
    public static short[] toIntArray(String inputfile, int sampleRate, int sampleTime){
        try{
            FileReader fr = new FileReader(new File(inputfile));
            short[] data = new short[sampleRate*sampleTime];
            
            data[0] = (short)Integer.parseInt(fr.read()+"");
            int i = 1;
            while(i < data.length && (data[i] = (short)(fr.read())) >= 0) i++;            
            return data;
        }catch(NumberFormatException e){
            return null;
        }catch(IOException e){
            return null;
        }
        
    }
}
