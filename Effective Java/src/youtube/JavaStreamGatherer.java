package youtube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;

public class JavaStreamGatherer {
        public static void main(String[] args) {
                List<String> sentences = Arrays.asList("java is powerful", "streams make code clean",
                                "functional style is cool");

                List<String> resultWithoutStreams = new ArrayList<>();

                for (String sentence : sentences) {
                        String[] words = sentence.split(" ");
                        for (String word : words) {
                                if (word.length() > 3) {
                                        resultWithoutStreams.add(word.toUpperCase());
                                }
                        }
                }
                System.out.println(resultWithoutStreams);

                List<String> resultWithStreams = sentences.stream()
                                .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                                .filter(word -> word.length() > 3)
                                .map(String::toUpperCase)
                                .toList();
                System.out.println(resultWithStreams);

                /*
                 * Gatherers.fold -> collaps elements into a single result.
                 * Gatherers.scan(seed, op) -> running total/accumulation (reduce but returns
                 * intermediate results).
                 * Gatherers.windowFixed(size) -> split the stream into fixed-size batches.
                 * Gatherers.windowSliding(size) -> create overlapping sliding windows.
                 * Gatherers.mapConcurrent(size) -> run mapping function concurrently
                 * (IO/network tasks).
                 */
                List<Integer> numbers = List.of(10, 25, 40, 15);
                // Option 1: reduce
                int totalWithReduce = numbers.stream()
                                .reduce(0, Integer::sum);
                System.out.println("sum with reduce: " + totalWithReduce);

                // Option 2: mapToInt + sum (preferred to avoid boxing)
                int totalWithMapToInt = numbers.stream()
                                .mapToInt(Integer::intValue)
                                .sum();
                System.out.println("sum with mapToInt: " + totalWithMapToInt);

                // Option 3: collect with a collector
                int totalWithCollector = numbers.stream()
                                .collect(java.util.stream.Collectors.summingInt(Integer::intValue));
                System.out.println("sum with collector: " + totalWithCollector);

                Integer totalSum = numbers.stream()
                                .gather(Gatherers.fold(() -> 0, Integer::sum))
                                .findFirst()
                                .orElse(0);
                System.out.println("sum with gatherer: " + totalSum);

                List<Integer> transactions = List.of(1000, -200, -500, 200, -300);

                AtomicInteger runningTotal = new AtomicInteger(0);
                List<Integer> balancesHist = transactions.stream().map(runningTotal::addAndGet)
                                .toList();
                System.out.println("stream " + balancesHist);

                List<Integer> balanceHistory = transactions.stream()
                                .gather(Gatherers.scan(() -> 0, Integer::sum))
                                .toList();
                System.out.println("gatherers " + balanceHistory);

                List<Integer> orders = List.of(101, 102, 103, 104, 105, 106, 107);

                int batchSize = 3;
                List<List<Integer>> batches = IntStream.range(0, batchSize)
                                .mapToObj(i -> orders.subList(i * batchSize,
                                                Math.min((i + 1) * batchSize, orders.size())))
                                .toList();
                System.out.println("orderIds in batch using stream: " + batches);

                List<List<Integer>> orderIdsInBatch = orders.stream()
                                .gather(Gatherers.windowFixed(batchSize))
                                .toList();
                System.out.println("orderIds in batch using gatherers: " + orderIdsInBatch);

                // numbers: [1, 2, 3, 4, 5]
                // windowFixed: [[1,2,3],[4,5]]
                // sliding windows: [ [1,2,3], [2,3,4], [3,4,5] ]

                List<Integer> orderIds = List.of(101, 102, 103, 104, 105);
                int windowSize = 3;

                List<List<Integer>> orderList = IntStream.range(0, windowSize)
                                .mapToObj(i -> orderIds.subList(i, i + windowSize))
                                .toList();
                System.out.println("Processing with stream: " + orderList);

                List<List<Integer>> orderIdListGatherers = orderIds.stream()
                                .gather(Gatherers.windowSliding(windowSize))
                                .toList();
                System.out.println("Processing with gatherers: " + orderIdListGatherers);

                List<Integer> userIds = IntStream.rangeClosed(1, 10)
                                .boxed()
                                .toList();

                List<CompletableFuture<String>> futures = userIds.stream()
                                .map(id -> CompletableFuture.supplyAsync(() -> fetchUserProfile(id)))
                                .toList();

                List<String> results = futures.stream()
                                .map(CompletableFuture::join)
                                .toList();
                results.forEach(System.out::println);

                userIds.stream()
                                .gather(Gatherers.mapConcurrent(3, JavaStreamGatherer::fetchUserProfile))
                                .forEach(System.out::println);
        }

        private static String fetchUserProfile(Integer id) {
                try {
                        TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                }
                String threadName = Thread.currentThread().getName();
                return "Processed UserProfile: " + id + " by " + threadName;

        }
}
