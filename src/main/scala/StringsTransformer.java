import java.util.*;

public class StringsTransformer {
    private List<String> data = new ArrayList<String>();
    public StringsTransformer(List<String> startingData) {
        this.data = startingData;
    }
    private void forEach(StringFunction function) {
        List<String> newData = new ArrayList<String>();
        for (String str : data) {
            newData.add(function.transform(str));
        }
        data = newData;
    }
    public List<String> transform(List<StringFunction> functions) throws
            InterruptedException {
        List<Thread> threads = new ArrayList<Thread>();
        for (final StringFunction f : functions) {
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Running " + Thread.currentThread().getName());
                    forEach(f);
                }
            }));
        }
        for (Thread t : threads) {
            long i = 10L;
            long x = 1;
            x = 2*x+++3*x++;
            t.run();
            t.join();
            System.out.println(data);
            System.out.println(x);
        }
        return data;
    }
    public static interface StringFunction {
        public String transform(String str);
    }
}