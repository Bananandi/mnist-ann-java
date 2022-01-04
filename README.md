# mnist-ann-java
This is a simple 3-layer ANN for recognizing handwritten digits. It is written in Java and the algorithm is programmed without the use of external neural network libraries. Moreover the network algorithm works with the mnist dataset and the pretrained networks are trained with mnist.

The pretrained networks are in the "Netze" folder and the simple-mnist-ann.jar is a executable file compiled from the source code.

Reconize Handwritten Digits:
- Load a network (click the "Laden" button and select a pretrained network)
- Draw digits (from 0 to 9) in the draw panel and click "Berechnen" -> you can see what the net thinks you have drawn

Looking "into" the Network:
- Load a network
- Write a digit (0 to 9) under the draw panel
- Click the "Rückwärts" button -> on the drawing panel you can see how the net "imagines" you choosen digit

Train and Save a New Network:
- Dowload the mnist data set (consisting of mnist_train.csv and mnist_test.csv) e.g. from here https://www.kaggle.com/oddrationale/mnist-in-csv?select=mnist_train.csv
- Put both files (with the exact name) into the same folder as the executable java programm
- Set your prefered settings with the sliders (number of neurons in the 2. layer, learning rate, number of train digits, number of epochs)
- Click "Netz erstellen und trainieren" -> wait until it finished or cancel the calculation by clicking "Abbrechen"
- After the training finished you can see the "hit rate" of the net under the sliders
- Click "Speichern" to save your created network
