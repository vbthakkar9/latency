import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class LatencyReport3 {


	public static void main(String[] args) throws IOException,
	InterruptedException {
		List<String> retransmissionByTimeFrame = new ArrayList<>();
		Integer totalRetransmission = null;
		List<String> Latency = new ArrayList<>();
		Map<Double, Double> LatencyMap = new TreeMap<>();

		String rtt = "tshark -Tfields -E header=y -e frame.time_relative -e tcp.analysis.ack_rtt -r test.pcap";
		Process process3 = Runtime.getRuntime().exec(rtt);
		BufferedReader stdInput3 = new BufferedReader(new InputStreamReader(process3.getInputStream()));
		// Read the output from the command
		System.out.println("Here is the standard output of the command:\n");
		String s3 = null;
		int skipFirstLine=0;
		while ((s3 = stdInput3.readLine()) != null) {
			if(skipFirstLine>0){
				String[] aa = s3.split("\\s+");
				if((!"".equals(aa[0])) && aa.length==2 && !"".equals(aa[1]) ){
					LatencyMap.put(Double.parseDouble(aa[0]),Double.parseDouble(aa[1]));	
				}	
			}

			skipFirstLine++;
		}
		
		int i = 5;
		Map<Integer, List<Double>> xx = new TreeMap<>();
		for (Map.Entry<Double, Double> item1 : LatencyMap.entrySet()) {
			if (item1.getKey() < i) {
				xx.computeIfAbsent(i, k -> new ArrayList<>()).add(item1.getValue());
			} else {
				i = i + 5;
			}
		}

		System.out.println(LatencyMap.size());
		System.out.println(xx.size());
		xx.forEach((k,v)->{
			System.out.println();
			System.out.println("Key time "+k +" Size "+ v.size());
			
		});
		
		for (Map.Entry<Integer, List<Double>> item1 : xx.entrySet()) {
			item1.getValue().sort(Comparator.naturalOrder());
		}
		
		for (Map.Entry<Integer, List<Double>> item1 : xx.entrySet()) {
			double average  = item1.getValue().stream().mapToDouble(f -> f.doubleValue()).sum()/item1.getValue().size();
			System.out.println("Before Size" + item1.getValue().size());
			
			int index  = (item1.getValue().size()*95)/100;
			item1.getValue().remove(index);
			double p95  =item1.getValue().stream().mapToDouble(f -> f.doubleValue()).sum()/index;
			System.out.println("After Size" + item1.getValue().size());
			System.out.println("Average "+ average + " p95 "+p95);
		}

	}


}
