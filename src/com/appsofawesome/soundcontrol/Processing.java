package com.appsofawesome.soundcontrol;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.complex.Complex;
import java.lang.Math;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class Processing {
 
    //takes an array of raw audio data and transforms
    public static Complex[] fft(short[] data){
        
        //truncate to the nearest power of 2
        double i = 0;
        while(Math.pow(2.0,i) < data.length) i++;
        i--;
        
        double[] newdata = new double[(int)Math.pow(2.0,i)];
        
        for(int j = 0; j < Math.pow(2.0,i); j++){
            if(j < data.length) newdata[j] = (double)data[j];
            else newdata[j] = 0;
        }//newdata contains copy of 'data' padded with zeros
        
        //create instance of fast fourier transform
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        
        //complexdata holds the complex transformed data values
        return fft.transform(newdata, TransformType.FORWARD);
        
    }
    
    //sampleRate and cutoff are in Hz
    //returns [peakAmplitude (dB), highRatio]
    public static double[] amplitude_ratio(short[] data, short sampleRate, short cutoff){
        Complex[] transformed = fft(data);
        double[] vader = new double[2];
        
        //find max magnitude in first half of transformed complex array
        int maxIndex = 0, highFreqCount = 0, f;
        double amplitude = 0;
        for(int i=1; i < transformed.length/2; i++){
            if(transformed[maxIndex].abs() < transformed[i].abs()) maxIndex = i;
            if((i * sampleRate / transformed.length) > cutoff) highFreqCount++;
            amplitude += Math.log(transformed[i].abs());
        }
        //dominant pitch
        double peakFreq = maxIndex * sampleRate / transformed.length;
        //peakAmplitude in dB scale
        double pa = Math.log(transformed[maxIndex].abs());
        //avg amplitude in dB scale
        vader[0] = 2*amplitude/transformed.length;
        //highRatio
        //vader[1] = 2*highFreqCount/transformed.length;
        //normalized difference between dominant pitch and cutoff frequency
        vader[1] = (peakFreq - cutoff)/cutoff;
        return vader;
    }
    
    public static short[] getDataFromFile(String inputFile) throws IOException{
    	BufferedReader br = null;
        try{
        	br = new BufferedReader(new FileReader(new File(inputFile)));
            int count = 0;
            String s;
            while((s = br.readLine()) != null) count++;         
            short[] data = new short[count];
            
            BufferedReader br2 = new BufferedReader(new FileReader(new File(inputFile)));
            count = 0;
            while((s = br.readLine()) != null){
                data[count] = (short)Integer.parseInt(s);
                count++;
            }
            return data;
        }catch(IOException e){
            return null;
        }finally{
        	if(br != null)
        		br.close();
        }
    }
    
}

