import com.tolerancia.Failure_Simulator.FailureManager;
import com.tolerancia.Failure_Simulator.FailureSpec;
import com.tolerancia.Failure_Simulator.FailureStrategy;
import com.tolerancia.Failure_Simulator.strategies.ErrorFailure;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FailureSimulatorTest {
    @Test
    void shouldTriggerErrorFailureWhenProbabilityIsOne() {
        Map<String, FailureSpec> specs = Map.of(
                "/exchange", new FailureSpec("ERROR", 1.0, 5)
        );

        Map<String, FailureStrategy> strategies = Map.of(
                "ERROR", new ErrorFailure()
        );

        FailureManager manager = new FailureManager(specs, strategies);
        ResponseEntity<?> response = manager.errorFailure("/exchange");

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
