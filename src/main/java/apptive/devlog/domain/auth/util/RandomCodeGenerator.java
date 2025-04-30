package apptive.devlog.domain.auth.util;

import java.util.Random;

public class RandomCodeGenerator {
    public static String generate6DigitCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
