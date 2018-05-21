
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class TestCosineSimilarity {

	Map<String, Integer> v1 = new HashMap<>();
	Map<String, Integer> v2 = new HashMap<>();

	@Before
	public void before() {
		v1.put("Antonio", 1);
		//v1.put("Antonio", 1);
		
		v2.put("Antonio", 1);
		v2.put("Antonio", 1);
		//v2.put("Ciao1", 1);
	}

	@Test
	public void test() {
		System.out.println(cosineSimilarity1(v1, v2));
		System.out.println(cosineSimilarity2(v1, v2));
		
		
	}

	//@Test
	public void test2() {
		
		Map<String, Integer> a = getTermFrequencyMap("He is the hero Gotham deserves".split("\\W+"));
        Map<String, Integer> b = getTermFrequencyMap("but not the one it needs right now.".split("\\W+"));

		System.out.println(cosineSimilarity1(a, b));
		System.out.println(cosineSimilarity2(a, b));
		
		
	}

	
	//@Test
	public void testBenchmark() {
		benchmark(()-> cosineSimilarity2(v1, v2), "SCARTA");
		benchmark(()-> cosineSimilarity1(v1, v2), "SCARTA");
		
		benchmark(()-> cosineSimilarity2(v1, v2), "SIM2");
		benchmark(()-> cosineSimilarity1(v1, v2), "SIM1");
		
		benchmark(()-> cosineSimilarity1(v1, v2), "SIM1");
		benchmark(()-> cosineSimilarity2(v1, v2), "SIM2");
		
	}
	
	private void benchmark(Runnable r, String s) {
		long time = System.currentTimeMillis();
		long elapsed = 0;
		
		long calc = 0;
		while(elapsed < 1000) {
			r.run();
			calc++;
			elapsed = System.currentTimeMillis() - time;
		}
		
		System.out.println(s + ": " + calc);
	}
	static double cosineSimilarity1(Map<String, Integer> v1, Map<String, Integer> v2) {
        Set<String> intersection = new HashSet<>(v1.keySet());
        intersection.retainAll(v2.keySet());
        
        double dotProduct = 0;
        for (String k : intersection) dotProduct += v1.get(k) * v2.get(k);
        
        double norm1 = 0;
        for (Integer value : v1.values()) norm1 += value * value;
        
        double norm2 = 0;
        for (Integer value : v2.values()) norm2 += value * value;
        
        return dotProduct / Math.sqrt(norm1 * norm2);
	}
	
	public Double cosineSimilarity2(final Map<String, Integer> v1,	final Map<String, Integer> v2) {
		if (v1 == null || v2 == null) {
			throw new IllegalArgumentException("Vectors must not be null");
		}

		Set<String> intersection = new HashSet<>(v1.keySet());
	    intersection.retainAll(v2.keySet());
	        
		double dotProduct = 0;
		for (final String key : intersection) {
            dotProduct += v1.get(key) * v2.get(key);
        }
		
		double d1 = 0.0d;
		for (final Integer value : v1.values()) {
			d1 += Math.pow(value, 2);
		}
		
		double d2 = 0.0d;
		for (final Integer value : v2.values()) {
			d2 += Math.pow(value, 2);
		}
		
		double cosineSimilarity;
		if (d1 <= 0.0 || d2 <= 0.0) {
			cosineSimilarity = 0.0;
		} else {
			cosineSimilarity = dotProduct / (Math.sqrt(d1) * Math.sqrt(d2));
		}
		
		return cosineSimilarity;
	}


	
	public static Map<String, Integer> getTermFrequencyMap(String[] terms) {
        Map<String, Integer> termFrequencyMap = new HashMap<>();
        for (String term : terms) {
            Integer n = termFrequencyMap.get(term);
            n = (n == null) ? 1 : ++n;
            termFrequencyMap.put(term, n);
        }
        return termFrequencyMap;
    }

}
