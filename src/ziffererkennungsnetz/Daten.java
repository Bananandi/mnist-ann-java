package ziffererkennungsnetz;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Daten {
	BufferedReader in;
	String[] werte = new String[785];
	
	// Datenleser erstellen
	public Daten(String file) throws FileNotFoundException {
		in = new BufferedReader(new FileReader(file));
	}	
	
	// Daten einlesen
	public void lesen(double[] input, double[] zieloutput) throws IOException {
		// Zeilenweise (ein Bild) einlesen und aufspalten
		String zeile = in.readLine();
		zeile.trim();
	    String[] teile = zeile.split("\\s*,\\s*");
	    
	    // Eingabedaten und Zielausgabedaten fuer Netz aufbereiten
	    int zielziffer = Integer.parseInt(teile[0]);
	    for(int i=1; i<teile.length; i++) {
            input[i-1] = Double.parseDouble(teile[i])/255*0.99+0.01;
        }
		for(int o=0; o<zieloutput.length; o++) {
			if(o==zielziffer) {
				zieloutput[o] = 0.99;
			}else {
				zieloutput[o] = 0.01;
			}
		}
	}	
	
	// Datenleser schließen
	public void close() throws IOException {
		in.close();
	}
}