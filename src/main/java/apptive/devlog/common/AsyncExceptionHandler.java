package apptive.devlog.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import java.lang.reflect.Method;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
            Class<?> declaringClass = method.getDeclaringClass();
            String simpleName = declaringClass.getSimpleName();
            log.warn("비동기 작업중 예외 발생 = className : {}, methodName : {}, ex = {}"
                    , simpleName, method.getName(), ex.toString());
    }
}
