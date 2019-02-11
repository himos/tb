import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Question2 {
    private Long m_time;
    private String m_name;
    private List<Long> m_numbers;
    private List<String> m_strings;

    // Do we really need to use Date, (example of issues https://stackoverflow.com/questions/1969442/whats-wrong-with-java-date-time-api)?
    // We can use millis since epoch as class API doesnt really depends on Date (except constructor)
    public Question2(Long time, String name, List<Long> numbers, List<String>
            strings) {

        m_time = time;
        m_name = name;
        m_numbers = numbers;
        m_strings = strings;
    }

//        It's hard to say why equals is implemented this way and for what purposes, as a rule of thumb its better to implement
//        both equals and hashCode together, also I would use equality on Class object instead of instanceOf since obj can be subclass of MyClass,
//        also better to use @Override fot less space of typos and errors
//    public boolean equals(Object obj) {
//        if (obj instanceof Question2) {
//            return m_name.equals(((Question2)obj).m_name);
//        }
//        return false;
//    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return Objects.equals(m_name, ((Question2) obj).m_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_name);
    }

    public String toString() {
//        String out = m_name;
//        for (long item : m_numbers) {
//            out += " " + item;
//        }
//        return out;
//
//        use Long instead of long to avoid unboxing/boxing
//        StringBuilder instead of String concatenation for better performance

        StringBuilder sb = new StringBuilder();
        for (Long item : m_numbers) {
            sb.append(item);
        }
        return sb.toString();
    }

    public void removeString(String str) {
//        for (int i = 0; i < m_strings.size(); i++) {
//            if (m_strings.get(i).equals(str)) {
//                m_strings.remove(i);
//            }
//        }

//        m_strings.get and m.string.remove have unpredictable complexity since we don't know actual implementation
//        usage of iterator should be better
        Iterator<String> itr = m_strings.iterator();
        while (itr.hasNext()) {
            if(str.equals(itr.next())){
                itr.remove();
            }
        }
    }

    public boolean containsNumber(Long number) {
//        for (long num : m_numbers) {
//            if (num == number) {
//                return true;
//            }
//        }
//        return false
//
//        Let's use Long instead of long to avoid BoxingUnboxing
//        and let's use contains implementation of the actual collection since it should be optimized for real underlying collection
        return m_numbers.contains(number);
    }

    public boolean isHistoric() {
//        return m_time.before(new Date());
//
//        If we drop usage of Date class:
        return m_time < System.currentTimeMillis();
    }
}