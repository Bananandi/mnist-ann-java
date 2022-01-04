package ziffererkennungsnetz;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Netzwerk {
	// Neuronenanzahlen der Schichten deklarieren
	int iknoten, hknoten, oknoten;
	
	// Array fuer hidden und output Schicht deklarieren
	double[] hidden, output;
	 
	// Arrays fuer Gewichtsschichten wih und who deklarieren
	double[][] wih, who;
	
	// Lernrate deklarieren
	double lr;
	
	public Netzwerk(int inputknoten, int hiddenknoten, int outputknoten, double lernrate) {
		// Neuronenanzahlen der Schichten initialisieren
		iknoten = inputknoten;
		hknoten = hiddenknoten;
		oknoten = outputknoten;
		
		// hidden und output Array initialisieren
		hidden = new double[hknoten];
		output = new double[oknoten];

		// alle Gewichte mit Zufallswerten initialisieren
		wih = new double[iknoten][hknoten];
		for(int h=0; h<hknoten; h++) {
			for(int i=0; i<iknoten; i++) {
				wih[i][h] = Math.random()*0.2-0.1;
			}
		}
		who = new double[hknoten][oknoten];
		for(int o=0; o<oknoten; o++) {
			for(int h=0; h<(hknoten); h++) {
				who[h][o] = Math.random()*0.2-0.1;
			}
		}
		
		// lr initialisieren
		lr = lernrate;
	}
	
	public double sigmoidfkt(double netzinput) {
		// sigmoid Schwellwertfunktion y = 1 / 1+e^x
		netzinput = 1/(1+Math.exp(-netzinput));
		return netzinput;
	}
	
	public double[] durchlauf(double[] input) {
		// Hilsvariabe fuer Summenberechnungen
		double summe = 0;
		
		hidden = new double[hknoten];
		
		output = new double[10];
		
		// hidden = input * wih durch Sigmoidfunktion
		for(int h=0; h<hknoten; h++) {
			for(int i=0; i<iknoten; i++) {
				summe += input[i] * wih[i][h];
			}
			hidden[h] = sigmoidfkt(summe);
			summe = 0;
		}
		
		// output = hidden * who durch Sigmoidfunktion
		for(int o=0; o<oknoten; o++) {
			for(int h=0; h<hknoten; h++) {
				summe += hidden[h] * who[h][o];
			}
			output[o] = sigmoidfkt(summe);
			summe = 0;
		}
		return output;
	}	
		
	public void trainieren(double input[], double[] zieloutput) {
		// Hilsvariabe fuer Summenberechnungen
		double summe = 0;

		// Netz vorwaertspropagieren
		durchlauf(input);
		
		// Netz rueckpropagieren
		// who verbessern
		for(int h=0; h<hknoten; h++) {
			for(int o=0; o<oknoten; o++) {
				who[h][o] += lr * hidden[h] * output[o] * (1-output[o]) * (zieloutput[o]-output[o]);
			}
		}
		// wih verbessern
		for(int i=0; i<iknoten; i++) {
			for(int h=0; h<hknoten; h++) {
				for(int o=0; o<oknoten; o++) {
					summe += output[o] * (1-output[o]) * (zieloutput[o]-output[o]) *  who[h][o];
				}
				wih[i][h] += lr * input[i] * hidden[h] * (1-hidden[h]) * summe;
				summe = 0;
			}
		}
	}
	
	// Netz rueckwerts durchrechnen
	public double[] rueckwerts(double[] output) {
		// Hilsvariabe fuer Summenberechnungen
		double summe = 0;
		
		hidden = new double[hknoten];
		
		double[] input = new double[iknoten];
		
		// hidden = output * who durch Sigmoidfunktion
		for(int h=0; h<hknoten; h++) {
			for(int o=0; o<oknoten; o++) {
				summe += output[o] * who[h][o];
			}
			hidden[h] = sigmoidfkt(summe);
			summe = 0;
		}
		
		// input = hidden * wih durch Sigmoidfunktion
		for(int i=0; i<iknoten; i++) {
			for(int h=0; h<hknoten; h++) {
				summe += hidden[h] * wih[i][h];
			}
			input[i] = sigmoidfkt(summe);
			summe = 0;
		}
		return input;
	}
	
	// ERDM: Speichern des Netzes in einem File
    public void speichern( ObjectOutputStream outputStream) throws IOException {
        outputStream.writeInt(iknoten);
        outputStream.writeInt(hknoten);
        outputStream.writeInt(oknoten);
        outputStream.writeObject(wih);
        outputStream.writeObject(who);
        outputStream.writeDouble(lr);
    }

 // ERDM: Einlesen des Netzes aus einem File
    public void lesen(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        iknoten = inputStream.readInt();
        hknoten = inputStream.readInt();
        oknoten = inputStream.readInt();
        wih = (double[][]) inputStream.readObject();
        who = (double[][]) inputStream.readObject();
        lr = inputStream.readDouble();
    }
}