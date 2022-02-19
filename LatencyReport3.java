
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LatencyReportGenerator {
	public static void main(String[] args) throws IOException,
	InterruptedException {
		List<String> retransmissionByTimeFrame = new ArrayList<>();
		Integer totalRetransmission = null;
		Map<Double,Double> LatencyMap = new HashMap<>();
		String retransmissionByTimeFrameCommand = "tshark -r test.pcap -q -z io,stat,5,\"COUNT(tcp.analysis.retransmission) tcp.analysis.retransmission\"| grep -P \"\\d+\\.?\\d*\\s+<>\\s+|Interval +\\|\" | tr \"|\" \" \" | sed -E 's/<>/-/;'";

		Process process = Runtime.getRuntime().exec(
				retransmissionByTimeFrameCommand);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		// Read the output from the command
		//System.out.println("Here is the standard output of the command:\n");
		String s = null;
		while ((s = stdInput.readLine()) != null) {
			retransmissionByTimeFrame.add(s);
		}

		String totalRetransmissionCommnd = "tshark -n -r test.pcap -Y \"tcp.analysis.retransmission\" -T fields -e tcp.stream | wc -l";
		Process process2 = Runtime.getRuntime().exec(totalRetransmissionCommnd);

		BufferedReader stdInput2 = new BufferedReader(new InputStreamReader(
				process2.getInputStream()));

		// Read the output from the command
		//System.out.println("Here is the standard output of the command:\n");
		String s2 = null;
		while ((s2 = stdInput2.readLine()) != null) {
			totalRetransmission = Integer.parseInt(s2);
		}

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

		int i = 5;
		Map<Integer, List<Double>> xx = new TreeMap<>();
		for (Map.Entry<Double, Double> item1 : LatencyMap.entrySet()) {
			if (item1.getKey() < i) {
				xx.computeIfAbsent(i, k -> new ArrayList<>()).add(item1.getValue());
			} else {
				i = i + 5;
			}
		}
		for (Map.Entry<Integer, List<Double>> item1 : xx.entrySet()) {
			item1.getValue().sort(Comparator.naturalOrder());
			double average  = item1.getValue().stream().mapToDouble(f -> f).average().getAsDouble();
			//System.out.println("Before Size" + item1.getValue().size());
			int index  = (item1.getValue().size()*95)/100;
			double p95  =item1.getValue().stream().mapToDouble(f -> f).limit(index).average().getAsDouble();
			//System.out.println("After Size" + item1.getValue().stream().mapToDouble(f -> f).limit(index).count());
			System.out.println("Latency Average:  "+ average + "    p95:  "+p95);
		}

		System.out.println("Retransmission by every 5 second: "
				+ retransmissionByTimeFrame);
		System.out.println("Total Retransmission: " + totalRetransmission);

	}

}
