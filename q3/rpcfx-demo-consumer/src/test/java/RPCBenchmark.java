import com.zst.week9.q3.client.Rpcfx;
import com.zst.week9.q3.demo.api.User;
import com.zst.week9.q3.demo.api.UserService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;

@SpringBootTest
public class RPCBenchmark {
    @Test
    public void runBenchmark() throws InterruptedException {
        AtomicLong a = new AtomicLong(0);
        new Thread(() -> {
            UserService userService = Rpcfx.create(UserService.class, "http://localhost:8080/");
            while (true) {
                User user = userService.findById(1);
                a.incrementAndGet();
            }
        }).start();

        int i = 1;
        while (true) {
            Thread.sleep(1000);
            System.err.println(a.get() / i++);
        }
    }
}
