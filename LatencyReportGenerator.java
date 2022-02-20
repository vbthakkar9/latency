import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LatencyReportGenerator {
	public static void main(String[] args) throws IOException,
	InterruptedException {

		Map<Double, Double> LatencyMap = new TreeMap<>();

		String rtt = "tshark -Tfields -E header=y -e frame.time_relative -e tcp.analysis.ack_rtt -r test.pcap";
		Process process3 = Runtime.getRuntime().exec(rtt);
		BufferedReader stdInput3= new BufferedReader(new InputStreamReader(
				process3.getInputStream()));
		// Read the output from the command
		//System.out.println("Here is the standard output of the command:\n");
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

		int i = 10;//iterate on every 5 second
		Map<Integer, List<Double>> mapByDuration = new TreeMap<>();
		for (Map.Entry<Double, Double> item1 : LatencyMap.entrySet()) {
			if (item1.getKey() < i) {
				mapByDuration.computeIfAbsent(i, k -> new ArrayList<>()).add(item1.getValue());
			} else {
				i = i + 10;
			}
		}
		System.out.println("-----LATENCY REPORT--------");
		System.out.println("");
		int lastEntry = ((TreeMap<Integer, List<Double>>) mapByDuration).lastEntry().getKey();
		System.out.println("Interval     AVERAGE                 P95               P99");
		for (Map.Entry<Integer, List<Double>> item1 : mapByDuration.entrySet()) {
			item1.getValue().sort(Comparator.naturalOrder());
			double average  = item1.getValue().stream().mapToDouble(f -> f).average().getAsDouble();
			//System.out.println("Before Size" + item1.getValue().size());
			int index95  = (item1.getValue().size()*95)/100;
			double p95  =item1.getValue().stream().mapToDouble(f -> f).limit(index95).average().getAsDouble();

			int index99  = (item1.getValue().size()*99)/100;
			double p99  =item1.getValue().stream().mapToDouble(f -> f).limit(index99).average().getAsDouble();

			if(item1.getKey()==lastEntry){
				System.out.println(item1.getKey()-5+" - "+"Dur  "+ average +"  "+ p95+"  "+p99);
			}else{
				System.out.println(item1.getKey()-5+" - "+item1.getKey()+ "  "+ average + "  "+p95+"  "+p99);	
			}
		}
	}
}
