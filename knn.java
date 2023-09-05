import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class Knn {
static class Sample {
int label;
int[] pixels;
int dist;
public Sample(int label, int[] pixels) {
    this.label = label;
    this.pixels = pixels;
}

public int getLabel() {
    return label;
}

public int[] getPixels() {
    return pixels;
}

public int getDist() {
    return dist;
}

public void setDist(int dist) {
    this.dist = dist;
}
}

private static List<Sample> readFile(String file) throws IOException {
List<Sample> samples = new ArrayList<Sample>();
BufferedReader reader = new BufferedReader(new FileReader(file));
try {
    String line = reader.readLine(); // ignore header
    while ((line = reader.readLine()) != null) {
        String[] tokens = line.split(",");
        int label = Integer.parseInt(tokens[0]);
        int[] pixels = new int[tokens.length - 1];
        for (int i = 1; i < tokens.length; i++) {
            pixels[i - 1] = Integer.parseInt(tokens[i]);
        }
        Sample sample = new Sample(label, pixels);
        samples.add(sample);
    }
} finally {
    reader.close();
}
return samples;
}

private static int distance(int[] a, int[] b, String distance) {
int sum = 0;
if (distance=="Manhattan"){
for (int i = 0; i < a.length; i++) {
   
        int h = Math.abs(a[i] - b[i]);
        sum += h;
    }
    sum=(int)sum;
}

if (distance=="Euclidian"){
    for (int i = 0; i < a.length; i++) {
       
            int h = (a[i] - b[i])*(a[i] - b[i]);
            sum += h;
        }
       sum= (int) Math.sqrt(sum);
    }

    if (distance=="Chebychev"){
        
        for (int i = 0; i < a.length; i++) {
            int h = Math.abs(a[i] - b[i]);
            if(h>sum){
                sum=h;
            }
           
            }
           sum= (int) sum;
        }
    
        if (distance=="Minkowski"){
            double n = 3;
            double p = 1/3;
            for (int i = 0; i < a.length; i++) {
                double h = Math.abs(a[i] - b[i]);;
                
                int resultat = (int) Math.pow(h, n); // Le résultat sera 8
                sum += resultat;
                }
               sum= (int) Math.pow(sum, p);
            }
return sum;

}

private static int classify(List<Sample> trainingSet, int[] pixels, int k,String distance) {
for (Sample sample : trainingSet) {
    int dist = distance(sample.getPixels(), pixels,distance);
    sample.setDist(dist);
}
// sort trainingSet based on distance to the pixels
Collections.sort(trainingSet, Comparator.comparingInt(Sample::getDist));

// count the occurrences of each label among the k nearest neighbors
int[] labelCounts = new int[20]; // assuming labels are in the range [0, 9]
for (int i = 0; i < k; i++) {
    int l = trainingSet.get(i).getLabel();
    labelCounts[l]++;
}

// find the label with the highest count
int maxCount = -1;
int label = -1;
for (int l = 0; l < labelCounts.length; l++) {
    int count = labelCounts[l];
    if (count > maxCount) {
        maxCount = count;
        label = l;
    }
}
return label;
}

public static void main(String[] argv) throws IOException {
List<Sample> trainingSet = readFile("trainingsample.csv");
List<Sample> validationSet = readFile("validationsample.csv");
int numCorrect = 0;
for (Sample sample : validationSet) {
    int predictedLabel = classify(trainingSet, sample.getPixels(), 4,"Euclidian");
    if (predictedLabel == sample.getLabel()) {
        numCorrect++;
    }
    // System.out.println("Predicted label: " + predictedLabel + ", Actual label: " + sample.getLabel());
}
double accuracy = (double) numCorrect / validationSet.size() * 100;
System.out.println("Accuracy: " + accuracy + "%");
}
}
