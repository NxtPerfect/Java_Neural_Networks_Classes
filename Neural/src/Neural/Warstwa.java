package Neural;

public class Warstwa {
	Neuron[] neurony;
	int liczbaNeuronow;
	double[] PopWejscie;

	public Warstwa() {
		neurony = null;
		liczbaNeuronow = 0;
	}

	public Warstwa(int liczba_wejsc, int liczba_neuronow) {
		this.liczbaNeuronow = liczba_neuronow;
		neurony = new Neuron[liczba_neuronow];
		for (int i = 0; i < liczba_neuronow; i++)
			neurony[i] = new Neuron(liczba_wejsc);
	}

	double[] ObliczWyjscie(double[] wejscie) {
		PopWejscie = wejscie;
		double[] wyjscie = new double[liczbaNeuronow];
		for (int i = 0; i < liczbaNeuronow; i++)
			wyjscie[i] = neurony[i].ObliczWyjscie(wejscie);
		return wyjscie;
	}

	public double[] ObliczDolnaWarstwaDelta() {
		int LicznikNeuronowDolnejWar = neurony[0].LiczbaWejsc();
		double[] delta = new double[LicznikNeuronowDolnejWar];

		for (int i = 0; i < LicznikNeuronowDolnejWar; i++) {
			for (int j = 0; j < neurony.length; j++) {
				delta[i] += neurony[j].DeltaRazyWagi(i);
			}
		}
		return delta;
	}

	public void UstawDeleteWNeuronach(double[] delta) {
		for (int i = 0; i < delta.length; i++) {
			neurony[i].UstawDelte(delta[i]);
		}
	}

	public void ZmienWagi() {
		for (int i = 0; i < neurony.length; i++)
			neurony[i].ZmienWagi(PopWejscie);
	}

}
